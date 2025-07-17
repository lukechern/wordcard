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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.data.WordEntity_7ree

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
    modifier: Modifier = Modifier
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
    
    LazyColumn(
        state = listState,
        modifier = modifier
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