package com.x7ree.wordcard.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

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
                .offset(x = textWidth / 2 + 5.dp) // 定位到文本末尾并右移4dp避免重叠
                .width(2.dp)
                .height(textStyle.fontSize.value.dp * 1.2f)
                .alpha(alpha)
                .background(androidx.compose.ui.graphics.Color.Black)
        )
    }
}
