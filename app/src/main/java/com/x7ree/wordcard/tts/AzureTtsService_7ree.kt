package com.x7ree.wordcard.tts

import android.content.Context
import android.util.Log
import com.x7ree.wordcard.config.ApiConfig_7ree
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import android.media.MediaPlayer
import java.io.File
import java.io.FileOutputStream

/**
 * Azure Text-to-Speech 服务封装类
 * 提供文本转语音功能，支持多种语音和语言
 */
class AzureTtsService_7ree(private val context: Context) {
    
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }
    
    private var mediaPlayer: MediaPlayer? = null
    private var apiConfig: ApiConfig_7ree? = null
    
    companion object {
        private const val TAG = "AzureTtsService_7ree"
        private const val SSML_TEMPLATE = """
            <speak version='1.0' xml:lang='en-US'>
                <voice xml:lang='en-US' xml:gender='Female' name='en-US-JennyNeural'>
                    %s
                </voice>
            </speak>
        """
    }
    
    /**
     * 更新API配置
     */
    fun updateConfig(config: ApiConfig_7ree) {
        this.apiConfig = config
        // Log.d(TAG, "Azure TTS配置已更新")
    }
    
    /**
     * 检查配置是否有效
     */
    fun isConfigValid(): Boolean {
        return apiConfig?.let { config ->
            val cleanApiKey = config.azureSpeechApiKey.trim().replace("\n", "").replace("\r", "")
            cleanApiKey.isNotBlank() && 
            (config.azureSpeechEndpoint.isNotBlank() || config.azureSpeechRegion.isNotBlank())
        } ?: false
    }
    
    /**
     * 获取访问令牌
     */
    private suspend fun getAccessToken(): String? {
        return try {
            val config = apiConfig ?: return null
            val tokenUrl = if (config.azureSpeechEndpoint.isNotBlank()) {
                "${config.azureSpeechEndpoint.trimEnd('/')}/sts/v1.0/issueToken"
            } else {
                "https://${config.azureSpeechRegion}.api.cognitive.microsoft.com/sts/v1.0/issueToken"
            }
            
            // 清理API密钥，移除可能的换行符和空白字符
            val cleanApiKey = config.azureSpeechApiKey.trim().replace("\n", "").replace("\r", "")
            
            val response = client.post(tokenUrl) {
                headers {
                    append("Ocp-Apim-Subscription-Key", cleanApiKey)
                    append("Content-Type", "application/x-www-form-urlencoded")
                }
            }
            
            if (response.status == HttpStatusCode.OK) {
                response.body<String>()
            } else {
                Log.e(TAG, "获取访问令牌失败: ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取访问令牌异常: ${e.message}", e)
            null
        }
    }
    
    /**
     * 文本转语音
     * @param text 要转换的文本
     * @param voiceName 语音名称，如果为空则使用配置中的音色
     * @param language 语言代码，自动从音色名称推断
     * @param onPlayStart 播放开始回调
     * @param onPlayComplete 播放完成回调
     */
    suspend fun textToSpeech(
        text: String, 
        voiceName: String? = null,
        language: String? = null,
        onPlayStart: (() -> Unit)? = null,
        onPlayComplete: (() -> Unit)? = null
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (!isConfigValid()) {
                    Log.e(TAG, "Azure TTS配置无效")
                    return@withContext false
                }
                
                val accessToken = getAccessToken()
                if (accessToken == null) {
                    Log.e(TAG, "无法获取访问令牌")
                    return@withContext false
                }
                
                val config = apiConfig!!
                
                // 使用配置中的音色，如果参数没有指定的话
                val actualVoiceName = voiceName ?: config.azureSpeechVoice.ifBlank { "en-US-JennyNeural" }
                
                // 从音色名称自动推断语言
                val actualLanguage = language ?: when {
                    actualVoiceName.startsWith("zh-CN") -> "zh-CN"
                    actualVoiceName.startsWith("en-US") -> "en-US"
                    actualVoiceName.startsWith("en-GB") -> "en-GB"
                    actualVoiceName.startsWith("en-AU") -> "en-AU"
                    actualVoiceName.startsWith("ja-JP") -> "ja-JP"
                    else -> "en-US"
                }
                
                // 使用正确的Azure Speech Service URL格式
                val ttsUrl = "https://${config.azureSpeechRegion}.tts.speech.microsoft.com/cognitiveservices/v1"
                
                // 构建正确的SSML
                val ssmlText = buildSsmlRequest(text, actualVoiceName, actualLanguage)
                
                Log.d(TAG, "发送TTS请求到: $ttsUrl")
                Log.d(TAG, "使用音色: $actualVoiceName, 语言: $actualLanguage")
                
                val response = client.post(ttsUrl) {
                    headers {
                        append("Authorization", "Bearer $accessToken")
                        append("Content-Type", "application/ssml+xml")
                        append("X-Microsoft-OutputFormat", "audio-16khz-128kbitrate-mono-mp3")
                        append("User-Agent", "WordCard_7ree")
                    }
                    setBody(ssmlText)
                }
                
                if (response.status == HttpStatusCode.OK) {
                    val audioData = response.body<ByteArray>()
                    playAudio(audioData, onPlayStart, onPlayComplete)
                    true
                } else {
                    Log.e(TAG, "TTS请求失败: ${response.status}")
                    false
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "TTS转换异常: ${e.message}", e)
                false
            }
        }
    }
    
    /**
     * 构建SSML请求
     */
    private fun buildSsmlRequest(text: String, voiceName: String, language: String): String {
        return """<?xml version="1.0" encoding="UTF-8"?>
<speak version="1.0" xmlns="http://www.w3.org/2001/10/synthesis" xml:lang="$language">
    <voice name="$voiceName">
        $text
    </voice>
</speak>""".trim()
    }
    
    /**
     * 播放音频数据
     */
    private suspend fun playAudio(
        audioData: ByteArray,
        onPlayStart: (() -> Unit)? = null,
        onPlayComplete: (() -> Unit)? = null
    ) {
        withContext(Dispatchers.Main) {
            try {
                // 停止当前播放
                stopPlaying()
                
                // 将音频数据写入临时文件
                val tempFile = File(context.cacheDir, "temp_tts_${System.currentTimeMillis()}.mp3")
                FileOutputStream(tempFile).use { fos ->
                    fos.write(audioData)
                }
                
                // 创建MediaPlayer并播放
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(tempFile.absolutePath)
                    setOnCompletionListener { mp ->
                        mp.release()
                        tempFile.delete()
                        mediaPlayer = null
                        onPlayComplete?.invoke()
                    }
                    setOnErrorListener { mp, what, extra ->
                        Log.e(TAG, "MediaPlayer错误: what=$what, extra=$extra")
                        mp.release()
                        tempFile.delete()
                        mediaPlayer = null
                        true
                    }
                    prepareAsync()
                    setOnPreparedListener { mp ->
                        mp.start()
                        Log.d(TAG, "开始播放TTS音频")
                        onPlayStart?.invoke()
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "播放音频异常: ${e.message}", e)
            }
        }
    }
    
    /**
     * 停止播放
     */
    fun stopPlaying() {
        mediaPlayer?.let { mp ->
            try {
                if (mp.isPlaying) {
                    mp.stop()
                }
                mp.release()
            } catch (e: Exception) {
                Log.e(TAG, "停止播放异常: ${e.message}", e)
            }
            mediaPlayer = null
        }
    }
    
    /**
     * 检查是否正在播放
     */
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
    
    /**
     * 释放资源
     */
    fun release() {
        stopPlaying()
        client.close()
    }
    
    /**
     * 获取支持的语音列表（示例）
     */
    fun getSupportedVoices(): List<VoiceInfo_7ree> {
        return listOf(
            VoiceInfo_7ree("en-US-JennyNeural", "Jenny", "en-US", "Female"),
            VoiceInfo_7ree("en-US-GuyNeural", "Guy", "en-US", "Male"),
            VoiceInfo_7ree("en-US-AriaNeural", "Aria", "en-US", "Female"),
            VoiceInfo_7ree("en-US-DavisNeural", "Davis", "en-US", "Male"),
            VoiceInfo_7ree("en-GB-SoniaNeural", "Sonia", "en-GB", "Female"),
            VoiceInfo_7ree("en-GB-RyanNeural", "Ryan", "en-GB", "Male")
        )
    }
}

/**
 * 语音信息数据类
 */
data class VoiceInfo_7ree(
    val name: String,
    val displayName: String,
    val locale: String,
    val gender: String
)