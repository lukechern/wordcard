package com.x7ree.wordcard.utils

import com.x7ree.wordcard.data.WordEntity_7ree
import com.x7ree.wordcard.data.ArticleEntity_7ree
import java.util.*
import kotlin.math.round

/**
 * 文章数据统计工具类
 * 提供文章相关统计数据的计算逻辑
 */
object ArticleStatistics_7ree {
    
    /**
     * 文章统计数据类
     */
    data class ArticleStatisticsData_7ree(
        val generatedArticles: Int,     // 生成文章数
        val viewedArticles: Int,        // 查阅文章数
        val favoritedArticles: Int      // 收藏文章数
    )
    
    /**
     * 计算文章统计数据
     * @param articles 文章列表
     * @param words 单词列表（保留参数兼容性，但不用于计算生成文章数）
     * @return 文章统计数据
     */
    fun calculateArticleStatistics_7ree(
        articles: List<ArticleEntity_7ree>
    ): ArticleStatisticsData_7ree {
        // 生成文章数应该是实际的文章数量，而不是基于单词数量计算
        val generatedArticles = articles.size
        
        // 查阅文章数
        val viewedArticles = articles.sumOf { it.viewCount }
        
        // 收藏文章数
        val favoritedArticles = articles.count { it.isFavorite }
        
        return ArticleStatisticsData_7ree(
            generatedArticles = generatedArticles,
            viewedArticles = viewedArticles,
            favoritedArticles = favoritedArticles
        )
    }
    
    /**
     * 计算生成文章数
     * 这里使用一个简单的计算方式：每10个单词生成1篇文章
     * @param words 单词列表
     * @return 生成文章数
     */
    private fun calculateGeneratedArticles_7ree(words: List<WordEntity_7ree>): Int {
        // 简单的计算方式：每10个单词生成1篇文章
        return (words.size / 10).coerceAtLeast(0)
    }
    
    /**
     * 格式化生成文章数显示文本
     */
    fun formatGeneratedArticles_7ree(count: Int): String {
        return count.toString()
    }
    
    /**
     * 格式化查阅文章数显示文本
     */
    fun formatViewedArticles_7ree(count: Int): String {
        return count.toString()
    }
    
    /**
     * 格式化收藏文章数显示文本
     */
    fun formatFavoritedArticles_7ree(count: Int): String {
        return count.toString()
    }
}