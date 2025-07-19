package com.x7ree.wordcard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.data.WordEntity_7ree
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

// 自定义刷新图标 - 基于提供的SVG
val RefreshIcon: ImageVector
    get() {
        return ImageVector.Builder(
            name = "RefreshIcon",
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
fun PaginatedWordList_7ree(
    words: List<WordEntity_7ree>,
    isLoadingMore: Boolean,
    hasMoreData: Boolean,
    onWordClick: (String) -> Unit,
    onFavoriteToggle: (WordEntity_7ree) -> Unit,
    onWordDelete: (WordEntity_7ree) -> Unit,
    onLoadMore: () -> Unit,
    onWordSpeak: (String) -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
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
    
    // 下拉刷新容器
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier = modifier,
        indicator = {
            // 自定义刷新指示器 - 只在下拉或刷新时显示
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
                            // 刷新中使用相同的自定义图标，持续旋转
                            val infiniteTransition = rememberInfiniteTransition(label = "refresh_rotation")
                            val rotation by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "refresh_rotation"
                            )
                            
                            Icon(
                                imageVector = RefreshIcon,
                                contentDescription = "刷新中",
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(rotation),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // 刷新中的提示文字，带白色背景
                            Text(
                                text = "单词本刷新中，请稍候",
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
                            // 自定义下拉指示器 - 使用自定义刷新图标
                            // 根据下拉距离计算旋转角度
                            val rotation = 360f * pullToRefreshState.distanceFraction
                            
                            Icon(
                                imageVector = RefreshIcon,
                                contentDescription = "下拉刷新",
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(rotation),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // 下拉提示文字，带白色背景
                            Text(
                                text = "松手后，自动刷新单词本",
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
        LazyColumn(
            state = listState
        ) {
            items(words) { wordEntity_7ree ->
            HistoryWordItem_7ree(
                wordEntity_7ree = wordEntity_7ree,
                onWordClick_7ree = onWordClick,
                onFavoriteToggle_7ree = onFavoriteToggle,
                onDismiss_7ree = { onWordDelete(wordEntity_7ree) },
                onWordSpeak_7ree = onWordSpeak
            )
            Spacer(modifier = Modifier.height(8.dp))
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
                            text = "加载更多...",
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
                    text = "向左滑动单词条目，然后点击",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "删除",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "图标可将其删除",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 如果没有更多数据，显示到底提示
            if (!hasMoreData && words.isNotEmpty()) {
                Text(
                    text = "已显示全部单词",
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