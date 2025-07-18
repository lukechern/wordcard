package com.x7ree.wordcard.widget

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * Widget TTS管理器
 * 负责处理语音朗读功能
 */
class WidgetTTSManager_7ree(private val context: Context) : TextToSpeech.OnInitListener {
    
    private var tts_7ree: TextToSpeech? = null
    private var isTtsReady_7ree = false
    private val TAG_7ree = "WidgetTTSManager_7ree"
    
    /**
     * 懒加载初始化TTS
     */
    fun initializeTtsLazy_7ree() {
        if (tts_7ree != null) {
            Log.d(TAG_7ree, "TTS已经初始化过，跳过重复初始化")
            return // 已经初始化过，不再重复初始化
        }
        
        try {
            Log.d(TAG_7ree, "开始懒加载初始化TTS")
            tts_7ree = TextToSpeech(context, this)
            Log.d(TAG_7ree, "TTS懒加载初始化请求已发送")
        } catch (e: Exception) {
            Log.e(TAG_7ree, "TTS懒加载初始化失败: ${e.message}")
        }
    }
    
    override fun onInit(status: Int) {
        Log.d(TAG_7ree, "onInit: Received status: $status")
        if (status == TextToSpeech.SUCCESS) {
            Log.d(TAG_7ree, "onInit: TextToSpeech initialized successfully.")
            
            val resultUs_7ree = tts_7ree?.setLanguage(Locale.US)
            Log.d(TAG_7ree, "onInit: setLanguage(Locale.US) result: $resultUs_7ree")
            
            if (resultUs_7ree != null && resultUs_7ree >= TextToSpeech.LANG_AVAILABLE) {
                isTtsReady_7ree = true
                Log.d(TAG_7ree, "onInit: TTS is ready for English.")
            } else {
                Log.w(TAG_7ree, "onInit: English language not supported.")
            }
        } else {
            Log.e(TAG_7ree, "onInit: TextToSpeech initialization failed with status: $status")
        }
    }
    
    /**
     * 朗读单词
     * @param word 要朗读的单词
     */
    fun speakWord_7ree(word: String) {
        Log.d(TAG_7ree, "speakWord_7ree: 尝试朗读单词: \"$word\", TTS状态: tts_7ree=${tts_7ree != null}, isTtsReady_7ree=$isTtsReady_7ree")
        
        if (word.isBlank()) {
            Log.w(TAG_7ree, "speakWord_7ree: 单词为空，无法朗读")
            return
        }
        
        if (tts_7ree == null) {
            Log.w(TAG_7ree, "speakWord_7ree: TTS未初始化，尝试立即初始化")
            initializeTtsLazy_7ree()
            // 初始化后需要等待onInit回调，此次朗读可能失败
            Log.w(TAG_7ree, "speakWord_7ree: TTS正在初始化中，请稍后再试")
            return
        }
        
        if (!isTtsReady_7ree) {
            Log.w(TAG_7ree, "speakWord_7ree: TTS未准备好，可能还在初始化中")
            return
        }
        
        tts_7ree?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "widget_word")
        Log.d(TAG_7ree, "speakWord_7ree: 开始朗读单词: \"$word\"")
    }
    
    /**
     * 检查TTS是否准备就绪
     */
    fun isTtsReady_7ree(): Boolean {
        return isTtsReady_7ree
    }
    
    /**
     * 释放TTS资源
     */
    fun release_7ree() {
        tts_7ree?.let {
            it.stop()
            it.shutdown()
        }
        Log.d(TAG_7ree, "release_7ree: TTS resources released")
    }
}