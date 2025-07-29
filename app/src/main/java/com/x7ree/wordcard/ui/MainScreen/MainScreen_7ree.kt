package com.x7ree.wordcard.ui.MainScreen

import android.util.Log
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
import com.x7ree.wordcard.ui.SplashScreen_7ree
import com.x7ree.wordcard.ui.MainScreen.HandleOperationResultToast_7ree
import com.x7ree.wordcard.ui.MainScreen.HandleSplashScreenLogic_7ree
import com.x7ree.wordcard.ui.MainScreen.HandleViewModelAvailableLogic_7ree
import com.x7ree.wordcard.ui.MainScreen.ShowSearchScreen_7ree
import com.x7ree.wordcard.ui.MainScreen.ShowHistoryScreen_7ree
import com.x7ree.wordcard.ui.MainScreen.ShowArticleScreen_7ree
import com.x7ree.wordcard.ui.MainScreen.ShowSettingsScreen_7ree
import com.x7ree.wordcard.ui.MainScreen.HandleLoadingTimeoutLogic_7ree

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree?,
    isInitializationComplete_7ree: Boolean = false,
    onImportFile_7ree: () -> Unit = {}
) {
    // 从ViewModel获取当前屏幕状态
    val currentScreenString_7ree by wordQueryViewModel_7ree?.currentScreen_7ree?.collectAsState() ?: mutableStateOf("SEARCH")
    val currentScreen_7ree = when (currentScreenString_7ree) {
        "HISTORY" -> Screen_7ree.HISTORY
        "ARTICLE" -> Screen_7ree.ARTICLE
        "SETTINGS" -> Screen_7ree.SETTINGS
        else -> Screen_7ree.SEARCH
    }
    var showSplash_7ree by remember { mutableStateOf(true) }
    var showCustomToast_7ree by remember { mutableStateOf(false) }
    var toastMessage_7ree by remember { mutableStateOf("") }
    val showSplashState_7ree = remember { mutableStateOf(showSplash_7ree) }

    // 处理操作结果提示条显示逻辑
    HandleOperationResultToast_7ree(
        wordQueryViewModel_7ree = wordQueryViewModel_7ree,
        showCustomToast_7ree = { showCustomToast_7ree = it },
        setToastMessage_7ree = { toastMessage_7ree = it }
    )

    // 处理启动画面逻辑
    HandleSplashScreenLogic_7ree(
        isInitializationComplete_7ree = isInitializationComplete_7ree,
        showSplash_7ree = showSplashState_7ree
    )
    
    // 额外的安全机制 - 如果ViewModel可用但初始化标志未设置，也关闭启动画面
    HandleViewModelAvailableLogic_7ree(
        wordQueryViewModel_7ree = wordQueryViewModel_7ree,
        showSplash_7ree = showSplashState_7ree
    )

    // 更新showSplash_7ree状态
    showSplash_7ree = showSplashState_7ree.value

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
                                Screen_7ree.ARTICLE -> "ARTICLE"
                                Screen_7ree.SETTINGS -> "SETTINGS"
                                else -> "SEARCH"
                            }
                            wordQueryViewModel_7ree.setCurrentScreen_7ree(screenString)
                        },
                        onSearchReset_7ree = { wordQueryViewModel_7ree.resetQueryState_7ree() }
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
                                ShowSearchScreen_7ree(wordQueryViewModel_7ree = wordQueryViewModel_7ree)
                            }
                            Screen_7ree.HISTORY -> {
                                ShowHistoryScreen_7ree(
                                    wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                                    onWordClick_7ree = { word ->
                                        wordQueryViewModel_7ree.loadWordFromHistory_7ree(word)
                                        wordQueryViewModel_7ree.setCurrentScreen_7ree("SEARCH")
                                    }
                                )
                            }
                            Screen_7ree.ARTICLE -> {
                                ShowArticleScreen_7ree(wordQueryViewModel_7ree = wordQueryViewModel_7ree)
                            }
                            Screen_7ree.SETTINGS -> {
                                ShowSettingsScreen_7ree(
                                    wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                                    onImportFile_7ree = onImportFile_7ree
                                )
                            }
                        }
                    } else {
                        // 如果ViewModel还未初始化完成，显示加载状态，但添加超时保护
                        val showLoadingTimeout = HandleLoadingTimeoutLogic_7ree()
                        
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (showLoadingTimeout) {
                                Text(
                                    text = "应用启动异常，请重新打开应用",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
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
        }
        
        // 自定义提示条显示在最顶层
        CustomToast_7ree(
            message = toastMessage_7ree,
            isVisible = showCustomToast_7ree,
            onDismiss = { showCustomToast_7ree = false }
        )
    }
}
