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

/**
 * 滑动导航组件
 * 提供上下滑动切换功能，包含视觉反馈
 */
@Composable
fun SwipeNavigationComponent_7ree(
    canNavigate: Boolean,
    onNavigateToPrevious: () -> Unit,
    onNavigateToNext: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val TAG_7ree = "SwipeNavigationComponent_7ree"
    
    // 滑动反馈状态
    var showSwipeFeedback_7ree by remember { mutableStateOf(false) }
    var swipeDirection_7ree by remember { mutableStateOf("") } // "up" 或 "down"
    var totalDragDistance_7ree by remember { mutableStateOf(0f) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                // 设置更高的z轴层级，确保手势检测不被其他组件遮挡
                shadowElevation = 1000f
            }
            // 添加一个透明的手势检测层，覆盖左侧区域
            .pointerInput(canNavigate) {
                if (canNavigate) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            // 检查触摸起始位置是否在左侧75%的区域内
                            val screenWidth_7ree = this.size.width
                            val leftAreaWidth_7ree = screenWidth_7ree * 0.75f
                            if (offset.x > leftAreaWidth_7ree) {
                                // 如果触摸位置在右侧25%区域，不处理手势
                                Log.d(TAG_7ree, "触摸位置在右侧区域，忽略手势: x=${offset.x}, 屏幕宽度=$screenWidth_7ree")
                                return@detectDragGestures
                            }
                            Log.d(TAG_7ree, "触摸位置在左侧区域，开始手势检测: x=${offset.x}, 屏幕宽度=$screenWidth_7ree")
                        },
                        onDragEnd = { 
                            Log.d(TAG_7ree, "手势检测结束，总滑动距离: $totalDragDistance_7ree")
                            // 在手指抬起时判断是否触发动作
                            if (showSwipeFeedback_7ree && swipeDirection_7ree.isNotEmpty() && abs(totalDragDistance_7ree) > 50f) {
                                Log.d(TAG_7ree, "手指抬起，触发单词切换: ${swipeDirection_7ree}")
                                if (swipeDirection_7ree == "down") {
                                    // 向下滑动，进入上一个单词
                                    Log.d(TAG_7ree, "向下滑动，切换到上一个单词")
                                    onNavigateToPrevious()
                                } else {
                                    // 向上滑动，进入下一个单词
                                    Log.d(TAG_7ree, "向上滑动，切换到下一个单词")
                                    onNavigateToNext()
                                }
                            }
                            // 手指抬起时重置状态
                            showSwipeFeedback_7ree = false
                            swipeDirection_7ree = ""
                            totalDragDistance_7ree = 0f
                        },
                        onDragCancel = { 
                            Log.d(TAG_7ree, "手势检测取消")
                            showSwipeFeedback_7ree = false
                            swipeDirection_7ree = ""
                            totalDragDistance_7ree = 0f
                        },
                        onDrag = { change, dragAmount ->
                            val (x, y) = dragAmount
                            totalDragDistance_7ree += y
                            Log.d(TAG_7ree, "检测到拖拽: x=$x, y=$y, 总距离: $totalDragDistance_7ree")
                            
                            // 只处理垂直滑动，忽略水平滑动
                            if (abs(y) > abs(x) && abs(totalDragDistance_7ree) > 15f) { // 降低触发阈值
                                // 当检测到有效的垂直滑动时，消费所有变化以防止滚动视图干扰
                                change.consumeAllChanges()
                                val newDirection = if (totalDragDistance_7ree > 0) "down" else "up"
                                
                                // 只有当方向改变时才更新状态，避免闪烁
                                if (swipeDirection_7ree != newDirection) {
                                    swipeDirection_7ree = newDirection
                                    Log.d(TAG_7ree, "滑动方向改变: $newDirection")
                                }
                                if (!showSwipeFeedback_7ree) {
                                    showSwipeFeedback_7ree = true
                                    Log.d(TAG_7ree, "显示滑动反馈")
                                }
                            } else {
                                // 如果滑动距离不够，隐藏反馈
                                if (showSwipeFeedback_7ree) {
                                    showSwipeFeedback_7ree = false
                                    swipeDirection_7ree = ""
                                    Log.d(TAG_7ree, "隐藏滑动反馈")
                                }
                            }
                        }
                    )
                } else {
                    Log.d(TAG_7ree, "无法导航: canNavigate=$canNavigate")
                }
            }
    ) {
        // 主要内容 - 设置较低的z轴层级
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    shadowElevation = 0f
                }
        ) {
            content()
        }
        
        // 灰色背景手势检测层 - 覆盖左侧区域，避开右侧按钮区域
        if (canNavigate) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f) // 只覆盖左侧75%的宽度，避开右侧25%的区域
                    .fillMaxHeight()
                    // .graphicsLayer {
                    //     shadowElevation = 1500f
                    // }
                    .background(androidx.compose.ui.graphics.Color.Transparent)
                    .pointerInput(canNavigate) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                // 检查触摸起始位置是否在左侧75%的区域内
                                val screenWidth_7ree = this.size.width
                                val leftAreaWidth_7ree = screenWidth_7ree * 0.75f
                                if (offset.x > leftAreaWidth_7ree) {
                                    // 如果触摸位置在右侧25%区域，不处理手势
                                    Log.d(TAG_7ree, "透明层触摸位置在右侧区域，忽略手势: x=${offset.x}, 屏幕宽度=$screenWidth_7ree")
                                    return@detectDragGestures
                                }
                                Log.d(TAG_7ree, "透明层触摸位置在左侧区域，开始手势检测: x=${offset.x}, 屏幕宽度=$screenWidth_7ree")
                            },
                            onDragEnd = { 
                                Log.d(TAG_7ree, "透明层手势检测结束，总滑动距离: $totalDragDistance_7ree")
                                // 在手指抬起时判断是否触发动作
                                if (showSwipeFeedback_7ree && swipeDirection_7ree.isNotEmpty() && abs(totalDragDistance_7ree) > 50f) {
                                    Log.d(TAG_7ree, "透明层手指抬起，触发单词切换: ${swipeDirection_7ree}")
                                    if (swipeDirection_7ree == "down") {
                                        // 向下滑动，进入上一个单词
                                        Log.d(TAG_7ree, "向下滑动，切换到上一个单词")
                                        onNavigateToPrevious()
                                    } else {
                                        // 向上滑动，进入下一个单词
                                        Log.d(TAG_7ree, "向上滑动，切换到下一个单词")
                                        onNavigateToNext()
                                    }
                                }
                                // 手指抬起时重置状态
                                showSwipeFeedback_7ree = false
                                swipeDirection_7ree = ""
                                totalDragDistance_7ree = 0f
                            },
                            onDragCancel = { 
                                Log.d(TAG_7ree, "透明层手势检测取消")
                                showSwipeFeedback_7ree = false
                                swipeDirection_7ree = ""
                                totalDragDistance_7ree = 0f
                            },
                            onDrag = { change, dragAmount ->
                                val (x, y) = dragAmount
                                totalDragDistance_7ree += y
                                Log.d(TAG_7ree, "透明层检测到拖拽: x=$x, y=$y, 总距离: $totalDragDistance_7ree")
                                
                                // 只处理垂直滑动，忽略水平滑动
                                if (abs(y) > abs(x) && abs(totalDragDistance_7ree) > 15f) { // 降低触发阈值
                                    // 当检测到有效的垂直滑动时，消费所有变化以防止滚动视图干扰
                                    change.consumeAllChanges()
                                    val newDirection = if (totalDragDistance_7ree > 0) "down" else "up"
                                    
                                    // 只有当方向改变时才更新状态，避免闪烁
                                    if (swipeDirection_7ree != newDirection) {
                                        swipeDirection_7ree = newDirection
                                        Log.d(TAG_7ree, "透明层滑动方向改变: $newDirection")
                                    }
                                    if (!showSwipeFeedback_7ree) {
                                        showSwipeFeedback_7ree = true
                                        Log.d(TAG_7ree, "透明层显示滑动反馈")
                                    }
                                } else {
                                    // 如果滑动距离不够，隐藏反馈
                                    if (showSwipeFeedback_7ree) {
                                        showSwipeFeedback_7ree = false
                                        swipeDirection_7ree = ""
                                        Log.d(TAG_7ree, "透明层隐藏滑动反馈")
                                    }
                                }
                            }
                        )
                    }
            ) {
                // 透明层，不显示任何内容
            }
        }
        
        // 滑动反馈箭头 - 设置最高的z轴层级
        if (showSwipeFeedback_7ree && swipeDirection_7ree.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .graphicsLayer {
                        shadowElevation = 2000f
                    },
                contentAlignment = if (swipeDirection_7ree == "up") Alignment.TopCenter else Alignment.BottomCenter
            ) {
                Icon(
                    imageVector = if (swipeDirection_7ree == "up") Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (swipeDirection_7ree == "up") "向上滑动" else "向下滑动",
                    modifier = Modifier
                        .size(180.dp) // 放大3倍：60dp * 3 = 180dp
                        .graphicsLayer {
                            // 取消阴影效果，移除shadowElevation
                            shape = CircleShape
                            clip = true
                        },
                    tint = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f) // 变成半透明
                )
            }
        }
    }
} 