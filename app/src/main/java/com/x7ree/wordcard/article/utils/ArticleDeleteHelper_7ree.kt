package com.x7ree.wordcard.article.utils

import com.x7ree.wordcard.data.ArticleRepository_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ArticleDeleteHelper_7ree(
    private val articleRepository_7ree: ArticleRepository_7ree,
    private val wordRepository_7ree: WordRepository_7ree? = null
) {
    /**
     * 删除文章（异步回调方式）
     */
    fun deleteArticle(
        articleId: Long,
        coroutineScope: CoroutineScope,
        onResult: (String) -> Unit
    ) {
        coroutineScope.launch {
            try {
                // 先获取文章信息，以便在删除后更新关键词引用次数
                val article = articleRepository_7ree.getArticle_7ree(articleId)
                
                // 删除文章
                articleRepository_7ree.deleteArticle_7ree(articleId)
                
                // 如果文章存在，更新关键词引用次数
                article?.let {
                    decreaseWordReferenceCount(it.keyWords)
                }
                
                onResult("文章已删除")
            } catch (e: Exception) {
                onResult("删除文章失败: ${e.message}")
            }
        }
    }
    
    /**
     * 删除文章（同步方式，用于批量删除）
     */
    suspend fun deleteArticleSync(articleId: Long) {
        // 先获取文章信息，以便在删除后更新关键词引用次数
        val article = articleRepository_7ree.getArticle_7ree(articleId)
        
        // 删除文章
        articleRepository_7ree.deleteArticle_7ree(articleId)
        
        // 如果文章存在，更新关键词引用次数
        article?.let {
            decreaseWordReferenceCount(it.keyWords)
        }
    }
    
    /**
     * 减少相关单词的引用次数
     */
    private suspend fun decreaseWordReferenceCount(keywords: String) {
        // 检查 wordRepository_7ree 是否为空
        wordRepository_7ree ?: return
        
        // 解析关键词字符串，通常是以逗号分隔的单词列表
        val words = keywords.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        
        // 对每个关键词减少引用次数
        for (word in words) {
            try {
                // 获取当前单词记录
                val wordEntity = wordRepository_7ree.getWord_7ree(word)
                
                // 如果单词存在，减少其引用次数（但不能小于0）
                wordEntity?.let {
                    val newReferenceCount = kotlin.math.max(0, it.referenceCount - 1)
                    wordRepository_7ree.updateReferenceCount_7ree(word, newReferenceCount)
                }
            } catch (e: Exception) {
                // 如果在更新过程中出现异常，记录错误日志但不中断删除操作
            }
        }
    }
}
