package com.x7ree.wordcard.article

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.data.ArticleEntity_7ree
import com.x7ree.wordcard.article.utils.ArticleCard_7ree

@Composable
fun ArticleTwoColumnList_7ree(
    articles: List<ArticleEntity_7ree>,
    isManagementMode: Boolean,
    selectedArticleIds: Set<Long>,
    onArticleClick: (ArticleEntity_7ree) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onToggleArticleSelection: (Long) -> Unit
) {
    // 使用两列布局，文章已经在ViewModel中进行了筛选和排序
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        // 直接按行处理，每行显示两个文章
        items(
            count = (articles.size + 1) / 2,
            key = { rowIndex -> 
                // 使用行中文章的ID作为key，确保稳定性
                val leftIndex = rowIndex * 2
                val rightIndex = leftIndex + 1
                val leftId = if (leftIndex < articles.size) articles[leftIndex].id else -1L
                val rightId = if (rightIndex < articles.size) articles[rightIndex].id else -2L
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
                    if (leftIndex < articles.size) {
                        val leftArticle = articles[leftIndex]
                        ArticleCard_7ree(
                            article = leftArticle,
                            onClick = { 
                                if (isManagementMode) {
                                    onToggleArticleSelection(leftArticle.id)
                                } else {
                                    onArticleClick(leftArticle)
                                }
                            },
                            onToggleFavorite = { onToggleFavorite(leftArticle.id) },
                            isManagementMode = isManagementMode,
                            isSelected = selectedArticleIds.contains(leftArticle.id),
                            onToggleSelection = { onToggleArticleSelection(leftArticle.id) }
                        )
                    }
                }
                
                // 右列 - 显示奇数索引的文章 (1, 3, 5, ...)
                Box(modifier = Modifier.weight(1f)) {
                    if (rightIndex < articles.size) {
                        val rightArticle = articles[rightIndex]
                        ArticleCard_7ree(
                            article = rightArticle,
                            onClick = { 
                                if (isManagementMode) {
                                    onToggleArticleSelection(rightArticle.id)
                                } else {
                                    onArticleClick(rightArticle)
                                }
                            },
                            onToggleFavorite = { onToggleFavorite(rightArticle.id) },
                            isManagementMode = isManagementMode,
                            isSelected = selectedArticleIds.contains(rightArticle.id),
                            onToggleSelection = { onToggleArticleSelection(rightArticle.id) }
                        )
                    }
                }
            }
        }
    }
}
