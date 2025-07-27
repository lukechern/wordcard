package com.x7ree.wordcard.article.utils

import android.content.Context
import com.x7ree.wordcard.article.ArticleTtsManager_7ree

class ArticleTtsHelper_7ree(context: Context) {
    private val articleTtsManager_7ree = ArticleTtsManager_7ree(context)
    
    val isReading = articleTtsManager_7ree.isReading
    val isInitializing = articleTtsManager_7ree.isInitializing
    val errorMessage = articleTtsManager_7ree.errorMessage
    val buttonState = articleTtsManager_7ree.buttonState
    val currentEngine = articleTtsManager_7ree.currentEngine
    
    /**
     * 朗读文章
     */
    fun readArticle(englishContent: String, englishTitle: String) {
        articleTtsManager_7ree.readArticle(
            englishContent = englishContent,
            englishTitle = englishTitle
        )
    }
    
    /**
     * 停止朗读
     */
    fun stopReading() {
        articleTtsManager_7ree.stopReading()
    }
    
    /**
     * 切换朗读状态（朗读/停止）
     */
    fun toggleReading(englishContent: String, englishTitle: String) {
        articleTtsManager_7ree.toggleReading(
            englishContent = englishContent,
            englishTitle = englishTitle
        )
    }
    
    /**
     * 清除TTS错误信息
     */
    fun clearError() {
        articleTtsManager_7ree.clearError()
    }
    
    /**
     * 释放资源
     */
    fun release() {
        articleTtsManager_7ree.release()
    }
}
