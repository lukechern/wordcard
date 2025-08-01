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
                SELECT word, apiResult, queryTimestamp, viewCount, isFavorite, spellingCount, chineseDefinition, phonetic, partOfSpeech, referenceCount
                FROM words 
                ORDER BY queryTimestamp DESC
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
                        word = decodeHtmlEntities(row["word"]?.jsonPrimitive?.content ?: ""),
                        apiResult = decodeHtmlEntities(row["apiResult"]?.jsonPrimitive?.content ?: ""),
                        queryTimestamp = row["queryTimestamp"]?.jsonPrimitive?.longOrNull ?: 0L,
                        viewCount = row["viewCount"]?.jsonPrimitive?.intOrNull ?: 0,
                        isFavorite = row["isFavorite"]?.jsonPrimitive?.intOrNull == 1,
                        spellingCount = row["spellingCount"]?.jsonPrimitive?.intOrNull ?: 0,
                        chineseDefinition = decodeHtmlEntities(row["chineseDefinition"]?.jsonPrimitive?.content ?: ""),
                        phonetic = decodeHtmlEntities(row["phonetic"]?.jsonPrimitive?.content ?: ""),
                        partOfSpeech = decodeHtmlEntities(row["partOfSpeech"]?.jsonPrimitive?.content ?: ""),
                        referenceCount = row["referenceCount"]?.jsonPrimitive?.intOrNull ?: 0
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
                SELECT id, generationTimestamp, keyWords, viewCount, apiResult, englishTitle, titleTranslation, englishContent, chineseContent, bilingualComparison, isFavorite, author
                FROM articles 
                ORDER BY generationTimestamp DESC
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
                    ArticleData(
                        id = row["id"]?.jsonPrimitive?.longOrNull ?: 0L,
                        generationTimestamp = row["generationTimestamp"]?.jsonPrimitive?.longOrNull ?: 0L,
                        keyWords = decodeHtmlEntities(row["keyWords"]?.jsonPrimitive?.content ?: ""),
                        viewCount = row["viewCount"]?.jsonPrimitive?.intOrNull ?: 0,
                        apiResult = decodeHtmlEntities(row["apiResult"]?.jsonPrimitive?.content ?: ""),
                        englishTitle = decodeHtmlEntities(row["englishTitle"]?.jsonPrimitive?.content ?: ""),
                        titleTranslation = decodeHtmlEntities(row["titleTranslation"]?.jsonPrimitive?.content ?: ""),
                        englishContent = decodeHtmlEntities(row["englishContent"]?.jsonPrimitive?.content ?: ""),
                        chineseContent = decodeHtmlEntities(row["chineseContent"]?.jsonPrimitive?.content ?: ""),
                        bilingualComparison = decodeHtmlEntities(row["bilingualComparison"]?.jsonPrimitive?.content ?: ""),
                        isFavorite = row["isFavorite"]?.jsonPrimitive?.intOrNull == 1,
                        author = decodeHtmlEntities(row["author"]?.jsonPrimitive?.content ?: ""),
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
                // 直接映射本地WordEntity_7ree的所有字段
                put("word", wordData.word)
                put("apiResult", wordData.apiResult)
                put("queryTimestamp", wordData.queryTimestamp)
                put("viewCount", wordData.viewCount)
                put("isFavorite", wordData.isFavorite)
                put("spellingCount", wordData.spellingCount)
                put("chineseDefinition", wordData.chineseDefinition)
                put("phonetic", wordData.phonetic)
                put("partOfSpeech", wordData.partOfSpeech)
                put("referenceCount", wordData.referenceCount)
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
            buildJsonObject {
                // 直接映射本地ArticleEntity_7ree的所有字段
                put("id", articleData.id)
                put("generationTimestamp", articleData.generationTimestamp)
                put("keyWords", articleData.keyWords)
                put("viewCount", articleData.viewCount)
                put("apiResult", articleData.apiResult)
                put("englishTitle", articleData.englishTitle)
                put("titleTranslation", articleData.titleTranslation)
                put("englishContent", articleData.englishContent)
                put("chineseContent", articleData.chineseContent)
                put("bilingualComparison", articleData.bilingualComparison)
                put("isFavorite", articleData.isFavorite)
                put("author", articleData.author)
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
     * 转换HTML实体和转义字符为正常字符
     */
    private fun decodeHtmlEntities(text: String): String {
        return text
            // HTML实体转换
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&#10;", "\n")  // 换行符实体
            .replace("&#13;", "\r")  // 回车符实体
            .replace("&#9;", "\t")   // 制表符实体
            // 转义字符转换
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
            .replace("\\\"", "\"")
            .replace("\\'", "'")
            .replace("\\\\", "\\")
            // Unicode转义
            .replace("\\u000A", "\n")
            .replace("\\u000D", "\r")
            .replace("\\u0009", "\t")
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

// 数据类定义 - 完全匹配本地表结构
data class WordData(
    val word: String,
    val apiResult: String,
    val queryTimestamp: Long,
    val viewCount: Int,
    val isFavorite: Boolean,
    val spellingCount: Int,
    val chineseDefinition: String,
    val phonetic: String,
    val partOfSpeech: String,
    val referenceCount: Int
)

data class ArticleData(
    val id: Long,
    val generationTimestamp: Long,
    val keyWords: String,
    val viewCount: Int,
    val apiResult: String,
    val englishTitle: String,
    val titleTranslation: String,
    val englishContent: String,
    val chineseContent: String,
    val bilingualComparison: String,
    val isFavorite: Boolean,
    val author: String,
    val words: List<ArticleWordData>
)

data class ArticleWordData(
    val word: String,
    val translation: String,
    val position: Int
)

