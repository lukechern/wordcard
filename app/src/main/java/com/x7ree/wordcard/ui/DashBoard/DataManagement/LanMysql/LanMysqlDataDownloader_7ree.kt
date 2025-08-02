package com.x7ree.wordcard.ui.DashBoard.DataManagement.LanMysql

import android.content.Context
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

/**
 * 局域网PHP API数据下载器
 * 负责从PHP API下载MySQL数据到本地
 */
class LanMysqlDataDownloader_7ree(
    private val context: Context,
    private val wordQueryViewModel: WordQueryViewModel_7ree
) {
    private val json = Json { ignoreUnknownKeys = true }
    
    /**
     * 从PHP API下载数据
     */
    suspend fun downloadData(
        serverUrl: String,
        apiKey: String,
        onProgress: (String) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            onProgress("正在生成动态token...")
            
            // 生成当日token
            val token = generateDailyToken(apiKey)
            
            onProgress("正在连接PHP API服务器...")
            
            // 构建下载请求
            val url = URL(serverUrl)
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            
            onProgress("正在请求数据...")
            
            // 构建下载请求数据
            val requestData = """
                {
                    "action": "download",
                    "token": "$token"
                }
            """.trimIndent()
            
            // 发送请求
            connection.outputStream.use { os ->
                os.write(requestData.toByteArray())
            }
            
            onProgress("正在等待服务器响应...")
            
            // 获取响应
            val responseCode = connection.responseCode
            val responseText = if (responseCode == 200) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
            }
            
            connection.disconnect()
            
            if (responseCode == 200) {
                onProgress("正在解析服务器数据...")
                
                // 解析响应
                try {
                    val response = json.parseToJsonElement(responseText) as JsonObject
                    if (response["success"]?.jsonPrimitive?.boolean == true) {
                        val wordsArray = response["words"] as? JsonArray ?: JsonArray(emptyList())
                        val articlesArray = response["articles"] as? JsonArray ?: JsonArray(emptyList())
                        
                        onProgress("正在导入本地数据库...")
                        
                        // 导入到本地数据库
                        val importResult = importToLocalDatabase(wordsArray, articlesArray)
                        if (importResult.isFailure) {
                            return@withContext Result.failure(
                                Exception("导入本地数据库失败: ${importResult.exceptionOrNull()?.message}")
                            )
                        }
                        
                        onProgress("正在完成PHP API下载...")
                        
                        val wordCount = wordsArray.size
                        val articleCount = articlesArray.size
                        
                        Result.success("PHP API数据下载成功！已从MySQL同步 $wordCount 条单词记录和 $articleCount 篇文章到本地")
                    } else {
                        val errorMsg = response["message"]?.jsonPrimitive?.content ?: "未知错误"
                        Result.failure(Exception("PHP API下载失败: $errorMsg"))
                    }
                } catch (e: Exception) {
                    Result.failure(Exception("PHP API响应解析失败: $responseText"))
                }
            } else {
                Result.failure(Exception("PHP API下载失败: HTTP $responseCode - $responseText"))
            }
            
        } catch (e: Exception) {
            Result.failure(Exception("PHP API下载过程异常: ${e.message}"))
        }
    }
    
    /**
     * 生成当日有效的token
     */
    private fun generateDailyToken(apiKey: String): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val tokenSource = apiKey + currentDate
        
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(tokenSource.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * 导入到本地数据库
     */
    private suspend fun importToLocalDatabase(
        wordsArray: JsonArray,
        articlesArray: JsonArray
    ): Result<Unit> {
        return try {
            val dataExportImportManager = wordQueryViewModel.getDataExportImportManager()
            
            // 导入单词数据
            if (wordsArray.isNotEmpty()) {
                val wordJsonData = createWordJsonData(wordsArray)
                val wordFile = createTempFile("php_api_words", ".json")
                wordFile.writeText(wordJsonData)
                
                val importWordResult = dataExportImportManager.importData_7ree(android.net.Uri.fromFile(wordFile))
                if (importWordResult.isFailure) {
                    val errorMsg = importWordResult.exceptionOrNull()?.message ?: "未知错误"
                    return Result.failure(Exception("导入单词数据失败: $errorMsg"))
                }
                
                wordFile.delete() // 清理临时文件
            }
            
            // 导入文章数据
            if (articlesArray.isNotEmpty()) {
                val articleJsonData = createArticleJsonData(articlesArray)
                val articleFile = createTempFile("php_api_articles", ".json")
                articleFile.writeText(articleJsonData)
                
                val importArticleResult = dataExportImportManager.importArticleData_7ree(android.net.Uri.fromFile(articleFile))
                if (importArticleResult.isFailure) {
                    val errorMsg = importArticleResult.exceptionOrNull()?.message ?: "未知错误"
                    return Result.failure(Exception("导入文章数据失败: $errorMsg"))
                }
                
                articleFile.delete() // 清理临时文件
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("导入过程异常: ${e.message}"))
        }
    }
    
    /**
     * 创建单词JSON数据
     */
    private fun createWordJsonData(wordsArray: JsonArray): String {
        val jsonObject = buildJsonObject {
            put("words", wordsArray)
            put("exportTime", System.currentTimeMillis())
            put("source", "PHP API")
            put("version", "1.0")
        }
        
        return json.encodeToString(JsonObject.serializer(), jsonObject)
    }
    
    /**
     * 创建文章JSON数据
     */
    private fun createArticleJsonData(articlesArray: JsonArray): String {
        val jsonObject = buildJsonObject {
            put("articles", articlesArray)
            put("exportTime", System.currentTimeMillis())
            put("source", "PHP API")
            put("version", "1.0")
        }
        
        return json.encodeToString(JsonObject.serializer(), jsonObject)
    }
    
    /**
     * 创建临时文件
     */
    private fun createTempFile(prefix: String, suffix: String): File {
        val tempDir = File(context.cacheDir, "mysql_temp")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        return File.createTempFile(prefix, suffix, tempDir)
    }
}

