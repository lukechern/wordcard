package com.x7ree.wordcard.article.ArticleDetailScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.data.ArticleEntity_7ree
import com.x7ree.wordcard.ui.components.MarkdownText_7ree
import com.x7ree.wordcard.ui.components.BilingualComparisonContent_7ree
import kotlinx.coroutines.launch

@Composable
fun ArticleContentTabs_7ree(
    article: ArticleEntity_7ree
) {
    // 合并的卡片：英文文章和中文翻译，使用tab切换
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        var selectedTab by remember { mutableStateOf(0) } // 0 for English, 1 for Chinese, 2 for Bilingual
        
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Tab布局
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 英文文章tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "英文文章",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selectedTab == 0) FontWeight.ExtraBold else FontWeight.Normal,
                        color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // 中英对照tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 2 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "中英对照",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selectedTab == 2) FontWeight.ExtraBold else FontWeight.Normal,
                        color = if (selectedTab == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // 中文翻译tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 1 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "中文翻译",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selectedTab == 1) FontWeight.ExtraBold else FontWeight.Normal,
                        color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Tab内容区域
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 根据选中的tab显示相应内容
                when (selectedTab) {
                    0 -> {
                        // 显示英文文章内容
                        MarkdownText_7ree(
                            text = article.englishContent,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    1 -> {
                        // 显示中文翻译内容
                        if (article.chineseContent.isNotEmpty()) {
                            MarkdownText_7ree(
                                text = article.chineseContent,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = 24.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Text(
                                text = "暂无中文翻译",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    2 -> {
                        // 显示中英对照内容
                        BilingualComparisonContent_7ree(
                            bilingualComparison = article.bilingualComparison
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleKeywordsCard_7ree(
    article: ArticleEntity_7ree,
    keywordStats: Map<String, Int>,
    onKeywordClick: (String) -> Unit
) {
    // 关键词卡片（保持不变）
    if (article.keyWords.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "文章关键词",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // 关键词标签显示（带统计）
                KeywordTagsWithStats_7ree(
                    keywords = article.keyWords.split(",").map { it.trim() },
                    keywordStats = keywordStats,
                    onKeywordClick = onKeywordClick
                )
            }
        }
    }
}

@Composable
fun RelatedArticlesCard_7ree(
    relatedArticles: List<ArticleEntity_7ree>,
    onRelatedArticleClick: (ArticleEntity_7ree) -> Unit,
    scrollState: androidx.compose.foundation.ScrollState
) {
    val scope = rememberCoroutineScope()
    // 相关文章卡片
    if (relatedArticles.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "相关文章",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // 显示最多5篇相关文章
                relatedArticles.take(5).forEachIndexed { index, relatedArticle ->
                    RelatedArticleItem_7ree(
                        article = relatedArticle,
                        onClick = {
                            // 滚动到顶部
                            scope.launch {
                                scrollState.scrollTo(0)
                            }
                            onRelatedArticleClick(relatedArticle)
                        }
                    )
                    // 添加行间距（除了最后一项）
                    if (index < relatedArticles.take(5).size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
