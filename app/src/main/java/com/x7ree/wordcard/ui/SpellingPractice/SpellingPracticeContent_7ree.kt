package com.x7ree.wordcard.ui.SpellingPractice

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import android.util.Log
import kotlinx.coroutines.delay

/**
 * 拼写练习内容组件
 * 包含拼写练习的核心业务逻辑
 */
@Composable
fun SpellingPracticeContent_7ree(
    targetWord: String,
    chineseMeaning: String,
    onDismiss: () -> Unit,
    onSpellingSuccess: () -> Unit
) {
    Log.d("SpellingPractice_7ree", "拼写练习组件 - 单词: '$targetWord', 中文词义: '$chineseMeaning'")
    var userInput_7ree by remember { mutableStateOf("") }
    var showResult_7ree by remember { mutableStateOf(false) }
    var isCorrect_7ree by remember { mutableStateOf(false) }
    var inputTextColor_7ree by remember { mutableStateOf(Color.Unspecified) }
    val focusRequester_7ree = remember { FocusRequester() }
    val view = LocalView.current
    
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
        } catch (e: Exception) {
            // 忽略聚焦异常，不影响功能
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
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
                Log.d("SpellingPractice_7ree", "显示中文词义: '$displayMeaning_7ree'（原始: '$chineseMeaning'）")
            } else {
                Log.w("SpellingPractice_7ree", "中文词义为空，未显示")
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            // 字母输入框
            LetterInputBoxes_7ree(
                targetWord = targetWord,
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
                textColor = inputTextColor_7ree
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 结果显示
            SpellingResultDisplay_7ree(
                showResult = showResult_7ree,
                isCorrect = isCorrect_7ree
            )
        }
    }
}