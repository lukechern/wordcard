package com.x7ree.wordcard.query.navigation

import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.query.manager.WordQueryManager_7ree
import com.x7ree.wordcard.query.state.NavigationState_7ree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 导航功能处理模块
 */
class NavigationHandler_7ree(
    private val wordRepository_7ree: WordRepository_7ree,
    private val wordQueryManager_7ree: WordQueryManager_7ree,
    private val navigationState_7ree: NavigationState_7ree,
    private val coroutineScope: CoroutineScope
) {
    
    /**
     * 确保单词列表已加载
     */
    fun ensureWordsLoaded_7ree() {
        loadAllWords_7ree()
    }
    
    /**
     * 加载所有单词列表
     */
    private fun loadAllWords_7ree() {
        if (navigationState_7ree.allWords_7ree.value.isNotEmpty()) {
            return
        }
        
        // println("DEBUG: 开始加载所有单词列表")
        
        coroutineScope.launch {
            try {
                wordRepository_7ree.getAllWords_7ree().collect { words ->
                    navigationState_7ree.updateAllWords_7ree(words)
                    // println("DEBUG: 加载所有单词列表完成，共${words.size}个单词")
                }
            } catch (e: Exception) {
                // println("DEBUG: 加载单词列表失败: ${e.message}")
            }
        }
    }
    
    /**
     * 导航到上一个单词
     */
    fun navigateToPreviousWord_7ree(wordInput: String) {
        // println("DEBUG: navigateToPreviousWord_7ree - 开始切换到上一个单词")
        
        // 确保单词列表已加载
        ensureWordsLoaded_7ree()
        
        val previousWord = navigationState_7ree.getPreviousWord_7ree(wordInput)
        if (previousWord != null) {
            // println("DEBUG: 切换到上一个单词: ${previousWord.word}")
            loadWordFromHistory_7ree(previousWord.word)
        } else {
            // println("DEBUG: 无法导航到上一个单词")
        }
    }
    
    /**
     * 导航到下一个单词
     */
    fun navigateToNextWord_7ree(wordInput: String) {
        // println("DEBUG: navigateToNextWord_7ree - 开始切换到下一个单词")
        
        // 确保单词列表已加载
        ensureWordsLoaded_7ree()
        
        val nextWord = navigationState_7ree.getNextWord_7ree(wordInput)
        if (nextWord != null) {
            // println("DEBUG: 切换到下一个单词: ${nextWord.word}")
            loadWordFromHistory_7ree(nextWord.word)
        } else {
            // println("DEBUG: 无法导航到下一个单词")
        }
    }
    
    /**
     * 检查是否可以导航
     */
    fun canNavigate_7ree(wordInput: String): Boolean {
        // 确保单词列表已加载
        ensureWordsLoaded_7ree()
        
        val canNavigate = navigationState_7ree.canNavigate_7ree(wordInput)
        // println("DEBUG: canNavigate_7ree - wordInput='$wordInput', canNavigate=$canNavigate")
        return canNavigate
    }
    
    /**
     * 从历史记录加载单词
     */
    private fun loadWordFromHistory_7ree(word: String) {
        // 标记为从单词本进入
        navigationState_7ree.updateFromWordBook_7ree(true)
        wordQueryManager_7ree.loadWordFromHistory_7ree(word)
    }
    
    /**
     * 设置当前屏幕
     */
    fun setCurrentScreen_7ree(screen: String) {
        navigationState_7ree.updateCurrentScreen_7ree(screen)
    }
    
    /**
     * 返回单词本
     */
    fun returnToWordBook_7ree() {
        navigationState_7ree.returnToWordBook_7ree()
    }
}
