package com.x7ree.wordcard.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.components.CustomCursor_7ree
import com.x7ree.wordcard.utils.CustomKeyboard.CustomKeyboard_7ree
import com.x7ree.wordcard.utils.CustomKeyboard.rememberCustomKeyboardState_7ree

/**
 * 搜索栏组件
 * 支持在标题栏和搜索框之间切换
 */
@Composable
fun SearchBarComponent_7ree(
    title: String,
    searchQuery: String,
    isSearchMode: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchModeToggle: (Boolean) -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null,
    wordQueryViewModel: WordQueryViewModel_7ree? = null,
    onCustomKeyboardStateChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val customKeyboardState_7ree = rememberCustomKeyboardState_7ree()
    
    // 获取键盘类型配置
    val generalConfig_7ree by (wordQueryViewModel?.generalConfig_7ree?.collectAsState() ?: remember { mutableStateOf(null) })
    val useCustomKeyboard = generalConfig_7ree?.keyboardType == "custom"
    
    // 使用TextFieldValue来管理文本和光标位置
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = searchQuery, selection = TextRange(searchQuery.length)))
    }
    
    // 监听searchQuery变化，同步更新TextFieldValue
    LaunchedEffect(searchQuery) {
        if (textFieldValue.text != searchQuery) {
            textFieldValue = TextFieldValue(
                text = searchQuery,
                selection = TextRange(searchQuery.length)
            )
        }
    }
    
    // 焦点状态
    var isFocused by remember { mutableStateOf(false) }
    
    // 当进入搜索模式时自动获取焦点和显示键盘
    LaunchedEffect(isSearchMode) {
        if (isSearchMode) {
            focusRequester.requestFocus()
            if (useCustomKeyboard == true) {
                keyboardController?.hide()
                customKeyboardState_7ree.show_7ree()
                onCustomKeyboardStateChange?.invoke(true)
            }
        } else {
            // 退出搜索模式时隐藏键盘
            if (useCustomKeyboard == true) {
                customKeyboardState_7ree.hide_7ree()
                onCustomKeyboardStateChange?.invoke(false)
            }
        }
    }
    
    // 监听自定义键盘状态变化
    LaunchedEffect(customKeyboardState_7ree.isVisible_7ree.value) {
        onCustomKeyboardStateChange?.invoke(customKeyboardState_7ree.isVisible_7ree.value)
    }
    
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp) // 固定标题栏高度，确保两种模式下高度一致
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
        if (isSearchMode) {
            // 搜索模式：显示搜索框
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { newTextFieldValue ->
                        // 过滤输入，只允许英文字母
                        val filteredText = newTextFieldValue.text.filter { it.isLetter() && (it in 'a'..'z' || it in 'A'..'Z') }
                        
                        // 更新TextFieldValue，保持光标在末尾
                        textFieldValue = TextFieldValue(
                            text = filteredText,
                            selection = TextRange(filteredText.length)
                        )
                        
                        // 同步更新外部状态
                        onSearchQueryChange(filteredText)
                    },
                    readOnly = useCustomKeyboard == true, // 自定义键盘模式下设置为只读
                    placeholder = { Text("搜索单词本...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp) // 恢复固定高度，确保与标题行高度一致
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                            if (focusState.isFocused && useCustomKeyboard == true) {
                                keyboardController?.hide()
                                customKeyboardState_7ree.show_7ree()
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                // 点击输入框时只请求焦点，不自动弹出键盘
                                // 键盘现在通过右下角按钮控制
                                focusRequester.requestFocus()
                            })
                        },
                    shape = RoundedCornerShape(16.dp), // 与首页搜索框保持一致的圆角
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        lineHeight = 20.sp // 适中的行高，确保文字完全显示且垂直居中
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = if (useCustomKeyboard == true) androidx.compose.ui.graphics.Color.Transparent else MaterialTheme.colorScheme.primary
                    ),

                    trailingIcon = {
                        // 关闭搜索按钮放在搜索框内部右侧
                        IconButton(
                            onClick = {
                                onSearchModeToggle(false)
                                onSearchQueryChange("")
                                keyboardController?.hide()
                                if (useCustomKeyboard == true) {
                                    customKeyboardState_7ree.hide_7ree()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "关闭搜索",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    keyboardOptions = if (useCustomKeyboard == true) {
                        KeyboardOptions(imeAction = ImeAction.None)
                    } else {
                        KeyboardOptions(imeAction = ImeAction.Search)
                    },
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                        }
                    )
                )
                
                // 自定义光标组件 - 专门为搜索框左对齐设计
                if (useCustomKeyboard == true && isFocused) {
                    SearchCustomCursor_7ree(
                        text = searchQuery,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                            textAlign = TextAlign.Start
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp) // 与OutlinedTextField内部文字的左边距保持一致
                    )
                }
            }
            
            // 自定义键盘支持
            if (useCustomKeyboard == true && isSearchMode) {
                LaunchedEffect(Unit) {
                    // 自动显示自定义键盘
                    keyboardController?.hide()
                    customKeyboardState_7ree.show_7ree()
                }
            }
        } else {
            // 标题模式：显示标题和操作按钮
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 搜索按钮
                IconButton(
                    onClick = { onSearchModeToggle(true) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "搜索",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // 其他操作按钮（如收藏按钮）
                trailingIcon?.invoke()
            }
        }
        }
        

    }
}

/**
 * 专门为搜索框设计的左对齐光标组件
 * 与原有的CustomCursor_7ree区别：左对齐而非居中对齐
 */
@Composable
fun SearchCustomCursor_7ree(
    text: String,
    textStyle: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    // 光标闪烁动画
    val infiniteTransition = rememberInfiniteTransition(label = "search_cursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "search_cursor_alpha"
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
        modifier = modifier.height(56.dp), // 与搜索框高度一致
        contentAlignment = Alignment.CenterStart // 左对齐
    ) {
        // 光标线条 - 左对齐定位
        Box(
            modifier = Modifier
                .offset(x = textWidth + 2.dp) // 定位到文本末尾并右移2dp避免重叠
                .width(2.dp)
                .height(textStyle.fontSize.value.dp * 1.2f)
                .alpha(alpha)
                .background(MaterialTheme.colorScheme.primary) // 使用主题色
        )
    }
}