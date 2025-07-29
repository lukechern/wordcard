package com.x7ree.wordcard.article.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp

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
