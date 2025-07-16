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
    var stats_7ree by remember { mutableStateOf(DataStatistics_7ree.StatisticsData_7ree(0, 0, 0, 0, 0.0f, 0.0f, 0, 0.0f, 0.0f)) }
    var animatedValues_7ree by remember { mutableStateOf(DataStatistics_7ree.StatisticsData_7ree(0, 0, 0, 0, 0.0f, 0.0f, 0, 0.0f, 0.0f)) }
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
        try {
            // 获取当前实时数据
            allWords_7ree = wordQueryViewModel_7ree.getHistoryWords_7ree().first()
            
            // 直接从数据库获取计数数据，而不依赖ViewModel的Flow
            val wordCount = if (allWords_7ree.isNotEmpty()) allWords_7ree.size else 0
            val totalViews = allWords_7ree.sumOf { it.viewCount }
            
            val baseStats = DataStatistics_7ree.calculateStatistics_7ree(allWords_7ree)
            stats_7ree = baseStats.copy(
                totalWords = wordCount,
                totalViews = totalViews
            )
            
            // 数据加载完成
        } catch (e: Exception) {
            // 数据加载失败: ${e.message}
            e.printStackTrace()
        }
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
    
    // 直接使用统计数据，不使用动画效果，避免数字显示为0的问题
    LaunchedEffect(stats_7ree) {
        animatedValues_7ree = stats_7ree
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 新的统计卡片网格
        item {
            NewStatisticsGrid_7ree(animatedValues_7ree)
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
private fun TestStatisticsTable_7ree(stats_7ree: DataStatistics_7ree.StatisticsData_7ree) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "统计数据测试表格",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 表格数据
            val tableData = listOf(
                "单词总数" to stats_7ree.totalWords.toString(),
                "学习天数" to stats_7ree.studyDays.toString(),
                "收藏总数" to stats_7ree.favoriteWords.toString(),
                "查阅总数" to stats_7ree.totalViews.toString(),
                "查阅倍率" to DataStatistics_7ree.formatReviewRatio_7ree(stats_7ree.reviewRatio),
                "每日查阅" to DataStatistics_7ree.formatDailyStudy_7ree(stats_7ree.dailyStudy),
                "拼写练习" to stats_7ree.totalSpelling.toString(),
                "拼写倍率" to DataStatistics_7ree.formatSpellingRatio_7ree(stats_7ree.spellingRatio),
                "每日拼写" to DataStatistics_7ree.formatDailySpelling_7ree(stats_7ree.dailySpelling)
            )
            
            tableData.forEachIndexed { index, (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${index + 1}. $label:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (index < tableData.size - 1) {
                    Divider(
                        modifier = Modifier.padding(vertical = 2.dp),
                        color = Color.Gray.copy(alpha = 0.3f)
                    )
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
        // 第一行：3个卡片 - 红色系（最深）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 单词总数卡片 - 极深红色
            StatCard_7ree(
                modifier = Modifier.weight(1f),
                value = stats_7ree.totalWords.toString(),
                label = "单词总数",
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF4B0000), // 极深红色
                        Color(0xFF8B0000)  // 极深红色到深红色
                    )
                ),
                numberColor = Color(0xFFDC143C) // 使用更明显的红色
            )
            
            // 学习天数卡片 - 很深红色（交换内容）
            StatCard_7ree(
                modifier = Modifier.weight(1f),
                value = stats_7ree.studyDays.toString(),
                label = "学习天数",
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF660000), // 很深红色
                        Color(0xFFB22222)  // 很深红色到深红色
                    )
                ),
                numberColor = Color(0xFFB22222) // 使用更明显的红色
            )
            
            // 收藏总数卡片 - 深红色
            StatCard_7ree(
                modifier = Modifier.weight(1f),
                value = stats_7ree.favoriteWords.toString(),
                label = "收藏总数",
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF8B0000), // 深红色
                        Color(0xFFDC143C)  // 深红色到红色
                    )
                ),
                numberColor = Color(0xFFDC143C) // 使用更明显的红色
            )
        }
        
        // 第二行：3个卡片 - 绿色系（中等深度）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 查阅总数卡片 - 较深绿色（交换内容）
            StatCard_7ree(
                modifier = Modifier.weight(1f),
                value = stats_7ree.totalViews.toString(),
                label = "查阅总数",
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF003300), // 较深绿色
                        Color(0xFF006400)  // 较深绿色到深绿色
                    )
                ),
                numberColor = Color(0xFF228B22) // 使用更明显的绿色
            )
            
            // 复习倍率卡片 - 中绿色
            StatCard_7ree(
                modifier = Modifier.weight(1f),
                value = DataStatistics_7ree.formatReviewRatio_7ree(stats_7ree.reviewRatio),
                label = "查阅倍率",
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF004400), // 深绿色
                        Color(0xFF228B22)  // 深绿色到绿色
                    )
                ),
                numberColor = Color(0xFF228B22) // 使用更明显的绿色
            )
            
            // 每日学习卡片 - 中绿色
            StatCard_7ree(
                modifier = Modifier.weight(1f),
                value = DataStatistics_7ree.formatDailyStudy_7ree(stats_7ree.dailyStudy),
                label = "每日查阅",
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF006400), // 中绿色
                        Color(0xFF228B22)  // 中绿色到绿色
                    )
                ),
                numberColor = Color(0xFF228B22) // 使用更明显的绿色
            )
        }
        
        // 第三行：3个卡片 - 蓝色系（原第一行深度）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 拼写练习卡片 - 深蓝色（原第一行第一列深度）
            StatCard_7ree(
                modifier = Modifier.weight(1f),
                value = stats_7ree.totalSpelling.toString(),
                label = "拼写练习",
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF000080), // 深蓝色
                        Color(0xFF4169E1)  // 深蓝色到蓝色
                    )
                ),
                numberColor = Color(0xFF4169E1) // 使用更明显的蓝色
            )
            
            // 拼写倍率卡片 - 中蓝色（原第一行第二列深度）
            StatCard_7ree(
                modifier = Modifier.weight(1f),
                value = DataStatistics_7ree.formatSpellingRatio_7ree(stats_7ree.spellingRatio),
                label = "拼写倍率",
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF191970), // 中蓝色
                        Color(0xFF6495ED)  // 中蓝色到矢车菊蓝
                    )
                ),
                numberColor = Color(0xFF4169E1) // 使用更明显的蓝色
            )
            
            // 每日拼写卡片 - 浅蓝色（原第一行第三列深度）
            StatCard_7ree(
                modifier = Modifier.weight(1f),
                value = DataStatistics_7ree.formatDailySpelling_7ree(stats_7ree.dailySpelling),
                label = "每日拼写",
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF4169E1), // 浅蓝色
                        Color(0xFF87CEEB)  // 浅蓝色到淡蓝
                    )
                ),
                numberColor = Color(0xFF4169E1) // 使用更明显的蓝色
            )
        }
    }
}


@Composable
private fun MonthlyChart_7ree(words_7ree: List<WordEntity_7ree>) {
    // 使用新的MonthlyChartComponent_7ree组件
    MonthlyChartComponent_7ree(words_7ree = words_7ree)
}