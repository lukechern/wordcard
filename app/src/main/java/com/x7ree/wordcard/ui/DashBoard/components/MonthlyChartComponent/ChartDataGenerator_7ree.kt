package com.x7ree.wordcard.ui.DashBoard.components.MonthlyChartComponent

import com.x7ree.wordcard.data.WordEntity_7ree
import java.text.SimpleDateFormat
import java.util.*

// 生成年度月度统计数据
fun generateMonthlyChartData_7ree(words_7ree: List<WordEntity_7ree>, articles_7ree: List<com.x7ree.wordcard.data.ArticleEntity_7ree>): List<MonthlyData_7ree> {
    val dateFormat = SimpleDateFormat("M月", Locale.getDefault()) // 使用月份格式
    val result = mutableListOf<MonthlyData_7ree>()
    
    // 获取当前年份的开始时间
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    
    // 设置到今年1月1日
    calendar.set(currentYear, Calendar.JANUARY, 1, 0, 0, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    
    // 年度开始时间计算完成
    
    // 生成12个月的数据
    for (i in 0..11) {
        val currentMonth = Calendar.getInstance().apply {
            set(currentYear, i, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val nextMonth = Calendar.getInstance().apply {
            set(currentYear, i + 1, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
            if (i == 11) { // 如果是12月，则设置为下一年的1月
                add(Calendar.YEAR, 1)
            }
        }
        
        val monthStr = dateFormat.format(currentMonth.time)
        
        // 计算当月的单词数量和查阅次数
        val monthWords = words_7ree.filter { word ->
            val wordTimestamp = word.queryTimestamp
            wordTimestamp >= currentMonth.timeInMillis && wordTimestamp < nextMonth.timeInMillis
        }
        
        // 计算当月的文章数量
        val monthArticles = articles_7ree.filter { article ->
            val articleTimestamp = article.generationTimestamp
            articleTimestamp >= currentMonth.timeInMillis && articleTimestamp < nextMonth.timeInMillis
        }
        
        val wordCount = monthWords.size
        val viewCount = monthWords.sumOf { it.viewCount } / 10
        val spellingCount = monthWords.sumOf { it.spellingCount }
        val articleCount = monthArticles.size  // 真正的文章生成数量
        
        result.add(MonthlyData_7ree(monthStr, wordCount, viewCount, spellingCount, articleCount))
        
        // 月度统计数据计算完成
    }
    
    // 月度数据生成完成
    return result
}
