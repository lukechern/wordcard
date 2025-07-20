package com.x7ree.wordcard.ui.MainScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.state.ScrollPosition_7ree
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.PaginatedWordList_7ree
import com.x7ree.wordcard.ui.components.TtsButtonState_7ree
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onWordClick_7ree: (String) -> Unit
) {
    val pagedWords_7ree by wordQueryViewModel_7ree.pagedWords_7ree.collectAsState()
    val isLoadingMore_7ree by wordQueryViewModel_7ree.isLoadingMore_7ree.collectAsState()
    val hasMoreData_7ree by wordQueryViewModel_7ree.hasMoreData_7ree.collectAsState()
    val showFavoritesOnly_7ree by wordQueryViewModel_7ree.showFavoritesOnly_7ree.collectAsState()
    val isSpeaking_7ree = wordQueryViewModel_7ree.isSpeaking_7ree
    val isSpeakingWord_7ree = wordQueryViewModel_7ree.isSpeakingWord_7ree
    
    var deletedWords_7ree by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isInitialLoading_7ree by remember { mutableStateOf(true) }
    var isRefreshing_7ree by remember { mutableStateOf(false) }
    var currentSpeakingWord_7ree by remember { mutableStateOf("") }
    var ttsState_7ree by remember { mutableStateOf(TtsButtonState_7ree.IDLE) }
    val coroutineScope = rememberCoroutineScope()
    
    // 监听TTS状态变化 - 简化的状态管理，避免重复触发
    LaunchedEffect(isSpeakingWord_7ree) {
        if (isSpeakingWord_7ree && currentSpeakingWord_7ree.isNotEmpty()) {
            // 延迟一下确保载入状态能被看到，然后切换到播放状态
            delay(600) // 确保载入图标显示足够长时间
            if (isSpeakingWord_7ree && currentSpeakingWord_7ree.isNotEmpty()) {
                ttsState_7ree = TtsButtonState_7ree.PLAYING
            }
        }
    }
    
    // 监听播放结束 - 移除currentSpeakingWord_7ree依赖，避免重复触发
    LaunchedEffect(isSpeaking_7ree, isSpeakingWord_7ree) {
        // 当TTS完全停止时，立即恢复到默认状态
        if (!isSpeaking_7ree && !isSpeakingWord_7ree && currentSpeakingWord_7ree.isNotEmpty()) {
            // 立即重置状态，不要延迟
            currentSpeakingWord_7ree = ""
            ttsState_7ree = TtsButtonState_7ree.IDLE
        }
    }
    
    // 创建或获取保存的滚动状态
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = wordQueryViewModel_7ree.savedWordBookScrollPosition_7ree.firstVisibleItemIndex,
        initialFirstVisibleItemScrollOffset = wordQueryViewModel_7ree.savedWordBookScrollPosition_7ree.firstVisibleItemScrollOffset
    )
    
    // 保存滚动位置
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        wordQueryViewModel_7ree.savedWordBookScrollPosition_7ree = wordQueryViewModel_7ree.savedWordBookScrollPosition_7ree.copy(
            firstVisibleItemIndex = listState.firstVisibleItemIndex,
            firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset
        )
    }
    
    // 恢复滚动位置（当从单词详情页返回时）
    val isFromWordBook_7ree by wordQueryViewModel_7ree.isFromWordBook_7ree.collectAsState()
    LaunchedEffect(pagedWords_7ree.size, isFromWordBook_7ree) {
        if (pagedWords_7ree.isNotEmpty() && 
            !isFromWordBook_7ree && // 只有在不是从单词本进入时才恢复位置（即刚从单词详情页返回）
            wordQueryViewModel_7ree.savedWordBookScrollPosition_7ree.firstVisibleItemIndex > 0) {
            try {
                delay(100) // 稍微延迟以确保列表已渲染
                listState.scrollToItem(
                    index = wordQueryViewModel_7ree.savedWordBookScrollPosition_7ree.firstVisibleItemIndex,
                    scrollOffset = wordQueryViewModel_7ree.savedWordBookScrollPosition_7ree.firstVisibleItemScrollOffset
                )
                // 恢复后重置滚动位置，避免重复恢复
                wordQueryViewModel_7ree.savedWordBookScrollPosition_7ree = ScrollPosition_7ree()
            } catch (e: Exception) {
                // 如果滚动失败，忽略错误
            }
        }
    }
    
    // 只过滤掉已删除的单词，收藏过滤由ViewModel在数据库层面处理
    val filteredWords_7ree = remember(pagedWords_7ree, deletedWords_7ree) {
        pagedWords_7ree.filter { it.word !in deletedWords_7ree }
    }
    
    // 下拉刷新逻辑
    val handleRefresh: () -> Unit = {
        coroutineScope.launch {
            isRefreshing_7ree = true
            try {
                // 重置分页状态并重新加载数据
                wordQueryViewModel_7ree.resetPagination_7ree()
                wordQueryViewModel_7ree.loadInitialWords_7ree()
                // 重新加载单词计数
                wordQueryViewModel_7ree.loadWordCount_7ree()
                // 清空已删除单词集合
                deletedWords_7ree = emptySet()
                // 稍微延迟以显示刷新动画
                delay(500)
            } finally {
                isRefreshing_7ree = false
            }
        }
    }
    
    // 初始加载
    LaunchedEffect(Unit) {
        // 只有在没有数据时才重置分页状态并加载初始数据
        if (pagedWords_7ree.isEmpty()) {
            wordQueryViewModel_7ree.resetPagination_7ree()
            wordQueryViewModel_7ree.loadInitialWords_7ree()
        }
        
        // 按需加载单词计数
        wordQueryViewModel_7ree.loadWordCount_7ree()
        
        // 等待初始数据加载完成
        delay(500)
        isInitialLoading_7ree = false
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 标题行，包含标题和收藏过滤按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "单词本",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = { wordQueryViewModel_7ree.toggleFavoriteFilter_7ree() }
            ) {
                Icon(
                    imageVector = if (showFavoritesOnly_7ree) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (showFavoritesOnly_7ree) "显示全部单词" else "只显示收藏单词",
                    tint = if (showFavoritesOnly_7ree) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (isInitialLoading_7ree) {
            // 显示初始加载状态
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "单词本加载中...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (filteredWords_7ree.isEmpty() && !hasMoreData_7ree) {
            // 显示空状态
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (showFavoritesOnly_7ree) "暂无收藏的单词" else "单词本暂无记录",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            PaginatedWordList_7ree(
                words = filteredWords_7ree,
                isLoadingMore = isLoadingMore_7ree,
                hasMoreData = hasMoreData_7ree,
                onWordClick = onWordClick_7ree,
                onFavoriteToggle = { entity ->
                    wordQueryViewModel_7ree.setFavorite_7ree(entity.word, !entity.isFavorite)
                    // 如果当前在收藏过滤模式下，需要重新加载数据
                    if (showFavoritesOnly_7ree) {
                        wordQueryViewModel_7ree.resetPagination_7ree()
                        wordQueryViewModel_7ree.loadInitialWords_7ree()
                    }
                },
                onWordDelete = { wordEntity_7ree ->
                    // 立即添加到删除集合，从UI中移除
                    deletedWords_7ree = deletedWords_7ree + wordEntity_7ree.word
                    // 执行实际的删除操作
                    wordQueryViewModel_7ree.deleteWord_7ree(wordEntity_7ree.word)
                    // 重新加载数据以保持分页正确性
                    wordQueryViewModel_7ree.resetPagination_7ree()
                    wordQueryViewModel_7ree.loadInitialWords_7ree()
                },
                onLoadMore = {
                    wordQueryViewModel_7ree.loadMoreWords_7ree()
                },
                onWordSpeak = { word ->
                    // 防止重复点击
                    if (currentSpeakingWord_7ree.isEmpty() || currentSpeakingWord_7ree != word) {
                        currentSpeakingWord_7ree = word
                        // 立即设置为加载状态
                        ttsState_7ree = TtsButtonState_7ree.LOADING
                        
                        // 调用TTS播放
                        wordQueryViewModel_7ree.speakWord_7ree(word)
                    }
                },
                onWordStopSpeak = {
                    wordQueryViewModel_7ree.stopSpeaking_7ree()
                    currentSpeakingWord_7ree = ""
                    ttsState_7ree = TtsButtonState_7ree.IDLE
                },
                ttsState = ttsState_7ree,
                currentSpeakingWord = currentSpeakingWord_7ree,
                listState = listState,
                isRefreshing = isRefreshing_7ree,
                onRefresh = handleRefresh
            )
        }
    }
}