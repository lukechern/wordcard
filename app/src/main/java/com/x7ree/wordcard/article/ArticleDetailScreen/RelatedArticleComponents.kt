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
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.data.ArticleEntity_7ree

@Composable
fun RelatedArticleItem_7ree(
    article: ArticleEntity_7ree,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)) // 圆角
            .background(Color(0xFFD1D9D2)) // 浅绿色背景 (再减淡6%)
            .padding(horizontal = 12.dp, vertical = 7.dp) // 调整内边距
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = filterMarkdownStars(article.englishTitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1, // 最多显示一行
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
                    text = "${article.viewCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1
                )
            }
        }
    }
}
