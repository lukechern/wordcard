package com.x7ree.wordcard.article.utils

import com.x7ree.wordcard.data.ArticleEntity_7ree
import com.x7ree.wordcard.data.ArticleRepository_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ArticleDetailHelper_7ree(
    private val articleRepository_7ree: ArticleRepository_7ree,
    private val wordRepository_7ree: WordRepository_7ree?
) {
    /**
     * 处理文章详情页的显示逻辑
     */
    fun handleArticleSelection(
        article: ArticleEntity_7ree,
        coroutineScope: CoroutineScope,
        onArticleSelected: (ArticleEntity_7ree) -> Unit,
        onStatsCalculated: (Map<String, Int>) -> Unit,
        articles: List<ArticleEntity_7ree>
    ) {
        coroutineScope.launch {
            try {
                // 检查文章内容是否包含 ### 符号，如果有说明解析失败
                val needsReparse = article.englishContent.contains("###") || 
                                 article.chineseContent.contains("###") ||
                                 article.englishTitle == "Generated Article" ||
                                 article.chineseContent == "翻译暂不可用"
                
                val finalArticle = if (needsReparse && article.apiResult.isNotEmpty()) {
                    // 重新解析文章
                    val parser = com.x7ree.wordcard.article.ArticleMarkdownParser_7ree()
                    val parsedResult = parser.parseArticleMarkdown(article.apiResult)
                    
                    // 更新数据库
                    val updatedArticle = article.copy(
                        englishTitle = parsedResult.englishTitle,
                        titleTranslation = parsedResult.chineseTitle,
                        englishContent = parsedResult.englishContent,
                        chineseContent = parsedResult.chineseContent,
                        keyWords = parsedResult.keywords
                    )
                    
                    // 保存更新后的文章到数据库
                    articleRepository_7ree.updateArticle_7ree(updatedArticle)
                    
                    // 验证数据库更新是否成功
                    val verifyArticle = articleRepository_7ree.getArticle_7ree(updatedArticle.id)
                    verifyArticle ?: updatedArticle
                } else {
                    // 文章解析正常，直接使用
                    article
                }
                
                // 回调通知UI更新选中的文章
                onArticleSelected(finalArticle)
                
                // 计算关键词统计
                calculateKeywordStats(coroutineScope, finalArticle, articles, onStatsCalculated)
                
            } catch (e: Exception) {
                // 发生错误时仍然显示原文章
                onArticleSelected(article)
            }
        }
    }
    
    /**
     * 计算关键词统计
     */
    private fun calculateKeywordStats(
        coroutineScope: CoroutineScope,
        currentArticle: ArticleEntity_7ree,
        articles: List<ArticleEntity_7ree>,
        onStatsCalculated: (Map<String, Int>) -> Unit
    ) {
        coroutineScope.launch {
            try {
                // 获取当前文章的关键词
                val currentKeywords = currentArticle.keyWords.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                
                // 统计每个关键词在所有文章中的出现次数
                val keywordCountMap = mutableMapOf<String, Int>()
                
                currentKeywords.forEach { keyword -> 
                    var count = 0
                    articles.forEach { article ->
                        val articleKeywords = article.keyWords.split(",").map { it.trim() }
                        if (articleKeywords.contains(keyword)) {
                            count++
                        }
                    }
                    // 确保每个关键词至少计数为1（在当前文章中出现）
                    keywordCountMap[keyword] = if (count == 0) 1 else count
                }
                
                // 回调更新状态
                onStatsCalculated(keywordCountMap)
                
                // 同步到单词本数据库
                syncKeywordStatsToWordDatabase(coroutineScope, keywordCountMap)
            } catch (e: Exception) {
                // 静默处理统计错误
                // 即使出错也要确保回调被调用，避免UI状态不更新
                onStatsCalculated(emptyMap())
            }
        }
    }
    
    /**
     * 将关键词统计同步到单词本数据库
     */
    private fun syncKeywordStatsToWordDatabase(
        coroutineScope: CoroutineScope,
        keywordStats: Map<String, Int>
    ) {
        coroutineScope.launch {
            try {
                if (wordRepository_7ree == null) {
                    return@launch
                }
                
                keywordStats.forEach { (keyword, count) ->
                    try {
                        // 检查单词是否存在于单词本中
                        val existingWord = wordRepository_7ree.getWord_7ree(keyword)
                        if (existingWord != null) {
                            // 更新现有单词的引用次数
                            wordRepository_7ree.updateReferenceCount_7ree(keyword, count)
                        }
                    } catch (e: Exception) {
                        // 静默处理单个单词更新错误
                    }
                }
            } catch (e: Exception) {
                // 静默处理同步错误
            }
        }
    }
}
