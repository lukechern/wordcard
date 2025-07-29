package com.x7ree.wordcard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * 瀑布流网格布局组件
 * 实现真正的瀑布流效果，卡片会向上靠紧最短的列
 */
@Composable
fun <T> StaggeredGrid_7ree(
    items: List<T>,
    columns: Int = 2,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    Layout(
        content = {
            items.forEach { item ->
                content(item)
            }
        },
        modifier = modifier
    ) { measurables, constraints ->
        // 计算每列的宽度
        val columnWidth = (constraints.maxWidth - (horizontalSpacing.roundToPx() * (columns - 1))) / columns
        
        // 为每个子组件创建约束
        val itemConstraints = constraints.copy(
            minWidth = columnWidth,
            maxWidth = columnWidth
        )
        
        // 测量所有子组件
        val placeables = measurables.map { measurable ->
            measurable.measure(itemConstraints)
        }
        
        // 计算每列的当前高度
        val columnHeights = IntArray(columns) { 0 }
        
        // 为每个子组件分配位置
        val itemPositions = mutableListOf<Pair<Int, Int>>() // x, y
        
        placeables.forEachIndexed { index, placeable ->
            // 找到最短的列
            val shortestColumnIndex = columnHeights.indices.minByOrNull { columnHeights[it] } ?: 0
            
            // 计算x位置
            val x = shortestColumnIndex * (columnWidth + horizontalSpacing.roundToPx())
            
            // 计算y位置
            val y = columnHeights[shortestColumnIndex]
            
            // 记录位置
            itemPositions.add(Pair(x, y))
            
            // 更新列高度
            columnHeights[shortestColumnIndex] += placeable.height + verticalSpacing.roundToPx()
        }
        
        // 计算总高度
        val totalHeight = columnHeights.maxOrNull() ?: 0
        
        layout(constraints.maxWidth, totalHeight) {
            placeables.forEachIndexed { index, placeable ->
                val (x, y) = itemPositions[index]
                placeable.placeRelative(x, y)
            }
        }
    }
}

/**
 * 支持LazyColumn的瀑布流组件
 * 用于大量数据的高性能显示
 */
@Composable
fun <T> LazyStaggeredGrid_7ree(
    items: List<T>,
    columns: Int = 2,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    content: @Composable (T) -> Unit
) {
    // 将items分组到不同的列中，实现瀑布流效果
    val columnItems = remember(items) {
        val columnLists = Array(columns) { mutableListOf<T>() }
        val columnHeights = IntArray(columns) { 0 }
        
        items.forEach { item ->
            // 找到最短的列
            val shortestColumnIndex = columnHeights.indices.minByOrNull { columnHeights[it] } ?: 0
            
            // 将item添加到最短的列
            columnLists[shortestColumnIndex].add(item)
            
            // 估算高度增加（这里使用固定值，实际应用中可以根据内容动态计算）
            columnHeights[shortestColumnIndex] += 200 // 估算的卡片高度
        }
        
        columnLists.map { it.toList() }
    }
    
    LazyColumn(
        state = state,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        // 计算最大行数
        val maxRows = columnItems.maxOfOrNull { it.size } ?: 0
        
        items(maxRows) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
            ) {
                repeat(columns) { columnIndex ->
                    Box(modifier = Modifier.weight(1f)) {
                        if (rowIndex < columnItems[columnIndex].size) {
                            content(columnItems[columnIndex][rowIndex])
                        }
                    }
                }
            }
        }
    }
}