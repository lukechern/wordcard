package com.x7ree.wordcard.ui.DashBoard.components.DailyChartComponent

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.data.WordEntity_7ree
import com.x7ree.wordcard.ui.DashBoard.components.DailyChartComponent.DailyChartCanvas_7ree

/**
语言包定义

    'pl_weekly_stats_7r' => '本周每日统计',
    'pl_words_7r' => '单词',
    'pl_views_7r' => '查阅',
    'pl_date_7r' => '日期',
    'pl_count_7r' => '数量',
**/

@Composable
fun DailyChartComponent_7ree(
    words_7ree: List<WordEntity_7ree>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
            .height(280.dp), // 降低30%高度（从400dp减少到280dp）
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp  // 增加阴影使其更明显
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "本周统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 使用Compose Canvas绘制图表
            DailyChartCanvas_7ree(
                words_7ree = words_7ree,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}
