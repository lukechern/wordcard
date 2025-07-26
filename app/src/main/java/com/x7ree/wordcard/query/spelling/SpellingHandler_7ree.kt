package com.x7ree.wordcard.query.spelling

import com.x7ree.wordcard.query.manager.DataManager_7ree
import com.x7ree.wordcard.query.state.WordQueryState_7ree

/**
 * 拼写练习功能模块
 */
class SpellingHandler_7ree(
    private val dataManager_7ree: DataManager_7ree,
    private val queryState_7ree: WordQueryState_7ree
) {
    
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
