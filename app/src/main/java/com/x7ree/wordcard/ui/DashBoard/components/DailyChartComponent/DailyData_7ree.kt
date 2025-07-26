package com.x7ree.wordcard.ui.DashBoard.components.DailyChartComponent

import com.x7ree.wordcard.data.WordEntity_7ree
import java.text.SimpleDateFormat
import java.util.*

// 数据类用于存储每日统计数据
data class DailyData_7ree(
    val date: String,
    val wordCount: Int,
    val viewCount: Int,
    val spellingCount: Int
)

// 生成本周统计数据
fun generateDailyChartData_7ree(words_7ree: List<WordEntity_7ree>): List<DailyData_7ree> {
    val dateFormat = SimpleDateFormat("E", Locale.getDefault()) // 使用星期几格式
    val result = mutableListOf<DailyData_7ree>()
    
    // 获取本周的开始日期（周一）
    val calendar = Calendar.getInstance()
    // 先获取当前日期
    val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    // 计算到本周一的天数差
    val daysToMonday = when (currentDayOfWeek) {
        Calendar.SUNDAY -> -6  // 周日到周一差6天
        Calendar.MONDAY -> 0   // 周一差0天
        Calendar.TUESDAY -> -1 // 周二到周一差1天
        Calendar.WEDNESDAY -> -2 // 周三到周一差2天
        Calendar.THURSDAY -> -3 // 周四到周一差3天
        Calendar.FRIDAY -> -4   // 周五到周一差4天
        Calendar.SATURDAY -> -5 // 周六到周一差5天
        else -> 0
    }
    
    // 设置到本周一
    calendar.add(Calendar.DAY_OF_YEAR, daysToMonday)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    
    val weekStart = calendar.timeInMillis
    
    // 本周开始时间计算完成
    
    // 生成本周7天的数据
    for (i in 0..6) {
        val currentDate = Calendar.getInstance().apply {
            timeInMillis = weekStart + (i * 24 * 60 * 60 * 1000L) // 每天增加24小时
        }
        
        val dateStr = dateFormat.format(currentDate.time)
        
        // 计算当天的单词数量和查阅次数
        val dayWords = words_7ree.filter { word ->
            val wordDate = Calendar.getInstance().apply {
                timeInMillis = word.queryTimestamp
            }
            
            // 比较年月日，忽略时分秒
            val isSameDay = wordDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                           wordDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)
            
            // 日期比较逻辑
            
            isSameDay
        }
        
        val wordCount = dayWords.size
        val viewCount = dayWords.sumOf { it.viewCount } / 10
        val spellingCount = dayWords.sumOf { it.spellingCount }
        
        result.add(DailyData_7ree(dateStr, wordCount, viewCount, spellingCount))
        
        // 统计数据计算完成
    }
    
    // 数据生成完成
    return result
}
