package com.x7ree.wordcard.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.x7ree.wordcard.config.ApiConfig_7ree
import com.x7ree.wordcard.config.GeneralConfig_7ree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * TTS管理器
 * 统一管理Google TTS和Azure TTS服务
 * 根据用户配置选择使用哪种TTS引擎
 */
class TtsManager_7ree(private val context: Context) {
    
    companion object {
        private const val TAG = "TtsManager_7ree"
    }
    
    private var googleTts: TextToSpeech? = null
    private var azureTts: AzureTtsService_7ree? = null
    private var isGoogleTtsReady = false
    
    private var generalConfig: GeneralConfig_7ree = GeneralConfig_7ree()
    private var apiConfig: ApiConfig_7ree = ApiConfig_7ree()
    
    // TTS状态回调
    var onTtsStateChanged: ((isReady: Boolean, engine: String) -> Unit)? = null
    var onSpeakingStateChanged: ((isSpeaking: Boolean, engine: String) -> Unit)? = null
    
    init {
        initializeServices()
    }
    
    /**
     * 初始化TTS服务
     */
    private fun initializeServices() {
        // 初始化Google TTS
        googleTts = TextToSpeech(context) { status ->
            isGoogleTtsReady = (status == TextToSpeech.SUCCESS)
            if (isGoogleTtsReady) {
                googleTts?.language = Locale.US
                Log.d(TAG, "Google TTS初始化成功")
            } else {
                Log.e(TAG, "Google TTS初始化失败")
            }
            onTtsStateChanged?.invoke(isGoogleTtsReady, "google")
        }
        
        // 初始化Azure TTS
        azureTts = AzureTtsService_7ree(context)
        Log.d(TAG, "Azure TTS服务已创建")
    }
    
    /**
     * 更新通用配置
     */
    fun updateGeneralConfig(config: GeneralConfig_7ree) {
        this.generalConfig = config
        Log.d(TAG, "TTS引擎配置更新为: ${config.ttsEngine}")
    }
    
    /**
     * 更新API配置
     */
    fun updateApiConfig(config: ApiConfig_7ree) {
        this.apiConfig = config
        azureTts?.updateConfig(config)
        Log.d(TAG, "Azure TTS API配置已更新")
    }
    
    /**
     * 检查当前TTS引擎是否可用
     */
    fun isCurrentEngineReady(): Boolean {
        return when (generalConfig.ttsEngine) {
            "google" -> isGoogleTtsReady
            "azure" -> azureTts?.isConfigValid() ?: false
            else -> isGoogleTtsReady
        }
    }
    
    /**
     * 获取当前TTS引擎名称
     */
    fun getCurrentEngineName(): String {
        return when (generalConfig.ttsEngine) {
            "google" -> "Google TTS"
            "azure" -> "Azure Speech"
            else -> "Google TTS"
        }
    }
    
    /**
     * 朗读文本
     * @param text 要朗读的文本
     * @param onStart 开始朗读回调
     * @param onComplete 完成朗读回调
     * @param onError 错误回调
     */
    suspend fun speak(
        text: String,
        onStart: (() -> Unit)? = null,
        onComplete: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        if (text.isBlank()) {
            onError?.invoke("文本为空")
            return
        }
        
        when (generalConfig.ttsEngine) {
            "google" -> speakWithGoogle(text, onStart, onComplete, onError)
            "azure" -> speakWithAzure(text, onStart, onComplete, onError)
            else -> speakWithGoogle(text, onStart, onComplete, onError)
        }
    }
    
    /**
     * 使用Google TTS朗读
     */
    private suspend fun speakWithGoogle(
        text: String,
        onStart: (() -> Unit)?,
        onComplete: (() -> Unit)?,
        onError: ((String) -> Unit)?
    ) {
        withContext(Dispatchers.Main) {
            try {
                if (!isGoogleTtsReady) {
                    onError?.invoke("Google TTS未准备就绪")
                    return@withContext
                }
                
                onStart?.invoke()
                onSpeakingStateChanged?.invoke(true, "google")
                
                val utteranceId = "tts_${System.currentTimeMillis()}"
                
                googleTts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Log.d(TAG, "Google TTS开始朗读")
                    }
                    
                    override fun onDone(utteranceId: String?) {
                        Log.d(TAG, "Google TTS朗读完成")
                        onSpeakingStateChanged?.invoke(false, "google")
                        onComplete?.invoke()
                    }
                    
                    override fun onError(utteranceId: String?) {
                        Log.e(TAG, "Google TTS朗读错误")
                        onSpeakingStateChanged?.invoke(false, "google")
                        onError?.invoke("Google TTS朗读失败")
                    }
                })
                
                val result = googleTts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
                if (result != TextToSpeech.SUCCESS) {
                    onSpeakingStateChanged?.invoke(false, "google")
                    onError?.invoke("Google TTS朗读启动失败")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Google TTS朗读异常: ${e.message}", e)
                onSpeakingStateChanged?.invoke(false, "google")
                onError?.invoke("Google TTS朗读异常: ${e.message}")
            }
        }
    }
    
    /**
     * 使用Azure TTS朗读
     */
    private suspend fun speakWithAzure(
        text: String,
        onStart: (() -> Unit)?,
        onComplete: (() -> Unit)?,
        onError: ((String) -> Unit)?
    ) {
        try {
            val azureService = azureTts
            if (azureService == null || !azureService.isConfigValid()) {
                onError?.invoke("Azure TTS配置无效")
                return
            }
            
            onStart?.invoke()
            onSpeakingStateChanged?.invoke(true, "azure")
            
            Log.d(TAG, "开始使用Azure TTS朗读: $text")
            
            val success = azureService.textToSpeech(text)
            
            onSpeakingStateChanged?.invoke(false, "azure")
            
            if (success) {
                Log.d(TAG, "Azure TTS朗读成功")
                onComplete?.invoke()
            } else {
                Log.e(TAG, "Azure TTS朗读失败")
                onError?.invoke("Azure TTS朗读失败")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Azure TTS朗读异常: ${e.message}", e)
            onSpeakingStateChanged?.invoke(false, "azure")
            onError?.invoke("Azure TTS朗读异常: ${e.message}")
        }
    }
    
    /**
     * 停止朗读
     */
    fun stopSpeaking() {
        try {
            googleTts?.stop()
            azureTts?.stopPlaying()
            onSpeakingStateChanged?.invoke(false, getCurrentEngineName())
            Log.d(TAG, "已停止所有TTS朗读")
        } catch (e: Exception) {
            Log.e(TAG, "停止朗读异常: ${e.message}", e)
        }
    }
    
    /**
     * 检查是否正在朗读
     */
    fun isSpeaking(): Boolean {
        return when (generalConfig.ttsEngine) {
            "google" -> googleTts?.isSpeaking ?: false
            "azure" -> azureTts?.isPlaying() ?: false
            else -> googleTts?.isSpeaking ?: false
        }
    }
    
    /**
     * 释放资源
     */
    fun release() {
        try {
            googleTts?.stop()
            googleTts?.shutdown()
            googleTts = null
            
            azureTts?.release()
            azureTts = null
            
            Log.d(TAG, "TTS管理器资源已释放")
        } catch (e: Exception) {
            Log.e(TAG, "释放TTS资源异常: ${e.message}", e)
        }
    }
    
    /**
     * 获取TTS引擎状态信息
     */
    fun getEngineStatus(): TtsEngineStatus_7ree {
        return TtsEngineStatus_7ree(
            currentEngine = generalConfig.ttsEngine,
            googleReady = isGoogleTtsReady,
            azureReady = azureTts?.isConfigValid() ?: false,
            isSpeaking = isSpeaking()
        )
    }
}

/**
 * TTS引擎状态数据类
 */
data class TtsEngineStatus_7ree(
    val currentEngine: String,
    val googleReady: Boolean,
    val azureReady: Boolean,
    val isSpeaking: Boolean
)