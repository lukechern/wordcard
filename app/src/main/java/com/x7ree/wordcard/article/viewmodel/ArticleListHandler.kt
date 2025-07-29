package com.x7ree.wordcard.article.viewmodel

import androidx.lifecycle.viewModelScope
import com.x7ree.wordcard.article.utils.ArticleListHelper_7ree
import com.x7ree.wordcard.article.utils.ArticleSortType_7ree
import kotlinx.coroutines.launch

class ArticleListHandler(private val state: ArticleState, private val articleListHelper: ArticleListHelper_7ree) {

    fun initializeArticleFlow(scope: kotlinx.coroutines.CoroutineScope) {
        articleListHelper.initializeArticleFlow(
            scope,
            { loading -> state._isLoading.value = loading },
            { articles ->
                state._rawArticles.value = articles
                applyFilterAndSort()
            },
            { error -> state._operationResult.value = error }
        )
    }

    fun applyFilterAndSort() {
        val rawArticles = state._rawArticles.value
        val filterState = state.filterState.value

        var filteredArticles = rawArticles
        if (filterState.showFavoritesOnly) {
            filteredArticles = filteredArticles.filter { it.isFavorite }
        }

        val sortedArticles = when (filterState.sortType) {
            ArticleSortType_7ree.PUBLISH_TIME_ASC -> filteredArticles.sortedBy { it.generationTimestamp }
            ArticleSortType_7ree.PUBLISH_TIME_DESC -> filteredArticles.sortedByDescending { it.generationTimestamp }
            ArticleSortType_7ree.VIEW_COUNT_ASC -> filteredArticles.sortedBy { it.viewCount }
            ArticleSortType_7ree.VIEW_COUNT_DESC -> filteredArticles.sortedByDescending { it.viewCount }
            null -> filteredArticles.sortedByDescending { it.generationTimestamp }
        }

        state._articles.value = sortedArticles
    }

    fun incrementViewCount(articleId: Long, scope: kotlinx.coroutines.CoroutineScope) {
        articleListHelper.incrementViewCount(articleId, scope)
    }
}