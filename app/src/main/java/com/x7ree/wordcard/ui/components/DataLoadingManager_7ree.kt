package com.x7ree.wordcard.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.data.WordEntity_7ree
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DataLoadingManager_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    pagedWords_7ree: List<WordEntity_7ree>,
    deletedWords_7ree: Set<String>,
    onDeletedWordsChange: (Set<String>) -> Unit,
    onInitialLoadingChange: (Boolean) -> Unit,
    onRefreshingChange: (Boolean) -> Unit,
    onFilteredWordsChange: (List<WordEntity_7ree>) -> Unit,
    onHandleRefreshChange: (() -> Unit) -> Unit
) {
    var isInitialLoading_7ree by remember { mutableStateOf(true) }
    var isRefreshing_7ree by remember { mutableStateOf(false) }
    var deletedWordsInternal by remember { mutableStateOf(deletedWords_7ree) }
    val coroutineScope = rememberCoroutineScope()
    
    // 只过滤掉已删除的单词，收藏过滤由ViewModel在数据库层面处理
    val filteredWords_7ree = remember(pagedWords_7ree, deletedWordsInternal) {
        pagedWords_7ree.filter { it.word !in deletedWordsInternal }
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
                deletedWordsInternal = emptySet()
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
    
    // 更新外部状态
    LaunchedEffect(isInitialLoading_7ree) {
        onInitialLoadingChange(isInitialLoading_7ree)
    }
    
    // 更新外部刷新状态
    LaunchedEffect(isRefreshing_7ree) {
        onRefreshingChange(isRefreshing_7ree)
    }
    
    // 更新外部删除单词状态
    LaunchedEffect(deletedWordsInternal) {
        onDeletedWordsChange(deletedWordsInternal)
    }
    
    // 更新外部过滤单词状态
    LaunchedEffect(filteredWords_7ree) {
        onFilteredWordsChange(filteredWords_7ree)
    }
    
    // 更新外部刷新处理函数
    LaunchedEffect(handleRefresh) {
        onHandleRefreshChange(handleRefresh)
    }
}
