package com.x7ree.wordcard.article

import android.content.Context
import android.util.Log
import com.x7ree.wordcard.config.AppConfigManager_7ree
import com.x7ree.wordcard.config.ApiConfig_7ree
import com.x7ree.wordcard.config.GeneralConfig_7ree
import com.x7ree.wordcard.tts.TtsManager_7ree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 文章朗读管理器
 * 基于原有的TtsManager_7ree实现，支持多引擎选择和完整的状态管理
 */
class ArticleTtsManager_7ree(private val context: Context) {
    
    companion object {
        private const val TAG = "ArticleTtsManager"
    }
    
    private val appConfigManager = AppConfigManager_7ree(context)
    private val ttsManager = TtsManager_7ree(context)
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    
    // 按钮状态枚举
    enum class TtsButtonState {
        READY,      // 准备就绪，可以开始朗读
        LOADING,    // 正在初始化或准备朗读
        PLAYING,    // 正在朗读
        ERROR       // 错误状态
    }
    
    // 按钮状态
    private val _buttonState = MutableStateFlow(TtsButtonState.LOADING)
    val buttonState: StateFlow<TtsButtonState> = _buttonState.asStateFlow()
    
    // 朗读状态（向后兼容）
    private val _isReading = MutableStateFlow(false)
    val isReading: StateFlow<Boolean> = _isReading.asStateFlow()
    
    // 初始化状态
    private val _isInitializing = MutableStateFlow(true)
    val isInitializing: StateFlow<Boolean> = _isInitializing.asStateFlow()
    
    // 错误信息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // 当前引擎信息
    private val _currentEngine = MutableStateFlow("未知")
    val currentEngine: StateFlow<String> = _currentEngine.asStateFlow()
    
    init {
        initializeTtsManager()
    }
    
    /**
     * 初始化TTS管理器
     */
    private fun initializeTtsManager() {
        Log.d(TAG, "开始初始化文章TTS管理器")
        _buttonState.value = TtsButtonState.LOADING
        _isInitializing.value = true
        
        try {
            // 加载配置
            val generalConfig = appConfigManager.loadGeneralConfig_7ree()
            val apiConfig = appConfigManager.loadApiConfig_7ree()
            
            // 更新TTS管理器配置
            ttsManager.updateGeneralConfig(generalConfig)
            ttsManager.updateApiConfig(apiConfig)
            
            // 设置状态监听器
            ttsManager.onTtsStateChanged = { isReady, engine ->
                Log.d(TAG, "TTS状态变化: isReady=$isReady, engine=$engine")
                _currentEngine.value = engine
                
                if (isReady) {
                    _buttonState.value = TtsButtonState.READY
                    _isInitializing.value = false
                    _errorMessage.value = null
                    Log.d(TAG, "TTS引擎准备就绪: $engine")
                } else {
                    _buttonState.value = TtsButtonState.ERROR
                    _errorMessage.value = "TTS引擎初始化失败: $engine"
                    Log.e(TAG, "TTS引擎初始化失败: $engine")
                }
            }
            
            ttsManager.onSpeakingStateChanged = { isSpeaking, engine ->
                Log.d(TAG, "朗读状态变化: isSpeaking=$isSpeaking, engine=$engine")
                _isReading.value = isSpeaking
                
                if (isSpeaking) {
                    _buttonState.value = TtsButtonState.PLAYING
                } else {
                    _buttonState.value = if (ttsManager.isCurrentEngineReady()) {
                        TtsButtonState.READY
                    } else {
                        TtsButtonState.ERROR
                    }
                }
            }
            
            Log.d(TAG, "TTS管理器初始化完成")
            
        } catch (e: Exception) {
            Log.e(TAG, "TTS管理器初始化失败: ${e.message}", e)
            _buttonState.value = TtsButtonState.ERROR
            _errorMessage.value = "TTS管理器初始化失败: ${e.message}"
            _isInitializing.value = false
        }
    }
    
    /**
     * 朗读文章英文内容
     * 支持使用清理后的TTS文本，去除Markdown格式和语音说明前缀
     */
    fun readArticle(englishContent: String, englishTitle: String = "") {
        if (englishContent.isBlank()) {
            Log.w(TAG, "文章内容为空，无法朗读")
            _errorMessage.value = "文章内容为空"
            return
        }
        
        if (!ttsManager.isCurrentEngineReady()) {
            Log.w(TAG, "TTS引擎未准备就绪")
            _errorMessage.value = "语音引擎未准备就绪，请检查配置"
            return
        }
        
        // 如果正在朗读，先停止
        if (_isReading.value) {
            stopReading()
            return
        }
        
        coroutineScope.launch {
            try {
                _buttonState.value = TtsButtonState.LOADING
                _errorMessage.value = null
                
                // 清理TTS文本，去除Markdown格式和语音说明前缀
                val cleanTitle = cleanTextForTts(englishTitle)
                val cleanContent = cleanTextForTts(englishContent)
                
                // 准备朗读内容，使用长停顿分隔标题和正文（更长停顿）
                val contentToRead = if (cleanTitle.isNotBlank()) {
                    // 使用15个句号来创造更长的停顿效果
                    // 每个句号大约停顿0.3-0.5秒，15个句号约4.5-7.5秒停顿
                    "$cleanTitle. . . . . . . . . . . . . . . $cleanContent"
                } else {
                    cleanContent
                }
                
                Log.d(TAG, "开始朗读文章")
                Log.d(TAG, "当前引擎: ${ttsManager.getCurrentEngineName()}")
                Log.d(TAG, "清理后标题: '$cleanTitle'")
                Log.d(TAG, "清理后内容长度: ${cleanContent.length}")
                Log.d(TAG, "最终朗读内容长度: ${contentToRead.length}")
                Log.d(TAG, "朗读内容预览: ${contentToRead.take(100)}...")
                
                // 开始朗读
                ttsManager.speak(
                    text = contentToRead,
                    utteranceId = "article_${System.currentTimeMillis()}",
                    onStart = {
                        Log.d(TAG, "文章朗读开始")
                        _buttonState.value = TtsButtonState.PLAYING
                        _isReading.value = true
                    },
                    onComplete = {
                        Log.d(TAG, "文章朗读完成")
                        _buttonState.value = TtsButtonState.READY
                        _isReading.value = false
                    },
                    onError = { error ->
                        Log.e(TAG, "文章朗读失败: $error")
                        _buttonState.value = TtsButtonState.ERROR
                        _errorMessage.value = "朗读失败: $error"
                        _isReading.value = false
                    }
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "朗读过程中发生异常: ${e.message}", e)
                _buttonState.value = TtsButtonState.ERROR
                _errorMessage.value = "朗读失败: ${e.message}"
                _isReading.value = false
            }
        }
    }
    
    /**
     * 清理文本用于TTS朗读
     * 去除Markdown格式标记和语音说明前缀
     */
    private fun cleanTextForTts(text: String): String {
        if (text.isEmpty()) {
            return ""
        }
        
        var cleanedText = text
        
        // 去除三个星号包裹的粗体标记 ***text*** -> text
        cleanedText = cleanedText.replace(Regex("\\*\\*\\*([^*]+)\\*\\*\\*"), "$1")
        
        // 去除两个星号包裹的粗体标记 **text** -> text
        cleanedText = cleanedText.replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1")
        
        // 去除单个星号包裹的斜体标记 *text* -> text
        cleanedText = cleanedText.replace(Regex("\\*([^*]+)\\*"), "$1")
        
        // 去除标题前的"title"语音说明
        cleanedText = cleanedText.replace(Regex("^title\\s*:?\\s*", RegexOption.IGNORE_CASE), "")
        
        // 去除文章前的"content"语音说明
        cleanedText = cleanedText.replace(Regex("^content\\s*:?\\s*", RegexOption.IGNORE_CASE), "")
        
        // 去除其他可能的语音说明前缀
        cleanedText = cleanedText.replace(Regex("^(article|text|story)\\s*:?\\s*", RegexOption.IGNORE_CASE), "")
        
        // 清理多余的空白字符
        cleanedText = cleanedText.replace(Regex("\\s+"), " ").trim()
        
        return cleanedText
    }
    
    /**
     * 停止朗读
     */
    fun stopReading() {
        try {
            Log.d(TAG, "停止朗读")
            ttsManager.stopSpeaking()
            _buttonState.value = if (ttsManager.isCurrentEngineReady()) {
                TtsButtonState.READY
            } else {
                TtsButtonState.ERROR
            }
            _isReading.value = false
        } catch (e: Exception) {
            Log.e(TAG, "停止朗读时发生异常: ${e.message}", e)
            _buttonState.value = TtsButtonState.ERROR
            _errorMessage.value = "停止朗读失败: ${e.message}"
        }
    }
    
    /**
     * 切换朗读状态（朗读/停止）
     */
    fun toggleReading(englishContent: String, englishTitle: String = "") {
        when (_buttonState.value) {
            TtsButtonState.READY -> {
                readArticle(englishContent, englishTitle)
            }
            TtsButtonState.PLAYING -> {
                stopReading()
            }
            TtsButtonState.LOADING -> {
                Log.d(TAG, "TTS正在加载中，请稍候")
            }
            TtsButtonState.ERROR -> {
                Log.d(TAG, "TTS处于错误状态，尝试重新初始化")
                reinitialize()
            }
        }
    }
    
    /**
     * 重新初始化TTS
     */
    fun reinitialize() {
        Log.d(TAG, "重新初始化TTS")
        initializeTtsManager()
    }
    
    /**
     * 检查TTS是否可用
     */
    fun isTtsAvailable(): Boolean {
        return ttsManager.isCurrentEngineReady()
    }
    
    /**
     * 获取TTS引擎状态信息
     */
    fun getTtsEngineStatus(): String {
        val status = ttsManager.getEngineStatus()
        return """
            当前引擎: ${status.currentEngine}
            Google TTS: ${if (status.googleReady) "就绪" else "未就绪"}
            Azure TTS: ${if (status.azureReady) "就绪" else "未就绪"}
            正在朗读: ${if (status.isSpeaking) "是" else "否"}
            按钮状态: ${_buttonState.value}
        """.trimIndent()
    }
    
    /**
     * 获取按钮显示文本
     */
    fun getButtonText(): String {
        return when (_buttonState.value) {
            TtsButtonState.READY -> "朗读文章"
            TtsButtonState.LOADING -> "加载中..."
            TtsButtonState.PLAYING -> "停止朗读"
            TtsButtonState.ERROR -> "重试"
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _errorMessage.value = null
        if (_buttonState.value == TtsButtonState.ERROR && ttsManager.isCurrentEngineReady()) {
            _buttonState.value = TtsButtonState.READY
        }
    }
    
    /**
     * 更新配置
     */
    fun updateConfig() {
        try {
            val generalConfig = appConfigManager.loadGeneralConfig_7ree()
            val apiConfig = appConfigManager.loadApiConfig_7ree()
            
            ttsManager.updateGeneralConfig(generalConfig)
            ttsManager.updateApiConfig(apiConfig)
            
            Log.d(TAG, "TTS配置已更新")
        } catch (e: Exception) {
            Log.e(TAG, "更新TTS配置失败: ${e.message}", e)
        }
    }
    
    /**
     * 释放资源
     */
    fun release() {
        Log.d(TAG, "释放文章TTS管理器资源")
        try {
            ttsManager.release()
            _buttonState.value = TtsButtonState.ERROR
            _isReading.value = false
            _isInitializing.value = false
            _errorMessage.value = null
        } catch (e: Exception) {
            Log.e(TAG, "释放TTS资源时发生异常: ${e.message}", e)
        }
    }
}