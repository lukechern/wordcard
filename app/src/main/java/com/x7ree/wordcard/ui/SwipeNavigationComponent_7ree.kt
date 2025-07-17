package com.x7ree.wordcard.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * 滑动导航组件
 * 提供上下滑动切换功能，返回滑动状态给外部组件
 */
@Composable
fun SwipeNavigationComponent_7ree(
    canNavigate: Boolean,
    onNavigateToPrevious: () -> Unit,
    onNavigateToNext: () -> Unit,
    onSwipeStateChange: (isSwipping: Boolean, direction: String, distance: Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f) // 只占70%宽度
            .fillMaxHeight() // 保持全高度
            .pointerInput(canNavigate) {
                if (canNavigate) {
                    var dragOffset = 0f
                    detectDragGestures(
                        onDragStart = {
                            onSwipeStateChange(true, "", 0f)
                        },
                        onDragEnd = {
                            val dragThreshold = 50.dp.toPx()
                            when {
                                dragOffset > dragThreshold -> {
                                    onNavigateToPrevious()
                                }
                                dragOffset < -dragThreshold -> {
                                    onNavigateToNext()
                                }
                            }
                            dragOffset = 0f
                            onSwipeStateChange(false, "", 0f)
                        }
                    ) { _, dragAmount ->
                        dragOffset += dragAmount.y
                        
                        // 更新滑动方向和距离
                        val direction = if (dragOffset > 0) "down" else "up"
                        onSwipeStateChange(true, direction, dragOffset)
                    }
                }
            }
    )
}