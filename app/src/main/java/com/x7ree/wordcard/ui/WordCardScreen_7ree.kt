package com.x7ree.wordcard.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.components.LoadingComponent_7ree
import com.x7ree.wordcard.ui.components.WordInputComponent_7ree
import com.x7ree.wordcard.ui.components.WordResultComponent_7ree
import com.x7ree.wordcard.utils.CustomKeyboard.CustomKeyboard_7ree
import com.x7ree.wordcard.utils.CustomKeyboard.rememberCustomKeyboardState_7ree
import kotlinx.coroutines.delay

private const val TAG_7ree = "WordCardScreen_7ree"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordCardScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree
) {
    // 用于显示输入提示的状态
    var showInputWarning_7ree by remember { mutableStateOf(false) }
    
    // 拼写练习对话框状态
    var showSpellingDialog_7ree by remember { mutableStateOf(false) }
    
    // 获取是否从单词本进入的状态
    val isFromWordBook_7ree by wordQueryViewModel_7ree.isFromWordBook_7ree.collectAsState()
    
    // 自定义键盘状态
    val customKeyboardState_7ree = rememberCustomKeyboardState_7ree()
    var showCustomKeyboard_7ree by remember { mutableStateOf(false) }
    
    // 获取通用配置
    val generalConfig_7ree by wordQueryViewModel_7ree.generalConfig_7ree.collectAsState()
    val useCustomKeyboard = generalConfig_7ree.keyboardType == "custom"
    
    // 自动隐藏提示的效果
    LaunchedEffect(showInputWarning_7ree) {
        if (showInputWarning_7ree) {
            delay(2000) // 2秒后自动隐藏
            showInputWarning_7ree = false
        }
    }

    // 使用边缘滑动导航组件包装内容
    EdgeSwipeNavigationComponent_7ree(
        isFromWordBook = isFromWordBook_7ree,
        onReturnToWordBook = {
            wordQueryViewModel_7ree.returnToWordBook_7ree()
        }
    ) {
        // 使用Box来分层显示内容和键盘
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 主内容区域 - 保持原有的边距
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .animateContentSize(animationSpec = tween(durationMillis = 300)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    // 如果正在加载且没有查询结果，显示加载动画
                    wordQueryViewModel_7ree.isLoading_7ree && wordQueryViewModel_7ree.queryResult_7ree.isBlank() -> {
                        LoadingComponent_7ree(
                            wordInput = wordQueryViewModel_7ree.wordInput_7ree
                        )
                    }
                    // 未开始查询时，显示输入界面
                    !wordQueryViewModel_7ree.isWordConfirmed_7ree || wordQueryViewModel_7ree.queryResult_7ree.isBlank() -> {
                        WordInputComponent_7ree(
                            wordQueryViewModel = wordQueryViewModel_7ree,
                            showInputWarning = showInputWarning_7ree,
                            onInputWarningChange = { showInputWarning_7ree = it },
                            onCustomKeyboardStateChange = { isVisible ->
                                showCustomKeyboard_7ree = isVisible
                            },
                            customKeyboardState = customKeyboardState_7ree
                        )
                    }
                    // 有查询结果时，显示结果界面
                    else -> {
                        WordResultComponent_7ree(
                            wordQueryViewModel = wordQueryViewModel_7ree,
                            showSpellingDialog = showSpellingDialog_7ree,
                            onShowSpellingDialogChange = { showSpellingDialog_7ree = it }
                        )
                    }
                }
            }
            
            // 自定义键盘 - 固定在屏幕底部，占满整个屏幕宽度
            if (useCustomKeyboard && showCustomKeyboard_7ree && customKeyboardState_7ree.isVisible_7ree.value) {
                CustomKeyboard_7ree(
                    onKeyPress_7ree = { key ->
                        val currentInput = wordQueryViewModel_7ree.wordInput_7ree
                        when (key) {
                            "BACKSPACE" -> {
                                if (currentInput.isNotEmpty()) {
                                    wordQueryViewModel_7ree.onWordInputChanged_7ree(currentInput.dropLast(1))
                                }
                            }
                            "SEARCH" -> {
                                if (currentInput.length >= 3) {
                                    wordQueryViewModel_7ree.queryWord_7ree()
                                    customKeyboardState_7ree.hide_7ree()
                                    showCustomKeyboard_7ree = false
                                }
                            }
                            else -> {
                                // 添加字母
                                wordQueryViewModel_7ree.onWordInputChanged_7ree(currentInput + key)
                            }
                        }
                    },
                    onBackspace_7ree = {
                        val currentInput = wordQueryViewModel_7ree.wordInput_7ree
                        if (currentInput.isNotEmpty()) {
                            wordQueryViewModel_7ree.onWordInputChanged_7ree(currentInput.dropLast(1))
                        }
                    },
                    onSearch_7ree = {
                        if (wordQueryViewModel_7ree.wordInput_7ree.length >= 3) {
                            wordQueryViewModel_7ree.queryWord_7ree()
                            customKeyboardState_7ree.hide_7ree()
                            showCustomKeyboard_7ree = false
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}
