package com.x7ree.wordcard.article.viewmodel

import com.x7ree.wordcard.article.ArticleMarkdownParser_7ree
import com.x7ree.wordcard.article.utils.ArticleTtsHelper_7ree
import com.x7ree.wordcard.data.ArticleEntity_7ree

class ArticleTtsHandler(private val state: ArticleState, private val articleTtsHelper: ArticleTtsHelper_7ree) {

    fun readArticle(article: ArticleEntity_7ree) {
        val parser = ArticleMarkdownParser_7ree()
        val cleanTitle = parser.cleanTextForTts(article.englishTitle)
        val cleanContent = parser.cleanTextForTts(article.englishContent)

        articleTtsHelper.readArticle(cleanContent, cleanTitle)
    }

    fun stopReading() {
        articleTtsHelper.stopReading()
    }

    fun toggleReading() {
        state.selectedArticle.value?.let { article ->
            val parser = ArticleMarkdownParser_7ree()
            val cleanTitle = parser.cleanTextForTts(article.englishTitle)
            val cleanContent = parser.cleanTextForTts(article.englishContent)

            articleTtsHelper.toggleReading(cleanContent, cleanTitle)
        }
    }

    fun clearTtsError() {
        articleTtsHelper.clearError()
    }

    fun release() {
        articleTtsHelper.release()
    }
}