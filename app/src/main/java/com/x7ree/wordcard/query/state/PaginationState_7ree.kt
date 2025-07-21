package com.x7ree.wordcard.query.state

import com.x7ree.wordcard.data.WordEntity_7ree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 分页加载状态管理
 */
class PaginationState_7ree {
    
    // 分页加载相关状态
    private val _pagedWords_7ree = MutableStateFlow<List<WordEntity_7ree>>(emptyList())
    val pagedWords_7ree: StateFlow<List<WordEntity_7ree>> = _pagedWords_7ree
    
    private val _isLoadingMore_7ree = MutableStateFlow(false)
    val isLoadingMore_7ree: StateFlow<Boolean> = _isLoadingMore_7ree
    
    private val _hasMoreData_7ree = MutableStateFlow(true)
    val hasMoreData_7ree: StateFlow<Boolean> = _hasMoreData_7ree
    
    // 收藏过滤状态
    private val _showFavoritesOnly_7ree = MutableStateFlow(false)
    val showFavoritesOnly_7ree: StateFlow<Boolean> = _showFavoritesOnly_7ree
    
    // 搜索状态
    private val _searchQuery_7ree = MutableStateFlow("")
    val searchQuery_7ree: StateFlow<String> = _searchQuery_7ree
    
    private val _isSearchMode_7ree = MutableStateFlow(false)
    val isSearchMode_7ree: StateFlow<Boolean> = _isSearchMode_7ree
    
    var currentPage_7ree = 0
        private set
    val pageSize_7ree = 10 // 每页10个项目

    // 统计数据状态
    private val _wordCount_7ree = MutableStateFlow(0)
    val wordCount_7ree: StateFlow<Int> = _wordCount_7ree
    
    private val _totalViews_7ree = MutableStateFlow(0)
    val totalViews_7ree: StateFlow<Int> = _totalViews_7ree
    
    private val _exportPath_7ree = MutableStateFlow("")
    val exportPath_7ree: StateFlow<String> = _exportPath_7ree

    fun updatePagedWords_7ree(words: List<WordEntity_7ree>) {
        _pagedWords_7ree.value = words
    }

    fun addMoreWords_7ree(newWords: List<WordEntity_7ree>) {
        val currentWords = _pagedWords_7ree.value.toMutableList()
        currentWords.addAll(newWords)
        _pagedWords_7ree.value = currentWords
    }

    fun removeWord_7ree(word: String) {
        val currentWords = _pagedWords_7ree.value.toMutableList()
        currentWords.removeAll { it.word == word }
        _pagedWords_7ree.value = currentWords
    }

    fun addNewWordToTop_7ree(newWord: WordEntity_7ree) {
        val currentWords = _pagedWords_7ree.value.toMutableList()
        val existingIndex = currentWords.indexOfFirst { it.word == newWord.word }
        if (existingIndex >= 0) {
            currentWords[existingIndex] = newWord
        } else {
            currentWords.add(0, newWord)
        }
        _pagedWords_7ree.value = currentWords
    }

    fun updateLoadingMore_7ree(loading: Boolean) {
        _isLoadingMore_7ree.value = loading
    }

    fun updateHasMoreData_7ree(hasMore: Boolean) {
        _hasMoreData_7ree.value = hasMore
    }

    fun updateShowFavoritesOnly_7ree(showFavoritesOnly: Boolean) {
        _showFavoritesOnly_7ree.value = showFavoritesOnly
    }

    fun toggleFavoriteFilter_7ree() {
        _showFavoritesOnly_7ree.value = !_showFavoritesOnly_7ree.value
    }

    fun incrementPage_7ree() {
        currentPage_7ree++
    }

    fun resetPagination_7ree() {
        currentPage_7ree = 0
        _pagedWords_7ree.value = emptyList()
        _hasMoreData_7ree.value = true
        _isLoadingMore_7ree.value = false
    }

    fun updateWordCount_7ree(count: Int) {
        _wordCount_7ree.value = count
    }

    fun updateTotalViews_7ree(totalViews: Int) {
        _totalViews_7ree.value = totalViews
    }

    fun updateExportPath_7ree(path: String) {
        _exportPath_7ree.value = path
    }

    fun updateSearchQuery_7ree(query: String) {
        _searchQuery_7ree.value = query
    }

    fun updateSearchMode_7ree(isSearchMode: Boolean) {
        _isSearchMode_7ree.value = isSearchMode
    }

    fun toggleSearchMode_7ree() {
        _isSearchMode_7ree.value = !_isSearchMode_7ree.value
    }

    fun clearSearch_7ree() {
        _searchQuery_7ree.value = ""
        _isSearchMode_7ree.value = false
    }
}