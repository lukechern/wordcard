package com.x7ree.wordcard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.x7ree.wordcard.core.AppInitializer_7ree
import com.x7ree.wordcard.core.IntentHandler_7ree
import com.x7ree.wordcard.core.TtsManager_7ree
import com.x7ree.wordcard.ui.MainScreen_7ree
import com.x7ree.wordcard.ui.theme.WordCardTheme
import com.x7ree.wordcard.utils.BackPressHandler_7ree

class MainActivity : ComponentActivity() {
    private val TAG_7ree = "MainActivity_7ree"
    
    // 核心组件
    private lateinit var appInitializer_7ree: AppInitializer_7ree
    private lateinit var ttsManager_7ree: TtsManager_7ree
    private lateinit var intentHandler_7ree: IntentHandler_7ree
    private var backPressHandler_7ree: BackPressHandler_7ree? = null
    
    // 状态跟踪
    private var isInitializationComplete_7ree by mutableStateOf(false)
    
    // 单词数据文件选择器
    private val wordFilePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            Log.d(TAG_7ree, "单词数据文件选择器返回: $selectedUri")
            appInitializer_7ree.wordQueryViewModel_7ree?.importHistoryData_7ree(selectedUri)
        }
    }
    
    // 文章数据文件选择器
    private val articleFilePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            Log.d(TAG_7ree, "文章数据文件选择器返回: $selectedUri")
            appInitializer_7ree.wordQueryViewModel_7ree?.importArticleData_7ree(selectedUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 记录启动intent的详细信息
        Log.d(TAG_7ree, "onCreate called with intent action: ${intent?.action}")
        Log.d(TAG_7ree, "onCreate intent extras: query_word=${intent?.getStringExtra("query_word")}, show_detail=${intent?.getBooleanExtra("show_detail", false)}")
        
        // 初始化核心组件
        initializeComponents()
        
        // 立即显示UI，不等待初始化
        enableEdgeToEdge()
        setContent {
            WordCardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen_7ree(
                        wordQueryViewModel_7ree = appInitializer_7ree.wordQueryViewModel_7ree,
                        isInitializationComplete_7ree = isInitializationComplete_7ree,
                        onImportWordFile_7ree = {
                            wordFilePickerLauncher.launch("application/json")
                        },
                        onImportArticleFile_7ree = {
                            articleFilePickerLauncher.launch("application/json")
                        }
                    )
                }
            }
        }
        
        // 异步初始化所有组件
        initializeAppAsync_7ree(intent)
    }
    
    private fun initializeComponents() {
        // 初始化应用核心组件
        appInitializer_7ree = AppInitializer_7ree(this, this)
        ttsManager_7ree = TtsManager_7ree(this)
        intentHandler_7ree = IntentHandler_7ree(this, this)
    }
    
    private fun initializeAppAsync_7ree(intent: Intent?) {
        appInitializer_7ree.initializeAppAsync_7ree { viewModel ->
            if (viewModel != null) {
                // 初始化完成
                isInitializationComplete_7ree = true
                
                // 设置Intent处理器的ViewModel
                intentHandler_7ree.setViewModel(viewModel)
                intentHandler_7ree.setInitializationStatus(true)
                
                // 初始化TTS管理器
                ttsManager_7ree.initializeTtsLazy_7ree(viewModel)
                
                // 初始化返回键处理器
                backPressHandler_7ree = BackPressHandler_7ree(this, viewModel)
                
                // 处理初始Intent
                intentHandler_7ree.handleWidgetIntent_7ree(intent)
            } else {
                // 初始化失败处理
                Log.e(TAG_7ree, "应用初始化失败")
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        intentHandler_7ree.handleWidgetIntent_7ree(intent)
    }

    override fun onDestroy() {
        // 清理资源
        ttsManager_7ree.shutdown()
        backPressHandler_7ree?.cancelToast()
        super.onDestroy()
    }
}
