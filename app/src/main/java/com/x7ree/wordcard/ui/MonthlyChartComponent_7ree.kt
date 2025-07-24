package com.x7ree.wordcard.ui

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
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

@Composable
private fun MonthlyChartCanvas_7ree(
    words_7ree: List<WordEntity_7ree>,
    modifier: Modifier = Modifier
) {
    val chartData_7ree = remember(words_7ree) {
        // 使用真实数据生成图表
        generateMonthlyChartData_7ree(words_7ree)
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
        val hasRealData = chartData_7ree.any { it.wordCount > 0 || it.viewCount > 0 || it.spellingCount > 0 }
        
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
                            offset.x <= (60f + 20f + 120f + legendClickRadius) &&
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
                val maxValue = maxOf(maxWordCount, maxViewCount, maxSpellingCount, 1)
                
                // 绘制坐标轴
                drawAxes_7ree(width, height, padding, chartData_7ree, maxValue)
                
                // 绘制背景网格
                drawGrid_7ree(width, height, padding, chartData_7ree.size)
                
                // 绘制柱状图（根据选中状态）
                drawBars_7ree(
                    chartData_7ree,
                    maxValue,
                    chartWidth,
                    chartHeight,
                    padding,
                    height,
                    animationProgress,
                    selectedLegend
                )
                
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
    height: Float,
    animationProgress: Float,
    selectedLegend: String? = null
) {
    if (data.isEmpty()) return
    
    // 每个月份的宽度
    val monthWidth = chartWidth / data.size
    // 每个柱子的宽度（各占33.3%的x轴格子）
    val barWidth = monthWidth / 3f
    
    // 绘制每个月的数据
    for (i in data.indices) {
        val monthX = padding + (monthWidth * i)
        val centerX = monthX + monthWidth / 2
        
        // 根据选中状态决定绘制哪些柱子
        when (selectedLegend) {
            "word" -> {
                // 只显示单词数量柱子，居中显示
                val wordCount = data[i].wordCount
                val targetWordBarHeight = if (maxValue > 0) (chartHeight * wordCount / maxValue) else 0f
                val animatedWordBarHeight = targetWordBarHeight * animationProgress
                val wordBarX = centerX - barWidth / 2  // 居中显示
                val wordBarY = height - padding - 80f - animatedWordBarHeight
                
                drawRect(
                    color = Color(0xFF191970),
                    topLeft = androidx.compose.ui.geometry.Offset(wordBarX, wordBarY),
                    size = androidx.compose.ui.geometry.Size(barWidth, animatedWordBarHeight)
                )
            }
            "view" -> {
                // 只显示查阅次数柱子，居中显示
                val viewCount = data[i].viewCount
                val targetViewBarHeight = if (maxValue > 0) (chartHeight * viewCount / maxValue) else 0f
                val animatedViewBarHeight = targetViewBarHeight * animationProgress
                val viewBarX = centerX - barWidth / 2  // 居中显示
                val viewBarY = height - padding - 80f - animatedViewBarHeight
                
                drawRect(
                    color = Color(0xFFD2691E),
                    topLeft = androidx.compose.ui.geometry.Offset(viewBarX, viewBarY),
                    size = androidx.compose.ui.geometry.Size(barWidth, animatedViewBarHeight)
                )
            }
            "spelling" -> {
                // 只显示拼写练习柱子，居中显示
                val spellingCount = data[i].spellingCount
                val targetSpellingBarHeight = if (maxValue > 0) (chartHeight * spellingCount / maxValue) else 0f
                val animatedSpellingBarHeight = targetSpellingBarHeight * animationProgress
                val spellingBarX = centerX - barWidth / 2  // 居中显示
                val spellingBarY = height - padding - 80f - animatedSpellingBarHeight
                
                drawRect(
                    color = Color(0xFF228B22),
                    topLeft = androidx.compose.ui.geometry.Offset(spellingBarX, spellingBarY),
                    size = androidx.compose.ui.geometry.Size(barWidth, animatedSpellingBarHeight)
                )
            }
            else -> {
                // 显示所有柱子
                // 单词数量柱子（左1/3部分）- 蓝色
                val wordCount = data[i].wordCount
                val targetWordBarHeight = if (maxValue > 0) (chartHeight * wordCount / maxValue) else 0f
                val animatedWordBarHeight = targetWordBarHeight * animationProgress
                val wordBarX = monthX
                val wordBarY = height - padding - 80f - animatedWordBarHeight
                
                drawRect(
                    color = Color(0xFF191970),
                    topLeft = androidx.compose.ui.geometry.Offset(wordBarX, wordBarY),
                    size = androidx.compose.ui.geometry.Size(barWidth, animatedWordBarHeight)
                )
                
                // 查阅次数柱子（中1/3部分）- 橙色
                val viewCount = data[i].viewCount
                val targetViewBarHeight = if (maxValue > 0) (chartHeight * viewCount / maxValue) else 0f
                val animatedViewBarHeight = targetViewBarHeight * animationProgress
                val viewBarX = monthX + barWidth
                val viewBarY = height - padding - 80f - animatedViewBarHeight
                
                drawRect(
                    color = Color(0xFFD2691E),
                    topLeft = androidx.compose.ui.geometry.Offset(viewBarX, viewBarY),
                    size = androidx.compose.ui.geometry.Size(barWidth, animatedViewBarHeight)
                )
                
                // 拼写练习柱子（右1/3部分）- 绿色
                val spellingCount = data[i].spellingCount
                val targetSpellingBarHeight = if (maxValue > 0) (chartHeight * spellingCount / maxValue) else 0f
                val animatedSpellingBarHeight = targetSpellingBarHeight * animationProgress
                val spellingBarX = monthX + barWidth * 2
                val spellingBarY = height - padding - 80f - animatedSpellingBarHeight
                
                drawRect(
                    color = Color(0xFF228B22),
                    topLeft = androidx.compose.ui.geometry.Offset(spellingBarX, spellingBarY),
                    size = androidx.compose.ui.geometry.Size(barWidth, animatedSpellingBarHeight)
                )
            }
        }
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
    height: Float,
    padding: Float,
    selectedLegend: String? = null
) {
    // 图例位置调整到X轴下方
    val legendX = padding + 20f // 距离左边缘20px
    val legendY = height - padding + 40f // 在X轴下方40f的位置
    
    // 计算透明度和大小
    val wordAlpha = if (selectedLegend == null || selectedLegend == "word") 1f else 0.3f
    val viewAlpha = if (selectedLegend == null || selectedLegend == "view") 1f else 0.3f
    val spellingAlpha = if (selectedLegend == null || selectedLegend == "spelling") 1f else 0.3f
    
    // 绘制单词图例（方块）
    val wordSize = if (selectedLegend == "word") 28f else 24f
    drawRect(
        color = Color(0xFF191970).copy(alpha = wordAlpha), // 使用单词总数的深蓝色
        topLeft = androidx.compose.ui.geometry.Offset(legendX, legendY - wordSize/2),
        size = androidx.compose.ui.geometry.Size(wordSize, wordSize)
    )
    
    // 绘制查阅图例（方块），调整位置
    val viewSize = if (selectedLegend == "view") 28f else 24f
    drawRect(
        color = Color(0xFFD2691E).copy(alpha = viewAlpha), // 使用查阅总数的深橙色
        topLeft = androidx.compose.ui.geometry.Offset(legendX + 220f, legendY - viewSize/2),
        size = androidx.compose.ui.geometry.Size(viewSize, viewSize)
    )
    
    // 绘制拼写练习图例（方块）
    val spellingSize = if (selectedLegend == "spelling") 28f else 24f
    drawRect(
        color = Color(0xFF228B22).copy(alpha = spellingAlpha), // 使用绿色表示拼写练习
        topLeft = androidx.compose.ui.geometry.Offset(legendX + 500f, legendY - spellingSize/2),
        size = androidx.compose.ui.geometry.Size(spellingSize, spellingSize)
    )
    
    // 使用drawIntoCanvas绘制图例文本
    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            textAlign = android.graphics.Paint.Align.LEFT
            isAntiAlias = true
        }
        
        // 绘制单词图例文本（深蓝色）
        paint.textSize = if (selectedLegend == "word") 40f else 35f
        paint.color = android.graphics.Color.argb(
            (255 * wordAlpha).toInt(),
            0x19, 0x19, 0x70
        )
        canvas.nativeCanvas.drawText(
            "收集单词",
            legendX + 30f,
            legendY + 12f,
            paint
        )
        
        // 绘制查阅图例文本（深橙色），调整位置
        paint.textSize = if (selectedLegend == "view") 40f else 35f
        paint.color = android.graphics.Color.argb(
            (255 * viewAlpha).toInt(),
            0xD2, 0x69, 0x1E
        )
        canvas.nativeCanvas.drawText(
            "查阅次数/10",
            legendX + 250f,
            legendY + 12f,
            paint
        )
        
        // 绘制拼写练习图例文本（绿色）
        paint.textSize = if (selectedLegend == "spelling") 40f else 35f
        paint.color = android.graphics.Color.argb(
            (255 * spellingAlpha).toInt(),
            0x22, 0x8B, 0x22
        )
        canvas.nativeCanvas.drawText(
            "拼写练习",
            legendX + 530f,
            legendY + 12f,
            paint
        )
    }
}

// 数据类用于存储月度统计数据
private data class MonthlyData_7ree(
    val month: String,
    val wordCount: Int,
    val viewCount: Int,
    val spellingCount: Int
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
    
    // 年度开始时间计算完成
    
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
        val viewCount = monthWords.sumOf { it.viewCount } / 10
        val spellingCount = monthWords.sumOf { it.spellingCount ?: 0 }
        
        result.add(MonthlyData_7ree(monthStr, wordCount, viewCount, spellingCount))
        
        // 月度统计数据计算完成
    }
    
    // 月度数据生成完成
    return result
}
