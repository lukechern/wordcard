package com.x7ree.wordcard.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.util.Log
import com.x7ree.wordcard.utils.showKeyboardWithDelay_7ree
import kotlinx.coroutines.delay

// 获取前两个中文词义的工具函数
fun getFirstTwoMeanings_7ree(chineseMeaning: String): String {
    // 按逗号或分号分割词义
    val meanings = chineseMeaning.split(Regex("[,，;；]"))
        .map { it.trim() }
        .filter { it.isNotEmpty() }
    
    return when {
        meanings.isEmpty() -> chineseMeaning
        meanings.size == 1 -> meanings[0]
        else -> "${meanings[0]}，${meanings[1]}"
    }
}

// 拼写卡片组件
@Composable
fun SpellingCard_7ree(
    spellingCount: Int,
    onSpellingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onSpellingClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Keyboard,
                contentDescription = "拼写",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (spellingCount > 0) "拼写${spellingCount}次" else "拼写",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            // 添加额外的空间来匹配其他卡片的高度
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// 拼写练习对话框组件
@Composable
fun SpellingPracticeDialog_7ree(
    targetWord: String,
    chineseMeaning: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSpellingSuccess: () -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            SpellingPracticeContent_7ree(
                targetWord = targetWord,
                chineseMeaning = chineseMeaning,
                onDismiss = onDismiss,
                onSpellingSuccess = onSpellingSuccess
            )
        }
    }
}

// 拼写练习内容组件
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
                inputTextColor_7ree = Color(0xFF2E7D32) // 深绿色，与标题一致
            } else {
                inputTextColor_7ree = Color(0xFFF44336) // 红色
            }
            
            // 延迟一下让用户看到颜色变化
            delay(100)
            
            // 然后显示结果
            showResult_7ree = true
            
            if (isCorrect_7ree) {
                // 正确时，3秒后关闭对话框并调用成功回调
                delay(3000)
                onSpellingSuccess()
                onDismiss()
            } else {
                // 错误时，3秒后清空输入框并重置颜色
                delay(3000)
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
            delay(500) // 增加延迟确保对话框完全显示
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
                    color = Color(0xFF2E7D32), // 深绿色
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
            AnimatedVisibility(
                visible = showResult_7ree,
                enter = scaleIn(animationSpec = tween(300)) + fadeIn(),
                exit = scaleOut(animationSpec = tween(300)) + fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isCorrect_7ree) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "正确",
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF2E7D32) // 深绿色，与标题一致
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "恭喜，拼写正确！",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2E7D32) // 深绿色，与标题一致
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "抱歉，拼写错误！",

                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFFF44336) // 红色
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "错误，请重新拼写！",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFF44336)
                        )
                    }
                }
            }
        }
    }
}

// 字母输入框组件
@Composable
fun LetterInputBoxes_7ree(
    targetWord: String,
    userInput: String,
    onInputChange: (String) -> Unit,
    focusRequester: FocusRequester,
    textColor: Color = Color.Unspecified
) {
    // 使用普通TextField替代BasicTextField，避免BringIntoViewRequester问题
    TextField(
        value = userInput,
        onValueChange = onInputChange,
        modifier = Modifier
             .fillMaxWidth()
             .height(80.dp)
             .focusRequester(focusRequester),
        placeholder = {
            Text(
                "点击这里开始拼写单词",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        textStyle = TextStyle(
              textAlign = TextAlign.Center,
              letterSpacing = 2.sp,
              fontSize = 27.sp,
              fontWeight = FontWeight.Black,
              color = if (textColor != Color.Unspecified) textColor else Color.Unspecified
          ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    )
}