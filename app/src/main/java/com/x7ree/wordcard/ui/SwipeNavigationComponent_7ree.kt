package com.x7ree.wordcard.ui

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import kotlin.math.abs
import androidx.compose.foundation.layout.BoxScope

/**
 * 滑动导航组件
 * 提供上下滑动切换功能，包含视觉反馈
 */
@Composable
fun SwipeNavigationComponent_7ree(
    canNavigate: Boolean,
    onNavigateToPrevious: () -> Unit,
    onNavigateToNext: () -> Unit
) {
    val TAG_7ree = "SwipeNavigationComponent_7ree"
    
    // 滑动反馈状态
    var showSwipeFeedback_7ree by remember { mutableStateOf(false) }
    var swipeDirection_7ree by remember { mutableStateOf("") } // "up" 或 "down"
    var totalDragDistance_7ree by remember { mutableStateOf(0f) }
    
    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f) // 只占70%宽度
            .fillMaxHeight() // 保持全高度
            .pointerInput(canNavigate) {
                if (canNavigate) {
                    var dragOffset = 0f
                    detectDragGestures(
                        onDragStart = {
                            showSwipeFeedback_7ree = true
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
                            showSwipeFeedback_7ree = false
                            swipeDirection_7ree = ""
                        }
                    ) { _, dragAmount ->
                        dragOffset += dragAmount.y
                        totalDragDistance_7ree = dragOffset
                        
                        // 更新滑动方向
                        swipeDirection_7ree = if (dragOffset > 0) "down" else "up"
                    }
                }
            }
    ) {
        // 显示滑动反馈箭头
        if (showSwipeFeedback_7ree && canNavigate) {
            val alpha = (abs(totalDragDistance_7ree) / 100f).coerceIn(0.3f, 1.0f)
            
            if (swipeDirection_7ree == "up") {
                // 向上箭头 - 切换到下一个单词
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "下一个单词",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 32.dp)
                        .size(48.dp)
                        .background(
                            color = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            shape = CircleShape
                        )
                        .padding(8.dp)
                        .graphicsLayer { this.alpha = alpha },
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                )
            } else if (swipeDirection_7ree == "down") {
                // 向下箭头 - 切换到上一个单词
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "上一个单词",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                        .size(48.dp)
                        .background(
                            color = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            shape = CircleShape
                        )
                        .padding(8.dp)
                        .graphicsLayer { this.alpha = alpha },
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}