package com.x7ree.wordcard.article.utils

import com.x7ree.wordcard.data.ArticleEntity_7ree
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
                // 先获取文章的当前状态
                val article = articleRepository_7ree.getArticle_7ree(articleId)
                val wasFavorite = article?.isFavorite ?: false
                
                // 切换收藏状态
                articleRepository_7ree.toggleFavorite_7ree(articleId)
                
                // 根据之前的状态显示相应的提示
                val message = if (wasFavorite) "已取消收藏" else "已添加收藏"
                onResult(message)
            } catch (e: Exception) {
                onResult("更新收藏状态失败: ${e.message}")
            }
        }
    }
}
