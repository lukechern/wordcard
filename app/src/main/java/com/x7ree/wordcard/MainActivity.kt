package com.x7ree.wordcard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.Engine
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.x7ree.wordcard.ui.MainScreen_7ree
import com.x7ree.wordcard.ui.WordCardScreen_7ree
import com.x7ree.wordcard.ui.SplashScreen_7ree
import com.x7ree.wordcard.ui.theme.WordCardTheme
import java.util.Locale
import androidx.activity.viewModels
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.data.WordDatabase_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import android.widget.Toast

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private var tts_7ree: TextToSpeech? = null
    private val TAG_7ree = "MainActivity_7ree"
    
    // 初始化状态跟踪
    private var isInitializationComplete_7ree = false
    private var isTtsInitialized_7ree = false
    private var isDatabaseInitialized_7ree = false

    // 创建数据库和仓库实例 - 改为异步初始化
    private var database_7ree: WordDatabase_7ree? = null
    private var wordRepository_7ree: WordRepository_7ree? = null
    private var wordQueryViewModel_7ree: WordQueryViewModel_7ree? = null
    
    // 文件选择器
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            Log.d(TAG_7ree, "文件选择器返回: $selectedUri")
            wordQueryViewModel_7ree?.importHistoryData_7ree(selectedUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 立即显示UI，不等待初始化
        enableEdgeToEdge()
        setContent {
            WordCardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen_7ree(
                        wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                        isInitializationComplete_7ree = isInitializationComplete_7ree,
                        speak_7ree = { text, utteranceId ->
                            if (tts_7ree != null && wordQueryViewModel_7ree?.isTtsReady_7ree == true && text.isNotBlank()) {
                                tts_7ree?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
                                Log.d(TAG_7ree, "speak_7ree: Attempting to speak: \"$text\" with utteranceId: $utteranceId")
                            } else {
                                Log.e(TAG_7ree, "speak_7ree: TTS not ready or text is blank, cannot speak. isTtsReady_7ree: ${wordQueryViewModel_7ree?.isTtsReady_7ree}")
                                if (!isFinishing) {
                                    Toast.makeText(this, "语音服务未准备好，请检查设备设置。", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        stopSpeaking_7ree = { // New lambda to stop speaking
                            tts_7ree?.stop()
                            wordQueryViewModel_7ree?.setIsSpeaking_7ree(false)
                            wordQueryViewModel_7ree?.setIsSpeakingWord_7ree(false)
                            wordQueryViewModel_7ree?.setIsSpeakingExamples_7ree(false)
                            Log.d(TAG_7ree, "stopSpeaking_7ree: TTS stopped.")
                        },
                        onImportFile_7ree = {
                            filePickerLauncher.launch("application/json")
                        }
                    )
                }
            }
        }
        
        // 异步初始化所有组件
        initializeAppAsync_7ree()
    }
    
    private fun initializeAppAsync_7ree() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG_7ree, "开始异步初始化应用组件")
                
                // 并行初始化TTS和数据库
                val ttsJob = launch { initializeTtsAsync_7ree() }
                val databaseJob = launch { initializeDatabaseAsync_7ree() }
                
                // 等待两个初始化完成
                ttsJob.join()
                databaseJob.join()
                
                // 初始化ViewModel
                initializeViewModel_7ree()
                
                // 标记初始化完成
                withContext(Dispatchers.Main) {
                    isInitializationComplete_7ree = true
                    Log.d(TAG_7ree, "应用初始化完成")
                }
                
            } catch (e: Exception) {
                Log.e(TAG_7ree, "异步初始化失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "应用初始化失败，部分功能可能不可用", Toast.LENGTH_LONG).show()
                    isInitializationComplete_7ree = true // 即使失败也要完成初始化流程
                }
            }
        }
    }
    
    private suspend fun initializeTtsAsync_7ree() {
        try {
            Log.d(TAG_7ree, "开始异步初始化TTS")
            tts_7ree = TextToSpeech(this@MainActivity, this@MainActivity)
            Log.d(TAG_7ree, "TTS初始化请求已发送")
        } catch (e: Exception) {
            Log.e(TAG_7ree, "TTS异步初始化失败: ${e.message}", e)
            withContext(Dispatchers.Main) {
                if (!isFinishing) {
                    Toast.makeText(this@MainActivity, "文本转语音初始化失败，请检查设备设置。", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private suspend fun initializeDatabaseAsync_7ree() {
        try {
            Log.d(TAG_7ree, "开始异步初始化数据库")
            database_7ree = WordDatabase_7ree.getDatabase_7ree(this@MainActivity)
            wordRepository_7ree = WordRepository_7ree(database_7ree!!.wordDao_7ree())
            isDatabaseInitialized_7ree = true
            Log.d(TAG_7ree, "数据库初始化完成")
        } catch (e: Exception) {
            Log.e(TAG_7ree, "数据库异步初始化失败: ${e.message}", e)
        }
    }
    
    private suspend fun initializeViewModel_7ree() {
        try {
            Log.d(TAG_7ree, "开始初始化ViewModel")
            if (wordRepository_7ree != null) {
                wordQueryViewModel_7ree = WordQueryViewModel_7ree(OpenAiApiService_7ree(), wordRepository_7ree!!, this@MainActivity)
                Log.d(TAG_7ree, "ViewModel初始化完成")
            } else {
                Log.e(TAG_7ree, "WordRepository未初始化，无法创建ViewModel")
            }
        } catch (e: Exception) {
            Log.e(TAG_7ree, "ViewModel初始化失败: ${e.message}", e)
        }
    }

    override fun onInit(status: Int) {
        Log.d(TAG_7ree, "onInit: Received status: $status")
        if (status == TextToSpeech.SUCCESS) {
            Log.d(TAG_7ree, "onInit: TextToSpeech initialized successfully.")

            var isAnyLanguageSupported_7ree = false

            val resultUs_7ree = tts_7ree?.setLanguage(Locale.US)
            Log.d(TAG_7ree, "onInit: setLanguage(Locale.US) result: $resultUs_7ree")

            if (resultUs_7ree != null && resultUs_7ree >= TextToSpeech.LANG_AVAILABLE) {
                isAnyLanguageSupported_7ree = true
                Log.d(TAG_7ree, "onInit: Language set to US locale successfully.")
            } else {
                Log.w(TAG_7ree, "onInit: US English language not supported or data missing (${resultUs_7ree}). Trying Chinese.")
                val resultChinese_7ree = tts_7ree?.setLanguage(Locale.CHINESE)
                Log.d(TAG_7ree, "onInit: setLanguage(Locale.CHINESE) result: $resultChinese_7ree")

                if (resultChinese_7ree != null && resultChinese_7ree >= TextToSpeech.LANG_AVAILABLE) {
                    isAnyLanguageSupported_7ree = true
                    Log.d(TAG_7ree, "onInit: Language set to Chinese successfully.")
                } else {
                    Log.e(TAG_7ree, "onInit: Chinese language also not supported or data missing (${resultChinese_7ree}).")
                }
            }

            wordQueryViewModel_7ree?.isTtsReady_7ree = isAnyLanguageSupported_7ree
            isTtsInitialized_7ree = true
            
            if (!isAnyLanguageSupported_7ree && !isFinishing) {
                Toast.makeText(this, "文本转语音：所需语言数据不可用，请前往设置下载。", Toast.LENGTH_LONG).show()
                val installIntent_7ree = Intent()
                installIntent_7ree.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
                if (installIntent_7ree.resolveActivity(packageManager) != null) {
                    startActivity(installIntent_7ree)
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
            wordQueryViewModel_7ree?.isTtsReady_7ree = false
            isTtsInitialized_7ree = false
            if (!isFinishing) {
                Toast.makeText(this, "文本转语音初始化失败，请检查设备设置。", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        if (tts_7ree != null) {
            tts_7ree?.stop()
            tts_7ree?.shutdown()
            Log.d(TAG_7ree, "onDestroy: TextToSpeech stopped and shut down.")
        }
        super.onDestroy()
    }
}