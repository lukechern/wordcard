package com.x7ree.wordcard.test.speech

import android.util.Log
import com.x7ree.wordcard.config.ApiConfig_7ree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Speech API HTTP 客户端
 */
class SpeechHttpClient {
    
    companion object {
        private const val TAG = "SpeechHttpClient"
        private const val TEST_TEXT = "Hello, this is a test."
    }
    
    /**
     * 尝试单个Azure Speech API请求
     */
    suspend fun tryAzureSpeechRequest(urlString: String, apiConfig: ApiConfig_7ree): TestResult {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                
                // 创建连接
                val connection = url.openConnection() as HttpsURLConnection
                
                // 设置请求头
                connection.requestMethod = "POST"
                connection.setRequestProperty("Ocp-Apim-Subscription-Key", apiConfig.azureSpeechApiKey)
                connection.setRequestProperty("Content-Type", "application/ssml+xml")
                connection.setRequestProperty("X-Microsoft-OutputFormat", "audio-16khz-128kbitrate-mono-mp3")
                connection.setRequestProperty("User-Agent", "WordCard-Test")
                connection.doOutput = true
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                
                // 构建SSML请求体
                val ssml = buildSsmlRequest(TEST_TEXT, apiConfig.azureSpeechVoice)
                
                // 发送请求
                connection.outputStream.use { outputStream ->
                    outputStream.write(ssml.toByteArray(Charsets.UTF_8))
                    outputStream.flush()
                }
                
                // 检查响应
                val responseCode = connection.responseCode
                
                when (responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                        // 读取响应数据
                        val audioData = try {
                            connection.inputStream.use { inputStream ->
                                val data = readInputStream(inputStream)
                                data
                            }
                        } catch (e: Exception) {
                            ByteArray(0)
                        }
                        
                        if (audioData.isNotEmpty()) {
                            // 验证音频数据格式
                            val isValidAudio = AudioProcessor().validateAudioData(audioData)
                            
                            if (isValidAudio) {
                                TestResult(true, "Speech API测试成功！获得有效音频数据 ${audioData.size} 字节")
                            } else {
                                // 显示前几个字节用于调试
                                val preview = audioData.take(20).joinToString(" ") { "%02X".format(it) }
                                TestResult(false, "Speech API返回了数据但格式可能不正确。数据大小: ${audioData.size} 字节，前20字节: $preview")
                            }
                        } else {
                            // 尝试读取错误流
                            val errorData = try {
                                connection.errorStream?.use { errorStream ->
                                    readInputStream(errorStream).toString(Charsets.UTF_8)
                                }
                            } catch (e: Exception) {
                                "无法读取错误信息: ${e.message}"
                            }
                            
                            TestResult(false, "Speech API响应为空。错误信息: ${errorData ?: "无"}")
                        }
                    }
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        TestResult(false, "认证失败，请检查API密钥是否正确")
                    }
                    HttpURLConnection.HTTP_FORBIDDEN -> {
                        TestResult(false, "访问被拒绝，请检查API密钥权限")
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        val errorMessage = connection.errorStream?.use { errorStream ->
                            readInputStream(errorStream).toString(Charsets.UTF_8)
                        } ?: "请求格式错误"
                        TestResult(false, "请求错误: $errorMessage")
                    }
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        TestResult(false, "404 Not Found - 端点路径不正确")
                    }
                    else -> {
                        val errorMessage = connection.errorStream?.use { errorStream ->
                            readInputStream(errorStream).toString(Charsets.UTF_8)
                        } ?: "未知错误"
                        TestResult(false, "API调用失败 (HTTP $responseCode): $errorMessage")
                    }
                }
                
            } catch (e: Exception) {
                TestResult(false, "API调用异常: ${e.localizedMessage ?: e.message}")
            }
        }
    }
    
    /**
     * 构建SSML请求
     */
    fun buildSsmlRequest(text: String, voiceName: String): String {
        // 根据音色名称确定语言
        val lang = when {
            voiceName.startsWith("zh-CN") -> "zh-CN"
            voiceName.startsWith("en-US") -> "en-US"
            voiceName.startsWith("en-GB") -> "en-GB"
            voiceName.startsWith("en-AU") -> "en-AU"
            voiceName.startsWith("ja-JP") -> "ja-JP"
            else -> "en-US" // 默认语言
        }
        
        return """<?xml version="1.0" encoding="UTF-8"?>
<speak version="1.0" xmlns="http://www.w3.org/2001/10/synthesis" xml:lang="$lang">
    <voice name="$voiceName">
        $text
    </voice>
</speak>""".trim()
    }
    
    /**
     * 读取输入流数据
     */
    fun readInputStream(inputStream: InputStream): ByteArray {
        val buffer = ByteArrayOutputStream()
        val data = ByteArray(1024)
        var bytesRead: Int
        
        while (inputStream.read(data, 0, data.size).also { bytesRead = it } != -1) {
            buffer.write(data, 0, bytesRead)
        }
        
        return buffer.toByteArray()
    }
}
