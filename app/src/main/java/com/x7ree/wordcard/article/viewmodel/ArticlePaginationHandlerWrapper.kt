package com.x7ree.wordcard.article.viewmodel

import androidx.lifecycle.viewModelScope
import com.x7ree.wordcard.article.utils.ArticlePaginationHandler_7ree
import kotlinx.coroutines.launch

class ArticlePaginationHandlerWrapper(private val state: ArticleState, private val articlePaginationHandler: ArticlePaginationHandler_7ree) {

    val pagedArticles = articlePaginationHandler.pagedArticles
    val isLoadingMore = articlePaginationHandler.isLoadingMore
    val hasMoreData = articlePaginationHandler.hasMoreData
    val isPaginationRefreshing = articlePaginationHandler.isRefreshing

    fun loadInitialArticles(scope: kotlinx.coroutines.CoroutineScope) {
        scope.launch {
            try {
                state._isLoading.value = true
                articlePaginationHandler.loadInitialArticles(state.filterState.value)
            } catch (e: Exception) {
                state._operationResult.value = "加载文章失败: ${e.message}"
            } finally {
                state._isLoading.value = false
            }
        }
    }

    fun loadMoreArticles(scope: kotlinx.coroutines.CoroutineScope) {
        scope.launch {
            try {
                if (articlePaginationHandler.isInSearchMode()) {
                    articlePaginationHandler.loadMoreSearchResults()
                } else {
                    articlePaginationHandler.loadMoreArticles()
                }
            } catch (e: Exception) {
                state._operationResult.value = "加载更多文章失败: ${e.message}"
            }
        }
    }

    fun paginationRefreshArticles(scope: kotlinx.coroutines.CoroutineScope) {
        scope.launch {
            try {
                articlePaginationHandler.setRefreshing(true)
                articlePaginationHandler.resetPagination()
                loadInitialArticles(scope)
                kotlinx.coroutines.delay(500)
            } catch (e: Exception) {
                state._operationResult.value = "刷新失败: ${e.message}"
            } finally {
                articlePaginationHandler.setRefreshing(false)
            }
        }
    }
}