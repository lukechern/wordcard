package com.x7ree.wordcard

/**
语言包定义

    'pl_exit_app_7r' => '再按一次退出应用',
**/

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
import com.x7ree.wordcard.widget.WordQueryWidgetProvider_7ree

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private var tts_7ree: TextToSpeech? = null
    private val TAG_7ree = "MainActivity_7ree"
    
    // 初始化状态跟踪
    private var isInitializationComplete_7ree = false
    private var isTtsInitialized_7ree = false
    private var isDatabaseInitialized_7ree = false
    
    // 性能测量变量
    private var ttsInitStartTime_7ree: Long = 0

    // 双击退出相关变量
    private var backPressedTime_7ree: Long = 0
    private var exitToast_7ree: Toast? = null

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
                                    if (tts_7ree == null) {
                                        Toast.makeText(this, "语音服务正在初始化，请稍后再试", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this, "语音服务未准备好，请检查设备设置。", Toast.LENGTH_SHORT).show()
                                    }
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
        initializeAppAsync_7ree(intent)
        
        // 立即初始化TTS，确保朗读按钮可用
        initializeTtsLazy_7ree()
    }
    
    private fun initializeAppAsync_7ree(intent: Intent?) {
        // 记录启动开始时间
        val startTime = System.currentTimeMillis()
        
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG_7ree, "开始异步初始化应用组件")
                
                // 只初始化数据库，TTS改为懒加载
                initializeDatabaseAsync_7ree()
                
                // 初始化ViewModel
                initializeViewModel_7ree()
                
                // 标记初始化完成
                withContext(Dispatchers.Main) {
                    isInitializationComplete_7ree = true
                    
                    // 计算并记录启动时间
                    val endTime = System.currentTimeMillis()
                    val startupTime = endTime - startTime
                    Log.d(TAG_7ree, "应用初始化完成，耗时: ${startupTime}ms")
                    
                    // 在初始化完成后处理小组件Intent
                    handleWidgetIntent_7ree(intent)
                }
                
            } catch (e: Exception) {
                Log.e(TAG_7ree, "异步初始化失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "应用初始化失败，部分功能可能不可用", Toast.LENGTH_LONG).show()
                    isInitializationComplete_7ree = true // 即使失败也要完成初始化流程
                    
                    // 即使失败也记录启动时间
                    val endTime = System.currentTimeMillis()
                    val startupTime = endTime - startTime
                    Log.e(TAG_7ree, "应用初始化失败，耗时: ${startupTime}ms")
                }
            }
        }
    }
    
    // 改为懒加载方式初始化TTS
    private fun initializeTtsLazy_7ree() {
        if (tts_7ree != null) return // 已经初始化过，不再重复初始化
        
        // 记录TTS初始化开始时间
        ttsInitStartTime_7ree = System.currentTimeMillis()
        
        try {
            Log.d(TAG_7ree, "开始懒加载初始化TTS")
            tts_7ree = TextToSpeech(this@MainActivity, this@MainActivity)
            Log.d(TAG_7ree, "TTS懒加载初始化请求已发送，开始时间: ${ttsInitStartTime_7ree}ms")
        } catch (e: Exception) {
            Log.e(TAG_7ree, "TTS懒加载初始化失败: ${e.message}", e)
            if (!isFinishing) {
                Toast.makeText(this@MainActivity, "文本转语音初始化失败，请检查设备设置。", Toast.LENGTH_LONG).show()
            }
            
            // 记录TTS初始化失败时间
            val ttsEndTime = System.currentTimeMillis()
            val ttsDuration = ttsEndTime - ttsInitStartTime_7ree
            Log.e(TAG_7ree, "TTS懒加载初始化失败，耗时: ${ttsDuration}ms")
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
                
                // 如果TTS已经初始化完成，同步状态到ViewModel
                if (isTtsInitialized_7ree && tts_7ree != null) {
                    // 检查TTS是否真正可用
                    val isTtsReady = try {
                        tts_7ree?.language != null
                    } catch (e: Exception) {
                        false
                    }
                    wordQueryViewModel_7ree?.isTtsReady_7ree = isTtsReady
                    Log.d(TAG_7ree, "ViewModel初始化完成，TTS状态已同步: isTtsReady_7ree = $isTtsReady")
                } else {
                    Log.d(TAG_7ree, "ViewModel初始化完成，TTS尚未初始化")
                }
            } else {
                Log.e(TAG_7ree, "WordRepository未初始化，无法创建ViewModel")
            }
        } catch (e: Exception) {
            Log.e(TAG_7ree, "ViewModel初始化失败: ${e.message}", e)
        }
    }

    override fun onInit(status: Int) {
        // 记录TTS初始化完成时间
        val ttsEndTime = System.currentTimeMillis()
        
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

            // 确保ViewModel存在后再设置TTS状态
            if (wordQueryViewModel_7ree != null) {
                wordQueryViewModel_7ree?.isTtsReady_7ree = isAnyLanguageSupported_7ree
                Log.d(TAG_7ree, "onInit: TTS状态已设置到ViewModel，isTtsReady_7ree = $isAnyLanguageSupported_7ree")
            } else {
                Log.w(TAG_7ree, "onInit: ViewModel尚未初始化，将在ViewModel初始化后设置TTS状态")
                // 如果ViewModel还没初始化，我们需要在其他地方设置这个状态
            }
            isTtsInitialized_7ree = true
            
            // 计算TTS初始化总耗时
            val ttsDuration = ttsEndTime - ttsInitStartTime_7ree
            Log.d(TAG_7ree, "TTS初始化成功完成，耗时: ${ttsDuration}ms")
            
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
            // 确保ViewModel存在后再设置TTS状态
            if (wordQueryViewModel_7ree != null) {
                wordQueryViewModel_7ree?.isTtsReady_7ree = false
                Log.d(TAG_7ree, "onInit: TTS失败状态已设置到ViewModel，isTtsReady_7ree = false")
            } else {
                Log.w(TAG_7ree, "onInit: ViewModel尚未初始化，TTS失败状态无法设置")
            }
            isTtsInitialized_7ree = false
            
            // 计算TTS初始化失败耗时
            val ttsDuration = ttsEndTime - ttsInitStartTime_7ree
            Log.e(TAG_7ree, "TTS初始化失败，耗时: ${ttsDuration}ms")
            
            if (!isFinishing) {
                Toast.makeText(this, "文本转语音初始化失败，请检查设备设置。", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        // 检查是否从单词本进入单词详情页面
        val isFromWordBook_7ree = wordQueryViewModel_7ree?.isFromWordBook_7ree?.value ?: false
        val currentScreen_7ree = wordQueryViewModel_7ree?.currentScreen_7ree?.value ?: "SEARCH"
        
        if (isFromWordBook_7ree && currentScreen_7ree == "SEARCH") {
            // 如果是从单词本进入的单词详情页面，直接返回单词本
            wordQueryViewModel_7ree?.returnToWordBook_7ree()
            return
        }
        
        val currentTime_7ree = System.currentTimeMillis()
        
        if (currentTime_7ree - backPressedTime_7ree > 2000) {
            // 第一次按返回键
            backPressedTime_7ree = currentTime_7ree
            exitToast_7ree?.cancel() // 取消之前的Toast
            exitToast_7ree = Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT)
            exitToast_7ree?.show()
        } else {
            // 第二次按返回键，退出应用
            exitToast_7ree?.cancel()
            super.onBackPressed()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleWidgetIntent_7ree(intent)
    }
    
    private fun handleWidgetIntent_7ree(intent: Intent?) {
        Log.d(TAG_7ree, "handleWidgetIntent_7ree called with action: ${intent?.action}")
        
        // 处理来自WidgetConfigActivity_7ree的查看详情请求
        val queryWord = intent?.getStringExtra("query_word")
        val showDetail = intent?.getBooleanExtra("show_detail", false)
        
        if (!queryWord.isNullOrBlank() && (showDetail == true)) {
            Log.d(TAG_7ree, "收到查看详情请求: $queryWord")
            // 等待ViewModel初始化完成后执行查询并显示详情
            lifecycleScope.launch {
                // 等待初始化完成
                while (!isInitializationComplete_7ree || wordQueryViewModel_7ree == null) {
                    kotlinx.coroutines.delay(100)
                }
                
                // 在主线程执行查询并切换到查询页面
                withContext(Dispatchers.Main) {
                    // 设置查询文本
                    wordQueryViewModel_7ree?.onWordInputChanged_7ree(queryWord)
                    // 先切换到查询页面
                    wordQueryViewModel_7ree?.setCurrentScreen_7ree("SEARCH")
                    // 然后加载单词详情
                    wordQueryViewModel_7ree?.loadWordFromHistory_7ree(queryWord)
                    Log.d(TAG_7ree, "查看详情已执行: $queryWord，已切换到查询页面")
                }
            }
            return
        }
        
        when (intent?.action) {
            WordQueryWidgetProvider_7ree.ACTION_WIDGET_QUERY_7ree -> {
                val queryText = intent.getStringExtra(WordQueryWidgetProvider_7ree.EXTRA_QUERY_TEXT_7ree)
                Log.d(TAG_7ree, "收到小组件查询请求: $queryText")
                
                if (!queryText.isNullOrBlank()) {
                    // 等待ViewModel初始化完成后执行查询
                    lifecycleScope.launch {
                        // 等待初始化完成
                        while (!isInitializationComplete_7ree || wordQueryViewModel_7ree == null) {
                            kotlinx.coroutines.delay(100)
                        }
                        
                        // 在主线程执行查询
                        withContext(Dispatchers.Main) {
                            wordQueryViewModel_7ree?.onWordInputChanged_7ree(queryText)
                            wordQueryViewModel_7ree?.queryWord_7ree()
                            Log.d(TAG_7ree, "小组件查询已执行: $queryText")
                        }
                    }
                }
            }
            WordQueryWidgetProvider_7ree.ACTION_WIDGET_WORDBOOK_7ree -> {
                Log.d(TAG_7ree, "收到小组件单词本请求")
                try {
                    // 等待ViewModel初始化完成后切换到单词本页面
                    lifecycleScope.launch {
                        // 等待初始化完成
                        while (!isInitializationComplete_7ree || wordQueryViewModel_7ree == null) {
                            kotlinx.coroutines.delay(100)
                        }
                        
                        // 在主线程切换到单词本页面
                        withContext(Dispatchers.Main) {
                            wordQueryViewModel_7ree?.setCurrentScreen_7ree("HISTORY")
                            Log.d(TAG_7ree, "已切换到单词本页面")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG_7ree, "处理单词本请求时出错: ${e.message}", e)
                }
            }
        }
    }

    override fun onDestroy() {
        if (tts_7ree != null) {
            tts_7ree?.stop()
            tts_7ree?.shutdown()
            Log.d(TAG_7ree, "onDestroy: TextToSpeech stopped and shut down.")
        }
        exitToast_7ree?.cancel() // 清理Toast
        super.onDestroy()
    }
}