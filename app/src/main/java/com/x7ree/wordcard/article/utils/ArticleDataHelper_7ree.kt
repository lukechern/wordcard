package com.x7ree.wordcard.article.utils

import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.data.ArticleEntity_7ree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ArticleDataHelper_7ree(
    private val wordRepository_7ree: WordRepository_7ree?
) {
    /**
     * 计算关键词统计
     */
    fun calculateKeywordStats(
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
                    keywordCountMap[keyword] = count
                }
                
                // 回调更新状态
                onStatsCalculated(keywordCountMap)
                
            } catch (e: Exception) {
                // 静默处理统计错误
            }
        }
    }
    
    /**
     * 将关键词统计同步到单词本数据库
     */
    suspend fun syncKeywordStatsToWordDatabase(keywordStats: Map<String, Int>) {
        try {
            if (wordRepository_7ree == null) {
                return
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
