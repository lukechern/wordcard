package com.x7ree.wordcard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.utils.CacheManager_7ree
import com.x7ree.wordcard.utils.DataStatistics_7ree
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

// 统计数据组件
@Composable
fun StatisticsComponent_7ree(
    wordQueryViewModel: WordQueryViewModel_7ree,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cacheManager_7ree = remember { CacheManager_7ree(context) }
    var cachedStats_7ree by remember { mutableStateOf(DataStatistics_7ree.StatisticsData_7ree(0, 0, 0, 0, 0.0f, 0.0f, 0, 0.0f, 0.0f)) }
    
    // 加载缓存的统计数据
    LaunchedEffect(Unit) {
        // 延迟加载统计数据，不阻塞UI显示
        delay(100) // 短暂延迟，让UI先渲染
        
        // 检查是否需要更新缓存
        if (cacheManager_7ree.shouldUpdateCache_7ree()) {
            // 需要更新缓存时，获取最新数据
            val allWords_7ree = wordQueryViewModel.getHistoryWords_7ree().first()
            cachedStats_7ree = DataStatistics_7ree.calculateStatistics_7ree(allWords_7ree)
            cacheManager_7ree.updateCacheTimestamp_7ree()
        } else {
            // 使用缓存数据，快速获取一次性数据
            val allWords_7ree = wordQueryViewModel.getHistoryWords_7ree().first()
            cachedStats_7ree = DataStatistics_7ree.calculateStatistics_7ree(allWords_7ree)
        }
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "已收集${cachedStats_7ree.totalWords}个单词",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp)) // 两个统计数据之间的间距
        Text(
            text = "已累计查阅${cachedStats_7ree.totalViews}次",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp)) // 两个统计数据之间的间距
        Text(
            text = "已持续学习${cachedStats_7ree.studyDays}天",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp)) // 在统计数据和底部之间增加一些间距
    }
}
