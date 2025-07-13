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
        // 按需加载单词计数和总查阅次数
        wordQueryViewModel_7ree.loadWordCount_7ree()
        wordQueryViewModel_7ree.loadTotalViews_7ree()
        
        // 加载历史单词列表
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
    
    // 数字动效 - 统一在1秒内完成
    LaunchedEffect(stats_7ree) {
        val animationDuration = 1000 // 动画时长（毫秒）

        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = animationDuration, easing = EaseOutCubic)
        ) { progress, _ ->
            animatedValues_7ree = DashboardStats_7ree(
                totalWords = (stats_7ree.totalWords * progress).toInt(),
                totalViews = (stats_7ree.totalViews * progress).toInt(),
                favoriteWords = (stats_7ree.favoriteWords * progress).toInt(),
                studyDays = (stats_7ree.studyDays * progress).toInt()
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
    }
}

@Composable
private fun StatisticsCards_7ree(stats_7ree: DashboardStats_7ree) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 单词总数卡片 - 蓝色系（原来收藏总数的颜色）
        StatCard_7ree(
            modifier = Modifier.weight(1f),
            value = stats_7ree.totalWords,
            label = "单词总数",
            gradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF191970), // 深蓝色（原来收藏总数的颜色）
                    Color(0xFF4169E1)  // 深蓝色到蓝色
                )
            ),
            numberColor = Color(0xFF191970) // 深蓝色
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
        
        // 收藏总数卡片 - 冷色系（青色，原来学习天数的颜色）
        StatCard_7ree(
            modifier = Modifier.weight(1f),
            value = stats_7ree.favoriteWords,
            label = "收藏总数",
            gradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF008B8B), // 深青色（原来学习天数的颜色）
                    Color(0xFF20B2AA)  // 深青色到青色
                )
            ),
            numberColor = Color(0xFF008B8B) // 深青色
        )
        
        // 学习天数卡片 - 暖色系（红色，原来收藏总数的颜色）
        StatCard_7ree(
            modifier = Modifier.weight(1f),
            value = stats_7ree.studyDays,
            label = "学习天数",
            gradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF8B0000), // 深红色（原来收藏总数的颜色）
                    Color(0xFFDC143C)  // 深红色到红色
                )
            ),
            numberColor = Color(0xFF8B0000) // 深红色
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
private fun MonthlyChart_7ree(words_7ree: List<WordEntity_7ree>) {
    // 使用新的MonthlyChartComponent_7ree组件
    MonthlyChartComponent_7ree(words_7ree = words_7ree)
}



// 数据类
data class DashboardStats_7ree(
    val totalWords: Int,
    val totalViews: Int,
    val favoriteWords: Int,
    val studyDays: Int
)