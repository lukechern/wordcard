package com.x7ree.wordcard.query.input

import com.x7ree.wordcard.query.manager.WordQueryManager_7ree

/**
 * 输入处理功能模块
 */
class InputHandler_7ree(
    private val wordQueryManager_7ree: WordQueryManager_7ree
) {
    
    /**
     * 处理单词输入变化
     */
    fun onWordInputChanged_7ree(newInput: String, wordInputUpdater: (String) -> Unit) {
        wordInputUpdater(newInput)
    }
    
    /**
     * 执行单词查询
     */
    fun queryWord_7ree() {
        wordQueryManager_7ree.queryWord_7ree()
    }
}
