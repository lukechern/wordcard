package com.x7ree.wordcard.ui.DashBoard.components.DailyChartComponent

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.data.WordEntity_7ree
import com.x7ree.wordcard.ui.DashBoard.components.DailyChartComponent.DailyData_7ree
import com.x7ree.wordcard.ui.DashBoard.components.DailyChartComponent.draw.drawAxes_7ree
import com.x7ree.wordcard.ui.DashBoard.components.DailyChartComponent.draw.drawGrid_7ree
import com.x7ree.wordcard.ui.DashBoard.components.DailyChartComponent.draw.drawLine_7ree
import com.x7ree.wordcard.ui.DashBoard.components.DailyChartComponent.draw.drawLegend_7ree
import com.x7ree.wordcard.ui.DashBoard.components.DailyChartComponent.generateDailyChartData_7ree

@Composable
fun DailyChartCanvas_7ree(
    words_7ree: List<WordEntity_7ree>,
    articles_7ree: List<com.x7ree.wordcard.data.ArticleEntity_7ree>,
    modifier: Modifier = Modifier
) {
    val chartData_7ree = remember(words_7ree, articles_7ree) {
        // 使用真实数据生成图表
        generateDailyChartData_7ree(words_7ree, articles_7ree)
    }
    
    // 动画进度状态
    var animationProgress by remember { mutableStateOf(0f) }
    
    // 图例点击状态管理
    var selectedLegend by remember { mutableStateOf<String?>(null) }
    
    // 启动动画
    LaunchedEffect(chartData_7ree) {
        animationProgress = 0f
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic)
        ) { value, _ ->
            animationProgress = value
        }
    }
    
    if (chartData_7ree.isNotEmpty()) {
        // 检查是否有实际数据（不是全为0）
        val hasRealData = chartData_7ree.any { it.wordCount > 0 || it.viewCount > 0 || it.spellingCount > 0 || it.articleCount > 0 }
        
        if (hasRealData) {
            Canvas(
                modifier = modifier.pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // 检测图例点击
                        val legendY = size.height - 60f + 40f
                        val legendClickRadius = 30f
                        
                        when {
                            // 检测单词图例点击
                            offset.x >= (60f + 20f - legendClickRadius) && 
                            offset.x <= (60f + 20f + 100f + legendClickRadius) &&
                            offset.y >= (legendY - legendClickRadius) && 
                            offset.y <= (legendY + legendClickRadius) -> {
                                selectedLegend = if (selectedLegend == "word") null else "word"
                            }
                            // 检测查阅图例点击
                            offset.x >= (60f + 20f + 220f - legendClickRadius) && 
                            offset.x <= (60f + 20f + 220f + 150f + legendClickRadius) &&
                            offset.y >= (legendY - legendClickRadius) && 
                            offset.y <= (legendY + legendClickRadius) -> {
                                selectedLegend = if (selectedLegend == "view") null else "view"
                            }
                            // 检测拼写练习图例点击
                            offset.x >= (60f + 20f + 500f - legendClickRadius) && 
                            offset.x <= (60f + 20f + 500f + 120f + legendClickRadius) &&
                            offset.y >= (legendY - legendClickRadius) && 
                            offset.y <= (legendY + legendClickRadius) -> {
                                selectedLegend = if (selectedLegend == "spelling") null else "spelling"
                            }
                        }
                    }
                }
            ) {
                val width = size.width
                val height = size.height
                val padding = 60f // 增加padding以容纳坐标轴标签
                
                val chartWidth = width - 2 * padding
                val chartHeight = height - 2 * padding - 80f // 为图例预留80f空间
                
                // 计算数据范围
                val maxWordCount = chartData_7ree.maxOfOrNull { it.wordCount } ?: 0
                val maxViewCount = chartData_7ree.maxOfOrNull { it.viewCount } ?: 0
                val maxSpellingCount = chartData_7ree.maxOfOrNull { it.spellingCount } ?: 0
                val maxArticleCount = chartData_7ree.maxOfOrNull { it.articleCount } ?: 0
                val maxValue = maxOf(maxWordCount, maxViewCount, maxSpellingCount, maxArticleCount, 1)
                
                // 绘制坐标轴
                drawAxes_7ree(width, height, padding, chartData_7ree, maxValue)
                
                // 绘制背景网格
                drawGrid_7ree(width, height, padding, chartData_7ree.size)
                
                // 根据选中状态绘制曲线
                when (selectedLegend) {
                    "word" -> {
                        // 只显示单词数量曲线
                        drawLine_7ree(
                            chartData_7ree.map { it.wordCount },
                            maxValue,
                            chartWidth,
                            chartHeight,
                            padding,
                            Color(0xFF191970),
                            animationProgress
                        )
                    }
                    "view" -> {
                        // 只显示生成文章数曲线
                        drawLine_7ree(
                            chartData_7ree.map { it.articleCount },
                            maxValue,
                            chartWidth,
                            chartHeight,
                            padding,
                            Color(0xFFD2691E),
                            animationProgress
                        )
                    }
                    "spelling" -> {
                        // 只显示拼写练习曲线
                        drawLine_7ree(
                            chartData_7ree.map { it.spellingCount },
                            maxValue,
                            chartWidth,
                            chartHeight,
                            padding,
                            Color(0xFF228B22),
                            animationProgress
                        )
                    }
                    else -> {
                        // 显示所有曲线
                        drawLine_7ree(
                            chartData_7ree.map { it.wordCount },
                            maxValue,
                            chartWidth,
                            chartHeight,
                            padding,
                            Color(0xFF191970),
                            animationProgress
                        )
                        
                        drawLine_7ree(
                            chartData_7ree.map { it.articleCount },
                            maxValue,
                            chartWidth,
                            chartHeight,
                            padding,
                            Color(0xFFD2691E),
                            animationProgress
                        )
                        
                        drawLine_7ree(
                            chartData_7ree.map { it.spellingCount },
                            maxValue,
                            chartWidth,
                            chartHeight,
                            padding,
                            Color(0xFF228B22),
                            animationProgress
                        )
                    }
                }
                
                // 绘制图例（传递选中状态）
                drawLegend_7ree(height, padding, selectedLegend)
            }
        } else {
            // 如果没有数据，显示占位符
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF191970).copy(alpha = 0.1f),
                                Color(0xFFD2691E).copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📊 暂无学习数据\n请添加单词开始学习",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        // 如果没有数据，显示占位符
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF191970).copy(alpha = 0.1f),
                            Color(0xFFD2691E).copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📊 暂无学习数据\n请添加单词开始学习",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
