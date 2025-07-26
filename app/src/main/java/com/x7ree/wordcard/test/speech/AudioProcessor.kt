package com.x7ree.wordcard.test.speech

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 音频处理器
 */
class AudioProcessor(private val context: Context? = null) {
    
    companion object {
        private const val TAG = "AudioProcessor"
    }
    
    /**
     * 验证音频数据是否有效
     */
    fun validateAudioData(audioData: ByteArray): Boolean {
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
     * 播放音频数据
     */
    suspend fun playAudioData(audioData: ByteArray): Boolean {
        return withContext(Dispatchers.Main) {
            try {
                // 确保context不为空
                val ctx = context ?: return@withContext false
                
                // 使用MediaPlayer播放音频
                val mediaPlayer = MediaPlayer()
                
                // 创建临时文件
                val tempFile = File.createTempFile("speech_test", ".mp3", ctx.cacheDir)
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
                
                mediaPlayer.setOnErrorListener { _, _, _ ->
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
                Log.e(TAG, "播放音频失败: ${e.message}", e)
                false
            }
        }
    }
    
    /**
     * 尝试获取音频数据
     */
    suspend fun tryGetAudioData(urlString: String, apiConfig: com.x7ree.wordcard.config.ApiConfig_7ree, text: String): ByteArray {
        return withContext(Dispatchers.IO) {
            try {
                val url = java.net.URL(urlString)
                val connection = url.openConnection() as javax.net.ssl.HttpsURLConnection
                
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
                val ssml = SpeechHttpClient().buildSsmlRequest(text, apiConfig.azureSpeechVoice)
                
                // 发送请求
                connection.outputStream.use { outputStream ->
                    outputStream.write(ssml.toByteArray(kotlin.text.Charsets.UTF_8))
                    outputStream.flush()
                }
                
                // 检查响应并读取音频数据
                if (connection.responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    connection.inputStream.use { inputStream ->
                        SpeechHttpClient().readInputStream(inputStream)
                    }
                } else {
                    ByteArray(0)
                }
            } catch (e: Exception) {
                Log.e(TAG, "获取音频数据失败: ${e.message}", e)
                ByteArray(0)
            }
        }
    }
}
