package com.x7ree.wordcard.ui.DashBoard.components.MonthlyChartComponent

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.data.WordEntity_7ree

/**
语言包定义

    'pl_monthly_stats_7r' => '今年月度统计',
    'pl_words_7r' => '单词',
    'pl_views_7r' => '查阅',
    'pl_month_7r' => '月份',
    'pl_count_7r' => '数量',
**/

@Composable
fun MonthlyChartComponent_7ree(
    words_7ree: List<WordEntity_7ree>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
            .height(300.dp),
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
                text = "年度统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 使用Compose Canvas绘制柱状图
            MonthlyChartCanvas_7ree(
                words_7ree = words_7ree,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}
