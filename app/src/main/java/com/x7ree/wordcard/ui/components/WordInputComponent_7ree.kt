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
import androidx.compose.material.icons.filled.Search
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

// 输入界面组件
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordInputComponent_7ree(
    wordQueryViewModel: WordQueryViewModel_7ree,
    showInputWarning: Boolean,
    onInputWarningChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // 获取通用配置
    val generalConfig_7ree by wordQueryViewModel.generalConfig_7ree.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val customKeyboardState_7ree = rememberCustomKeyboardState_7ree()
    
    // 根据键盘类型决定是否显示自定义键盘
    val useCustomKeyboard = generalConfig_7ree.keyboardType == "custom"
    val focusManager = LocalFocusManager.current
    
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
                        customKeyboardState_7ree.hide_7ree()
                    }
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset(y = (-0.15f * 100 + 50).dp), // 调整偏移量
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
                .fillMaxWidth(0.8f)
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
                        customKeyboardState_7ree.show_7ree()
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        // 点击输入框时请求焦点并显示键盘
                        focusRequester.requestFocus()
                        if (useCustomKeyboard) {
                            keyboardController?.hide()
                            customKeyboardState_7ree.show_7ree()
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
        LaunchedEffect(useCustomKeyboard) {
            if (useCustomKeyboard) {
                // 使用自定义键盘时，隐藏系统键盘并显示自定义键盘
                keyboardController?.hide()
                customKeyboardState_7ree.show_7ree()
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
                .fillMaxWidth(0.8f)
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
                imageVector = Icons.Filled.Search,
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
                modifier = Modifier.padding(top = 112.dp)
            )
        }
        
        // 自定义键盘 - 固定在屏幕底部
        if (useCustomKeyboard && customKeyboardState_7ree.isVisible_7ree.value) {
            CustomKeyboard_7ree(
                onKeyPress_7ree = { key ->
                    val currentInput = wordQueryViewModel.wordInput_7ree
                    when (key) {
                        "BACKSPACE" -> {
                            if (currentInput.isNotEmpty()) {
                                wordQueryViewModel.onWordInputChanged_7ree(currentInput.dropLast(1))
                            }
                        }
                        "SEARCH" -> {
                            if (currentInput.length >= 3) {
                                wordQueryViewModel.queryWord_7ree()
                                customKeyboardState_7ree.hide_7ree()
                            }
                        }
                        else -> {
                            // 添加字母
                            wordQueryViewModel.onWordInputChanged_7ree(currentInput + key)
                        }
                    }
                },
                onBackspace_7ree = {
                    val currentInput = wordQueryViewModel.wordInput_7ree
                    if (currentInput.isNotEmpty()) {
                        wordQueryViewModel.onWordInputChanged_7ree(currentInput.dropLast(1))
                    }
                },
                onSearch_7ree = {
                    if (wordQueryViewModel.wordInput_7ree.length >= 3) {
                        wordQueryViewModel.queryWord_7ree()
                        customKeyboardState_7ree.hide_7ree()
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// 统计数据组件
@Composable
fun StatisticsComponent_7ree(
    wordQueryViewModel: WordQueryViewModel_7ree,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cacheManager_7ree = remember { CacheManager_7ree(context) }
    var cachedStats_7ree by remember { mutableStateOf(DataStatistics_7ree.StatisticsData_7ree(0, 0, 0, 0, 0.0f, 0.0f, 0, 0.0f, 0.0f)) }
    
    // 加载缓存的统计数据
    LaunchedEffect(Unit) {
        // 延迟加载统计数据，不阻塞UI显示
        delay(100) // 短暂延迟，让UI先渲染
        
        // 检查是否需要更新缓存
        if (cacheManager_7ree.shouldUpdateCache_7ree()) {
            // 需要更新缓存时，获取最新数据
            val allWords_7ree = wordQueryViewModel.getHistoryWords_7ree().first()
            cachedStats_7ree = DataStatistics_7ree.calculateStatistics_7ree(allWords_7ree)
            cacheManager_7ree.updateCacheTimestamp_7ree()
        } else {
            // 使用缓存数据，快速获取一次性数据
            val allWords_7ree = wordQueryViewModel.getHistoryWords_7ree().first()
            cachedStats_7ree = DataStatistics_7ree.calculateStatistics_7ree(allWords_7ree)
        }
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "已收集${cachedStats_7ree.totalWords}个单词",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp)) // 两个统计数据之间的间距
        Text(
            text = "已累计查阅${cachedStats_7ree.totalViews}次",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp)) // 两个统计数据之间的间距
        Text(
            text = "已持续学习${cachedStats_7ree.studyDays}天",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp)) // 在统计数据和底部之间增加一些间距
    }
}

@Composable
fun CustomCursor_7ree(
    text: String,
    textStyle: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    // 光标闪烁动画
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )
    
    // 使用TextMeasurer精确计算文本宽度
    val textMeasurer = androidx.compose.ui.text.rememberTextMeasurer()
    val textWidth = remember(text, textStyle) {
        if (text.isEmpty()) {
            0.dp
        } else {
            val textLayoutResult = textMeasurer.measure(
                text = androidx.compose.ui.text.AnnotatedString(text),
                style = textStyle
            )
            with(density) {
                textLayoutResult.size.width.toDp()
            }
        }
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 光标线条
        Box(
            modifier = Modifier
                .offset(x = textWidth / 2 + 4.dp) // 定位到文本末尾并右移4dp避免重叠
                .width(2.dp)
                .height(textStyle.fontSize.value.dp * 1.2f)
                .alpha(alpha)
                .background(androidx.compose.ui.graphics.Color.Black)
        )
    }
}