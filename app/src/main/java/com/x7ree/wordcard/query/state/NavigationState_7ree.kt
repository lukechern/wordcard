package com.x7ree.wordcard.query.state

import com.x7ree.wordcard.data.WordEntity_7ree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 导航和屏幕状态管理
 */
class NavigationState_7ree {
    
    // 当前屏幕状态
    private val _currentScreen_7ree = MutableStateFlow("SEARCH")
    val currentScreen_7ree: StateFlow<String> = _currentScreen_7ree
    
    // 是否从单词本进入单词详情页面
    private val _isFromWordBook_7ree = MutableStateFlow(false)
    val isFromWordBook_7ree: StateFlow<Boolean> = _isFromWordBook_7ree
    
    // 单词本状态保存（用于返回时恢复状态）
    var savedWordBookScrollPosition_7ree = ScrollPosition_7ree()
    var savedWordBookFilterState_7ree = false

    // 单词列表导航相关
    private val _allWords_7ree = MutableStateFlow<List<WordEntity_7ree>>(emptyList())
    val allWords_7ree: StateFlow<List<WordEntity_7ree>> = _allWords_7ree
    private var currentWordIndex_7ree = -1

    fun updateCurrentScreen_7ree(screen: String) {
        _currentScreen_7ree.value = screen
        
        // 如果切换到非搜索页面，清除从单词本进入的标记
        if (screen != "SEARCH") {
            _isFromWordBook_7ree.value = false
        }
    }

    fun updateFromWordBook_7ree(fromWordBook: Boolean) {
        _isFromWordBook_7ree.value = fromWordBook
    }

    fun updateAllWords_7ree(words: List<WordEntity_7ree>) {
        _allWords_7ree.value = words
    }

    fun getCurrentWordIndex_7ree(currentWord: String): Int {
        return _allWords_7ree.value.indexOfFirst { it.word == currentWord }
    }

    fun canNavigate_7ree(currentWord: String): Boolean {
        return currentWord.isNotBlank() && _allWords_7ree.value.isNotEmpty()
    }

    fun getPreviousWord_7ree(currentWord: String): WordEntity_7ree? {
        val currentIndex = getCurrentWordIndex_7ree(currentWord)
        if (currentIndex == -1) return null
        
        val allWords = _allWords_7ree.value
        if (allWords.isEmpty()) return null
        
        val previousIndex = if (currentIndex == 0) allWords.size - 1 else currentIndex - 1
        return allWords[previousIndex]
    }

    fun getNextWord_7ree(currentWord: String): WordEntity_7ree? {
        val currentIndex = getCurrentWordIndex_7ree(currentWord)
        if (currentIndex == -1) return null
        
        val allWords = _allWords_7ree.value
        if (allWords.isEmpty()) return null
        
        val nextIndex = if (currentIndex == allWords.size - 1) 0 else currentIndex + 1
        return allWords[nextIndex]
    }

    fun returnToWordBook_7ree() {
        _currentScreen_7ree.value = "HISTORY"
        _isFromWordBook_7ree.value = false
    }
}