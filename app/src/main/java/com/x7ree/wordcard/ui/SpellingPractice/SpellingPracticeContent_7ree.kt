package com.x7ree.wordcard.ui.SpellingPractice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import android.util.Log
import kotlinx.coroutines.delay
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.utils.CustomKeyboard.CustomKeyboard_7ree
import com.x7ree.wordcard.utils.CustomKeyboard.CustomKeyboardState_7ree
import com.x7ree.wordcard.utils.CustomKeyboard.rememberCustomKeyboardState_7ree

/**
 * 拼写练习内容组件
 * 包含拼写练习的核心业务逻辑
 */
@Composable
fun SpellingPracticeContent_7ree(
    targetWord: String,
    chineseMeaning: String,
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onDismiss: () -> Unit,
    onSpellingSuccess: () -> Unit
) {
    // Log.d("SpellingPractice_7ree", "拼写练习组件 - 单词: '$targetWord', 中文词义: '$chineseMeaning'")
    var userInput_7ree by remember { mutableStateOf("") }
    var showResult_7ree by remember { mutableStateOf(false) }
    var isCorrect_7ree by remember { mutableStateOf(false) }
    var inputTextColor_7ree by remember { mutableStateOf(Color.Unspecified) }
    val focusRequester_7ree = remember { FocusRequester() }
    
    // 自定义键盘状态管理
    val generalConfig_7ree by wordQueryViewModel_7ree.generalConfig_7ree.collectAsState()
    val useCustomKeyboard = generalConfig_7ree.keyboardType == "custom"
    val customKeyboardState_7ree = rememberCustomKeyboardState_7ree()
    val keyboardController = LocalSoftwareKeyboardController.current
    var isInputFocused_7ree by remember { mutableStateOf(false) }
    
    // 自动朗读功能 - 当拼写卡片打开且配置启用时自动朗读单词
    LaunchedEffect(targetWord, generalConfig_7ree.autoReadOnSpellingCard) {
        if (generalConfig_7ree.autoReadOnSpellingCard && 
            targetWord.isNotBlank() &&
            wordQueryViewModel_7ree.isTtsReady_7ree) {
            // 延迟一小段时间确保对话框完全显示
            delay(800) // 稍微长一点的延迟，确保用户能看到界面
            wordQueryViewModel_7ree.speakWord_7ree(targetWord)
        }
    }
    
    // 检查拼写是否正确
    LaunchedEffect(userInput_7ree) {
        if (userInput_7ree.length == targetWord.length) {
            isCorrect_7ree = userInput_7ree.equals(targetWord, ignoreCase = true)
            
            // 先改变输入框文字颜色
            if (isCorrect_7ree) {
                inputTextColor_7ree = SpellingColors_7ree.SUCCESS_COLOR
            } else {
                inputTextColor_7ree = SpellingColors_7ree.ERROR_COLOR
            }
            
            // 延迟一下让用户看到颜色变化
            delay(SpellingConstants_7ree.RESULT_DISPLAY_DELAY)
            
            // 然后显示结果
            showResult_7ree = true
            
            if (isCorrect_7ree) {
                // 正确时，3秒后关闭对话框并调用成功回调
                delay(SpellingConstants_7ree.SUCCESS_AUTO_CLOSE_DELAY)
                onSpellingSuccess()
                onDismiss()
            } else {
                // 错误时，3秒后清空输入框并重置颜色
                delay(SpellingConstants_7ree.ERROR_RESET_DELAY)
                userInput_7ree = ""
                showResult_7ree = false
                inputTextColor_7ree = Color.Unspecified
                // 错误时不自动重新聚焦，避免异常
            }
        }
    }
    
    // 自动聚焦到输入框，延迟执行避免BringIntoViewRequester异常
    LaunchedEffect(targetWord) {
        try {
            delay(SpellingConstants_7ree.FOCUS_REQUEST_DELAY) // 增加延迟确保对话框完全显示
            focusRequester_7ree.requestFocus()
            isInputFocused_7ree = true
            if (useCustomKeyboard) {
                customKeyboardState_7ree.show_7ree()
            }
        } catch (e: Exception) {
            // 忽略聚焦异常，不影响功能
        }
    }
    
    // 管理自定义键盘显示状态
    LaunchedEffect(isInputFocused_7ree, useCustomKeyboard) {
        if (useCustomKeyboard) {
            if (isInputFocused_7ree) {
                customKeyboardState_7ree.show_7ree()
                keyboardController?.hide()
            } else {
                customKeyboardState_7ree.hide_7ree()
            }
        } else {
            customKeyboardState_7ree.hide_7ree()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // 单击空白区域关闭拼写卡片和键盘
                        onDismiss()
                    }
                )
            }
    ) {
        // 主要内容卡片
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            // 阻止事件冒泡到父级Box
                        }
                    )
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 大标题：中文词义（深绿色，只显示前两个词义）
                if (chineseMeaning.isNotEmpty()) {
                    val displayMeaning_7ree = getFirstTwoMeanings_7ree(chineseMeaning)
                    Text(
                        text = displayMeaning_7ree,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SpellingColors_7ree.SUCCESS_COLOR,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Log.d("SpellingPractice_7ree", "显示中文词义: '$displayMeaning_7ree'（原始: '$chineseMeaning'）")
                } else {
                    Log.w("SpellingPractice_7ree", "中文词义为空，未显示")
                }
                
                Spacer(modifier = Modifier.height(2.dp))
                
                // 字母输入框
                 LetterInputBoxes_7ree(
                     userInput = userInput_7ree,
                     onInputChange = { newInput ->
                         if (newInput.length <= targetWord.length && newInput.all { it.isLetter() }) {
                             // 重置文字颜色（当用户开始新的输入时）
                             if (inputTextColor_7ree != Color.Unspecified) {
                                 inputTextColor_7ree = Color.Unspecified
                             }
                             userInput_7ree = newInput
                         }
                     },
                     focusRequester = focusRequester_7ree,
                     wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                     textColor = inputTextColor_7ree,
                     onFocusChanged = { isFocused ->
                         isInputFocused_7ree = isFocused
                     }
                 )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 结果显示
                SpellingResultDisplay_7ree(
                    showResult = showResult_7ree,
                    isCorrect = isCorrect_7ree
                )
            }
        }
        
        // 自定义键盘 - 固定在屏幕底部，全屏宽度
        if (useCustomKeyboard && customKeyboardState_7ree.isVisible_7ree.value) {
            CustomKeyboard_7ree(
                onKeyPress_7ree = { key ->
                    when (key) {
                        "BACKSPACE" -> {
                            if (userInput_7ree.isNotEmpty()) {
                                val newInput = userInput_7ree.dropLast(1)
                                userInput_7ree = newInput
                            }
                        }
                        "SEARCH" -> {
                            // 完成输入
                            isInputFocused_7ree = false
                            customKeyboardState_7ree.hide_7ree()
                        }
                        else -> {
                            // 添加字母，只允许英文字母
                            if (key.length == 1 && key[0].isLetter() && userInput_7ree.length < targetWord.length) {
                                userInput_7ree += key
                            }
                        }
                    }
                },
                onBackspace_7ree = {
                    if (userInput_7ree.isNotEmpty()) {
                        val newInput = userInput_7ree.dropLast(1)
                        userInput_7ree = newInput
                    }
                },
                onSearch_7ree = {
                    // 完成输入
                    isInputFocused_7ree = false
                    customKeyboardState_7ree.hide_7ree()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                // 阻止键盘区域的点击事件冒泡到父级Box
                            }
                        )
                    }
            )
        }
    }
}
