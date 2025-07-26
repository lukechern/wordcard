package com.x7ree.wordcard.ui.help

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 自定义滚动指示器组件，用于显示滚动位置
 */
@Composable
fun ScrollIndicator_7ree(
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f), // 滑轨颜色更淡
    thumbColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f) // 滑动条颜色更淡
) {
    // 计算滚动进度
    val scrollProgress = if (scrollState.maxValue > 0) {
        scrollState.value.toFloat() / scrollState.maxValue.toFloat()
    } else {
        0f
    }
    
    // 动画化滚动进度
    val animatedProgress by animateFloatAsState(
        targetValue = scrollProgress,
        animationSpec = tween(durationMillis = 150),
        label = "scroll_progress"
    )
    
    // 只有当内容可滚动时才显示指示器
    if (scrollState.maxValue > 0) {
        Box(
            modifier = modifier
                .width(4.6.dp) // 原来4dp，增加15%变为4.6dp
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.3.dp))
                .background(trackColor)
        ) {
            Canvas(
                modifier = Modifier
                    .width(4.6.dp)
                    .fillMaxHeight()
            ) {
                val canvasHeight = size.height
                val canvasWidth = size.width
                
                // 计算滑块的高度，使其符合屏幕与页面长度的比例
                val minThumbHeight = 20.dp.toPx()
                val contentHeight = scrollState.maxValue + canvasHeight // 总内容高度
                val visibleRatio = canvasHeight / contentHeight // 可见区域比例
                val proportionalThumbHeight = canvasHeight * visibleRatio // 按比例计算的滑块高度
                val thumbHeight = minThumbHeight.coerceAtLeast(proportionalThumbHeight)
                
                // 计算滑块的位置
                val thumbTop = animatedProgress * (canvasHeight - thumbHeight)
                
                // 绘制滑块
                drawRoundRect(
                    color = thumbColor,
                    topLeft = Offset(0f, thumbTop),
                    size = Size(canvasWidth, thumbHeight),
                    cornerRadius = CornerRadius(2.3.dp.toPx())
                )
            }
        }
    }
}
