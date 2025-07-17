package com.x7ree.wordcard.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * 边缘滑动导航组件
 * 检测从屏幕左右边缘向中间的滑动手势
 * 用于从单词详情页面返回单词本
 */
@Composable
fun EdgeSwipeNavigationComponent_7ree(
    isFromWordBook: Boolean, // 是否从单词本进入
    onReturnToWordBook: () -> Unit, // 返回单词本的回调
    content: @Composable () -> Unit // 包装的内容
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    var isDragging_7ree by remember { mutableStateOf(false) }
    var dragStartX_7ree by remember { mutableStateOf(0f) }
    var dragCurrentX_7ree by remember { mutableStateOf(0f) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(isFromWordBook) {
                if (isFromWordBook) {
                    val edgeThreshold = 50.dp.toPx() // 边缘检测阈值
                    val swipeThreshold = 100.dp.toPx() // 滑动距离阈值
                    val screenWidthPx = screenWidth.toPx()
                    
                    detectDragGestures(
                        onDragStart = { offset ->
                            val startX = offset.x
                            // 检查是否从屏幕边缘开始滑动
                            if (startX <= edgeThreshold || startX >= screenWidthPx - edgeThreshold) {
                                isDragging_7ree = true
                                dragStartX_7ree = startX
                                dragCurrentX_7ree = startX
                            }
                        },
                        onDragEnd = {
                            if (isDragging_7ree) {
                                val dragDistance = abs(dragCurrentX_7ree - dragStartX_7ree)
                                val isFromLeftEdge = dragStartX_7ree <= edgeThreshold
                                val isFromRightEdge = dragStartX_7ree >= screenWidthPx - edgeThreshold
                                
                                // 检查滑动方向和距离
                                val isValidSwipe = if (isFromLeftEdge) {
                                    // 从左边缘向右滑动
                                    dragCurrentX_7ree > dragStartX_7ree && dragDistance >= swipeThreshold
                                } else if (isFromRightEdge) {
                                    // 从右边缘向左滑动
                                    dragCurrentX_7ree < dragStartX_7ree && dragDistance >= swipeThreshold
                                } else {
                                    false
                                }
                                
                                if (isValidSwipe) {
                                    onReturnToWordBook()
                                }
                                
                                isDragging_7ree = false
                                dragStartX_7ree = 0f
                                dragCurrentX_7ree = 0f
                            }
                        }
                    ) { _, dragAmount ->
                        if (isDragging_7ree) {
                            dragCurrentX_7ree += dragAmount.x
                        }
                    }
                }
            }
    ) {
        content()
    }
}