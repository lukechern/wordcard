package com.x7ree.wordcard.article.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.data.ArticleEntity_7ree
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.draw.rotate
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween

// 复用单词本的刷新图标 - 重命名以避免冲突
private val PaginatedArticleRefreshIcon: ImageVector
    get() {
        return ImageVector.Builder(
            name = "PaginatedArticleRefreshIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 48f,
            viewportHeight = 48f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color(0xFFC5C5C5)),
                strokeLineWidth = 3.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 4f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(36.7279f, 36.7279f)
                curveTo(33.4706f, 39.9853f, 28.9706f, 42f, 24f, 42f)
                curveTo(14.0589f, 42f, 6f, 33.9411f, 6f, 24f)
                curveTo(6f, 14.0589f, 14.0589f, 6f, 24f, 6f)
                curveTo(28.9706f, 6f, 33.4706f, 8.01472f, 36.7279f, 11.2721f)
                curveTo(38.3859f, 12.9301f, 42f, 17f, 42f, 17f)
            }
            path(
                fill = null,
                stroke = SolidColor(Color(0xFFC5C5C5)),
                strokeLineWidth = 3.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 4f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(42f, 8f)
                verticalLineTo(17f)
                horizontalLineTo(33f)
            }
        }.build()
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaginatedArticleList_7ree(
    articles: List<ArticleEntity_7ree>,
    isLoadingMore: Boolean,
    hasMoreData: Boolean,
    onArticleClick: (ArticleEntity_7ree) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onLoadMore: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    // 管理模式相关参数
    isManagementMode: Boolean = false,
    selectedArticleIds: Set<Long> = emptySet(),
    onToggleArticleSelection: (Long) -> Unit = {}
) {
    
    // 检测是否滚动到接近底部
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            
            lastVisibleItemIndex > (totalItemsNumber - 3) && hasMoreData && !isLoadingMore
        }
    }
    
    // 触发加载更多
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }
    
    // 下拉刷新状态
    val pullToRefreshState = rememberPullToRefreshState()
    
    // 下拉刷新容器 - 使用简化的自定义指示器
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier = modifier,
        indicator = {
            // 修复版本：在下拉时显示指示器，在刷新时显示不同状态
            if (isRefreshing || pullToRefreshState.distanceFraction > 0f) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        if (isRefreshing) {
                            // 刷新中：持续旋转的图标
                            val infiniteTransition = rememberInfiniteTransition(label = "article_refresh_rotation")
                            val rotation by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "article_refresh_rotation"
                            )
                            
                            Icon(
                                imageVector = PaginatedArticleRefreshIcon,
                                contentDescription = "刷新中",
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(rotation),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "文章列表刷新中，请稍候",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        } else {
                            // 下拉时：根据下拉距离旋转的图标
                            val rotation = 360f * pullToRefreshState.distanceFraction
                            
                            Icon(
                                imageVector = PaginatedArticleRefreshIcon,
                                contentDescription = "下拉刷新",
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(rotation),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "松手后，自动刷新文章列表",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
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
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                
                // 加载更多指示器
                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "加载更多文章...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                // 底部提示
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "点击文章卡片查看详情，点击",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.FavoriteBorder,
                            contentDescription = "收藏",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "图标可收藏文章",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // 如果没有更多数据，显示到底提示
                    if (!hasMoreData && articles.isNotEmpty()) {
                        Text(
                            text = "已显示全部文章",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}