package com.x7ree.wordcard.utils

import com.x7ree.wordcard.data.WordEntity_7ree
import java.util.*
import kotlin.math.round

/**
 * 数据统计工具类
 * 提供所有统计数据的计算逻辑，供仪表盘和查单词页面公用
 */
object DataStatistics_7ree {
    
    /**
     * 统计数据类
     */
    data class StatisticsData_7ree(
        val totalWords: Int,        // 单词总数
        val totalViews: Int,        // 查阅总数
        val favoriteWords: Int,     // 收藏总数
        val studyDays: Int,         // 学习天数
        val reviewRatio: Float,     // 复习倍率（查阅总数/单词总数）
        val dailyStudy: Float,      // 每日学习（查阅总数/学习天数）
        val totalSpelling: Int,     // 拼写练习总数
        val spellingRatio: Float,   // 拼写倍率（拼写总数/单词总数）
        val dailySpelling: Float    // 每日拼写（拼写总数/学习天数）
    )
    
    /**
     * 计算所有统计数据
     * @param words 单词列表
     * @return 统计数据
     */
    fun calculateStatistics_7ree(words: List<WordEntity_7ree>): StatisticsData_7ree {
        val totalWords = words.size
        val totalViews = words.sumOf { it.viewCount }
        val favoriteWords = words.count { it.isFavorite }
        val studyDays = calculateStudyDays_7ree(words)
        val totalSpelling = words.sumOf { it.spellingCount }
        
        // 计算复习倍率（查阅总数/单词总数），精确到小数点后2位
        val reviewRatio = if (totalWords > 0) {
            round((totalViews.toFloat() / totalWords) * 100) / 100
        } else {
            0.0f
        }
        
        // 计算每日学习（查阅总数/学习天数），精确到小数点后2位
        val dailyStudy = if (studyDays > 0) {
            round((totalViews.toFloat() / studyDays) * 100) / 100
        } else {
            0.0f
        }
        
        // 计算拼写倍率（拼写总数/单词总数），精确到小数点后2位
        val spellingRatio = if (totalWords > 0) {
            round((totalSpelling.toFloat() / totalWords) * 100) / 100
        } else {
            0.0f
        }
        
        // 计算每日拼写（拼写总数/学习天数），精确到小数点后2位
        val dailySpelling = if (studyDays > 0) {
            round((totalSpelling.toFloat() / studyDays) * 100) / 100
        } else {
            0.0f
        }
        
        return StatisticsData_7ree(
            totalWords = totalWords,
            totalViews = totalViews,
            favoriteWords = favoriteWords,
            studyDays = studyDays,
            reviewRatio = reviewRatio,
            dailyStudy = dailyStudy,
            totalSpelling = totalSpelling,
            spellingRatio = spellingRatio,
            dailySpelling = dailySpelling
        )
    }
    
    /**
     * 计算学习天数
     * @param words 单词列表
     * @return 学习天数（统计实际有单词记录的不同日期数量）
     */
    private fun calculateStudyDays_7ree(words: List<WordEntity_7ree>): Int {
        if (words.isEmpty()) return 0
        
        val calendar = Calendar.getInstance()
        val uniqueDates = mutableSetOf<String>()
        
        // 统计所有单词的查询日期
        words.forEach { word ->
            calendar.timeInMillis = word.queryTimestamp
            // 格式化为年-月-日字符串，用于去重
            val dateString = String.format(
                "%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, // Calendar.MONTH 从0开始
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            uniqueDates.add(dateString)
        }
        
        return uniqueDates.size
    }
    
    /**
     * 格式化复习倍率显示文本
     */
    fun formatReviewRatio_7ree(ratio: Float): String {
        return String.format("%.2f", ratio)
    }
    
    /**
     * 格式化每日学习显示文本
     */
    fun formatDailyStudy_7ree(daily: Float): String {
        return String.format("%.2f", daily)
    }
    
    /**
     * 格式化拼写倍率显示文本
     */
    fun formatSpellingRatio_7ree(ratio: Float): String {
        return String.format("%.2f", ratio)
    }
    
    /**
     * 格式化每日拼写显示文本
     */
    fun formatDailySpelling_7ree(daily: Float): String {
        return String.format("%.2f", daily)
    }
}