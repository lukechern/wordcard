package com.x7ree.wordcard.article.utils

import com.x7ree.wordcard.data.ArticleRepository_7ree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ArticleDeleteHelper_7ree(
    private val articleRepository_7ree: ArticleRepository_7ree
) {
    /**
     * 删除文章
     */
    fun deleteArticle(
        articleId: Long,
        coroutineScope: CoroutineScope,
        onResult: (String) -> Unit
    ) {
        coroutineScope.launch {
            try {
                articleRepository_7ree.deleteArticle_7ree(articleId)
                onResult("文章已删除")
            } catch (e: Exception) {
                onResult("删除文章失败: ${e.message}")
            }
        }
    }
}
