package com.x7ree.wordcard.core

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.Engine
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import com.x7ree.wordcard.R
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import java.util.Locale

class TtsManager_7ree(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts_7ree: TextToSpeech? = null
    private val TAG_7ree = "TtsManager_7ree"
    
    private var isTtsInitialized_7ree = false
    private var ttsInitStartTime_7ree: Long = 0
    private var wordQueryViewModel_7ree: WordQueryViewModel_7ree? = null
    
    fun initializeTtsLazy_7ree(viewModel: WordQueryViewModel_7ree) {
        wordQueryViewModel_7ree = viewModel
        
        if (tts_7ree != null) return // 已经初始化过，不再重复初始化
        
        // 记录TTS初始化开始时间
        ttsInitStartTime_7ree = System.currentTimeMillis()
        
        try {
            // Log.d(TAG_7ree, "开始懒加载初始化TTS")
            tts_7ree = TextToSpeech(context, this@TtsManager_7ree)
            // Log.d(TAG_7ree, "TTS懒加载初始化请求已发送，开始时间: ${ttsInitStartTime_7ree}ms")
        } catch (e: Exception) {
            Log.e(TAG_7ree, "TTS懒加载初始化失败: ${e.message}", e)
            Toast.makeText(context, "文本转语音初始化失败，请检查设备设置。", Toast.LENGTH_LONG).show()
            
            // 记录TTS初始化失败时间
            val ttsEndTime = System.currentTimeMillis()
            val ttsDuration = ttsEndTime - ttsInitStartTime_7ree
            Log.e(TAG_7ree, "TTS懒加载初始化失败，耗时: ${ttsDuration}ms")
        }
    }
    
    override fun onInit(status: Int) {
        // 记录TTS初始化完成时间
        val ttsEndTime = System.currentTimeMillis()
        
        // Log.d(TAG_7ree, "onInit: Received status: $status")
        if (status == TextToSpeech.SUCCESS) {
            // Log.d(TAG_7ree, "onInit: TextToSpeech initialized successfully.")

            var isAnyLanguageSupported_7ree = false

            val resultUs_7ree = tts_7ree?.setLanguage(Locale.US)
            // Log.d(TAG_7ree, "onInit: setLanguage(Locale.US) result: $resultUs_7ree")

            if (resultUs_7ree != null && resultUs_7ree >= TextToSpeech.LANG_AVAILABLE) {
                isAnyLanguageSupported_7ree = true
                // Log.d(TAG_7ree, "onInit: Language set to US locale successfully.")
            } else {
                Log.w(TAG_7ree, "onInit: US English language not supported or data missing (${resultUs_7ree}). Trying Chinese.")
                val resultChinese_7ree = tts_7ree?.setLanguage(Locale.CHINESE)
                // Log.d(TAG_7ree, "onInit: setLanguage(Locale.CHINESE) result: $resultChinese_7ree")

                if (resultChinese_7ree != null && resultChinese_7ree >= TextToSpeech.LANG_AVAILABLE) {
                    isAnyLanguageSupported_7ree = true
                    // Log.d(TAG_7ree, "onInit: Language set to Chinese successfully.")
                } else {
                    Log.e(TAG_7ree, "onInit: Chinese language also not supported or data missing (${resultChinese_7ree}).")
                }
            }

            // 确保ViewModel存在后再设置TTS状态
            if (wordQueryViewModel_7ree != null) {
                wordQueryViewModel_7ree?.isTtsReady_7ree = isAnyLanguageSupported_7ree
                // Log.d(TAG_7ree, "onInit: TTS状态已设置到ViewModel，isTtsReady_7ree = $isAnyLanguageSupported_7ree")
            } else {
                Log.w(TAG_7ree, "onInit: ViewModel尚未初始化，将在ViewModel初始化后设置TTS状态")
                // 如果ViewModel还没初始化，我们需要在其他地方设置这个状态
            }
            isTtsInitialized_7ree = true
            
            // 计算TTS初始化总耗时
            @Suppress("UNUSED_VARIABLE")
            val ttsDuration = ttsEndTime - ttsInitStartTime_7ree
            // Log.d(TAG_7ree, "TTS初始化成功完成，耗时: ${ttsDuration}ms")
            
            if (!isAnyLanguageSupported_7ree) {
                Toast.makeText(context, "文本转语音：所需语言数据不可用，请前往设置下载。", Toast.LENGTH_LONG).show()
                val installIntent_7ree = android.content.Intent()
                installIntent_7ree.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
                if (installIntent_7ree.resolveActivity(context.packageManager) != null) {
                    context.startActivity(installIntent_7ree)
                } else {
                    Log.e(TAG_7ree, "onInit: No activity to handle ACTION_INSTALL_TTS_DATA.")
                }
            }

            tts_7ree?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    // 根据utteranceId判断是单词朗读还是例句朗读
                    when (utteranceId) {
                        "word" -> wordQueryViewModel_7ree?.setIsSpeakingWord_7ree(true)
                        "examples" -> wordQueryViewModel_7ree?.setIsSpeakingExamples_7ree(true)
                        else -> wordQueryViewModel_7ree?.setIsSpeaking_7ree(true)
                    }
                    Log.d(TAG_7ree, "onStart: Utterance playback started for ID: $utteranceId")
                }

                override fun onDone(utteranceId: String?) {
                    // 根据utteranceId判断是单词朗读还是例句朗读
                    when (utteranceId) {
                        "word" -> wordQueryViewModel_7ree?.setIsSpeakingWord_7ree(false)
                        "examples" -> wordQueryViewModel_7ree?.setIsSpeakingExamples_7ree(false)
                        else -> wordQueryViewModel_7ree?.setIsSpeaking_7ree(false)
                    }
                    Log.d(TAG_7ree, "onDone: Utterance playback completed for ID: $utteranceId")
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    // 根据utteranceId判断是单词朗读还是例句朗读
                    when (utteranceId) {
                        "word" -> wordQueryViewModel_7ree?.setIsSpeakingWord_7ree(false)
                        "examples" -> wordQueryViewModel_7ree?.setIsSpeakingExamples_7ree(false)
                        else -> wordQueryViewModel_7ree?.setIsSpeaking_7ree(false)
                    }
                    Log.e(TAG_7ree, "onError: Utterance playback error for ID: $utteranceId")
                }
            })
        } else {
            Log.e(TAG_7ree, "onInit: TextToSpeech initialization failed with status: $status")
            // 确保ViewModel存在后再设置TTS状态
            if (wordQueryViewModel_7ree != null) {
                wordQueryViewModel_7ree?.isTtsReady_7ree = false
                // Log.d(TAG_7ree, "onInit: TTS失败状态已设置到ViewModel，isTtsReady_7ree = false")
            } else {
                Log.w(TAG_7ree, "onInit: ViewModel尚未初始化，TTS失败状态无法设置")
            }
            isTtsInitialized_7ree = false
            
            // 计算TTS初始化失败耗时
            val ttsDuration = ttsEndTime - ttsInitStartTime_7ree
            Log.e(TAG_7ree, "TTS初始化失败，耗时: ${ttsDuration}ms")
            
            Toast.makeText(context, "文本转语音初始化失败，请检查设备设置。", Toast.LENGTH_LONG).show()
        }
    }
    
    fun shutdown() {
        if (tts_7ree != null) {
            tts_7ree?.stop()
            tts_7ree?.shutdown()
            Log.d(TAG_7ree, "shutdown: TextToSpeech stopped and shut down.")
        }
    }
    
    // 提供访问TTS实例的方法
    fun getTtsInstance(): TextToSpeech? = tts_7ree
    
    // 提供检查TTS是否初始化完成的方法
    fun isTtsInitialized(): Boolean = isTtsInitialized_7ree
}
