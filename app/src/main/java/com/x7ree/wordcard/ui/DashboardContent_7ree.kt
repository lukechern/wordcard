package com.x7ree.wordcard.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.data.WordEntity_7ree
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay

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
    var allWords_7ree by remember { mutableStateOf<List<WordEntity_7ree>>(emptyList()) }
    var animatedValues_7ree by remember { mutableStateOf(DashboardStats_7ree(0, 0, 0, 0)) }
    
    // 加载数据
    LaunchedEffect(Unit) {
        wordQueryViewModel_7ree.getHistoryWords_7ree().collect { words_7ree ->
            allWords_7ree = words_7ree
        }
    }
    
    // 计算统计数据
    val stats_7ree = remember(allWords_7ree) {
        val totalWords = allWords_7ree.size
        val totalViews = allWords_7ree.sumOf { it.viewCount }
        val favoriteWords = allWords_7ree.count { it.isFavorite }
        val studyDays = calculateStudyDays_7ree(allWords_7ree)
        DashboardStats_7ree(totalWords, totalViews, favoriteWords, studyDays)
    }
    
    // 数字动效 - 相同速度跳动，同时开始但不同时结束
    LaunchedEffect(stats_7ree) {
        val baseDuration = 1000L // 基础动画时长（毫秒）
        val speedPerUnit = 50L // 每个数字单位需要的毫秒数
        
        // 计算每个数字的动画时长
        val totalWordsDuration = baseDuration + (stats_7ree.totalWords * speedPerUnit)
        val totalViewsDuration = baseDuration + (stats_7ree.totalViews * speedPerUnit)
        val favoriteWordsDuration = baseDuration + (stats_7ree.favoriteWords * speedPerUnit)
        val studyDaysDuration = baseDuration + (stats_7ree.studyDays * speedPerUnit)
        
        // 使用最长的动画时长作为总时长
        val maxDuration = Math.max(Math.max(totalWordsDuration, totalViewsDuration), Math.max(favoriteWordsDuration, studyDaysDuration))
        
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(maxDuration.toInt(), easing = EaseOutCubic)
        ) { progress, _ ->
            // 计算每个数字的当前值，根据各自的动画时长
            val currentTotalWords = if (progress * maxDuration <= totalWordsDuration) {
                (stats_7ree.totalWords * progress * maxDuration / totalWordsDuration).toInt()
            } else {
                stats_7ree.totalWords
            }
            
            val currentTotalViews = if (progress * maxDuration <= totalViewsDuration) {
                (stats_7ree.totalViews * progress * maxDuration / totalViewsDuration).toInt()
            } else {
                stats_7ree.totalViews
            }
            
            val currentFavoriteWords = if (progress * maxDuration <= favoriteWordsDuration) {
                (stats_7ree.favoriteWords * progress * maxDuration / favoriteWordsDuration).toInt()
            } else {
                stats_7ree.favoriteWords
            }
            
            val currentStudyDays = if (progress * maxDuration <= studyDaysDuration) {
                (stats_7ree.studyDays * progress * maxDuration / studyDaysDuration).toInt()
            } else {
                stats_7ree.studyDays
            }
            
            animatedValues_7ree = DashboardStats_7ree(
                totalWords = currentTotalWords,
                totalViews = currentTotalViews,
                favoriteWords = currentFavoriteWords,
                studyDays = currentStudyDays
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
            DailyChart_7ree(allWords_7ree)
        }
        
        // 12个月柱状图
        item {
            MonthlyChart_7ree(allWords_7ree)
        }
    }
}

@Composable
private fun StatisticsCards_7ree(stats_7ree: DashboardStats_7ree) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 单词总数卡片 - 暖色系（红色）
        StatCard_7ree(
            modifier = Modifier.weight(1f),
            value = stats_7ree.totalWords,
            label = "单词总数",
            gradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF8B0000), // 深红色
                    Color(0xFFDC143C)  // 深红色到红色
                )
            ),
            numberColor = Color(0xFF8B0000) // 深红色
        )
        
        // 查阅总数卡片 - 橙色系
        StatCard_7ree(
            modifier = Modifier.weight(1f),
            value = stats_7ree.totalViews,
            label = "查阅总数",
            gradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFD2691E), // 深橙色
                    Color(0xFFFF8C00)  // 深橙色到橙色
                )
            ),
            numberColor = Color(0xFFD2691E) // 深橙色
        )
        
        // 收藏总数卡片 - 蓝色系
        StatCard_7ree(
            modifier = Modifier.weight(1f),
            value = stats_7ree.favoriteWords,
            label = "收藏总数",
            gradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF191970), // 深蓝色
                    Color(0xFF4169E1)  // 深蓝色到蓝色
                )
            ),
            numberColor = Color(0xFF191970) // 深蓝色
        )
        
        // 学习天数卡片 - 冷色系（青色）
        StatCard_7ree(
            modifier = Modifier.weight(1f),
            value = stats_7ree.studyDays,
            label = "学习天数",
            gradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF008B8B), // 深青色
                    Color(0xFF20B2AA)  // 深青色到青色
                )
            ),
            numberColor = Color(0xFF008B8B) // 深青色
        )
    }
}

@Composable
private fun StatCard_7ree(
    modifier: Modifier = Modifier,
    value: Int,
    label: String,
    gradient: Brush,
    numberColor: Color
) {
    val lightGrayColor = Color(0xFFF5F5F5)
    
    Card(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = lightGrayColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 数字部分 - 浅灰色背景
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(lightGrayColor)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = numberColor,
                    fontSize = 22.sp
                )
            }
            
            // 标签文字部分 - 有背景
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradient)
                    .padding(vertical = 6.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun DailyChart_7ree(words_7ree: List<WordEntity_7ree>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp) // 移除水平padding，让图表区域最大化
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            Text(
                text = "近30天每日统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 简化的图表占位符
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF667eea).copy(alpha = 0.1f),
                                Color(0xFFf093fb).copy(alpha = 0.1f)
                            )
                        ),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📊 曲线图区域\n收集单词 & 查阅统计",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MonthlyChart_7ree(words_7ree: List<WordEntity_7ree>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp) // 移除水平padding，让图表区域最大化
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            Text(
                text = "近1年月度统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 简化的图表占位符
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4facfe).copy(alpha = 0.1f),
                                Color(0xFF00f2fe).copy(alpha = 0.1f)
                            )
                        ),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📈 柱状图区域\n月度数据统计",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}



// 数据类
data class DashboardStats_7ree(
    val totalWords: Int,
    val totalViews: Int,
    val favoriteWords: Int,
    val studyDays: Int
) 