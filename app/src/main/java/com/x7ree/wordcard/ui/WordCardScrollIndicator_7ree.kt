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
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.unit.dp

/**
 * 单词卡片查询详情页专用滚动指示器组件
 * 宽度调整为5.dp，颜色减淡30%，不透明度减少45%
 */
@Composable
fun WordCardScrollIndicator_7ree(
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    // 原始颜色
    val originalTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.11f)
    val originalThumbColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
    
    // 颜色减淡30%
    val lightenedTrackColor = lightenColor_7ree(originalTrackColor, 0.3f)
    val lightenedThumbColor = lightenColor_7ree(originalThumbColor, 0.3f)
    
    // 不透明度减少45%（原来是30%，再减少15%）
    val trackColor = lightenedTrackColor.copy(alpha = lightenedTrackColor.alpha * 0.55f)
    val thumbColor = lightenedThumbColor.copy(alpha = lightenedThumbColor.alpha * 0.55f)
    
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
                .width(5.dp) // 宽度调整为5.dp
                .fillMaxHeight()
                .clip(RoundedCornerShape(3.dp))
                .background(trackColor)
        ) {
            Canvas(
                modifier = Modifier
                    .width(5.dp) // 宽度调整为5.dp
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
                    cornerRadius = CornerRadius(3.dp.toPx())
                )
            }
        }
    }
}

/**
 * 减淡颜色的辅助函数
 */
fun lightenColor_7ree(color: Color, factor: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.RGBToHSV(
        (color.red * 255).toInt(),
        (color.green * 255).toInt(),
        (color.blue * 255).toInt(),
        hsv
    )
    
    // 减淡颜色（增加亮度）
    hsv[2] = (hsv[2] + factor * (1f - hsv[2])).coerceAtMost(1f)
    
    val lighterColor = android.graphics.Color.HSVToColor(hsv)
    return Color(
        android.graphics.Color.red(lighterColor) / 255f,
        android.graphics.Color.green(lighterColor) / 255f,
        android.graphics.Color.blue(lighterColor) / 255f,
        color.alpha,
        ColorSpaces.Srgb
    )
}
