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
import com.x7ree.wordcard.utils.ArticleStatistics_7ree
import com.x7ree.wordcard.utils.CacheManager_7ree
import com.x7ree.wordcard.ui.DashBoard.components.DailyChartComponent.DailyChartComponent_7ree

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
    var articleStats_7ree by remember { mutableStateOf(ArticleStatistics_7ree.ArticleStatisticsData_7ree(0, 0, 0)) }
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
            
            // 输出调试信息
            println("DEBUG: 单词数量 = ${allWords_7ree.size}")
            println("DEBUG: wordQueryViewModel_7ree.articleViewModel_7ree is null = ${wordQueryViewModel_7ree.articleViewModel_7ree == null}")
            
            // 获取文章列表数据
            val articles_7ree = if (wordQueryViewModel_7ree.articleViewModel_7ree != null) {
                println("DEBUG: articleViewModel_7ree is not null, trying to get articles...")
                try {
                    val articleViewModel = wordQueryViewModel_7ree.articleViewModel_7ree!!
                    val usePaginationMode = articleViewModel.usePaginationMode.first()
                    println("DEBUG: usePaginationMode = $usePaginationMode")
                    
                    val articles = if (usePaginationMode) {
                        // 分页模式：使用 pagedArticles
                        articleViewModel.pagedArticles.first()
                    } else {
                        // 非分页模式：使用 articles
                        articleViewModel.articles.first()
                    }
                    println("DEBUG: 成功获取文章列表，数量 = ${articles.size}")
                    articles
                } catch (e: Exception) {
                    println("DEBUG: 获取文章列表失败: ${e.message}")
                    emptyList()
                }
            } else {
                println("DEBUG: articleViewModel_7ree is null")
                emptyList()
            }
            
            // 输出每篇文章的viewCount和isFavorite信息
            for (article in articles_7ree) {
                println("DEBUG: 文章ID=${article.id}, 标题=${article.englishTitle}, 查阅次数=${article.viewCount}, 是否收藏=${article.isFavorite}")
            }
            
            // 计算文章统计数据
            articleStats_7ree = ArticleStatistics_7ree.calculateArticleStatistics_7ree(articles_7ree, allWords_7ree)
            
            // 输出计算结果
            println("DEBUG: 生成文章数 = ${articleStats_7ree.generatedArticles}")
            println("DEBUG: 查阅文章数 = ${articleStats_7ree.viewedArticles}")
            println("DEBUG: 收藏文章数 = ${articleStats_7ree.favoritedArticles}")
            
            // 数据加载完成
        } catch (e: Exception) {
            // 数据加载失败: ${e.message}
            println("DEBUG: 数据加载失败: ${e.message}")
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
            NewStatisticsGrid_7ree(animatedValues_7ree, articleStats_7ree)
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
private fun MonthlyChart_7ree(words_7ree: List<WordEntity_7ree>) {
    // 使用新的MonthlyChartComponent_7ree组件
    MonthlyChartComponent_7ree(words_7ree = words_7ree)
}
