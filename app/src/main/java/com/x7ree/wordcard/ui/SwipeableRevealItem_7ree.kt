package com.x7ree.wordcard.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * 可滑动暴露操作按钮的列表项组件
 * 
 * 提供苹果风格的滑动删除交互体验：
 * 1. 向左滑动暴露删除按钮
 * 2. 点击删除按钮执行删除操作
 * 3. 删除后自动收起
 * 4. 支持手动滑动恢复
 * 
 * @param modifier 自定义修饰符
 * @param revealWidth 滑动暴露的宽度，默认80dp
 * @param backgroundColor 背景颜色，默认暗红色
 * @param icon 操作图标，默认删除图标
 * @param onDeleteClick 删除点击回调
 * @param content 主要内容区域
 */
@Composable
fun SwipeableRevealItem_7ree(
    modifier: Modifier = Modifier,
    revealWidth: Dp = 80.dp,
    backgroundColor: Color = Color(0xFFD32F2F), // 更暗的红色
    icon: ImageVector = Icons.Filled.Delete,
    onDeleteClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val revealPx = with(LocalDensity.current) { revealWidth.toPx() }

    var offsetX by remember { mutableStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = tween(durationMillis = 180),
        label = "swipeAnimation"
    )

    Box(modifier = modifier.fillMaxWidth()) {
        // 背景（红色 + 删除按钮）
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(backgroundColor, MaterialTheme.shapes.medium),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(
                onClick = {
                    onDeleteClick()
                    offsetX = 0f // 删除后自动关闭
                },
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "删除",
                    tint = Color.White
                )
            }
        }

        // 可滑动前景内容
        Box(
            modifier = Modifier
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            offsetX = if (offsetX <= -revealPx / 2) -revealPx else 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = (offsetX + dragAmount).coerceIn(-revealPx, 0f)
                            offsetX = newOffset
                        }
                    )
                }
        ) {
            content()
        }
    }
} 