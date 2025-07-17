package com.x7ree.wordcard.ui

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
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
    thumbColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
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
                .width(4.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.dp))
                .background(trackColor)
        ) {
            Canvas(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
            ) {
                val canvasHeight = size.height
                val canvasWidth = size.width
                
                // 计算滑块的高度（至少20dp，最多为轨道高度的30%）
                val minThumbHeight = 20.dp.toPx()
                val maxThumbHeight = canvasHeight * 0.3f
                val thumbHeight = minThumbHeight.coerceAtMost(maxThumbHeight)
                
                // 计算滑块的位置
                val thumbTop = animatedProgress * (canvasHeight - thumbHeight)
                
                // 绘制滑块
                drawRoundRect(
                    color = thumbColor,
                    topLeft = Offset(0f, thumbTop),
                    size = Size(canvasWidth, thumbHeight),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
            }
        }
    }
}