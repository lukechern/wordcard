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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.data.ArticleEntity_7ree
import com.x7ree.wordcard.ui.components.StaggeredGrid_7ree
import androidx.compose.material3.ExperimentalMaterial3Api
import com.x7ree.wordcard.article.utils.PaginatedArticleList.PaginatedArticleRefreshIcon
import com.x7ree.wordcard.article.utils.PaginatedArticleList.PullToRefreshIndicator_7ree
import com.x7ree.wordcard.article.utils.PaginatedArticleList.EmptyStateIndicator_7ree

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
    onToggleArticleSelection: (Long) -> Unit = {},
    // 搜索模式相关参数
    isSearchMode: Boolean = false,
    searchQuery: String = "",
    // 滚动位置管理相关参数
    scrollPositionManager: ArticleScrollPositionManager_7ree? = null,
    shouldRestoreScrollPosition: Boolean = false,
    onScrollPositionRestored: () -> Unit = {}
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
    
    // 滚动位置恢复逻辑
    val scope = rememberCoroutineScope()
    LaunchedEffect(shouldRestoreScrollPosition, articles.size) {
        if (shouldRestoreScrollPosition && articles.isNotEmpty() && scrollPositionManager != null) {
            // 延迟一小段时间确保列表完全渲染
            kotlinx.coroutines.delay(100)
            scrollPositionManager.restoreScrollPosition(listState, scope)
            
            // 恢复完成后清除状态
            kotlinx.coroutines.delay(50)
            scrollPositionManager.clearSavedPosition()
            onScrollPositionRestored()
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
            PullToRefreshIndicator_7ree(
                isRefreshing = isRefreshing,
                pullToRefreshState = pullToRefreshState,
                onRefresh = onRefresh
            )
        }
    ) {
        if (articles.isEmpty()) {
            // 空状态 - 根据搜索模式显示不同的提示
            EmptyStateIndicator_7ree(
                isSearchMode = isSearchMode,
                searchQuery = searchQuery
            )
        } else {
            // 使用瀑布流布局替代简单的两列布局
            LazyColumn(
                state = listState,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                item {
                    StaggeredGrid_7ree(
                        items = articles,
                        columns = 2,
                        horizontalSpacing = 8.dp,
                        verticalSpacing = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) { article ->
                        ArticleCard_7ree(
                            article = article,
                            onClick = { 
                                if (isManagementMode) {
                                    onToggleArticleSelection(article.id)
                                } else {
                                    onArticleClick(article)
                                }
                            },
                            onToggleFavorite = { onToggleFavorite(article.id) },
                            isManagementMode = isManagementMode,
                            isSelected = selectedArticleIds.contains(article.id),
                            onToggleSelection = { onToggleArticleSelection(article.id) }
                        )
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
