package com.x7ree.wordcard.article.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.data.ArticleEntity_7ree
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ArticleCard_7ree(
    article: ArticleEntity_7ree,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    isManagementMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelection: () -> Unit = {}
) {
    // 使用key确保组件与数据的绑定稳定性
    key(article.id) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // 管理模式下的复选框和标题行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 管理模式下显示复选框
                    if (isManagementMode) {
                        IconButton(
                            onClick = onToggleSelection,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                contentDescription = if (isSelected) "取消选择" else "选择",
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    
                    // 标题（过滤星号标记）
                    Text(
                        text = cleanTextForPreview(article.englishTitle),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 4.dp)
                    )
                }
                
                // 标题翻译（过滤星号标记）
                Text(
                    text = cleanTextForPreview(article.titleTranslation),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // 文章内容预览（过滤星号标记）
                Text(
                    text = cleanTextForPreview(article.englishContent),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // 关键词
                if (article.keyWords.isNotEmpty()) {
                    Text(
                        text = "${article.keyWords}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                // 底部信息栏
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        // 生成时间
                        Text(
                            text = formatTimestamp(article.generationTimestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        // 浏览次数
                        Text(
                            text = "浏览 ${article.viewCount} 次",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                    
                    // 收藏按钮
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (article.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (article.isFavorite) "取消收藏" else "收藏",
                            tint = if (article.isFavorite) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 清理文本用于预览显示，去除Markdown格式标记
 * @param text 原始文本
 * @return 清理后的预览文本
 */
private fun cleanTextForPreview(text: String): String {
    if (text.isEmpty()) {
        return ""
    }
    
    var cleanedText = text
    
    // 去除三个星号包裹的粗体标记 ***text*** -> text
    cleanedText = cleanedText.replace(Regex("\\*\\*\\*([^*]+)\\*\\*\\*"), "$1")
    
    // 去除两个星号包裹的粗体标记 **text** -> text
    cleanedText = cleanedText.replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1")
    
    // 去除单个星号包裹的斜体标记 *text* -> text
    cleanedText = cleanedText.replace(Regex("\\*([^*]+)\\*"), "$1")
    
    // 去除标题前的"title"语音说明
    cleanedText = cleanedText.replace(Regex("^title\\s*:?\\s*", RegexOption.IGNORE_CASE), "")
    
    // 去除文章前的"content"语音说明
    cleanedText = cleanedText.replace(Regex("^content\\s*:?\\s*", RegexOption.IGNORE_CASE), "")
    
    // 去除其他可能的语音说明前缀
    cleanedText = cleanedText.replace(Regex("^(article|text|story)\\s*:?\\s*", RegexOption.IGNORE_CASE), "")
    
    // 清理多余的空白字符
    cleanedText = cleanedText.replace(Regex("\\s+"), " ").trim()
    
    return cleanedText
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
