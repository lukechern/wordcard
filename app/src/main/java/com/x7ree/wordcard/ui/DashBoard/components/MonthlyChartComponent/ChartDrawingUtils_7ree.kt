package com.x7ree.wordcard.ui.DashBoard.components.MonthlyChartComponent

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas

internal fun DrawScope.drawGrid_7ree(
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
            start = Offset(padding, y),
            end = Offset(width - padding, y),
            strokeWidth = 1f
        )
    }
    
    // 绘制垂直网格线（每月一条）
    for (i in 0 until dataPoints) {
        val x = padding + (chartWidth * i / dataPoints)
        drawLine(
            color = Color.Gray.copy(alpha = 0.3f),
            start = Offset(x, padding),
            end = Offset(x, height - padding - 80f), // 为图例预留空间
            strokeWidth = 1f
        )
    }
}

internal fun DrawScope.drawBars_7ree(
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
                    topLeft = Offset(wordBarX, wordBarY),
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
                    topLeft = Offset(viewBarX, viewBarY),
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
                    topLeft = Offset(spellingBarX, spellingBarY),
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
                    topLeft = Offset(wordBarX, wordBarY),
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
                    topLeft = Offset(viewBarX, viewBarY),
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
                    topLeft = Offset(spellingBarX, spellingBarY),
                    size = androidx.compose.ui.geometry.Size(barWidth, animatedSpellingBarHeight)
                )
            }
        }
    }
}

internal fun DrawScope.drawAxes_7ree(
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
        start = Offset(padding, height - padding - 80f), // 为图例预留空间
        end = Offset(width - padding, height - padding - 80f),
        strokeWidth = 2f
    )
    
    // 绘制Y轴（垂直轴）
    drawLine(
        color = Color.Black,
        start = Offset(padding, padding),
        end = Offset(padding, height - padding - 80f), // 为图例预留空间
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
                start = Offset(x, height - padding - 80f), // 为图例预留空间
                end = Offset(x, height - padding - 70f), // 调整刻度线长度
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
                start = Offset(padding - 5f, y),
                end = Offset(padding, y),
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

internal fun DrawScope.drawLegend_7ree(
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
        topLeft = Offset(legendX, legendY - wordSize/2),
        size = androidx.compose.ui.geometry.Size(wordSize, wordSize)
    )
    
    // 绘制查阅图例（方块），调整位置
    val viewSize = if (selectedLegend == "view") 28f else 24f
    drawRect(
        color = Color(0xFFD2691E).copy(alpha = viewAlpha), // 使用查阅总数的深橙色
        topLeft = Offset(legendX + 220f, legendY - viewSize/2),
        size = androidx.compose.ui.geometry.Size(viewSize, viewSize)
    )
    
    // 绘制拼写练习图例（方块）
    val spellingSize = if (selectedLegend == "spelling") 28f else 24f
    drawRect(
        color = Color(0xFF228B22).copy(alpha = spellingAlpha), // 使用绿色表示拼写练习
        topLeft = Offset(legendX + 500f, legendY - spellingSize/2),
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
