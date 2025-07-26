package com.x7ree.wordcard.test

import android.content.Context
import com.x7ree.wordcard.config.ApiConfig_7ree
import com.x7ree.wordcard.test.speech.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

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
                for ((_, urlString) in possibleUrls.withIndex()) {
                    
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
        return SpeechUrlBuilder().buildPossibleUrls(endpoint, region)
    }
    
    /**
     * 尝试单个Azure Speech API请求
     */
    private suspend fun tryAzureSpeechRequest(urlString: String, apiConfig: ApiConfig_7ree): TestResult {
        return SpeechHttpClient().tryAzureSpeechRequest(urlString, apiConfig)
    }
    
    /**
     * 验证音频数据是否有效
     */
    private fun validateAudioData(audioData: ByteArray): Boolean {
        return AudioProcessor().validateAudioData(audioData)
    }
    
    /**
     * 验证 Speech API 配置格式
     * 现在终结点是可选的，系统会根据区域自动生成
     */
    private fun validateSpeechConfig(apiConfig: ApiConfig_7ree): Pair<Boolean, String> {
        return SpeechConfigValidator().validateSpeechConfig(apiConfig)
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
    private suspend fun tryGetAudioData(urlString: String, apiConfig: ApiConfig_7ree, text: String): ByteArray {
        return AudioProcessor(context).tryGetAudioData(urlString, apiConfig, text)
    }
    
    /**
     * 播放音频数据
     */
    private suspend fun playAudioData(audioData: ByteArray): Boolean {
        return AudioProcessor(context).playAudioData(audioData)
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
                onError = { _ ->
                    ttsManager.release()
                }
            )
        } catch (e: Exception) {
        }
    }
    
}
