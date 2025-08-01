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
            
            onProgress("正在检查数据库表结构...")
            
            // 检查表结构是否存在
            val checkTablesResult = checkTables(apiClient)
            if (checkTablesResult.isFailure) {
                return@withContext Result.failure(
                    Exception("表结构检查失败: ${checkTablesResult.exceptionOrNull()?.message}")
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
     * 检查数据库表结构是否存在
     */
    private suspend fun checkTables(apiClient: CloudFlareApiClient_7ree): Result<Unit> {
        return try {
            // 检查words表结构
            val wordsTableResult = apiClient.executeQuery("PRAGMA table_info(words)")
            if (wordsTableResult.isFailure) {
                return Result.failure(Exception("无法访问words表，请先手动创建表结构。参考文档：document/本地单词和文章数据表的详细结构.md"))
            }
            
            val wordsTableInfo = wordsTableResult.getOrNull()
            val wordsColumns = wordsTableInfo?.results?.map { row ->
                row["name"]?.toString()?.trim('"') ?: ""
            } ?: emptyList()
            
            val requiredWordsFields = listOf("word", "apiResult", "queryTimestamp", "viewCount", "isFavorite", "spellingCount", "chineseDefinition", "phonetic", "partOfSpeech", "referenceCount")
            val missingWordsFields = requiredWordsFields.filter { it !in wordsColumns }
            
            if (missingWordsFields.isNotEmpty()) {
                return Result.failure(Exception("words表结构不完整，缺少字段: ${missingWordsFields.joinToString(", ")}。请参考文档手动创建完整表结构。"))
            }
            
            // 检查articles表结构
            val articlesTableResult = apiClient.executeQuery("PRAGMA table_info(articles)")
            if (articlesTableResult.isFailure) {
                return Result.failure(Exception("无法访问articles表，请先手动创建表结构。参考文档：document/本地单词和文章数据表的详细结构.md"))
            }
            
            val articlesTableInfo = articlesTableResult.getOrNull()
            val articlesColumns = articlesTableInfo?.results?.map { row ->
                row["name"]?.toString()?.trim('"') ?: ""
            } ?: emptyList()
            
            val requiredArticlesFields = listOf("id", "generationTimestamp", "keyWords", "viewCount", "apiResult", "englishTitle", "titleTranslation", "englishContent", "chineseContent", "bilingualComparison", "isFavorite", "author")
            val missingArticlesFields = requiredArticlesFields.filter { it !in articlesColumns }
            
            if (missingArticlesFields.isNotEmpty()) {
                return Result.failure(Exception("articles表结构不完整，缺少字段: ${missingArticlesFields.joinToString(", ")}。请参考文档手动创建完整表结构。"))
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("表结构检查失败: ${e.message}。请确保已按照文档手动创建表结构。"))
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
            
            // 验证表结构是否正确创建
            val verifyTableResult = apiClient.executeQuery("PRAGMA table_info(words)")
            if (verifyTableResult.isFailure) {
                return Result.failure(Exception("验证words表结构失败: ${verifyTableResult.exceptionOrNull()?.message}"))
            }
            
            // 输出表结构信息用于调试
            val tableInfo = verifyTableResult.getOrNull()
            val columnNames = tableInfo?.results?.map { row ->
                row["name"]?.toString()?.trim('"') ?: "unknown"
            } ?: emptyList()
            
            onProgress("表结构验证: 发现字段 ${columnNames.joinToString(", ")}")
            
            // 检查必要字段是否存在
            val requiredFields = listOf("word", "apiResult", "queryTimestamp", "viewCount", "isFavorite", "spellingCount", "chineseDefinition", "phonetic", "partOfSpeech", "referenceCount")
            val missingFields = requiredFields.filter { it !in columnNames }
            
            if (missingFields.isNotEmpty()) {
                return Result.failure(Exception("表结构不完整，缺少字段: ${missingFields.joinToString(", ")}"))
            }
            
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
            
            // 直接映射本地WordEntity_7ree的所有字段
            val word = wordElement["word"]?.toString()?.trim('"') ?: return@mapNotNull null
            val apiResult = wordElement["apiResult"]?.toString()?.trim('"')?.replace("'", "''") ?: ""
            val queryTimestamp = wordElement["queryTimestamp"]?.toString()?.trim('"')?.toLongOrNull() ?: System.currentTimeMillis()
            val viewCount = wordElement["viewCount"]?.toString()?.trim('"')?.toIntOrNull() ?: 0
            val isFavorite = if (wordElement["isFavorite"]?.toString()?.trim('"')?.toBoolean() == true) 1 else 0
            val spellingCount = wordElement["spellingCount"]?.toString()?.trim('"')?.toIntOrNull() ?: 0
            val chineseDefinition = wordElement["chineseDefinition"]?.toString()?.trim('"')?.replace("'", "''") ?: ""
            val phonetic = wordElement["phonetic"]?.toString()?.trim('"')?.replace("'", "''") ?: ""
            val partOfSpeech = wordElement["partOfSpeech"]?.toString()?.trim('"')?.replace("'", "''") ?: ""
            val referenceCount = wordElement["referenceCount"]?.toString()?.trim('"')?.toIntOrNull() ?: 0
            
            "('${word.replace("'", "''")}', '$apiResult', $queryTimestamp, $viewCount, $isFavorite, $spellingCount, '$chineseDefinition', '$phonetic', '$partOfSpeech', $referenceCount)"
        }
        
        return if (values.isNotEmpty()) {
            "INSERT INTO words (word, apiResult, queryTimestamp, viewCount, isFavorite, spellingCount, chineseDefinition, phonetic, partOfSpeech, referenceCount) VALUES ${values.joinToString(", ")}"
        } else {
            "SELECT 1" // 空操作
        }
    }
    
    /**
     * 插入单篇文章
     */
    private suspend fun insertArticle(apiClient: CloudFlareApiClient_7ree, articleElement: JsonObject): Result<Unit> {
        return try {
            // 直接映射本地ArticleEntity_7ree的所有字段
            val generationTimestamp = articleElement["generationTimestamp"]?.toString()?.trim('"')?.toLongOrNull() ?: System.currentTimeMillis()
            val keyWords = articleElement["keyWords"]?.toString()?.trim('"')?.replace("'", "''") ?: ""
            val viewCount = articleElement["viewCount"]?.toString()?.trim('"')?.toIntOrNull() ?: 0
            val apiResult = articleElement["apiResult"]?.toString()?.trim('"')?.replace("'", "''") ?: ""
            val englishTitle = articleElement["englishTitle"]?.toString()?.trim('"')?.replace("'", "''") ?: ""
            val titleTranslation = articleElement["titleTranslation"]?.toString()?.trim('"')?.replace("'", "''") ?: ""
            val englishContent = articleElement["englishContent"]?.toString()?.trim('"')?.replace("'", "''") ?: ""
            val chineseContent = articleElement["chineseContent"]?.toString()?.trim('"')?.replace("'", "''") ?: ""
            val bilingualComparison = articleElement["bilingualComparison"]?.toString()?.trim('"')?.replace("'", "''") ?: ""
            val isFavorite = if (articleElement["isFavorite"]?.toString()?.trim('"')?.toBoolean() == true) 1 else 0
            val author = articleElement["author"]?.toString()?.trim('"')?.replace("'", "''") ?: ""
            
            // 插入文章（完全匹配本地表结构）
            val insertArticleSql = """
                INSERT INTO articles (generationTimestamp, keyWords, viewCount, apiResult, englishTitle, titleTranslation, englishContent, chineseContent, bilingualComparison, isFavorite, author) 
                VALUES ($generationTimestamp, '$keyWords', $viewCount, '$apiResult', '$englishTitle', '$titleTranslation', '$englishContent', '$chineseContent', '$bilingualComparison', $isFavorite, '$author')
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