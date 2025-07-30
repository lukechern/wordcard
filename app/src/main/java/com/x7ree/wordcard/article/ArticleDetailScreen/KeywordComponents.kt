package com.x7ree.wordcard.article.ArticleDetailScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun KeywordTags_7ree(keywords: List<String>) {
    // 使用FlowRow布局显示关键词标签
    val maxRowWidth = 2 // 每行最多2个标签
    
    Column {
        var currentRow = mutableListOf<String>()
        val rows = mutableListOf<List<String>>()
        
        keywords.forEach { keyword ->
            currentRow.add(keyword)
            if (currentRow.size >= maxRowWidth) {
                rows.add(currentRow.toList())
                currentRow = mutableListOf()
            }
        }
        
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow.toList())
        }
        
        rows.forEach { rowKeywords ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowKeywords.forEach { keyword ->
                    Box(modifier = Modifier.weight(1f)) {
                        KeywordTag_7ree(keyword = keyword)
                    }
                }
                // 如果只有一个标签，添加一个占位符以确保标签宽度正确
                if (rowKeywords.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun KeywordTagsWithStats_7ree(
    keywords: List<String>,
    keywordStats: Map<String, Int>,
    onKeywordClick: (String) -> Unit = {}
) {
    // 使用FlowRow布局显示关键词标签
    val maxRowWidth = 2 // 每行最多2个标签
    
    Column {
        var currentRow = mutableListOf<String>()
        val rows = mutableListOf<List<String>>()
        
        keywords.forEach { keyword ->
            currentRow.add(keyword)
            if (currentRow.size >= maxRowWidth) {
                rows.add(currentRow.toList())
                currentRow = mutableListOf()
            }
        }
        
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow.toList())
        }
        
        rows.forEach { rowKeywords ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowKeywords.forEach { keyword ->
                    Box(modifier = Modifier.weight(1f)) {
                        KeywordTagWithStats_7ree(
                            keyword = keyword,
                            count = keywordStats[keyword] ?: 0,
                            onKeywordClick = onKeywordClick
                        )
                    }
                }
                // 如果只有一个标签，添加一个占位符以确保标签宽度正确
                if (rowKeywords.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun KeywordTagWithStats_7ree(keyword: String, count: Int, onKeywordClick: (String) -> Unit = {}) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp)) // 减少圆角
            .background(Color(0xFFD1D9D2)) // 浅绿色背景 (再减淡6%)
            .padding(horizontal = 12.dp, vertical = 7.dp) // 调整内边距
            .clickable { onKeywordClick(keyword) }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = keyword,
                style = MaterialTheme.typography.bodyMedium, // 加大文字
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold, // 加粗
                maxLines = 1, // 强制不允许换行
                modifier = Modifier.padding(end = 24.dp) // 为统计数字留出空间
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 8.dp) // 向右移动
                    .clip(RoundedCornerShape(50)) // 圆形背景
                    .background(Color(0xFF64A966)) // 较深的绿色背景 (再减淡4%)
                    .padding(horizontal = 8.dp, vertical = 4.dp), // 增大圆形背景
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.bodySmall, // 字号比单词小一点
                    color = Color.White, // 白色文字
                    fontWeight = FontWeight.Normal, // 不加粗
                    maxLines = 1 // 强制不允许换行
                )
            }
        }
    }
}

@Composable
fun KeywordTag_7ree(keyword: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp)) // 减少圆角
            .background(Color(0xFFD1D9D2)) // 浅绿色背景 (再减淡6%)
            .padding(horizontal = 12.dp, vertical = 7.dp) // 调整内边距
    ) {
        Text(
            text = keyword,
            style = MaterialTheme.typography.bodyMedium, // 加大文字
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold, // 加粗
            maxLines = 1 // 强制不允许换行
        )
    }
}
