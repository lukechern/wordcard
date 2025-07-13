package com.x7ree.wordcard.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.data.WordEntity_7ree
import java.text.SimpleDateFormat
import java.util.*

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
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "今年月度统计",
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

@Composable
private fun MonthlyChartCanvas_7ree(
    words_7ree: List<WordEntity_7ree>,
    modifier: Modifier = Modifier
) {
    val chartData_7ree = remember(words_7ree) {
        // 使用真实数据生成图表
        println("DEBUG: 使用真实数据生成月度图表，总单词数: ${words_7ree.size}")
        generateMonthlyChartData_7ree(words_7ree)
    }
    
    if (chartData_7ree.isNotEmpty()) {
        // 检查是否有实际数据（不是全为0）
        val hasRealData = chartData_7ree.any { it.wordCount > 0 || it.viewCount > 0 }
        
        if (hasRealData) {
            Canvas(modifier = modifier) {
                val width = size.width
                val height = size.height
                val padding = 60f // 增加padding以容纳坐标轴标签
                
                val chartWidth = width - 2 * padding
                val chartHeight = height - 2 * padding - 80f // 为图例预留80f空间
                
                // 计算数据范围
                val maxWordCount = chartData_7ree.maxOfOrNull { it.wordCount } ?: 0
                val maxViewCount = chartData_7ree.maxOfOrNull { it.viewCount } ?: 0
                val maxValue = maxOf(maxWordCount, maxViewCount, 1)
                
                // 绘制坐标轴
                drawAxes_7ree(width, height, padding, chartData_7ree, maxValue)
                
                // 绘制背景网格
                drawGrid_7ree(width, height, padding, chartData_7ree.size)
                
                // 绘制柱状图
                drawBars_7ree(
                    chartData_7ree,
                    maxValue,
                    chartWidth,
                    chartHeight,
                    padding,
                    height
                )
                
                // 绘制图例
                drawLegend_7ree(width, height, padding)
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

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGrid_7ree(
    width: Float,
    height: Float,
    padding: Float,
    dataPoints: Int
) {
    val chartWidth = width - 2 * padding
    val chartHeight = height - 2 * padding - 80f // 为图例预留80f空间
    
    // 绘制水平网格线
    for (i in 0..6) { // 7条水平线
        val y = padding + (chartHeight * i / 6)
        drawLine(
            color = Color.Gray.copy(alpha = 0.3f),
            start = androidx.compose.ui.geometry.Offset(padding, y),
            end = androidx.compose.ui.geometry.Offset(width - padding, y),
            strokeWidth = 1f
        )
    }
    
    // 绘制垂直网格线（每月一条）
    for (i in 0 until dataPoints) {
        val x = padding + (chartWidth * i / dataPoints)
        drawLine(
            color = Color.Gray.copy(alpha = 0.3f),
            start = androidx.compose.ui.geometry.Offset(x, padding),
            end = androidx.compose.ui.geometry.Offset(x, height - padding - 80f), // 为图例预留空间
            strokeWidth = 1f
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBars_7ree(
    data: List<MonthlyData_7ree>,
    maxValue: Int,
    chartWidth: Float,
    chartHeight: Float,
    padding: Float,
    height: Float
) {
    if (data.isEmpty()) return
    
    // 每个月份的宽度
    val monthWidth = chartWidth / data.size
    // 每个柱子的宽度（两个柱子并排，留有间距）
    val barWidth = monthWidth * 0.35f
    // 柱子间距
    val barSpacing = monthWidth * 0.1f
    
    // 绘制每个月的数据
    for (i in data.indices) {
        val monthX = padding + (monthWidth * i)
        val centerX = monthX + monthWidth / 2
        
        // 单词数量柱子（左侧）- 蓝色
        val wordCount = data[i].wordCount
        val wordBarHeight = if (maxValue > 0) (chartHeight * wordCount / maxValue) else 0f
        val wordBarX = centerX - barWidth - barSpacing / 2
        val wordBarY = height - padding - 80f - wordBarHeight // 为图例预留空间
        
        drawRect(
            color = Color(0xFF191970), // 深蓝色（单词总数的颜色）
            topLeft = androidx.compose.ui.geometry.Offset(wordBarX, wordBarY),
            size = androidx.compose.ui.geometry.Size(barWidth, wordBarHeight)
        )
        
        // 查阅次数柱子（右侧）- 橙色
        val viewCount = data[i].viewCount
        val viewBarHeight = if (maxValue > 0) (chartHeight * viewCount / maxValue) else 0f
        val viewBarX = centerX + barSpacing / 2
        val viewBarY = height - padding - 80f - viewBarHeight // 为图例预留空间
        
        drawRect(
            color = Color(0xFFD2691E), // 深橙色（查阅总数的颜色）
            topLeft = androidx.compose.ui.geometry.Offset(viewBarX, viewBarY),
            size = androidx.compose.ui.geometry.Size(barWidth, viewBarHeight)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAxes_7ree(
    width: Float,
    height: Float,
    padding: Float,
    chartData: List<MonthlyData_7ree>,
    maxValue: Int
) {
    val chartWidth = width - 2 * padding
    val chartHeight = height - 2 * padding - 80f // 为图例预留80f空间
    
    // 绘制X轴（水平轴）- 位置调整到图例上方
    drawLine(
        color = Color.Black,
        start = androidx.compose.ui.geometry.Offset(padding, height - padding - 80f), // 为图例预留空间
        end = androidx.compose.ui.geometry.Offset(width - padding, height - padding - 80f),
        strokeWidth = 2f
    )
    
    // 绘制Y轴（垂直轴）
    drawLine(
        color = Color.Black,
        start = androidx.compose.ui.geometry.Offset(padding, padding),
        end = androidx.compose.ui.geometry.Offset(padding, height - padding - 80f), // 为图例预留空间
        strokeWidth = 2f
    )
    
    // 使用drawIntoCanvas绘制文本
    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 30f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        
        // 绘制X轴标签（月份）
        for (i in chartData.indices) {
            // 每个月份的中心位置
            val monthWidth = chartWidth / chartData.size
            val x = padding + (monthWidth * i) + monthWidth / 2
            val label = chartData[i].month
            
            // 绘制刻度线
            drawLine(
                color = Color.Black,
                start = androidx.compose.ui.geometry.Offset(x, height - padding - 80f), // 为图例预留空间
                end = androidx.compose.ui.geometry.Offset(x, height - padding - 70f), // 调整刻度线长度
                strokeWidth = 1f
            )
            
            // 绘制X轴标签文本
            canvas.nativeCanvas.drawText(
                label,
                x,
                height - padding - 45f, // 调整文本位置
                paint
            )
        }
        
        // 绘制Y轴标签（数值）
        for (i in 0..6) { // 7个刻度
            val y = padding + (chartHeight * i / 6)
            val value = (maxValue * (6 - i) / 6)
            
            // 绘制刻度线
            drawLine(
                color = Color.Black,
                start = androidx.compose.ui.geometry.Offset(padding - 5f, y),
                end = androidx.compose.ui.geometry.Offset(padding, y),
                strokeWidth = 1f
            )
            
            // 绘制Y轴标签文本（跳过0值，避免与X轴重叠）
            if (value > 0) {
                paint.textAlign = android.graphics.Paint.Align.RIGHT
                canvas.nativeCanvas.drawText(
                    value.toString(),
                    padding - 10f,
                    y + 10f, // 调整垂直位置使文本居中对齐
                    paint
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLegend_7ree(
    width: Float,
    height: Float,
    padding: Float
) {
    // 图例位置调整到X轴下方
    val legendX = padding + 20f // 距离左边缘20px
    val legendY = height - padding + 40f // 在X轴下方40f的位置
    
    // 绘制单词图例（方块）
    drawRect(
        color = Color(0xFF191970), // 使用单词总数的深蓝色
        topLeft = androidx.compose.ui.geometry.Offset(legendX, legendY - 12f),
        size = androidx.compose.ui.geometry.Size(24f, 24f)
    )
    
    // 绘制查阅图例（方块），增大两个图例之间的距离
    drawRect(
        color = Color(0xFFD2691E), // 使用查阅总数的深橙色
        topLeft = androidx.compose.ui.geometry.Offset(legendX + 200f, legendY - 12f),
        size = androidx.compose.ui.geometry.Size(24f, 24f)
    )
    
    // 使用drawIntoCanvas绘制图例文本
    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            textSize = 35f // 增大图例文字
            textAlign = android.graphics.Paint.Align.LEFT
            isAntiAlias = true
        }
        
        // 绘制单词图例文本（深蓝色）
        paint.color = android.graphics.Color.parseColor("#191970")
        canvas.nativeCanvas.drawText(
            "收集单词",
            legendX + 30f,
            legendY + 12f,
            paint
        )
        
        // 绘制查阅图例文本（深橙色），增大文字间距
        paint.color = android.graphics.Color.parseColor("#D2691E")
        canvas.nativeCanvas.drawText(
            "查阅次数",
            legendX + 230f,
            legendY + 12f,
            paint
        )
    }
}

// 数据类用于存储月度统计数据
private data class MonthlyData_7ree(
    val month: String,
    val wordCount: Int,
    val viewCount: Int
)

// 生成年度月度统计数据
private fun generateMonthlyChartData_7ree(words_7ree: List<WordEntity_7ree>): List<MonthlyData_7ree> {
    val dateFormat = SimpleDateFormat("M月", Locale.getDefault()) // 使用月份格式
    val result = mutableListOf<MonthlyData_7ree>()
    
    // 获取当前年份的开始时间
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    
    // 设置到今年1月1日
    calendar.set(currentYear, Calendar.JANUARY, 1, 0, 0, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    
    val yearStart = calendar.timeInMillis
    
    println("DEBUG: 当前年份: $currentYear")
    println("DEBUG: 年度开始时间: ${Date(yearStart)}")
    
    // 生成12个月的数据
    for (i in 0..11) {
        val currentMonth = Calendar.getInstance().apply {
            set(currentYear, i, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val nextMonth = Calendar.getInstance().apply {
            set(currentYear, i + 1, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
            if (i == 11) { // 如果是12月，则设置为下一年的1月
                add(Calendar.YEAR, 1)
            }
        }
        
        val monthStr = dateFormat.format(currentMonth.time)
        
        // 计算当月的单词数量和查阅次数
        val monthWords = words_7ree.filter { word ->
            val wordTimestamp = word.queryTimestamp
            wordTimestamp >= currentMonth.timeInMillis && wordTimestamp < nextMonth.timeInMillis
        }
        
        val wordCount = monthWords.size
        val viewCount = monthWords.sumOf { it.viewCount }
        
        result.add(MonthlyData_7ree(monthStr, wordCount, viewCount))
        
        // 添加调试信息
        println("DEBUG: ${monthStr} - 单词数: $wordCount, 查阅次数: $viewCount")
    }
    
    println("DEBUG: 生成的月度数据: ${result.map { "${it.month}:${it.wordCount}/${it.viewCount}" }}")
    return result
}