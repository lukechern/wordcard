package com.x7ree.wordcard.test

import android.content.Context
import android.util.Log
import com.x7ree.wordcard.config.ApiConfig_7ree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Speech API 测试器
 */
class SpeechApiTester_7ree(private val context: Context) {
    
    companion object {
        private const val TAG = "SpeechApiTester_7ree"
        private const val TEST_TEXT = "Hello, this is a test."
        private const val TIMEOUT_MS = 30000L // 30秒超时
    }
    
    /**
     * 测试 Azure Speech API 连接和功能
     * @param apiConfig API 配置
     * @param onResult 测试结果回调 (isSuccess: Boolean, message: String)
     */
    suspend fun testSpeechApi(
        apiConfig: ApiConfig_7ree,
        onResult: (Boolean, String) -> Unit
    ) {
        
        try {
            // 验证配置完整性
            val validationResult = validateSpeechConfig(apiConfig)
            if (!validationResult.first) {
                onResult(false, validationResult.second)
                return
            }
            
            
            // 执行测试，带超时控制
            val result = withTimeoutOrNull(TIMEOUT_MS) {
                testAzureSpeechSynthesis(apiConfig)
            }
            
            if (result == null) {
                onResult(false, "Speech API测试超时（${TIMEOUT_MS/1000}秒），请检查网络连接和API配置")
            } else {
                onResult(result.success, result.message)
            }
            
        } catch (e: Exception) {
            onResult(false, "测试失败: ${e.localizedMessage ?: e.message ?: "未知错误"}")
        }
    }
    
    /**
     * 测试 Azure Speech 合成服务
     */
    private suspend fun testAzureSpeechSynthesis(apiConfig: ApiConfig_7ree): TestResult {
        return withContext(Dispatchers.IO) {
            try {
                // 尝试多种可能的 Azure Speech API 端点格式
                val possibleUrls = buildPossibleUrls(apiConfig.azureSpeechEndpoint, apiConfig.azureSpeechRegion)
                
                
                // 依次尝试每个可能的URL
                for ((index, urlString) in possibleUrls.withIndex()) {
                    
                    val result = tryAzureSpeechRequest(urlString, apiConfig)
                    if (result.success) {
                        return@withContext result
                    } else {
                        // 如果不是404错误，直接返回错误（可能是认证或其他问题）
                        if (!result.message.contains("404") && !result.message.contains("Not Found")) {
                            return@withContext result
                        }
                    }
                }
                
                // 所有URL都失败了
                TestResult(false, "所有可能的API端点都返回404错误，请检查终结点配置是否正确")
                
            } catch (e: Exception) {
                TestResult(false, "API调用异常: ${e.localizedMessage ?: e.message}")
            }
        }
    }
    
    /**
     * 构建可能的Azure Speech API URL列表
     * 优先使用区域自动生成标准终结点，然后尝试用户提供的终结点
     */
    private fun buildPossibleUrls(endpoint: String, region: String): List<String> {
        val cleanEndpoint = endpoint.let { 
            if (it.endsWith("/")) it.dropLast(1) else it 
        }
        
        val urls = mutableListOf<String>()
        
        // 优先级1: 根据区域自动生成标准的 Speech Service 终结点
        if (region.isNotBlank()) {
            val standardEndpoint = "https://$region.tts.speech.microsoft.com/cognitiveservices/v1"
            urls.add(standardEndpoint)
        }
        
        // 优先级2: 如果用户提供了终结点，尝试各种可能的格式
        if (cleanEndpoint.isNotBlank()) {
            // 从用户终结点中提取区域信息
            val extractedRegion = extractRegionFromEndpoint(cleanEndpoint)
            
            // 如果用户提供的是标准的 Speech Service 终结点
            if (cleanEndpoint.contains("tts.speech.microsoft.com")) {
                urls.add("$cleanEndpoint/cognitiveservices/v1")
            }
            
            // 如果用户提供的是通用的认知服务终结点，转换为正确的 Speech Service URL
            if (cleanEndpoint.contains("api.cognitive.microsoft.com") && extractedRegion.isNotEmpty()) {
                urls.add("https://$extractedRegion.tts.speech.microsoft.com/cognitiveservices/v1")
            }
            
            // 尝试用户提供的原始终结点的各种变体
            urls.addAll(listOf(
                "$cleanEndpoint/cognitiveservices/v1",
                cleanEndpoint,
                "$cleanEndpoint/tts/cognitiveservices/v1",
                "$cleanEndpoint/speech/cognitiveservices/v1"
            ))
        }
        
        // 去重并返回
        return urls.distinct()
    }
    
    /**
     * 从终结点中提取区域信息
     */
    private fun extractRegionFromEndpoint(endpoint: String): String {
        return try {
            when {
                // 从 eastasia.api.cognitive.microsoft.com 中提取 eastasia
                endpoint.contains("api.cognitive.microsoft.com") -> {
                    val parts = endpoint.split(".")
                    if (parts.size >= 3) {
                        parts[0].removePrefix("https://")
                    } else ""
                }
                // 从 eastasia.tts.speech.microsoft.com 中提取 eastasia
                endpoint.contains("tts.speech.microsoft.com") -> {
                    val parts = endpoint.split(".")
                    if (parts.size >= 4) {
                        parts[0].removePrefix("https://")
                    } else ""
                }
                else -> ""
            }
        } catch (e: Exception) {
            Log.w(TAG, "无法从终结点提取区域信息: $endpoint", e)
            ""
        }
    }
    
    /**
     * 尝试单个Azure Speech API请求
     */
    private fun tryAzureSpeechRequest(urlString: String, apiConfig: ApiConfig_7ree): TestResult {
        return try {
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
                    // 获取响应头信息
                    val contentType = connection.getHeaderField("Content-Type")
                    val contentLength = connection.getHeaderField("Content-Length")
                    val transferEncoding = connection.getHeaderField("Transfer-Encoding")
                    
                    
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
                        val isValidAudio = validateAudioData(audioData)
                        
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
    
    /**
     * 构建SSML请求
     */
    private fun buildSsmlRequest(text: String, voiceName: String): String {
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
    private fun readInputStream(inputStream: InputStream): ByteArray {
        val buffer = ByteArrayOutputStream()
        val data = ByteArray(1024)
        var bytesRead: Int
        
        while (inputStream.read(data, 0, data.size).also { bytesRead = it } != -1) {
            buffer.write(data, 0, bytesRead)
        }
        
        return buffer.toByteArray()
    }
    
    /**
     * 验证音频数据是否有效
     */
    private fun validateAudioData(audioData: ByteArray): Boolean {
        if (audioData.isEmpty()) return false
        
        // 检查MP3文件头 (ID3 tag 或 MP3 frame sync)
        val isMP3 = audioData.size >= 3 && (
            // ID3v2 header
            (audioData[0] == 0x49.toByte() && audioData[1] == 0x44.toByte() && audioData[2] == 0x33.toByte()) ||
            // MP3 frame sync (11 bits set)
            (audioData.size >= 2 && (audioData[0].toInt() and 0xFF) == 0xFF && (audioData[1].toInt() and 0xE0) == 0xE0)
        )
        
        // 检查WAV文件头
        val isWAV = audioData.size >= 12 && 
            audioData[0] == 0x52.toByte() && audioData[1] == 0x49.toByte() && 
            audioData[2] == 0x46.toByte() && audioData[3] == 0x46.toByte() &&
            audioData[8] == 0x57.toByte() && audioData[9] == 0x41.toByte() && 
            audioData[10] == 0x56.toByte() && audioData[11] == 0x45.toByte()
        
        // 检查OGG文件头
        val isOGG = audioData.size >= 4 && 
            audioData[0] == 0x4F.toByte() && audioData[1] == 0x67.toByte() && 
            audioData[2] == 0x67.toByte() && audioData[3] == 0x53.toByte()
        
        val result = isMP3 || isWAV || isOGG
        
        
        
        return result
    }
    
    /**
     * 验证 Speech API 配置格式
     * 现在终结点是可选的，系统会根据区域自动生成
     */
    fun validateSpeechConfig(apiConfig: ApiConfig_7ree): Pair<Boolean, String> {
        return when {
            apiConfig.azureSpeechApiKey.isBlank() -> false to "Azure Speech API 密钥不能为空"
            apiConfig.azureSpeechRegion.isBlank() -> false to "Azure Speech 区域不能为空"
            // 终结点不再是必填项，如果提供了则验证格式
            apiConfig.azureSpeechEndpoint.isNotBlank() && !apiConfig.azureSpeechEndpoint.startsWith("https://") -> 
                false to "Azure Speech 终结点必须以 https:// 开头（或留空使用自动生成）"
            else -> true to "Speech API 配置格式正确"
        }
    }
    
    /**
     * 使用Azure Speech API朗读测试文本
     * 这样可以真正验证Speech API的工作效果
     */
    suspend fun speakTestResult(apiConfig: ApiConfig_7ree, success: Boolean) {
        try {
            if (success) {
                // 测试成功时，使用Azure Speech API朗读测试文本，展示实际效果
                val result = testAndPlayAudio(apiConfig, TEST_TEXT)
                if (!result.success) {
                    fallbackToSystemTts("Speech API测试成功")
                }
            } else {
                // 测试失败时，使用系统TTS朗读失败信息
                fallbackToSystemTts("Speech API测试失败")
            }
        } catch (e: Exception) {
            fallbackToSystemTts("测试异常")
        }
    }
    
    /**
     * 测试并播放Azure Speech API生成的音频
     */
    private suspend fun testAndPlayAudio(apiConfig: ApiConfig_7ree, text: String): TestResult {
        return withContext(Dispatchers.IO) {
            try {
                val possibleUrls = buildPossibleUrls(apiConfig.azureSpeechEndpoint, apiConfig.azureSpeechRegion)
                
                // 尝试获取音频数据并播放
                for (urlString in possibleUrls) {
                    val audioData = tryGetAudioData(urlString, apiConfig, text)
                    if (audioData.isNotEmpty() && validateAudioData(audioData)) {
                        // 播放音频
                        val playResult = playAudioData(audioData)
                        if (playResult) {
                            return@withContext TestResult(true, "音频播放成功")
                        }
                    }
                }
                
                TestResult(false, "无法获取或播放音频数据")
            } catch (e: Exception) {
                TestResult(false, "播放音频异常: ${e.message}")
            }
        }
    }
    
    /**
     * 尝试获取音频数据
     */
    private fun tryGetAudioData(urlString: String, apiConfig: ApiConfig_7ree, text: String): ByteArray {
        return try {
            val url = URL(urlString)
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
            val ssml = buildSsmlRequest(text, apiConfig.azureSpeechVoice)
            
            // 发送请求
            connection.outputStream.use { outputStream ->
                outputStream.write(ssml.toByteArray(Charsets.UTF_8))
                outputStream.flush()
            }
            
            // 检查响应并读取音频数据
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.use { inputStream ->
                    readInputStream(inputStream)
                }
            } else {
                ByteArray(0)
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取音频数据失败: ${e.message}", e)
            ByteArray(0)
        }
    }
    
    /**
     * 播放音频数据
     */
    private suspend fun playAudioData(audioData: ByteArray): Boolean {
        return withContext(Dispatchers.Main) {
            try {
                
                // 使用MediaPlayer播放音频
                val mediaPlayer = android.media.MediaPlayer()
                
                // 创建临时文件
                val tempFile = java.io.File.createTempFile("speech_test", ".mp3", context.cacheDir)
                tempFile.writeBytes(audioData)
                
                mediaPlayer.setDataSource(tempFile.absolutePath)
                mediaPlayer.prepareAsync()
                
                var playbackCompleted = false
                
                mediaPlayer.setOnPreparedListener {
                    mediaPlayer.start()
                }
                
                mediaPlayer.setOnCompletionListener {
                    playbackCompleted = true
                    mediaPlayer.release()
                    tempFile.delete()
                }
                
                mediaPlayer.setOnErrorListener { _, what, extra ->
                    playbackCompleted = true
                    mediaPlayer.release()
                    tempFile.delete()
                    false
                }
                
                // 等待播放完成或超时
                var waitTime = 0
                while (!playbackCompleted && waitTime < 10000) { // 最多等待10秒
                    kotlinx.coroutines.delay(100)
                    waitTime += 100
                }
                
                if (!playbackCompleted) {
                    mediaPlayer.release()
                    tempFile.delete()
                }
                
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    
    /**
     * 回退到系统TTS
     */
    private suspend fun fallbackToSystemTts(text: String) {
        try {
            val ttsManager = com.x7ree.wordcard.tts.TtsManager_7ree(context)
            
            ttsManager.speak(
                text = text,
                onStart = {
                },
                onComplete = {
                    ttsManager.release()
                },
                onError = { error ->
                    ttsManager.release()
                }
            )
        } catch (e: Exception) {
        }
    }
    
    /**
     * 测试结果数据类
     */
    private data class TestResult(
        val success: Boolean,
        val message: String
    )
}
