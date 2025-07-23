package com.x7ree.wordcard.widget

import android.content.Context
import android.util.Log
import com.x7ree.wordcard.config.AppConfigManager_7ree
import com.x7ree.wordcard.tts.TtsManager_7ree as CoreTtsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Widget TTS管理器 - 升级版
 * 使用主应用的TTS管理器，支持配置的引擎和音色
 */
class WidgetTTSManager_7ree(private val context: Context) {
    
    private val TAG_7ree = "WidgetTTSManager_7ree"
    private val coreTtsManager_7ree = CoreTtsManager(context)
    private val configManager_7ree = AppConfigManager_7ree(context)
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    /**
     * 懒加载初始化TTS
     */
    fun initializeTtsLazy_7ree() {
        try {
            // Log.d(TAG_7ree, "开始初始化升级版TTS管理器")
            
            // 加载配置
            val apiConfig = configManager_7ree.loadApiConfig_7ree()
            val generalConfig = configManager_7ree.loadGeneralConfig_7ree()
            
            // 更新TTS管理器配置
            coreTtsManager_7ree.updateApiConfig(apiConfig)
            coreTtsManager_7ree.updateGeneralConfig(generalConfig)
            
            // Log.d(TAG_7ree, "TTS管理器配置已更新 - 引擎: ${generalConfig.ttsEngine}, 音色: ${apiConfig.azureSpeechVoice}")
            
        } catch (e: Exception) {
            // Log.e(TAG_7ree, "TTS管理器初始化失败: ${e.message}", e)
        }
    }
    
    /**
     * 朗读单词 - 使用配置的TTS引擎和音色
     * @param word 要朗读的单词
     * @param onPlayStart 播放开始回调
     * @param onPlayComplete 播放完成回调
     * @param onError 错误回调
     */
    fun speakWord_7ree(
        word: String,
        onPlayStart: (() -> Unit)? = null,
        onPlayComplete: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        // Log.d(TAG_7ree, "speakWord_7ree: 使用升级版TTS管理器朗读单词: \"$word\"")
        
        if (word.isBlank()) {
            // Log.w(TAG_7ree, "speakWord_7ree: 单词为空，无法朗读")
            onError?.invoke("单词为空")
            return
        }
        
        coroutineScope.launch {
            try {
                coreTtsManager_7ree.speak(
                    text = word,
                    onStart = {
                        // Log.d(TAG_7ree, "开始朗读单词: $word")
                        onPlayStart?.invoke()
                    },
                    onComplete = {
                        // Log.d(TAG_7ree, "单词朗读完成: $word")
                        onPlayComplete?.invoke()
                    },
                    onError = { error ->
                        // Log.e(TAG_7ree, "单词朗读失败: $error")
                        onError?.invoke(error)
                    }
                )
            } catch (e: Exception) {
                // Log.e(TAG_7ree, "朗读单词异常: ${e.message}", e)
                onError?.invoke("朗读异常: ${e.message}")
            }
        }
    }
    
    /**
     * 检查TTS是否准备就绪
     */
    fun isTtsReady_7ree(): Boolean {
        return coreTtsManager_7ree.isCurrentEngineReady()
    }
    
    /**
     * 获取当前TTS引擎状态
     */
    fun getTtsEngineStatus_7ree(): String {
        val status = coreTtsManager_7ree.getEngineStatus()
        return when {
            status.currentEngine == "google" && status.googleReady -> "Google TTS 已就绪"
            status.currentEngine == "azure" && status.azureReady -> "Azure Speech 已就绪"
            status.currentEngine == "google" && !status.googleReady -> "Google TTS 未就绪"
            status.currentEngine == "azure" && !status.azureReady -> "Azure Speech 配置无效"
            else -> "TTS 引擎未知状态"
        }
    }
    
    /**
     * 停止朗读
     */
    fun stopSpeaking_7ree() {
        coreTtsManager_7ree.stopSpeaking()
        // Log.d(TAG_7ree, "已停止朗读")
    }
    
    /**
     * 释放TTS资源
     */
    fun release_7ree() {
        try {
            coreTtsManager_7ree.release()
            // Log.d(TAG_7ree, "TTS管理器资源已释放")
        } catch (e: Exception) {
            // Log.e(TAG_7ree, "释放TTS资源异常: ${e.message}", e)
        }
    }
}