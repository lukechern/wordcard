package com.x7ree.wordcard.ui.MainScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.BottomNavigationBar_7ree
import com.x7ree.wordcard.ui.DashboardScreen_7ree
import com.x7ree.wordcard.ui.SplashScreen_7ree
import com.x7ree.wordcard.ui.WordCardScreen_7ree
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree?,
    isInitializationComplete_7ree: Boolean = false,
    speak_7ree: (String, String) -> Unit,
    stopSpeaking_7ree: () -> Unit,
    onImportFile_7ree: () -> Unit = {}
) {
    // 从ViewModel获取当前屏幕状态
    val currentScreenString_7ree by wordQueryViewModel_7ree?.currentScreen_7ree?.collectAsState() ?: mutableStateOf("SEARCH")
    val currentScreen_7ree = when (currentScreenString_7ree) {
        "HISTORY" -> Screen_7ree.HISTORY
        "SETTINGS" -> Screen_7ree.SETTINGS
        else -> Screen_7ree.SEARCH
    }
    var showSplash_7ree by remember { mutableStateOf(true) }
    var showCustomToast_7ree by remember { mutableStateOf(false) }
    var toastMessage_7ree by remember { mutableStateOf("") }
    val operationResult_7ree by wordQueryViewModel_7ree?.operationResult_7ree?.collectAsState() ?: mutableStateOf(null)

    // 监听操作结果，显示自定义提示条
    LaunchedEffect(operationResult_7ree) {
        operationResult_7ree?.let { result ->
            toastMessage_7ree = result
            showCustomToast_7ree = true
            // 清除操作结果
            wordQueryViewModel_7ree?.clearOperationResult_7ree()
        }
    }

    // 智能启动画面控制 - 并行执行，不增加总等待时间
    LaunchedEffect(isInitializationComplete_7ree) {
        if (isInitializationComplete_7ree) {
            // 如果初始化已完成，只显示500毫秒启动画面给用户视觉反馈
            delay(500)
            showSplash_7ree = false
        }
    }
    
    // 如果初始化时间过长，确保启动画面不会无限显示
    LaunchedEffect(Unit) {
        delay(3000) // 最多显示3秒启动画面
        if (showSplash_7ree) {
            showSplash_7ree = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                // 只有在初始化完成且不在启动画面时才显示底部导航
                if (!showSplash_7ree && wordQueryViewModel_7ree != null) {
                    BottomNavigationBar_7ree(
                        currentScreen_7ree = currentScreen_7ree,
                        onScreenSelected_7ree = { screen -> 
                            val screenString = when (screen) {
                                Screen_7ree.HISTORY -> "HISTORY"
                                Screen_7ree.SETTINGS -> "SETTINGS"
                                else -> "SEARCH"
                            }
                            wordQueryViewModel_7ree?.setCurrentScreen_7ree(screenString)
                        },
                        onSearchReset_7ree = { wordQueryViewModel_7ree?.resetQueryState_7ree() }
                    )
                }
            }
        ) { paddingValues_7ree ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues_7ree)
            ) {
                if (showSplash_7ree) {
                    SplashScreen_7ree()
                } else {
                    // 只有在初始化完成且ViewModel可用时才显示主界面
                    if (wordQueryViewModel_7ree != null) {
                        when (currentScreen_7ree) {
                            Screen_7ree.SEARCH -> {
                                WordCardScreen_7ree(
                                    wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                                    speak_7ree = speak_7ree,
                                    stopSpeaking_7ree = stopSpeaking_7ree
                                )
                            }
                            Screen_7ree.HISTORY -> {
                                HistoryScreen_7ree(
                                    wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                                    onWordClick_7ree = { word ->
                                        wordQueryViewModel_7ree.loadWordFromHistory_7ree(word)
                                        wordQueryViewModel_7ree.setCurrentScreen_7ree("SEARCH")
                                    },
                                    speak_7ree = speak_7ree
                                )
                            }
                            Screen_7ree.SETTINGS -> {
                                DashboardScreen_7ree(
                                    wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                                    onImportFile_7ree = onImportFile_7ree
                                )
                            }
                        }
                    } else {
                        // 如果ViewModel还未初始化完成，显示加载状态
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "正在加载...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        
        // 自定义提示条显示在最顶层
        CustomToast_7ree(
            message = toastMessage_7ree,
            isVisible = showCustomToast_7ree,
            onDismiss = { showCustomToast_7ree = false }
        )
    }
}