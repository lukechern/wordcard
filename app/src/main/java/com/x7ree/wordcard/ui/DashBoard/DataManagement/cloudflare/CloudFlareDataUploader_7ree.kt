package com.x7ree.wordcard.ui.DashBoard.DataManagement.cloudflare

import android.content.Context
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonArray
import java.io.File

/**
 * CloudFlare D1数据上传器
 * 负责将本地数据上传到CloudFlare D1数据库
 */
class CloudFlareDataUploader_7ree(
    private val context: Context,
    private val wordQueryViewModel: WordQueryViewModel_7ree
) {
    private val json = Json { ignoreUnknownKeys = true }
    
    /**
     * 上传数据到CloudFlare D1
     */
    suspend fun uploadData(
        accountId: String,
        databaseId: String,
        apiToken: String,
        onProgress: (String) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiClient = CloudFlareApiClient_7ree(accountId, databaseId, apiToken)
            
            onProgress("正在创建数据库表结构...")
            
            // 创建表结构
            val createTablesResult = createTables(apiClient)
            if (createTablesResult.isFailure) {
                return@withContext Result.failure(
                    Exception("创建表结构失败: ${createTablesResult.exceptionOrNull()?.message}")
                )
            }
            
            onProgress("正在获取本地单词数据...")
            
            // 获取并上传单词数据
            val wordDataResult = uploadWordData(apiClient, onProgress)
            if (wordDataResult.isFailure) {
                return@withContext Result.failure(
                    Exception("上传单词数据失败: ${wordDataResult.exceptionOrNull()?.message}")
                )
            }
            
            onProgress("正在获取本地文章数据...")
            
            // 获取并上传文章数据
            val articleDataResult = uploadArticleData(apiClient, onProgress)
            if (articleDataResult.isFailure) {
                return@withContext Result.failure(
                    Exception("上传文章数据失败: ${articleDataResult.exceptionOrNull()?.message}")
                )
            }
            
            onProgress("正在完成上传...")
            
            val wordCount = wordDataResult.getOrNull() ?: 0
            val articleCount = articleDataResult.getOrNull() ?: 0
            
            Result.success("数据上传成功！已同步 $wordCount 条单词记录和 $articleCount 篇文章到CloudFlare D1数据库")
            
        } catch (e: Exception) {
            Result.failure(Exception("上传过程异常: ${e.message}"))
        }
    }
    
    /**
     * 创建数据库表结构
     */
    private suspend fun createTables(apiClient: CloudFlareApiClient_7ree): Result<Unit> {
        return try {
            val createWordTableSql = """
                CREATE TABLE IF NOT EXISTS words (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    word TEXT NOT NULL,
                    translation TEXT,
                    pronunciation TEXT,
                    query_time INTEGER,
                    source TEXT,
                    created_at INTEGER DEFAULT (strftime('%s', 'now')),
                    updated_at INTEGER DEFAULT (strftime('%s', 'now'))
                )
            """.trimIndent()
            
            val createArticleTableSql = """
                CREATE TABLE IF NOT EXISTS articles (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    content TEXT,
                    word_count INTEGER DEFAULT 0,
                    created_at INTEGER DEFAULT (strftime('%s', 'now')),
                    updated_at INTEGER DEFAULT (strftime('%s', 'now'))
                )
            """.trimIndent()
            
            val createArticleWordsTableSql = """
                CREATE TABLE IF NOT EXISTS article_words (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    article_id INTEGER,
                    word TEXT NOT NULL,
                    translation TEXT,
                    position INTEGER,
                    created_at INTEGER DEFAULT (strftime('%s', 'now')),
                    FOREIGN KEY (article_id) REFERENCES articles (id)
                )
            """.trimIndent()
            
            val createIndexSql = listOf(
                "CREATE INDEX IF NOT EXISTS idx_words_word ON words(word)",
                "CREATE INDEX IF NOT EXISTS idx_words_query_time ON words(query_time)",
                "CREATE INDEX IF NOT EXISTS idx_articles_created_at ON articles(created_at)",
                "CREATE INDEX IF NOT EXISTS idx_article_words_article_id ON article_words(article_id)",
                "CREATE INDEX IF NOT EXISTS idx_article_words_word ON article_words(word)"
            )
            
            // 执行创建表的SQL
            val sqlStatements = listOf(createWordTableSql, createArticleTableSql, createArticleWordsTableSql) + createIndexSql
            val result = apiClient.executeBatch(sqlStatements)
            
            if (result.isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("创建表失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 上传单词数据
     */
    private suspend fun uploadWordData(
        apiClient: CloudFlareApiClient_7ree,
        onProgress: (String) -> Unit
    ): Result<Int> {
        return try {
            onProgress("正在导出本地单词数据...")
            
            // 获取本地单词数据
            val dataExportImportManager = wordQueryViewModel.getDataExportImportManager()
            val exportResult = dataExportImportManager.exportData_7ree()
            
            if (exportResult.isFailure) {
                return Result.failure(Exception("导出本地单词数据失败"))
            }
            
            val filePath = exportResult.getOrNull()
            if (filePath == null) {
                return Result.failure(Exception("导出文件路径为空"))
            }
            
            val file = File(filePath)
            if (!file.exists()) {
                return Result.failure(Exception("导出文件不存在"))
            }
            
            onProgress("正在解析单词数据...")
            
            val content = file.readText()
            val jsonElement = json.parseToJsonElement(content)
            
            if (jsonElement !is JsonObject) {
                return Result.failure(Exception("数据格式错误"))
            }
            
            val wordsArray = jsonElement["words"] as? JsonArray
            if (wordsArray == null) {
                return Result.failure(Exception("未找到单词数据"))
            }
            
            onProgress("正在上传单词数据到CloudFlare...")
            
            // 清空现有数据
            apiClient.executeQuery("DELETE FROM words")
            
            var uploadCount = 0
            val batchSize = 50 // 批量插入大小
            
            for (i in wordsArray.indices step batchSize) {
                val batch = wordsArray.subList(i, minOf(i + batchSize, wordsArray.size))
                val insertSql = buildWordInsertSql(batch)
                
                val result = apiClient.executeQuery(insertSql)
                if (result.isSuccess) {
                    uploadCount += batch.size
                    onProgress("已上传 $uploadCount/${wordsArray.size} 条单词记录...")
                } else {
                    return Result.failure(Exception("批量插入单词数据失败: ${result.exceptionOrNull()?.message}"))
                }
            }
            
            Result.success(uploadCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 上传文章数据
     */
    private suspend fun uploadArticleData(
        apiClient: CloudFlareApiClient_7ree,
        onProgress: (String) -> Unit
    ): Result<Int> {
        return try {
            onProgress("正在导出本地文章数据...")
            
            // 获取本地文章数据
            val dataExportImportManager = wordQueryViewModel.getDataExportImportManager()
            val exportResult = dataExportImportManager.exportArticleData_7ree()
            
            if (exportResult.isFailure) {
                return Result.failure(Exception("导出本地文章数据失败"))
            }
            
            val filePath = exportResult.getOrNull()
            if (filePath == null) {
                return Result.failure(Exception("导出文件路径为空"))
            }
            
            val file = File(filePath)
            if (!file.exists()) {
                return Result.failure(Exception("导出文件不存在"))
            }
            
            onProgress("正在解析文章数据...")
            
            val content = file.readText()
            val jsonElement = json.parseToJsonElement(content)
            
            if (jsonElement !is JsonObject) {
                return Result.failure(Exception("文章数据格式错误"))
            }
            
            val articlesArray = jsonElement["articles"] as? JsonArray
            if (articlesArray == null) {
                return Result.failure(Exception("未找到文章数据"))
            }
            
            onProgress("正在上传文章数据到CloudFlare...")
            
            // 清空现有数据
            apiClient.executeQuery("DELETE FROM article_words")
            apiClient.executeQuery("DELETE FROM articles")
            
            var uploadCount = 0
            
            for (articleElement in articlesArray) {
                if (articleElement !is JsonObject) continue
                
                // 插入文章记录
                val insertArticleResult = insertArticle(apiClient, articleElement)
                if (insertArticleResult.isSuccess) {
                    uploadCount++
                    onProgress("已上传 $uploadCount/${articlesArray.size} 篇文章...")
                } else {
                    return Result.failure(Exception("插入文章失败: ${insertArticleResult.exceptionOrNull()?.message}"))
                }
            }
            
            Result.success(uploadCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 构建单词批量插入SQL
     */
    private fun buildWordInsertSql(words: List<JsonElement>): String {
        val values = words.mapNotNull { wordElement ->
            if (wordElement !is JsonObject) return@mapNotNull null
            
            val word = wordElement["word"]?.toString()?.trim('"') ?: return@mapNotNull null
            
            // 正确映射本地WordEntity_7ree的字段
            val chineseDefinition = wordElement["chineseDefinition"]?.toString()?.trim('"') ?: ""
            val phonetic = wordElement["phonetic"]?.toString()?.trim('"') ?: ""
            val apiResult = wordElement["apiResult"]?.toString()?.trim('"') ?: ""
            val queryTimestamp = wordElement["queryTimestamp"]?.toString()?.trim('"')?.toLongOrNull() ?: System.currentTimeMillis()
            val partOfSpeech = wordElement["partOfSpeech"]?.toString()?.trim('"') ?: ""
            
            // 映射到CloudFlare D1字段
            val translation = chineseDefinition // chineseDefinition -> translation
            val pronunciation = phonetic // phonetic -> pronunciation
            val source = "local" // 默认来源
            
            // 处理查询时间 - 支持多种可能的字段名
            val queryTime = queryTimestamp
            
            // 处理创建和更新时间
            val createdAt = queryTime
            val updatedAt = queryTime
            
            // 转换为秒级时间戳（CloudFlare D1使用秒级）
            val queryTimeSeconds = if (queryTime > 1000000000000L) queryTime / 1000 else queryTime
            val createdAtSeconds = if (createdAt > 1000000000000L) createdAt / 1000 else createdAt
            val updatedAtSeconds = if (updatedAt > 1000000000000L) updatedAt / 1000 else updatedAt
            
            "('${word.replace("'", "''")}', '${translation.replace("'", "''")}', '${pronunciation.replace("'", "''")}', $queryTimeSeconds, '${source.replace("'", "''")}', $createdAtSeconds, $updatedAtSeconds)"
        }
        
        return if (values.isNotEmpty()) {
            "INSERT INTO words (word, translation, pronunciation, query_time, source, created_at, updated_at) VALUES ${values.joinToString(", ")}"
        } else {
            "SELECT 1" // 空操作
        }
    }
    
    /**
     * 插入单篇文章
     */
    private suspend fun insertArticle(apiClient: CloudFlareApiClient_7ree, articleElement: JsonObject): Result<Unit> {
        return try {
            // 正确映射本地ArticleEntity_7ree的字段
            val englishTitle = articleElement["englishTitle"]?.toString()?.trim('"') ?: ""
            val titleTranslation = articleElement["titleTranslation"]?.toString()?.trim('"') ?: ""
            val englishContent = articleElement["englishContent"]?.toString()?.trim('"') ?: ""
            val chineseContent = articleElement["chineseContent"]?.toString()?.trim('"') ?: ""
            val bilingualComparison = articleElement["bilingualComparison"]?.toString()?.trim('"') ?: ""
            val keyWords = articleElement["keyWords"]?.toString()?.trim('"') ?: ""
            
            // 组合标题：优先使用英文标题，如果为空则使用翻译
            val title = if (englishTitle.isNotEmpty()) {
                if (titleTranslation.isNotEmpty()) "$englishTitle ($titleTranslation)" else englishTitle
            } else if (titleTranslation.isNotEmpty()) {
                titleTranslation
            } else {
                "Untitled Article"
            }
            
            // 组合内容：包含英文内容、中文内容和双语对照
            val content = buildString {
                if (englishContent.isNotEmpty()) {
                    append("=== English Content ===\n")
                    append(englishContent)
                    append("\n\n")
                }
                if (chineseContent.isNotEmpty()) {
                    append("=== Chinese Translation ===\n")
                    append(chineseContent)
                    append("\n\n")
                }
                if (bilingualComparison.isNotEmpty()) {
                    append("=== Bilingual Comparison ===\n")
                    append(bilingualComparison)
                    append("\n\n")
                }
                if (keyWords.isNotEmpty()) {
                    append("=== Key Words ===\n")
                    append(keyWords)
                }
            }.trim()
            
            // 计算单词数量（基于英文内容）
            val wordCount = if (englishContent.isNotEmpty()) {
                englishContent.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
            } else 0
            
            // 处理时间戳字段 - 使用generationTimestamp
            val generationTimestamp = articleElement["generationTimestamp"]?.toString()?.trim('"')?.toLongOrNull() 
                ?: System.currentTimeMillis()
            
            // 转换为秒级时间戳（CloudFlare D1使用秒级）
            val createdAtSeconds = if (generationTimestamp > 1000000000000L) generationTimestamp / 1000 else generationTimestamp
            val updatedAtSeconds = createdAtSeconds
            
            // 插入文章（包含所有字段）
            val insertArticleSql = """
                INSERT INTO articles (title, content, word_count, created_at, updated_at) 
                VALUES ('${title.replace("'", "''")}', '${content.replace("'", "''")}', $wordCount, $createdAtSeconds, $updatedAtSeconds)
            """.trimIndent()
            
            val articleResult = apiClient.executeQuery(insertArticleSql)
            if (articleResult.isFailure) {
                return Result.failure(articleResult.exceptionOrNull() ?: Exception("插入文章失败"))
            }
            
            // 获取文章ID
            val getIdResult = apiClient.executeQuery("SELECT last_insert_rowid() as id")
            if (getIdResult.isFailure) {
                return Result.failure(Exception("获取文章ID失败"))
            }
            
            // 获取文章ID值
            val queryResult = getIdResult.getOrNull()
            val articleId = queryResult?.results?.firstOrNull()?.get("id")
                ?.toString()?.trim('"')?.toLongOrNull() ?: 0L
            
            // 插入关键词作为文章单词
            if (keyWords.isNotEmpty() && articleId > 0) {
                insertKeyWordsAsArticleWords(apiClient, articleId, keyWords)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 插入文章单词
     */
    private suspend fun insertArticleWords(
        apiClient: CloudFlareApiClient_7ree,
        articleId: Long,
        wordsArray: JsonArray
    ): Result<Unit> {
        return try {
            val wordValues = wordsArray.mapIndexedNotNull { index, wordElement ->
                if (wordElement !is JsonObject) return@mapIndexedNotNull null
                
                val word = wordElement["word"]?.toString()?.trim('"') ?: return@mapIndexedNotNull null
                val translation = wordElement["translation"]?.toString()?.trim('"') ?: ""
                val position = wordElement["position"]?.toString()?.trim('"')?.toIntOrNull() ?: index
                val createdAt = System.currentTimeMillis() / 1000 // 秒级时间戳
                
                "($articleId, '${word.replace("'", "''")}', '${translation.replace("'", "''")}', $position, $createdAt)"
            }
            
            if (wordValues.isNotEmpty()) {
                val insertWordsSql = """
                    INSERT INTO article_words (article_id, word, translation, position, created_at) 
                    VALUES ${wordValues.joinToString(", ")}
                """.trimIndent()
                
                val result = apiClient.executeQuery(insertWordsSql)
                if (result.isFailure) {
                    return Result.failure(Exception("插入文章单词失败: ${result.exceptionOrNull()?.message}"))
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 将关键词插入为文章单词
     */
    private suspend fun insertKeyWordsAsArticleWords(
        apiClient: CloudFlareApiClient_7ree,
        articleId: Long,
        keyWords: String
    ): Result<Unit> {
        return try {
            // 解析关键词（用逗号分隔）
            val words = keyWords.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            
            if (words.isEmpty()) {
                return Result.success(Unit)
            }
            
            val wordValues = words.mapIndexed { index, word ->
                val createdAt = System.currentTimeMillis() / 1000 // 秒级时间戳
                "($articleId, '${word.replace("'", "''")}', '', $index, $createdAt)"
            }
            
            val insertWordsSql = """
                INSERT INTO article_words (article_id, word, translation, position, created_at) 
                VALUES ${wordValues.joinToString(", ")}
            """.trimIndent()
            
            val result = apiClient.executeQuery(insertWordsSql)
            if (result.isFailure) {
                return Result.failure(Exception("插入关键词失败: ${result.exceptionOrNull()?.message}"))
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}