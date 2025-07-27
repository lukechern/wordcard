package com.x7ree.wordcard.article.utils

import com.x7ree.wordcard.data.ArticleRepository_7ree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ArticleFavoriteHelper_7ree(
    private val articleRepository_7ree: ArticleRepository_7ree
) {
    /**
     * 切换文章收藏状态
     */
    fun toggleFavorite(
        articleId: Long,
        coroutineScope: CoroutineScope,
        onResult: (String) -> Unit
    ) {
        coroutineScope.launch {
            try {
                articleRepository_7ree.toggleFavorite_7ree(articleId)
                onResult("收藏状态已更新")
            } catch (e: Exception) {
                onResult("更新收藏状态失败: ${e.message}")
            }
        }
    }
}
