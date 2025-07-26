package com.x7ree.wordcard.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.query.state.ScrollPosition_7ree
import kotlinx.coroutines.delay

@Composable
fun ScrollPositionManager_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    pagedWords_7ree: List<com.x7ree.wordcard.data.WordEntity_7ree>,
    listState: LazyListState,
    onSavedScrollPositionChange: (ScrollPosition_7ree) -> Unit
) {
    val isFromWordBook_7ree by wordQueryViewModel_7ree.isFromWordBook_7ree.collectAsState()
    val savedWordBookScrollPosition_7ree = wordQueryViewModel_7ree.savedWordBookScrollPosition_7ree
    
    // 保存滚动位置
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        val newPosition = savedWordBookScrollPosition_7ree.copy(
            firstVisibleItemIndex = listState.firstVisibleItemIndex,
            firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset
        )
        onSavedScrollPositionChange(newPosition)
    }
    
    // 恢复滚动位置（当从单词详情页返回时）
    LaunchedEffect(pagedWords_7ree.size, isFromWordBook_7ree) {
        if (pagedWords_7ree.isNotEmpty() && 
            !isFromWordBook_7ree && // 只有在不是从单词本进入时才恢复位置（即刚从单词详情页返回）
            savedWordBookScrollPosition_7ree.firstVisibleItemIndex > 0) {
            try {
                delay(100) // 稍微延迟以确保列表已渲染
                listState.scrollToItem(
                    index = savedWordBookScrollPosition_7ree.firstVisibleItemIndex,
                    scrollOffset = savedWordBookScrollPosition_7ree.firstVisibleItemScrollOffset
                )
                // 恢复后重置滚动位置，避免重复恢复
                onSavedScrollPositionChange(ScrollPosition_7ree())
            } catch (e: Exception) {
                // 如果滚动失败，忽略错误
            }
        }
    }
}
