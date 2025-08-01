package com.x7ree.wordcard.ui.DashBoard.DataManagement.cloudflare

import android.content.Context
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.io.File

/**
 * CloudFlare D1数据下载器
 * 负责从CloudFlare D1数据库下载数据到本地
 */
class CloudFlareDataDownloader_7ree(
    private val context: Context,
    private val wordQueryViewModel: WordQueryViewModel_7ree
) {
    private val json = Json { ignoreUnknownKeys = true }
    
    /**
     * 从CloudFlare D1下载数据
     */
    suspend fun downloadData(
        accountId: String,
        databaseId: String,
        apiToken: String,
        onProgress: (String) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiClient = CloudFlareApiClient_7ree(accountId, databaseId, apiToken)
            
            onProgress("正在连接CloudFlare D1数据库...")
            
            // 测试连接
            val connectionResult = apiClient.testConnection()
            if (connectionResult.isFailure) {
                return@withContext Result.failure(
                    Exception("连接失败: ${connectionResult.exceptionOrNull()?.message}")
                )
            }
            
            onProgress("正在查询云端数据...")
            
            // 检查表是否存在
            val tablesExist = checkTablesExist(apiClient)
            if (!tablesExist) {
                return@withContext Result.failure(Exception("云端数据库中未找到数据表，请先上传数据"))
            }
            
            onProgress("正在下载单词数据...")
            
            // 下载单词数据
            val wordDataResult = downloadWordData(apiClient, onProgress)
            if (wordDataResult.isFailure) {
                return@withContext Result.failure(
                    Exception("下载单词数据失败: ${wordDataResult.exceptionOrNull()?.message}")
                )
            }
            
            onProgress("正在下载文章数据...")
            
            // 下载文章数据
            val articleDataResult = downloadArticleData(apiClient, onProgress)
            if (articleDataResult.isFailure) {
                return@withContext Result.failure(
                    Exception("下载文章数据失败: ${articleDataResult.exceptionOrNull()?.message}")
                )
            }
            
            onProgress("正在导入到本地数据库...")
            
            // 导入到本地数据库
            val importResult = importToLocalDatabase(wordDataResult.getOrNull(), articleDataResult.getOrNull())
            if (importResult.isFailure) {
                return@withContext Result.failure(
                    Exception("导入本地数据库失败: ${importResult.exceptionOrNull()?.message}")
                )
            }
            
            val wordCount = wordDataResult.getOrNull()?.size ?: 0
            val articleCount = articleDataResult.getOrNull()?.size ?: 0
            
            Result.success("数据下载成功！已从CloudFlare D1同步 $wordCount 条单词记录和 $articleCount 篇文章到本地")
            
        } catch (e: Exception) {
            Result.failure(Exception("下载过程异常: ${e.message}"))
        }
    }
    
    /**
     * 检查数据表是否存在
     */
    private suspend fun checkTablesExist(apiClient: CloudFlareApiClient_7ree): Boolean {
        return try {
            val checkSql = """
                SELECT name FROM sqlite_master 
                WHERE type='table' AND name IN ('words', 'articles', 'article_words')
            """.trimIndent()
            
            val result = apiClient.executeQuery(checkSql)
            if (result.isSuccess) {
                val queryResult = result.getOrNull()
                val tableCount = queryResult?.results?.size ?: 0
                tableCount >= 2 // 至少要有words和articles表
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 下载单词数据
     */
    private suspend fun downloadWordData(
        apiClient: CloudFlareApiClient_7ree,
        onProgress: (String) -> Unit
    ): Result<List<WordData>> {
        return try {
            onProgress("正在查询云端单词数据...")
            
            val querySql = """
                SELECT word, translation, pronunciation, query_time, source, created_at, updated_at
                FROM words 
                ORDER BY created_at DESC
            """.trimIndent()
            
            val result = apiClient.executeQuery(querySql)
            if (result.isFailure) {
                return Result.failure(Exception("查询单词数据失败: ${result.exceptionOrNull()?.message}"))
            }
            
            val queryResult = result.getOrNull()
            val results = queryResult?.results ?: emptyList()
            
            onProgress("正在解析单词数据...")
            
            val wordDataList = results.mapNotNull { row ->
                try {
                    WordData(
                        word = row["word"]?.jsonPrimitive?.content ?: "",
                        translation = row["translation"]?.jsonPrimitive?.content ?: "",
                        pronunciation = row["pronunciation"]?.jsonPrimitive?.content ?: "",
                        queryTime = row["query_time"]?.jsonPrimitive?.longOrNull ?: 0L,
                        source = row["source"]?.jsonPrimitive?.content ?: "",
                        createdAt = row["created_at"]?.jsonPrimitive?.longOrNull ?: 0L,
                        updatedAt = row["updated_at"]?.jsonPrimitive?.longOrNull ?: 0L
                    )
                } catch (e: Exception) {
                    null // 跳过解析失败的记录
                }
            }
            
            onProgress("单词数据下载完成，共 ${wordDataList.size} 条记录")
            
            Result.success(wordDataList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 下载文章数据
     */
    private suspend fun downloadArticleData(
        apiClient: CloudFlareApiClient_7ree,
        onProgress: (String) -> Unit
    ): Result<List<ArticleData>> {
        return try {
            onProgress("正在查询云端文章数据...")
            
            val querySql = """
                SELECT id, title, content, word_count, created_at, updated_at
                FROM articles 
                ORDER BY created_at DESC
            """.trimIndent()
            
            val result = apiClient.executeQuery(querySql)
            if (result.isFailure) {
                return Result.failure(Exception("查询文章数据失败: ${result.exceptionOrNull()?.message}"))
            }
            
            val queryResult = result.getOrNull()
            val results = queryResult?.results ?: emptyList()
            
            onProgress("正在解析文章数据...")
            
            val articleDataList = results.mapNotNull { row ->
                try {
                    val articleId = row["id"]?.jsonPrimitive?.longOrNull ?: 0L
                    
                    ArticleData(
                        id = articleId,
                        title = row["title"]?.jsonPrimitive?.content ?: "",
                        content = row["content"]?.jsonPrimitive?.content ?: "",
                        wordCount = row["word_count"]?.jsonPrimitive?.intOrNull ?: 0,
                        createdAt = row["created_at"]?.jsonPrimitive?.longOrNull ?: 0L,
                        updatedAt = row["updated_at"]?.jsonPrimitive?.longOrNull ?: 0L,
                        words = emptyList() // 暂时不下载文章单词，可以后续扩展
                    )
                } catch (e: Exception) {
                    null // 跳过解析失败的记录
                }
            }
            
            onProgress("文章数据下载完成，共 ${articleDataList.size} 篇文章")
            
            Result.success(articleDataList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 导入到本地数据库
     */
    private suspend fun importToLocalDatabase(
        wordDataList: List<WordData>?,
        articleDataList: List<ArticleData>?
    ): Result<Unit> {
        return try {
            val dataExportImportManager = wordQueryViewModel.getDataExportImportManager()
            
            // 导入单词数据
            if (wordDataList != null && wordDataList.isNotEmpty()) {
                val wordJsonData = createWordJsonData(wordDataList)
                val wordFile = createTempFile("cloudflare_words", ".json")
                wordFile.writeText(wordJsonData)
                
                val importWordResult = dataExportImportManager.importData_7ree(android.net.Uri.fromFile(wordFile))
                if (importWordResult.isFailure) {
                    return Result.failure(Exception("导入单词数据失败"))
                }
                
                wordFile.delete() // 清理临时文件
            }
            
            // 导入文章数据
            if (articleDataList != null && articleDataList.isNotEmpty()) {
                val articleJsonData = createArticleJsonData(articleDataList)
                val articleFile = createTempFile("cloudflare_articles", ".json")
                articleFile.writeText(articleJsonData)
                
                val importArticleResult = dataExportImportManager.importArticleData_7ree(android.net.Uri.fromFile(articleFile))
                if (importArticleResult.isFailure) {
                    return Result.failure(Exception("导入文章数据失败"))
                }
                
                articleFile.delete() // 清理临时文件
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 创建单词JSON数据
     */
    private fun createWordJsonData(wordDataList: List<WordData>): String {
        val wordsJson = wordDataList.map { wordData ->
            buildJsonObject {
                put("word", wordData.word)
                
                // 映射为本地WordEntity_7ree的字段结构
                put("chineseDefinition", wordData.translation) // translation -> chineseDefinition
                put("phonetic", wordData.pronunciation) // pronunciation -> phonetic
                put("apiResult", "Imported from CloudFlare D1") // 默认API结果
                put("queryTimestamp", if (wordData.queryTime < 1000000000000L) wordData.queryTime * 1000 else wordData.queryTime) // query_time -> queryTimestamp
                put("partOfSpeech", "") // 默认词性为空
                
                // 默认字段
                put("viewCount", 0)
                put("isFavorite", false)
                put("spellingCount", 0)
                put("referenceCount", 0)
                
                // 兼容性字段（保留原有字段以防需要）
                put("translation", wordData.translation)
                put("pronunciation", wordData.pronunciation)
                put("queryTime", if (wordData.queryTime < 1000000000000L) wordData.queryTime * 1000 else wordData.queryTime)
                put("source", wordData.source)
                put("createdAt", if (wordData.createdAt < 1000000000000L) wordData.createdAt * 1000 else wordData.createdAt)
                put("updatedAt", if (wordData.updatedAt < 1000000000000L) wordData.updatedAt * 1000 else wordData.updatedAt)
                put("query_time", if (wordData.queryTime < 1000000000000L) wordData.queryTime * 1000 else wordData.queryTime)
                put("created_at", if (wordData.createdAt < 1000000000000L) wordData.createdAt * 1000 else wordData.createdAt)
                put("updated_at", if (wordData.updatedAt < 1000000000000L) wordData.updatedAt * 1000 else wordData.updatedAt)
            }
        }
        
        val jsonObject = buildJsonObject {
            put("words", JsonArray(wordsJson))
            put("exportTime", System.currentTimeMillis())
            put("source", "CloudFlare D1")
            put("version", "1.0")
        }
        
        return json.encodeToString(JsonObject.serializer(), jsonObject)
    }
    
    /**
     * 创建文章JSON数据
     */
    private fun createArticleJsonData(articleDataList: List<ArticleData>): String {
        val articlesJson = articleDataList.map { articleData ->
            // 解析CloudFlare D1中的组合内容，还原为本地ArticleEntity_7ree格式
            val (englishTitle, titleTranslation) = parseTitle(articleData.title)
            val (englishContent, chineseContent, bilingualComparison, keyWords) = parseContent(articleData.content)
            
            buildJsonObject {
                put("id", articleData.id)
                
                // 映射为本地ArticleEntity_7ree的字段结构
                put("englishTitle", englishTitle)
                put("titleTranslation", titleTranslation)
                put("englishContent", englishContent)
                put("chineseContent", chineseContent)
                put("bilingualComparison", bilingualComparison)
                put("keyWords", keyWords)
                
                // 转换为毫秒级时间戳（本地数据库使用毫秒级）
                put("generationTimestamp", if (articleData.createdAt < 1000000000000L) articleData.createdAt * 1000 else articleData.createdAt)
                
                // 默认字段
                put("viewCount", 0)
                put("apiResult", "Imported from CloudFlare D1")
                put("isFavorite", false)
                put("author", "CloudFlare D1")
                
                // 兼容性字段
                put("wordCount", articleData.wordCount)
                put("word_count", articleData.wordCount)
                put("createdAt", if (articleData.createdAt < 1000000000000L) articleData.createdAt * 1000 else articleData.createdAt)
                put("updatedAt", if (articleData.updatedAt < 1000000000000L) articleData.updatedAt * 1000 else articleData.updatedAt)
                put("created_at", if (articleData.createdAt < 1000000000000L) articleData.createdAt * 1000 else articleData.createdAt)
                put("updated_at", if (articleData.updatedAt < 1000000000000L) articleData.updatedAt * 1000 else articleData.updatedAt)
                put("timestamp", if (articleData.createdAt < 1000000000000L) articleData.createdAt * 1000 else articleData.createdAt)
                
                put("words", JsonArray(emptyList())) // 暂时为空，可以后续扩展
            }
        }
        
        val jsonObject = buildJsonObject {
            put("articles", JsonArray(articlesJson))
            put("exportTime", System.currentTimeMillis())
            put("source", "CloudFlare D1")
            put("version", "1.0")
        }
        
        return json.encodeToString(JsonObject.serializer(), jsonObject)
    }
    
    /**
     * 解析标题，分离英文标题和翻译
     */
    private fun parseTitle(combinedTitle: String): Pair<String, String> {
        return try {
            // 检查是否包含括号格式：English Title (中文翻译)
            val regex = """^(.+?)\s*\((.+?)\)$""".toRegex()
            val matchResult = regex.find(combinedTitle)
            
            if (matchResult != null) {
                val englishTitle = matchResult.groupValues[1].trim()
                val titleTranslation = matchResult.groupValues[2].trim()
                Pair(englishTitle, titleTranslation)
            } else {
                // 如果没有括号格式，判断是否为中文（简单判断）
                if (combinedTitle.any { it.toString().matches("""[\u4e00-\u9fa5]""".toRegex()) }) {
                    Pair("", combinedTitle) // 中文作为翻译
                } else {
                    Pair(combinedTitle, "") // 英文作为标题
                }
            }
        } catch (e: Exception) {
            Pair(combinedTitle, "") // 出错时全部作为英文标题
        }
    }
    
    /**
     * 解析内容，分离各个部分
     */
    private fun parseContent(combinedContent: String): Tuple4<String, String, String, String> {
        var englishContent = ""
        var chineseContent = ""
        var bilingualComparison = ""
        var keyWords = ""
        
        try {
            val sections = combinedContent.split("===").map { it.trim() }
            
            for (i in sections.indices) {
                val section = sections[i]
                when {
                    section.startsWith("English Content") && i + 1 < sections.size -> {
                        englishContent = sections[i + 1].trim()
                    }
                    section.startsWith("Chinese Translation") && i + 1 < sections.size -> {
                        chineseContent = sections[i + 1].trim()
                    }
                    section.startsWith("Bilingual Comparison") && i + 1 < sections.size -> {
                        bilingualComparison = sections[i + 1].trim()
                    }
                    section.startsWith("Key Words") && i + 1 < sections.size -> {
                        keyWords = sections[i + 1].trim()
                    }
                }
            }
            
            // 如果没有找到分段标记，将整个内容作为英文内容
            if (englishContent.isEmpty() && chineseContent.isEmpty() && bilingualComparison.isEmpty()) {
                englishContent = combinedContent
            }
        } catch (e: Exception) {
            // 解析失败时，将整个内容作为英文内容
            englishContent = combinedContent
        }
        
        return Tuple4(englishContent, chineseContent, bilingualComparison, keyWords)
    }
    
    /**
     * 创建临时文件
     */
    private fun createTempFile(prefix: String, suffix: String): File {
        val tempDir = File(context.cacheDir, "cloudflare_temp")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        return File.createTempFile(prefix, suffix, tempDir)
    }
}

// 数据类定义
data class WordData(
    val word: String,
    val translation: String,
    val pronunciation: String,
    val queryTime: Long,
    val source: String,
    val createdAt: Long,
    val updatedAt: Long
)

data class ArticleData(
    val id: Long,
    val title: String,
    val content: String,
    val wordCount: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val words: List<ArticleWordData>
)

data class ArticleWordData(
    val word: String,
    val translation: String,
    val position: Int
)

/**
 * 四元组数据类，用于返回四个值
 */
data class Tuple4<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)