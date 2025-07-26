package com.x7ree.wordcard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 自定义垂直滚动条组件
 * 与帮助页面的滚动条样式保持一致
 * 
 * @param listState LazyList的滚动状态
 * @param modifier 修饰符
 * @param trackColor 滚动轨道颜色
 * @param thumbColor 滚动拇指颜色
 * @param trackWidth 滚动轨道宽度
 * @param cornerRadius 圆角半径
 */
@Composable
fun CustomScrollbar_7ree(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    trackColor: Color = Color.Gray.copy(alpha = 0.11f),
    thumbColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
    trackWidth: androidx.compose.ui.unit.Dp = 12.dp,
    cornerRadius: androidx.compose.ui.unit.Dp = 3.dp
) {
    // 只有在有内容可滚动时才显示滚动条
    if (listState.layoutInfo.totalItemsCount > 0 && 
        listState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
        
        val firstVisibleItem = listState.layoutInfo.visibleItemsInfo.firstOrNull()
        val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
        
        if (firstVisibleItem != null && lastVisibleItem != null) {
            // 计算可见区域的相关参数
            val totalItems = listState.layoutInfo.totalItemsCount
            val visibleItems = listState.layoutInfo.visibleItemsInfo.size
            val firstVisibleIndex = firstVisibleItem.index
            val lastVisibleIndex = lastVisibleItem.index
            
            // 计算滚动进度和拇指大小
            val scrollProgress = if (totalItems > visibleItems) {
                firstVisibleIndex.toFloat() / (totalItems - visibleItems).toFloat()
            } else 0f
            
            val thumbSizeRatio = (visibleItems.toFloat() / totalItems.toFloat()).coerceIn(0.1f, 1f)
            
            // 只有在需要滚动时才显示滚动条
            if (thumbSizeRatio < 1f) {
                BoxWithConstraints(
                    modifier = modifier
                        .fillMaxHeight()
                        .width(trackWidth)
                        .padding(end = 2.dp, top = 8.dp, bottom = 8.dp)
                        .background(
                            color = trackColor,
                            shape = RoundedCornerShape(cornerRadius)
                        )
                ) {
                    val trackHeight = maxHeight
                    val thumbHeight = trackHeight * thumbSizeRatio
                    val availableSpace = trackHeight - thumbHeight
                    val thumbOffset = availableSpace * scrollProgress.coerceIn(0f, 1f)
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(thumbHeight)
                            .offset(y = thumbOffset)
                            .background(
                                color = thumbColor,
                                shape = RoundedCornerShape(cornerRadius)
                            )
                    )
                }
            }
        }
    }
}

/**
 * 为普通滚动状态设计的滚动条组件
 * 适用于使用 rememberScrollState() 的场景
 * 
 * @param scrollState 滚动状态
 * @param modifier 修饰符
 * @param trackColor 滚动轨道颜色
 * @param thumbColor 滚动拇指颜色
 * @param trackWidth 滚动轨道宽度
 * @param cornerRadius 圆角半径
 */
@Composable
fun CustomScrollbarForScrollState_7ree(
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier = Modifier,
    trackColor: Color = Color.Gray.copy(alpha = 0.11f),
    thumbColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
    trackWidth: androidx.compose.ui.unit.Dp = 12.dp,
    cornerRadius: androidx.compose.ui.unit.Dp = 3.dp
) {
    // 只有在有内容可滚动时才显示滚动条
    if (scrollState.maxValue > 0) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxHeight()
                .width(trackWidth)
                .padding(end = 2.dp, top = 8.dp, bottom = 8.dp)
                .background(
                    color = trackColor,
                    shape = RoundedCornerShape(cornerRadius)
                )
        ) {
            val trackHeight = maxHeight
            
            // 计算可见区域与总内容的比例
            val viewportHeight = scrollState.viewportSize.toFloat()
            val contentHeight = scrollState.maxValue.toFloat() + viewportHeight
            val thumbHeightRatio = (viewportHeight / contentHeight).coerceIn(0.1f, 1f)
            
            // 计算滚动进度
            val scrollProgress = if (scrollState.maxValue > 0) {
                scrollState.value.toFloat() / scrollState.maxValue.toFloat()
            } else 0f
            
            // 计算拇指位置 - 基于实际轨道高度
            val thumbHeight = trackHeight * thumbHeightRatio
            val availableSpace = trackHeight - thumbHeight
            val thumbOffset = availableSpace * scrollProgress
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(thumbHeight)
                    .offset(y = thumbOffset)
                    .background(
                        color = thumbColor,
                        shape = RoundedCornerShape(cornerRadius)
                    )
            )
        }
    }
}