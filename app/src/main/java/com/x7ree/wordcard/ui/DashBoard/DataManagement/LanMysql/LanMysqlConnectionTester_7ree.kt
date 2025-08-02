package com.x7ree.wordcard.ui.DashBoard.DataManagement.LanMysql

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

/**
 * 局域网PHP API连接测试器
 * 负责测试PHP API连接和token验证
 */
class LanMysqlConnectionTester_7ree {
    
    /**
     * 测试PHP API连接
     */
    suspend fun testConnection(
        serverUrl: String,
        apiKey: String,
        onProgress: (String) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            onProgress("正在生成动态token...")
            
            // 生成当日token
            val token = generateDailyToken(apiKey)
            
            onProgress("正在连接PHP API服务器...")
            
            // 构建测试请求
            val url = URL(serverUrl)
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            
            onProgress("正在验证API密钥...")
            
            // 构建测试请求数据
            val requestData = """
                {
                    "action": "test",
                    "token": "$token"
                }
            """.trimIndent()
            
            // 发送请求
            connection.outputStream.use { os ->
                os.write(requestData.toByteArray())
            }
            
            onProgress("正在检查服务器响应...")
            
            // 获取响应
            val responseCode = connection.responseCode
            val responseText = if (responseCode == 200) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
            }
            
            connection.disconnect()
            
            onProgress("PHP API连接测试完成")
            
            if (responseCode == 200) {
                // 解析响应
                if (responseText.contains("\"success\":true")) {
                    Result.success("PHP API连接成功！服务器配置正确，token验证通过")
                } else {
                    Result.failure(Exception("PHP API响应错误: $responseText"))
                }
            } else {
                Result.failure(Exception("PHP API连接失败: HTTP $responseCode - $responseText"))
            }
            
        } catch (e: Exception) {
            Result.failure(Exception("PHP API连接测试异常: ${e.message}"))
        }
    }
    
    /**
     * 生成当日有效的token
     * 使用密钥 + 当前日期（格式：20250802）生成MD5哈希
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
     * 快速连接测试（仅验证基本连接）
     */
    suspend fun quickTest(
        serverUrl: String,
        apiKey: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val token = generateDailyToken(apiKey)
            val url = URL(serverUrl)
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            
            val requestData = """{"action": "test", "token": "$token"}"""
            connection.outputStream.use { os ->
                os.write(requestData.toByteArray())
            }
            
            val responseCode = connection.responseCode
            connection.disconnect()
            
            if (responseCode == 200) {
                Result.success("PHP API快速连接测试成功")
            } else {
                Result.failure(Exception("HTTP $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}