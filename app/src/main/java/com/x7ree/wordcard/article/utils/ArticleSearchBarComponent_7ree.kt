package com.x7ree.wordcard.article.utils

import com.x7ree.wordcard.R

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.graphics.Color
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

/**
 * 文章搜索栏组件
 * 支持在标题栏和搜索框之间切换，专门用于文章搜索
 * UI样式与单词本搜索组件完全一致
 */
@Composable
fun ArticleSearchBarComponent_7ree(
    title: String,
    searchQuery: String,
    isSearchMode: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchModeToggle: (Boolean) -> Unit,
    onGenerateArticle: () -> Unit,
    onShowFilterMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
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
    
    // 当进入搜索模式时自动获取焦点
    LaunchedEffect(isSearchMode) {
        if (isSearchMode) {
            focusRequester.requestFocus()
        }
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
                            // 更新TextFieldValue，保持光标在末尾
                            textFieldValue = TextFieldValue(
                                text = newTextFieldValue.text,
                                selection = TextRange(newTextFieldValue.text.length)
                            )
                            
                            println("DEBUG: ArticleSearchBarComponent onValueChange: '${newTextFieldValue.text}'")
                            
                            // 同步更新外部状态
                            onSearchQueryChange(newTextFieldValue.text)
                        },
                        placeholder = { Text("搜索文章标题或关键词...") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp) // 恢复固定高度，确保与标题行高度一致
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                            }
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    focusRequester.requestFocus()
                                })
                            },
                        shape = RoundedCornerShape(16.dp), // 与单词本搜索框保持一致的圆角
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
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        trailingIcon = {
                            // 关闭搜索按钮放在搜索框内部右侧
                            IconButton(
                                onClick = {
                                    onSearchModeToggle(false)
                                    onSearchQueryChange("")
                                    keyboardController?.hide()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "关闭搜索",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                            }
                        )
                    )
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 生成文章按钮 - 背景减少30%，图标保持和单词本等大
                    IconButton(
                        onClick = onGenerateArticle,
                        modifier = Modifier
                            .size(30.dp) // 背景减少
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "生成文章",
                            tint = Color(0xFF2B7033), // 设置为更深的绿色
                            modifier = Modifier.size(28.dp) // 调整图标大小为28.dp
                        )
                    }
                    
                    // 搜索按钮
                    IconButton(
                        onClick = { onSearchModeToggle(true) },
                        modifier = Modifier.size(30.dp) // 与加号按钮保持一致的尺寸
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "搜索",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp) // 图标保持和单词本等大
                        )
                    }
                    
                    // 汉堡菜单按钮
                    IconButton(
                        onClick = onShowFilterMenu,
                        modifier = Modifier.size(30.dp) // 与加号按钮保持一致的尺寸
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "筛选与排序",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp) // 图标保持和单词本等大
                        )
                    }
                }
            }
        }
    }
}

/**
 * 专门为文章搜索框设计的左对齐光标组件
 * 与单词本搜索组件的光标实现保持一致
 */
@Composable
fun ArticleSearchCustomCursor_7ree(
    text: String,
    textStyle: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    // 光标闪烁动画
    val infiniteTransition = rememberInfiniteTransition(label = "article_search_cursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "article_search_cursor_alpha"
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
