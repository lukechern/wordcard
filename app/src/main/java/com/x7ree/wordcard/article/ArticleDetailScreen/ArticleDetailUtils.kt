package com.x7ree.wordcard.article.ArticleDetailScreen

import java.text.SimpleDateFormat
import java.util.*

/**
 * 过滤掉文本中的MD符号星号(**和***)
 */
fun filterMarkdownStars(text: String): String {
    return text.replace(Regex("\\*{2,3}"), "")
}

/**
 * 格式化时间戳
 */
fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
