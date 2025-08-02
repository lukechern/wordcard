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
 * 局域网PHP API数据上传器
 * 负责将本地数据通过PHP API上传到MySQL数据库
 */
class LanMysqlDataUploader_7ree(
    private val context: Context,
    private val wordQueryViewModel: WordQueryViewModel_7ree
) {
    private val json = Json { ignoreUnknownKeys = true }
    
    /**
     * 上传数据到PHP API
     */
    suspend fun uploadData(
        serverUrl: String,
        apiKey: String,
        onProgress: (String) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            onProgress("正在生成动态token...")
            
            // 生成当日token
            val token = generateDailyToken(apiKey)
            
            onProgress("正在导出本地数据...")
            
            // 获取本地单词数据
            val wordDataResult = exportWordData(onProgress)
            if (wordDataResult.isFailure) {
                return@withContext Result.failure(
                    Exception("导出单词数据失败: ${wordDataResult.exceptionOrNull()?.message}")
                )
            }
            
            // 获取本地文章数据
            val articleDataResult = exportArticleData(onProgress)
            if (articleDataResult.isFailure) {
                return@withContext Result.failure(
                    Exception("导出文章数据失败: ${articleDataResult.exceptionOrNull()?.message}")
                )
            }
            
            onProgress("正在上传到PHP API服务器...")
            
            // 构建上传请求
            val url = URL(serverUrl)
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            
            // 构建上传数据
            val uploadData = buildJsonObject {
                put("action", "upload")
                put("token", token)
                put("words", wordDataResult.getOrNull() ?: JsonArray(emptyList()))
                put("articles", articleDataResult.getOrNull() ?: JsonArray(emptyList()))
            }
            
            // 发送请求
            connection.outputStream.use { os ->
                os.write(uploadData.toString().toByteArray())
            }
            
            onProgress("正在等待服务器处理...")
            
            // 获取响应
            val responseCode = connection.responseCode
            val responseText = if (responseCode == 200) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
            }
            
            connection.disconnect()
            
            onProgress("正在完成PHP API上传...")
            
            if (responseCode == 200) {
                // 解析响应
                try {
                    val response = json.parseToJsonElement(responseText) as JsonObject
                    if (response["success"]?.jsonPrimitive?.boolean == true) {
                        val wordCount = response["word_count"]?.jsonPrimitive?.intOrNull ?: 0
                        val articleCount = response["article_count"]?.jsonPrimitive?.intOrNull ?: 0
                        Result.success("PHP API数据上传成功！已同步 $wordCount 条单词记录和 $articleCount 篇文章到MySQL数据库")
                    } else {
                        val errorMsg = response["message"]?.jsonPrimitive?.content ?: "未知错误"
                        Result.failure(Exception("PHP API上传失败: $errorMsg"))
                    }
                } catch (e: Exception) {
                    Result.failure(Exception("PHP API响应解析失败: $responseText"))
                }
            } else {
                Result.failure(Exception("PHP API上传失败: HTTP $responseCode - $responseText"))
            }
            
        } catch (e: Exception) {
            Result.failure(Exception("PHP API上传过程异常: ${e.message}"))
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
     * 导出单词数据
     */
    private suspend fun exportWordData(onProgress: (String) -> Unit): Result<JsonArray> {
        return try {
            onProgress("正在导出本地单词数据...")
            
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
            
            onProgress("单词数据导出完成，共 ${wordsArray.size} 条记录")
            
            Result.success(wordsArray)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 导出文章数据
     */
    private suspend fun exportArticleData(onProgress: (String) -> Unit): Result<JsonArray> {
        return try {
            onProgress("正在导出本地文章数据...")
            
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
            
            onProgress("文章数据导出完成，共 ${articlesArray.size} 篇文章")
            
            Result.success(articlesArray)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}