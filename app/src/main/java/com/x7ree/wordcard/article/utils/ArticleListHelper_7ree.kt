package com.x7ree.wordcard.article.utils

import com.x7ree.wordcard.data.ArticleRepository_7ree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ArticleListHelper_7ree(
    private val articleRepository_7ree: ArticleRepository_7ree
) {
    /**
     * 初始化文章列表数据流
     */
    fun initializeArticleFlow(
        coroutineScope: CoroutineScope,
        onLoading: (Boolean) -> Unit,
        onArticlesUpdated: (List<com.x7ree.wordcard.data.ArticleEntity_7ree>) -> Unit,
        onError: (String) -> Unit
    ) {
        coroutineScope.launch {
            try {
                onLoading(true)
                articleRepository_7ree.getAllArticles_7ree().collect { articleList ->
                    onArticlesUpdated(articleList)
                    onLoading(false)
                }
            } catch (e: Exception) {
                onError("加载文章失败: ${e.message}")
                onLoading(false)
            }
        }
    }
    
    /**
     * 增加文章浏览次数
     */
    fun incrementViewCount(
        articleId: Long,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            try {
                articleRepository_7ree.incrementViewCount_7ree(articleId)
            } catch (e: Exception) {
                // 浏览次数更新失败不需要显示错误信息
            }
        }
    }
}
