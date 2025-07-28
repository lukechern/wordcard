package com.x7ree.wordcard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.data.ArticleEntity_7ree
import com.x7ree.wordcard.article.utils.ArticlePullToRefreshComponent_7ree
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen_7ree(
    articles: List<ArticleEntity_7ree> = emptyList(),
    onGenerateArticle: (String) -> Unit = {},
    onSmartGenerate: (SmartGenerationType_7ree) -> Unit = {},
    onSmartGenerateWithKeywords: (SmartGenerationType_7ree, String) -> Unit = { _, _ -> },
    onArticleClick: (ArticleEntity_7ree) -> Unit = {},
    onToggleFavorite: (Long) -> Unit = {},
    isGenerating: Boolean = false,
    showSmartGenerationCard: Boolean = false,
    smartGenerationStatus: String = "",
    smartGenerationKeywords: List<String> = emptyList(),
    onCloseSmartGenerationCard: () -> Unit = {},
    currentSmartGenerationType: SmartGenerationType_7ree? = null,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    var showGenerationDialog by remember { mutableStateOf(false) }
    var shouldCloseDialogAfterGeneration by remember { mutableStateOf(false) }
    
    // 监听生成状态，如果正在生成文章，则在生成完成后关闭对话框
    LaunchedEffect(isGenerating) {
        if (!isGenerating && shouldCloseDialogAfterGeneration) {
            showGenerationDialog = false
            shouldCloseDialogAfterGeneration = false
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "读文章",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            // 生成文章按钮
            IconButton(
                onClick = { showGenerationDialog = true },
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "生成文章",
                    tint = Color.White
                )
            }
        }
        
        // 文章列表 - 使用下拉刷新组件包装
        ArticlePullToRefreshComponent_7ree(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            if (articles.isEmpty()) {
                // 空状态
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "暂无文章",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "点击右上角的 + 按钮生成文章",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "或下拉刷新文章列表",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                // 使用两列布局确保最新文章始终在第一行左边
                val sortedArticles = remember(articles) {
                    // 按生成时间排序，确保最新的在前面，并添加稳定的二级排序
                    articles.sortedWith(
                        compareByDescending<ArticleEntity_7ree> { it.generationTimestamp }
                            .thenByDescending { it.id }
                    )
                }
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 直接按行处理，每行显示两个文章
                    items(
                        count = (sortedArticles.size + 1) / 2,
                        key = { rowIndex -> 
                            // 使用行中文章的ID作为key，确保稳定性
                            val leftIndex = rowIndex * 2
                            val rightIndex = leftIndex + 1
                            val leftId = if (leftIndex < sortedArticles.size) sortedArticles[leftIndex].id else -1L
                            val rightId = if (rightIndex < sortedArticles.size) sortedArticles[rightIndex].id else -2L
                            "${leftId}_${rightId}"
                        }
                    ) { rowIndex ->
                        val leftIndex = rowIndex * 2
                        val rightIndex = leftIndex + 1
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 左列 - 显示偶数索引的文章 (0, 2, 4, ...)
                            Box(modifier = Modifier.weight(1f)) {
                                if (leftIndex < sortedArticles.size) {
                                    val leftArticle = sortedArticles[leftIndex]
                                    ArticleCard_7ree(
                                        article = leftArticle,
                                        onClick = { onArticleClick(leftArticle) },
                                        onToggleFavorite = { onToggleFavorite(leftArticle.id) }
                                    )
                                }
                            }
                            
                            // 右列 - 显示奇数索引的文章 (1, 3, 5, ...)
                            Box(modifier = Modifier.weight(1f)) {
                                if (rightIndex < sortedArticles.size) {
                                    val rightArticle = sortedArticles[rightIndex]
                                    ArticleCard_7ree(
                                        article = rightArticle,
                                        onClick = { onArticleClick(rightArticle) },
                                        onToggleFavorite = { onToggleFavorite(rightArticle.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // 文章生成对话框
    ArticleGenerationDialog_7ree(
        isVisible = showGenerationDialog,
        onDismiss = { showGenerationDialog = false },
        onGenerate = { keyWords ->
            onGenerateArticle(keyWords)
            showGenerationDialog = false
        },
        onSmartGenerate = { type ->
            onSmartGenerate(type)
            showGenerationDialog = false
        },
        onSmartGenerateWithKeywords = { type, keywords ->
            onSmartGenerateWithKeywords(type, keywords)
            // 对于手动输入，不立即关闭对话框，等待生成完成后再关闭
            shouldCloseDialogAfterGeneration = true
        },
        isGenerating = isGenerating
    )
    
    // 智能生成进度卡片
    SmartGenerationProgressCard_7ree(
        isVisible = showSmartGenerationCard,
        status = smartGenerationStatus,
        keywords = smartGenerationKeywords,
        onDismiss = onCloseSmartGenerationCard,
        currentSmartGenerationType = currentSmartGenerationType
    )
}

@Composable
private fun ArticleCard_7ree(
    article: ArticleEntity_7ree,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
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
                // 标题
                Text(
                    text = article.englishTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                // 标题翻译
                Text(
                    text = article.titleTranslation,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // 文章内容预览
                Text(
                    text = article.englishContent,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // 关键词
                if (article.keyWords.isNotEmpty()) {
                    Text(
                        text = "关键词: ${article.keyWords}",
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
                            tint = if (article.isFavorite) Color.Red else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
