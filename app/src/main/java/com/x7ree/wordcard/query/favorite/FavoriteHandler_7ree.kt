package com.x7ree.wordcard.query.favorite

import android.net.Uri
import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.query.manager.DataManager_7ree
import com.x7ree.wordcard.query.state.WordQueryState_7ree

/**
 * 收藏和单词管理功能模块
 */
class FavoriteHandler_7ree(
    private val dataManager_7ree: DataManager_7ree,
    private val wordRepository_7ree: WordRepository_7ree,
    private val queryState_7ree: WordQueryState_7ree
) {
    
    /**
     * 切换收藏状态
     */
    fun toggleFavorite_7ree(wordInput: String) {
        if (wordInput.isNotBlank()) {
            dataManager_7ree.toggleFavorite_7ree(wordInput)
        }
    }
    
    /**
     * 设置当前输入单词的收藏状态
     */
    fun setFavoriteForCurrentWord_7ree(wordInput: String, isFavorite: Boolean) {
        if (wordInput.isNotBlank()) {
            dataManager_7ree.setFavorite_7ree(wordInput, isFavorite)
        }
    }
    
    /**
     * 设置指定单词的收藏状态
     */
    fun setFavoriteForWord_7ree(word: String, isFavorite: Boolean) {
        dataManager_7ree.setFavorite_7ree(word, isFavorite)
    }
    
    /**
     * 删除单词
     */
    fun deleteWord_7ree(word: String) {
        dataManager_7ree.deleteWord_7ree(word)
    }
    
    /**
     * 获取历史单词
     */
    fun getHistoryWords_7ree() = wordRepository_7ree.getAllWords_7ree()
    
    /**
     * 处理拼写练习成功
     */
    fun onSpellingSuccess_7ree(wordInput: String) {
        if (wordInput.isNotBlank()) {
            dataManager_7ree.onSpellingSuccess_7ree(wordInput)
        }
    }
    
    /**
     * 获取当前拼写次数
     */
    fun getCurrentSpellingCount_7ree(): Int {
        return queryState_7ree.currentWordInfo_7ree?.spellingCount ?: 0
    }
}
