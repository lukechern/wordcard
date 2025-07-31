package com.x7ree.wordcard.article.viewmodel

import androidx.lifecycle.viewModelScope
import com.x7ree.wordcard.article.utils.ArticlePaginationHandler_7ree
import kotlinx.coroutines.launch

class ArticleSearchHandler(private val state: ArticleState, private val articlePaginationHandler: ArticlePaginationHandler_7ree) {

    fun toggleSearchMode(isSearchMode: Boolean, scope: kotlinx.coroutines.CoroutineScope, loadInitialArticles: () -> Unit) {
        state._isSearchMode.value = isSearchMode
        if (!isSearchMode) {
            state._searchQuery.value = ""
            state._searchResults.value = emptyList()

            if (state.usePaginationMode.value) {
                scope.launch {
                    articlePaginationHandler.clearSearch()
                    loadInitialArticles()
                }
            }
        }
    }

    fun updateSearchQuery(query: String, scope: kotlinx.coroutines.CoroutineScope) {
        state._searchQuery.value = query
        if (query.isBlank()) {
            state._searchResults.value = emptyList()
            return
        }

        performSearch(query, scope)
    }

    private fun performSearch(query: String, scope: kotlinx.coroutines.CoroutineScope) {
        scope.launch {
            try {
                val searchQuery = query.trim()
                if (state.usePaginationMode.value) {
                    articlePaginationHandler.searchArticles(searchQuery, state.filterState.value)
                } else {
                    val searchQueryLower = searchQuery.lowercase()
                    val articlesToSearch = state.articles.value
                    val filteredArticles = articlesToSearch.filter { article ->
                        val titleMatch = article.englishTitle.lowercase().contains(searchQueryLower)
                        val translationMatch = article.titleTranslation.lowercase().contains(searchQueryLower)
                        // 改进关键词匹配逻辑，支持逗号分隔的关键词搜索
                        val keywordsMatch = article.keyWords.lowercase()
                            .split(",")
                            .map { it.trim() }
                            .any { it.contains(searchQueryLower) }
                        val contentMatch = article.englishContent.lowercase().contains(searchQueryLower)
                        val chineseContentMatch = article.chineseContent.lowercase().contains(searchQueryLower)
                        
                        titleMatch || translationMatch || keywordsMatch || contentMatch || chineseContentMatch
                    }
                    state._searchResults.value = filteredArticles
                }
            } catch (e: Exception) {
                state._operationResult.value = "搜索失败: ${e.message}"
            }
        }
    }

    fun clearSearchResults() {
        state._searchQuery.value = ""
        state._searchResults.value = emptyList()
        state._isSearchMode.value = false
    }
}