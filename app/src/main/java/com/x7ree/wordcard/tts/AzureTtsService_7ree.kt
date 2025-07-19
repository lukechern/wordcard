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
        Log.d(TAG, "Azure TTS配置已更新")
    }
    
    /**
     * 检查配置是否有效
     */
    fun isConfigValid(): Boolean {
        return apiConfig?.let { config ->
            config.azureSpeechApiKey.isNotBlank() && 
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
            
            val response = client.post(tokenUrl) {
                headers {
                    append("Ocp-Apim-Subscription-Key", config.azureSpeechApiKey)
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
     * @param voiceName 语音名称，默认使用英语女声
     * @param language 语言代码，默认为en-US
     */
    suspend fun textToSpeech(
        text: String, 
        voiceName: String = "en-US-JennyNeural",
        language: String = "en-US"
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
                val ttsUrl = if (config.azureSpeechEndpoint.isNotBlank()) {
                    "${config.azureSpeechEndpoint.trimEnd('/')}/cognitiveservices/v1"
                } else {
                    "https://${config.azureSpeechRegion}.tts.speech.microsoft.com/cognitiveservices/v1"
                }
                
                val ssmlText = String.format(
                    SSML_TEMPLATE.replace("en-US-JennyNeural", voiceName)
                        .replace("en-US", language),
                    text
                )
                
                Log.d(TAG, "发送TTS请求到: $ttsUrl")
                
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
                    playAudio(audioData)
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
     * 播放音频数据
     */
    private suspend fun playAudio(audioData: ByteArray) {
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