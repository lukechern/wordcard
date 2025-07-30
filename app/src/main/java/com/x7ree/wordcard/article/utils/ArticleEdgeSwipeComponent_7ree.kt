package com.x7ree.wordcard.article.utils

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * 文章详情页边缘滑动返回组件
 * 提供从屏幕左右边缘向中间滑动返回功能
 * 特别处理与Android系统返回手势的冲突问题
 */
@Composable
fun ArticleEdgeSwipeComponent_7ree(
    onBackNavigation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    // 边缘检测区域宽度 - 设置为较小值避免与系统手势冲突
    val edgeWidth = with(density) { 24.dp.toPx() }
    // 滑动阈值 - 需要滑动的最小距离
    val swipeThreshold = with(density) { 80.dp.toPx() }
    
    Box(modifier = modifier.fillMaxSize()) {
        // 左边缘滑动区域
        Box(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            LeftEdgeSwipeArea(
                edgeWidth = edgeWidth,
                swipeThreshold = swipeThreshold,
                onBackNavigation = onBackNavigation
            )
        }
        
        // 右边缘滑动区域
        Box(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            RightEdgeSwipeArea(
                edgeWidth = edgeWidth,
                swipeThreshold = swipeThreshold,
                onBackNavigation = onBackNavigation
            )
        }
    }
}

@Composable
private fun LeftEdgeSwipeArea(
    edgeWidth: Float,
    swipeThreshold: Float,
    onBackNavigation: () -> Unit
) {
    val density = LocalDensity.current
    
    Box(
        modifier = Modifier
            .width(with(density) { edgeWidth.toDp() })
            .fillMaxHeight()
            .pointerInput(Unit) {
                var startX = 0f
                var startY = 0f
                var currentX = 0f
                var currentY = 0f
                var isValidSwipe = false
                var dragCount = 0
                
                detectDragGestures(
                    onDragStart = { offset ->
                        startX = offset.x
                        startY = offset.y
                        currentX = offset.x
                        currentY = offset.y
                        dragCount = 0
                        // 只有在边缘区域开始的滑动才有效
                        isValidSwipe = startX <= edgeWidth
                    },
                    onDragEnd = {
                        val totalDistanceX = currentX - startX
                        val totalDistanceY = currentY - startY
                        val shouldTrigger = isValidSwipe && totalDistanceX >= swipeThreshold
                        
                        if (shouldTrigger) {
                            onBackNavigation()
                        }
                        isValidSwipe = false
                    }
                ) { change, dragAmount ->
                    dragCount++
                    val oldCurrentX = currentX
                    val oldCurrentY = currentY
                    currentX += dragAmount.x
                    currentY += dragAmount.y
                    
                    if (isValidSwipe) {
                        // 确保只处理向右滑动
                        if (dragAmount.x < 0) {
                            isValidSwipe = false
                        }
                    }
                }
            }
    )
}

@Composable
private fun RightEdgeSwipeArea(
    edgeWidth: Float,
    swipeThreshold: Float,
    onBackNavigation: () -> Unit
) {
    val density = LocalDensity.current
    
    Box(
        modifier = Modifier
            .width(with(density) { edgeWidth.toDp() })
            .fillMaxHeight()
            .pointerInput(Unit) {
                var startX = 0f
                var startY = 0f
                var currentX = 0f
                var currentY = 0f
                var isValidSwipe = false
                var dragCount = 0
                
                detectDragGestures(
                    onDragStart = { offset ->
                        startX = offset.x
                        startY = offset.y
                        currentX = offset.x
                        currentY = offset.y
                        dragCount = 0
                        // 由于这个Box已经位于右边缘，所以任何在此区域的触摸都是有效的
                        isValidSwipe = true
                    },
                    onDragEnd = {
                        val totalDistanceX = startX - currentX  // 注意：右边缘是startX - currentX
                        val totalDistanceY = currentY - startY
                        val shouldTrigger = isValidSwipe && totalDistanceX >= swipeThreshold
                        
                        if (shouldTrigger) {
                            onBackNavigation()
                        }
                        isValidSwipe = false
                    }
                ) { change, dragAmount ->
                    dragCount++
                    val oldCurrentX = currentX
                    val oldCurrentY = currentY
                    currentX += dragAmount.x
                    currentY += dragAmount.y
                    
                    if (isValidSwipe) {
                        // 确保只处理向左滑动
                        if (dragAmount.x > 0) {
                            isValidSwipe = false
                        }
                    }
                }
            }
    )
}
