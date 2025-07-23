package com.x7ree.wordcard.ui.SpellingPractice

/**
 * 拼写练习相关的工具函数
 */

// 获取前两个中文词义的工具函数
fun getFirstTwoMeanings_7ree(chineseMeaning: String): String {
    // 按逗号或分号分割词义
    val meanings = chineseMeaning.split(Regex("[,，;；]"))
        .map { it.trim() }
        .filter { it.isNotEmpty() }
    
    return when {
        meanings.isEmpty() -> chineseMeaning
        meanings.size == 1 -> meanings[0]
        else -> "${meanings[0]}，${meanings[1]}"
    }
}

// 拼写练习相关的颜色常量
object SpellingColors_7ree {
    val SUCCESS_COLOR = androidx.compose.ui.graphics.Color(0xFF2E7D32) // 深绿色
    val ERROR_COLOR = androidx.compose.ui.graphics.Color(0xFFF44336) // 红色
}

// 拼写练习相关的常量
object SpellingConstants_7ree {
    const val RESULT_DISPLAY_DELAY = 100L // 结果显示延迟
    const val SUCCESS_AUTO_CLOSE_DELAY = 1000L // 成功后自动关闭延迟
    const val ERROR_RESET_DELAY = 1000L // 错误后重置延迟
    const val FOCUS_REQUEST_DELAY = 300L // 聚焦请求延迟
}