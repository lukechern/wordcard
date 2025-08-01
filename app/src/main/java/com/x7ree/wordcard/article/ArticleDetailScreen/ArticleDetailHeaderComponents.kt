package com.x7ree.wordcard.article.ArticleDetailScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.data.ArticleEntity_7ree
import com.x7ree.wordcard.ui.components.MarkdownText_7ree

@Composable
fun ArticleDetailAppBar_7ree(
    article: ArticleEntity_7ree,
    onToggleFavorite: () -> Unit,
    onShareClick: () -> Unit,
    onBackClick: () -> Unit,
    ttsButtonState: com.x7ree.wordcard.article.ArticleTtsManager_7ree.TtsButtonState
) {
    // 顶部应用栏 - 使用与文章列表页相同的结构
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp) // 固定标题栏高度，确保与文章列表页一致
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 标题模式：显示标题和操作按钮
            Text(
                text = "文章详情",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 收藏按钮
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = if (article.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (article.isFavorite) "取消收藏" else "收藏",
                        tint = if (article.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // 朗读按钮
                IconButton(
                    onClick = onShareClick,
                    enabled = ttsButtonState != com.x7ree.wordcard.article.ArticleTtsManager_7ree.TtsButtonState.LOADING,
                    modifier = Modifier.size(30.dp)
                ) {
                    when (ttsButtonState) {
                        com.x7ree.wordcard.article.ArticleTtsManager_7ree.TtsButtonState.LOADING -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        com.x7ree.wordcard.article.ArticleTtsManager_7ree.TtsButtonState.PLAYING -> {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "停止朗读",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        com.x7ree.wordcard.article.ArticleTtsManager_7ree.TtsButtonState.ERROR -> {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "重试朗读",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        com.x7ree.wordcard.article.ArticleTtsManager_7ree.TtsButtonState.READY -> {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "朗读文章",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                
                // 返回按钮（放置在右边）
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ArticleTitleCard_7ree(
    article: ArticleEntity_7ree,
    filterMarkdownStars: (String) -> String,
    formatTimestamp: (Long) -> String
) {
    // 第一个卡片：英文标题和中文标题
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            
            MarkdownText_7ree(
                text = filterMarkdownStars(article.englishTitle),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = filterMarkdownStars(article.titleTranslation),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // 文章信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "写作时间: ${formatTimestamp(article.generationTimestamp)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "浏览 ${article.viewCount} 次",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
