package com.x7ree.wordcard.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.data.WordEntity_7ree
import com.x7ree.wordcard.utils.DataStatistics_7ree
import com.x7ree.wordcard.utils.CacheManager_7ree

import kotlin.math.round

/**
语言包定义

    'pl_total_words_7r' => '单词总数',
    'pl_total_views_7r' => '查阅总数',
    'pl_favorite_words_7r' => '收藏总数',
    'pl_study_days_7r' => '学习天数',
    'pl_daily_stats_7r' => '近30天每日统计',
    'pl_monthly_stats_7r' => '近1年月度统计',
    'pl_words_7r' => '单词',
    'pl_views_7r' => '查阅',
    'pl_favorites_7r' => '收藏',
**/

@Composable
fun DashboardContent_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree
) {
    val context = LocalContext.current
    val cacheManager_7ree = remember { CacheManager_7ree(context) }
    
    var allWords_7ree by remember { mutableStateOf<List<WordEntity_7ree>>(emptyList()) }
    var stats_7ree by remember { mutableStateOf(DataStatistics_7ree.StatisticsData_7ree(0, 0, 0, 0, 0.0f, 0.0f)) }
    var animatedValues_7ree by remember { mutableStateOf(DataStatistics_7ree.StatisticsData_7ree(0, 0, 0, 0, 0.0f, 0.0f)) }
    var lastUpdateTime_7ree by remember { mutableStateOf(System.currentTimeMillis()) }
    var timeUntilNextUpdate_7ree by remember { mutableStateOf("") }
    
    val isManualUpdating_7ree by cacheManager_7ree.isManualUpdating_7ree.collectAsState()
    
    // 定时更新剩余时间显示
    LaunchedEffect(lastUpdateTime_7ree) {
        while (true) {
            timeUntilNextUpdate_7ree = cacheManager_7ree.formatTimeUntilNextUpdate_7ree()
            delay(1000) // 每秒更新一次
        }
    }
    
    // 数据加载函数
    suspend fun loadData_7ree() {
        // 确保数据源被初始化
        wordQueryViewModel_7ree.loadWordCount_7ree()
        wordQueryViewModel_7ree.loadTotalViews_7ree()
        
        // 等待数据加载完成
        delay(100)
        
        // 获取当前实时数据并缓存
        allWords_7ree = wordQueryViewModel_7ree.getHistoryWords_7ree().first()
        val wordCount = wordQueryViewModel_7ree.wordCount_7ree.first()
        val totalViews = wordQueryViewModel_7ree.totalViews_7ree.first()
        
        val baseStats = DataStatistics_7ree.calculateStatistics_7ree(allWords_7ree)
        stats_7ree = baseStats.copy(
            totalWords = wordCount,
            totalViews = totalViews
        )
    }
    
    // 初始加载数据
    LaunchedEffect(Unit) {
        loadData_7ree()
        lastUpdateTime_7ree = System.currentTimeMillis()
        cacheManager_7ree.updateCacheTimestamp_7ree()
    }
    
    // 自动更新检查
    LaunchedEffect(Unit) {
        while (true) {
            delay(60000) // 每分钟检查一次
            if (cacheManager_7ree.shouldUpdateCache_7ree()) {
                loadData_7ree()
                lastUpdateTime_7ree = System.currentTimeMillis()
                cacheManager_7ree.updateCacheTimestamp_7ree()
            }
        }
    }
    
    val coroutineScope = rememberCoroutineScope()
    
    // 手动更新函数
    fun manualUpdate_7ree() {
        if (!isManualUpdating_7ree) {
            coroutineScope.launch {
                cacheManager_7ree.forceUpdateCache_7ree {
                    loadData_7ree()
                    lastUpdateTime_7ree = System.currentTimeMillis()
                }
                // 显示更新成功提示
                wordQueryViewModel_7ree.setOperationResult_7ree("统计数据更新成功")
            }
        }
    }
    
    // 计算统计数据 - 使用缓存的数据
    // val stats_7ree = remember(allWords_7ree, cachedWordCount_7ree, cachedTotalViews_7ree) {
    //     val baseStats = DataStatistics_7ree.calculateStatistics_7ree(allWords_7ree)
    //     // 使用缓存的计数数据，而不是实时计算的数据
    //     baseStats.copy(
    //         totalWords = cachedWordCount_7ree,
    //         totalViews = cachedTotalViews_7ree
    //     )
    // }
    
    // 数字动效 - 统一在1秒内完成
    LaunchedEffect(stats_7ree) {
        val animationDuration = 1000 // 动画时长（毫秒）

        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = animationDuration, easing = EaseOutCubic)
        ) { progress, _ ->
            animatedValues_7ree = DataStatistics_7ree.StatisticsData_7ree(
                totalWords = (stats_7ree.totalWords * progress).toInt(),
                totalViews = (stats_7ree.totalViews * progress).toInt(),
                favoriteWords = (stats_7ree.favoriteWords * progress).toInt(),
                studyDays = (stats_7ree.studyDays * progress).toInt(),
                reviewRatio = round((stats_7ree.reviewRatio * progress) * 100) / 100,
                dailyStudy = round((stats_7ree.dailyStudy * progress) * 100) / 100
            )
        }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 统计卡片
        item {
            StatisticsCards_7ree(animatedValues_7ree)
        }
        
        // 30天曲线图
        item {
            DailyChartComponent_7ree(allWords_7ree)
        }
        
        // 12个月柱状图
        item {
            MonthlyChart_7ree(allWords_7ree)
        }
        
        // 缓存说明区域
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "统计数据缓存周期10分钟，上次更新：${cacheManager_7ree.getLastUpdateTime_7ree()}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.End
                    )
                    
                    Row(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clickable { manualUpdate_7ree() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isManualUpdating_7ree) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(10.dp),
                                strokeWidth = 1.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "更新中...",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "手动更新",
                                modifier = Modifier.size(10.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "手动更新",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticsCards_7ree(stats_7ree: DataStatistics_7ree.StatisticsData_7ree) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 第一行：3个卡片
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 单词总数卡片 - 蓝色系（冷色）
            StatCard_7ree(
                modifier = Modifier.weight(1f),
                value = stats_7ree.totalWords.toString(),
                label = "单词总数",
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF191970), // 深蓝色
                        Color(0xFF4169E1)  // 深蓝色到蓝色
                    )
                ),
                numberColor = Color(0xFF191970) // 深蓝色
            )
            
            // 查阅总数卡片 - 橙色系（暖色）
            StatCard_7ree(
                modifier = Modifier.weight(1f),
                value = stats_7ree.totalViews.toString(),
                label = "查阅总数",
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFD2691E), // 深橙色
                        Color(0xFFFF8C00)  // 深橙色到橙色
                    )
                ),
                numberColor = Color(0xFFD2691E) // 深橙色
            )
            
            // 收藏总数卡片 - 青色系（冷色）
            StatCard_7ree(
                modifier = Modifier.weight(1f),
                value = stats_7ree.favoriteWords.toString(),
                label = "收藏总数",
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF008B8B), // 深青色
                        Color(0xFF20B2AA)  // 深青色到青色
                    )
                ),
                numberColor = Color(0xFF008B8B) // 深青色
            )
        }
        
        // 第二行：3个卡片
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 学习天数卡片 - 紫色系（冷色）
            StatCard_7ree(
                modifier = Modifier.weight(1f),
                value = stats_7ree.studyDays.toString(),
                label = "学习天数",
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF4B0082), // 深紫色
                        Color(0xFF8A2BE2)  // 深紫色到紫色
                    )
                ),
                numberColor = Color(0xFF4B0082) // 深紫色
            )
            
            // 复习倍率卡片 - 红色系（暖色）
            StatCard_7ree(
                modifier = Modifier.weight(1f),
                value = DataStatistics_7ree.formatReviewRatio_7ree(stats_7ree.reviewRatio),
                label = "复习倍率",
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF8B0000), // 深红色
                        Color(0xFFDC143C)  // 深红色到红色
                    )
                ),
                numberColor = Color(0xFF8B0000) // 深红色
            )
            
            // 每日学习卡片 - 深绿色系（暖色）
            StatCard_7ree(
                modifier = Modifier.weight(1f),
                value = DataStatistics_7ree.formatDailyStudy_7ree(stats_7ree.dailyStudy),
                label = "每日查阅",
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF006400), // 更深的绿色
                        Color(0xFF228B22)  // 深绿色
                    )
                ),
                numberColor = Color(0xFF006400) // 更深的绿色
            )
        }
    }
}


@Composable
private fun MonthlyChart_7ree(words_7ree: List<WordEntity_7ree>) {
    // 使用新的MonthlyChartComponent_7ree组件
    MonthlyChartComponent_7ree(words_7ree = words_7ree)
}