package com.x7ree.wordcard.article.viewmodel

import androidx.lifecycle.viewModelScope
import com.x7ree.wordcard.article.utils.ArticleDetailHelper_7ree
import com.x7ree.wordcard.data.ArticleEntity_7ree
import kotlinx.coroutines.launch

class ArticleDetailHandler(
    private val state: ArticleState, 
    private val articleDetailHelper: ArticleDetailHelper_7ree,
    private val articleRepository: com.x7ree.wordcard.data.ArticleRepository_7ree? = null
) {

fun selectArticle(article: ArticleEntity_7ree, scope: kotlinx.coroutines.CoroutineScope, incrementViewCount: (Long) -> Unit) {
        // 设置从文章列表进入的标记
        state._isFromArticleList.value = true
        // 使用协程异步获取完整的文章列表用于统计
        scope.launch {
            val articlesToUse = try {
                if (articleRepository != null) {
                    // 直接从数据库获取所有文章用于准确统计
                    articleRepository.getAllArticlesSortedByTimeDesc(100, 0) // 获取最新100篇文章用于统计
                } else {
                    // 备用方案：使用状态中的文章数据
                    if (state.usePaginationMode.value) {
                        state.articles.value.ifEmpty { listOf(article) }
                    } else {
                        state.articles.value
                    }
                }
            } catch (e: Exception) {
                // 异常情况下至少包含当前文章
                listOf(article)
            }

            // 处理文章选择和统计
            articleDetailHelper.handleArticleSelection(
                article,
                scope,
                { selectedArticle -> state._selectedArticle.value = selectedArticle },
                { stats -> state._keywordStats.value = stats },
                articlesToUse
            )
            
            // 获取相关文章
            try {
                val relatedArticles = articleDetailHelper.getRelatedArticles(article, 5)
                state._relatedArticles.value = relatedArticles
            } catch (e: Exception) {
                // 发生错误时清空相关文章列表
                state._relatedArticles.value = emptyList()
            }

            // 增加浏览次数
            incrementViewCount(article.id)
        }

        // 立即显示详情页面
        state._showDetailScreen.value = true
    }

    fun closeDetailScreen() {
        state._showDetailScreen.value = false
        state._selectedArticle.value = null
        state._isFromArticleList.value = false
    }
    
    fun returnToArticleList() {
        state._showDetailScreen.value = false
        state._selectedArticle.value = null
        state._isFromArticleList.value = false
    }

fun toggleSelectedArticleFavorite(toggleFavorite: (Long) -> Unit) {
        state.selectedArticle.value?.let { article ->
            toggleFavorite(article.id)
            state._selectedArticle.value = article.copy(isFavorite = !article.isFavorite)
        }
    }
    
    /**
     * 获取相关文章
     */
    suspend fun getRelatedArticles(currentArticle: ArticleEntity_7ree, maxCount: Int = 5): List<ArticleEntity_7ree> {
        return articleDetailHelper.getRelatedArticles(currentArticle, maxCount)
    }
}
