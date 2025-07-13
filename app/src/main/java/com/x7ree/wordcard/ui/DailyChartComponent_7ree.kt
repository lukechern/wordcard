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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.data.WordEntity_7ree
import java.text.SimpleDateFormat
import java.util.*

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
            defaultElevation = 4.dp
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

@Composable
private fun DailyChartCanvas_7ree(
    words_7ree: List<WordEntity_7ree>,
    modifier: Modifier = Modifier
) {
    val chartData_7ree = remember(words_7ree) {
        // 使用真实数据生成图表
        println("DEBUG: 使用真实数据生成图表，总单词数: ${words_7ree.size}")
        generateDailyChartData_7ree(words_7ree)
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
                
                // 绘制单词数量曲线（即使为0也绘制）
                drawLine_7ree(
                    chartData_7ree.map { it.wordCount },
                    maxValue,
                    chartWidth,
                    chartHeight,
                    padding,
                    Color(0xFF191970), // 使用单词总数的深蓝色
                    "单词"
                )
                
                // 绘制查阅次数曲线（即使为0也绘制）
                drawLine_7ree(
                    chartData_7ree.map { it.viewCount },
                    maxValue,
                    chartWidth,
                    chartHeight,
                    padding,
                    Color(0xFFD2691E), // 使用查阅总数的深橙色
                    "查阅"
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
    for (i in 0..4) { // 减少网格线数量，与Y轴刻度保持一致
        val y = padding + (chartHeight * i / 4) // 改为i/4
        drawLine(
            color = Color.Gray.copy(alpha = 0.3f),
            start = androidx.compose.ui.geometry.Offset(padding, y),
            end = androidx.compose.ui.geometry.Offset(width - padding, y),
            strokeWidth = 1f
        )
    }
    
    // 绘制垂直网格线（每天一条）
    for (i in 0 until dataPoints) {
        val x = padding + (chartWidth * i / (dataPoints - 1))
        drawLine(
            color = Color.Gray.copy(alpha = 0.3f),
            start = androidx.compose.ui.geometry.Offset(x, padding),
            end = androidx.compose.ui.geometry.Offset(x, height - padding - 80f), // 为图例预留空间
            strokeWidth = 1f
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLine_7ree(
    data: List<Int>,
    maxValue: Int,
    chartWidth: Float,
    chartHeight: Float,
    padding: Float,
    color: Color,
    label: String
) {
    if (data.isEmpty()) return
    
    val path = Path()
    val points = mutableListOf<androidx.compose.ui.geometry.Offset>()
    
    // 计算数据点
    for (i in data.indices) {
        val x = padding + (chartWidth * i / (data.size - 1))
        // 确保即使数据为0也能正确绘制
        val normalizedValue = if (maxValue > 0) data[i] else 0
        val y = padding + chartHeight - (chartHeight * normalizedValue / maxOf(maxValue, 1))
        points.add(androidx.compose.ui.geometry.Offset(x, y))
        

    }
    
    // 绘制平滑曲线路径
    if (points.size >= 2) {
        path.moveTo(points.first().x, points.first().y)
        
        // 使用三次贝塞尔曲线创建更平滑的连接
        for (i in 0 until points.size - 1) {
            val current = points[i]
            val next = points[i + 1]
            
            // 计算更平滑的控制点
            val tension = 0.3f // 控制曲线的张力
            
            // 第一个控制点：从当前点向右延伸
            val control1X = current.x + (next.x - current.x) * tension
            val control1Y = current.y
            
            // 第二个控制点：从下一个点向左延伸
            val control2X = next.x - (next.x - current.x) * tension
            val control2Y = next.y
            
            // 使用三次贝塞尔曲线
            path.cubicTo(control1X, control1Y, control2X, control2Y, next.x, next.y)
        }
        
        // 绘制平滑线条（加粗5倍）
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 12.75f) // 从15f减少15%到12.75f
        )
        
        // 绘制数据点（相应增大）
        for (point in points) {
            drawCircle(
                color = color, // 使用与线条相同的颜色
                radius = 10f, // 相应增大数据点
                center = point
            )
        }
    } else if (points.size == 1) {
        // 只有一个点时只绘制点
        drawCircle(
            color = color,
            radius = 8f,
            center = points.first()
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAxes_7ree(
    width: Float,
    height: Float,
    padding: Float,
    chartData: List<DailyData_7ree>,
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
        
        // 绘制X轴标签（星期几）
        for (i in chartData.indices) {
            val x = padding + (chartWidth * i / (chartData.size - 1))
            val label = chartData[i].date
            
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
        for (i in 0..4) { // 减少刻度数量，压缩Y轴刻度间距
            val y = padding + (chartHeight * i / 4) // 改为i/4
            val value = (maxValue * (4 - i) / 4) // 改为(4-i)/4
            
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
    
    // 绘制单词图例（加大圆点）
    drawCircle(
        color = Color(0xFF191970), // 使用单词总数的深蓝色
        radius = 12f, // 加大圆点
        center = androidx.compose.ui.geometry.Offset(legendX, legendY)
    )
    
    // 绘制查阅图例（加大圆点），增大两个图例之间的距离
    drawCircle(
        color = Color(0xFFD2691E), // 使用查阅总数的深橙色
        radius = 12f, // 加大圆点
        center = androidx.compose.ui.geometry.Offset(legendX + 200f, legendY) // 从150f增加到200f
    )
    
    // 使用drawIntoCanvas绘制图例文本
    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            textSize = 35f // 从30f增加到35f，进一步增大图例文字
            textAlign = android.graphics.Paint.Align.LEFT
            isAntiAlias = true
        }
        
        // 绘制单词图例文本（深蓝色）
        paint.color = android.graphics.Color.parseColor("#191970")
        canvas.nativeCanvas.drawText(
            "收集单词",
            legendX + 25f,
            legendY + 12f, // 从10f增加到12f，调整文字垂直位置
            paint
        )
        
        // 绘制查阅图例文本（深橙色），增大文字间距
        paint.color = android.graphics.Color.parseColor("#D2691E")
        canvas.nativeCanvas.drawText(
            "查阅次数",
            legendX + 225f, // 从175f增加到225f
            legendY + 12f, // 从10f增加到12f，调整文字垂直位置
            paint
        )
    }
}

// 数据类用于存储每日统计数据
private data class DailyData_7ree(
    val date: String,
    val wordCount: Int,
    val viewCount: Int
)

// 生成本周统计数据
private fun generateDailyChartData_7ree(words_7ree: List<WordEntity_7ree>): List<DailyData_7ree> {
    val dateFormat = SimpleDateFormat("E", Locale.getDefault()) // 使用星期几格式
    val result = mutableListOf<DailyData_7ree>()
    
    // 获取本周的开始日期（周一）
    val calendar = Calendar.getInstance()
    // 先获取当前日期
    val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    // 计算到本周一的天数差
    val daysToMonday = when (currentDayOfWeek) {
        Calendar.SUNDAY -> -6  // 周日到周一差6天
        Calendar.MONDAY -> 0   // 周一差0天
        Calendar.TUESDAY -> -1 // 周二到周一差1天
        Calendar.WEDNESDAY -> -2 // 周三到周一差2天
        Calendar.THURSDAY -> -3 // 周四到周一差3天
        Calendar.FRIDAY -> -4   // 周五到周一差4天
        Calendar.SATURDAY -> -5 // 周六到周一差5天
        else -> 0
    }
    
    // 设置到本周一
    calendar.add(Calendar.DAY_OF_YEAR, daysToMonday)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    
    val weekStart = calendar.timeInMillis
    
    println("DEBUG: 当前日期: ${Date()}")
    println("DEBUG: 本周开始时间: ${Date(weekStart)}")
    println("DEBUG: 当前星期: $currentDayOfWeek, 到周一差: $daysToMonday 天")
    
    // 生成本周7天的数据
    for (i in 0..6) {
        val currentDate = Calendar.getInstance().apply {
            timeInMillis = weekStart + (i * 24 * 60 * 60 * 1000L) // 每天增加24小时
        }
        
        val dateStr = dateFormat.format(currentDate.time)
        
        // 计算当天的单词数量和查阅次数
        val dayWords = words_7ree.filter { word ->
            val wordDate = Calendar.getInstance().apply {
                timeInMillis = word.queryTimestamp
            }
            
            // 比较年月日，忽略时分秒
            val isSameDay = wordDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                           wordDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)
            
            // 添加调试信息
            if (i == 0) { // 只在第一天打印调试信息
                println("DEBUG: 单词 '${word.word}' 时间戳: ${word.queryTimestamp}, 日期: ${wordDate.time}")
                println("DEBUG: 当前比较日期: ${currentDate.time}")
                println("DEBUG: 是否同一天: $isSameDay")
            }
            
            isSameDay
        }
        
        val wordCount = dayWords.size
        val viewCount = dayWords.sumOf { it.viewCount }
        
        result.add(DailyData_7ree(dateStr, wordCount, viewCount))
        
        // 添加调试信息
        println("DEBUG: ${dateStr} - 单词数: $wordCount, 查阅次数: $viewCount")
        if (dayWords.isNotEmpty()) {
            println("DEBUG: ${dateStr} 的单词: ${dayWords.map { it.word }}")
        }
    }
    
    println("DEBUG: 生成的数据: ${result.map { "${it.date}:${it.wordCount}/${it.viewCount}" }}")
    return result
}