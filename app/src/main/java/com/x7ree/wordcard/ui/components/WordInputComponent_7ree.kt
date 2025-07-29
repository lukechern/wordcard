package com.x7ree.wordcard.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import com.x7ree.wordcard.utils.CustomKeyboard.CustomKeyboard_7ree
import com.x7ree.wordcard.utils.CustomKeyboard.CustomKeyboardState_7ree
import com.x7ree.wordcard.utils.CustomKeyboard.rememberCustomKeyboardState_7ree
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.R
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.utils.CacheManager_7ree
import com.x7ree.wordcard.utils.DataStatistics_7ree
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

// 导入拆分后的组件
import com.x7ree.wordcard.ui.components.StatisticsComponent_7ree
import com.x7ree.wordcard.ui.components.CustomCursor_7ree

// 输入界面组件
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordInputComponent_7ree(
    wordQueryViewModel: WordQueryViewModel_7ree,
    showInputWarning: Boolean,
    onInputWarningChange: (Boolean) -> Unit,
    onCustomKeyboardStateChange: ((Boolean) -> Unit)? = null,
    customKeyboardState: CustomKeyboardState_7ree? = null,
    autoShowKeyboard: Boolean = true, // 添加控制是否自动显示键盘的参数
    modifier: Modifier = Modifier
) {
    // 获取通用配置
    val generalConfig_7ree by wordQueryViewModel.generalConfig_7ree.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    
    // 根据键盘类型决定是否显示自定义键盘
    val useCustomKeyboard = generalConfig_7ree.keyboardType == "custom"
    val focusManager = LocalFocusManager.current
    
    // 使用传入的键盘状态或创建新的状态
    val actualKeyboardState = customKeyboardState ?: rememberCustomKeyboardState_7ree()
    
    // 使用TextFieldValue来管理文本和光标位置
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = wordQueryViewModel.wordInput_7ree, selection = TextRange(wordQueryViewModel.wordInput_7ree.length)))
    }
    
    // 监听ViewModel中的wordInput变化，同步更新TextFieldValue
    LaunchedEffect(wordQueryViewModel.wordInput_7ree) {
        if (textFieldValue.text != wordQueryViewModel.wordInput_7ree) {
            textFieldValue = TextFieldValue(
                text = wordQueryViewModel.wordInput_7ree,
                selection = TextRange(wordQueryViewModel.wordInput_7ree.length)
            )
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    // 点击空白区域时失去焦点并隐藏键盘
                    focusManager.clearFocus()
                    if (useCustomKeyboard) {
                        actualKeyboardState.hide_7ree()
                        onCustomKeyboardStateChange?.invoke(false)
                    }
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 0.dp)
                .offset(y = (-0.15f * 100 + 50 + 24).dp), // 向下移动一行（增加24dp）
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App图标
            Image(
                painter = painterResource(id = R.drawable.wordcardicon),
                contentDescription = "App图标",
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
                modifier = Modifier
                    .size(90.dp)
                    .padding(bottom = 16.dp)
            )
            
            // 标题
            Text(
                text = "AI查单词",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // 焦点状态
            var isFocused by remember { mutableStateOf(false) }
            
            // 输入框容器
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                // 输入框 - 统一设计风格，限制只能输入英文字母
                OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newTextFieldValue ->
                    // 检查是否包含非英文字符
                    val hasInvalidChars = newTextFieldValue.text.any { !it.isLetter() || (it !in 'a'..'z' && it !in 'A'..'Z') }
                    if (hasInvalidChars) {
                        onInputWarningChange(true)
                    }
                    
                    // 过滤输入，只允许英文字母
                    val filteredText = newTextFieldValue.text.filter { it.isLetter() && (it in 'a'..'z' || it in 'A'..'Z') }
                    
                    // 更新TextFieldValue，保持光标在末尾
                    textFieldValue = TextFieldValue(
                        text = filteredText,
                        selection = TextRange(filteredText.length)
                    )
                    
                    // 同步更新ViewModel
                    if (!useCustomKeyboard) {
                        wordQueryViewModel.onWordInputChanged_7ree(filteredText)
                    }
                },
                readOnly = useCustomKeyboard, // 自定义键盘模式下设置为只读
                label = { Text("请输入英文单词") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                        if (focusState.isFocused && useCustomKeyboard) {
                            keyboardController?.hide()
                            actualKeyboardState.show_7ree()
                            onCustomKeyboardStateChange?.invoke(true)
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            // 点击输入框时请求焦点并显示键盘
                            focusRequester.requestFocus()
                            if (useCustomKeyboard) {
                                keyboardController?.hide()
                                actualKeyboardState.show_7ree()
                                onCustomKeyboardStateChange?.invoke(true)
                            }
                        })
                    },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                ),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = if (useCustomKeyboard) androidx.compose.ui.graphics.Color.Transparent else MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = if (useCustomKeyboard) {
                    KeyboardOptions(imeAction = ImeAction.None)
                } else {
                    KeyboardOptions(imeAction = ImeAction.Search)
                },
                keyboardActions = KeyboardActions(
                    onSearch = {
                        // 点击回车时直接提交查询
                        if (wordQueryViewModel.wordInput_7ree.length >= 3) {
                            wordQueryViewModel.queryWord_7ree()
                        }
                    }
                )
            )
            
            // 自定义光标组件
            if (useCustomKeyboard && isFocused) {
                CustomCursor_7ree(
                    text = wordQueryViewModel.wordInput_7ree,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 24.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
            
            // 自动聚焦到输入框
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            
            // 处理输入框点击事件
            LaunchedEffect(useCustomKeyboard, autoShowKeyboard) {
                if (useCustomKeyboard && autoShowKeyboard) {
                    // 使用自定义键盘时，隐藏系统键盘并显示自定义键盘
                    keyboardController?.hide()
                    actualKeyboardState.show_7ree()
                    onCustomKeyboardStateChange?.invoke(true)
                }
            }
            
            // 输入提示条
            if (showInputWarning) {
                Text(
                    text = "⚠️ 只能输入英文字母",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 查询按钮 - 统一设计风格，添加放大镜图标
            Button(
                onClick = { wordQueryViewModel.queryWord_7ree() },
                enabled = !wordQueryViewModel.isLoading_7ree && wordQueryViewModel.wordInput_7ree.length >= 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 2.dp,
                    disabledElevation = 0.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = "查询",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "用AI查询",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 统计数据组件
            StatisticsComponent_7ree(
                wordQueryViewModel = wordQueryViewModel,
                modifier = Modifier.padding(top = 88.dp) // 向上移动1行：112 - 24 = 88
            )
        }
        

    }
}
