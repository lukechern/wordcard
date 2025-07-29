package com.x7ree.wordcard.article.viewmodel

import androidx.lifecycle.viewModelScope
import com.x7ree.wordcard.article.utils.ArticleDetailHelper_7ree
import com.x7ree.wordcard.data.ArticleEntity_7ree

class ArticleDetailHandler(private val state: ArticleState, private val articleDetailHelper: ArticleDetailHelper_7ree) {

    fun selectArticle(article: ArticleEntity_7ree, scope: kotlinx.coroutines.CoroutineScope, incrementViewCount: (Long) -> Unit) {
        val articlesToUse = if (state.usePaginationMode.value) {
            // This part needs to be adapted based on how pagination is handled
            emptyList()
        } else {
            state.articles.value
        }

        articleDetailHelper.handleArticleSelection(
            article,
            scope,
            { selectedArticle -> state._selectedArticle.value = selectedArticle },
            { stats -> state._keywordStats.value = stats },
            articlesToUse
        )

        state._showDetailScreen.value = true

        articleDetailHelper.handleArticleSelection(
            article,
            scope,
            { selectedArticle -> incrementViewCount(selectedArticle.id) },
            { stats -> },
            articlesToUse
        )
    }

    fun closeDetailScreen() {
        state._showDetailScreen.value = false
        state._selectedArticle.value = null
    }

    fun toggleSelectedArticleFavorite(toggleFavorite: (Long) -> Unit) {
        state.selectedArticle.value?.let { article ->
            toggleFavorite(article.id)
            state._selectedArticle.value = article.copy(isFavorite = !article.isFavorite)
        }
    }
}