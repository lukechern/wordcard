package com.x7ree.wordcard.ui.DashBoard.components.DailyChartComponent.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.x7ree.wordcard.ui.DashBoard.components.DailyChartComponent.DailyData_7ree

fun DrawScope.drawGrid_7ree(
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
            start = Offset(padding, y),
            end = Offset(width - padding, y),
            strokeWidth = 1f
        )
    }
    
    // 绘制垂直网格线（每天一条）
    for (i in 0 until dataPoints) {
        val x = padding + (chartWidth * i / (dataPoints - 1))
        drawLine(
            color = Color.Gray.copy(alpha = 0.3f),
            start = Offset(x, padding),
            end = Offset(x, height - padding - 80f), // 为图例预留空间
            strokeWidth = 1f
        )
    }
}

fun DrawScope.drawLine_7ree(
    data: List<Int>,
    maxValue: Int,
    chartWidth: Float,
    chartHeight: Float,
    padding: Float,
    color: Color,
    animationProgress: Float
) {
    if (data.isEmpty()) return
    
    val path = Path()
    val points = mutableListOf<Offset>()
    
    // 计算数据点
    for (i in data.indices) {
        val x = padding + (chartWidth * i / (data.size - 1))
        // 确保即使数据为0也能正确绘制
        val normalizedValue = if (maxValue > 0) data[i] else 0
        // 应用动画效果：从底部（0值位置）向上移动到目标位置
        val baseY = padding + chartHeight // 底部位置（0值）
        val targetY = padding + chartHeight - (chartHeight * normalizedValue / maxOf(maxValue, 1))
        val animatedY = baseY + (targetY - baseY) * animationProgress
        points.add(Offset(x, animatedY))
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
        
        // 绘制平滑线条（减少15%线条宽度）
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 9.21f) // 从10.84f再减少15%到9.21f
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

fun DrawScope.drawAxes_7ree(
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
        
        // 绘制X轴标签（星期几）
        for (i in chartData.indices) {
            val x = padding + (chartWidth * i / (chartData.size - 1))
            val label = chartData[i].date
            
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
        for (i in 0..4) { // 减少刻度数量，压缩Y轴刻度间距
            val y = padding + (chartHeight * i / 4) // 改为i/4
            val value = (maxValue * (4 - i) / 4) // 改为(4-i)/4
            
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

fun DrawScope.drawLegend_7ree(
    height: Float,
    padding: Float,
    selectedLegend: String? = null
) {
    // 图例位置调整到X轴下方
    val legendX = padding + 20f // 距离左边缘20px
    val legendY = height - padding + 40f // 在X轴下方40f的位置
    
    // 绘制单词图例（加大圆点）
    val wordAlpha = if (selectedLegend == null || selectedLegend == "word") 1f else 0.3f
    drawCircle(
        color = Color(0xFF191970).copy(alpha = wordAlpha), // 使用单词总数的深蓝色
        radius = if (selectedLegend == "word") 15f else 12f, // 选中时加大圆点
        center = Offset(legendX, legendY)
    )
    
    // 绘制查阅图例（加大圆点），增大两个图例之间的距离
    val viewAlpha = if (selectedLegend == null || selectedLegend == "view") 1f else 0.3f
    drawCircle(
        color = Color(0xFFD2691E).copy(alpha = viewAlpha), // 使用查阅总数的深橙色
        radius = if (selectedLegend == "view") 15f else 12f, // 选中时加大圆点
        center = Offset(legendX + 220f, legendY) // 调整位置
    )
    
    // 绘制拼写练习图例（加大圆点）
    val spellingAlpha = if (selectedLegend == null || selectedLegend == "spelling") 1f else 0.3f
    drawCircle(
        color = Color(0xFF228B22).copy(alpha = spellingAlpha), // 使用绿色表示拼写练习
        radius = if (selectedLegend == "spelling") 15f else 12f, // 选中时加大圆点
        center = Offset(legendX + 500f, legendY) // 进一步向右移动以避免重叠
    )
    
    // 使用drawIntoCanvas绘制图例文本
    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            textSize = 35f // 从30f增加到35f，进一步增大图例文字
            textAlign = android.graphics.Paint.Align.LEFT
            isAntiAlias = true
        }
        
        // 绘制单词图例文本（深蓝色）
        val wordColor = Color(0xFF191970).copy(alpha = wordAlpha)
        paint.color = android.graphics.Color.argb(
            (wordColor.alpha * 255).toInt(),
            (wordColor.red * 255).toInt(),
            (wordColor.green * 255).toInt(),
            (wordColor.blue * 255).toInt()
        )
        paint.textSize = if (selectedLegend == "word") 38f else 35f // 选中时加大文字
        canvas.nativeCanvas.drawText(
            "收集单词",
            legendX + 25f,
            legendY + 12f,
            paint
        )
        
        // 绘制查阅图例文本（深橙色）
        val viewColor = Color(0xFFD2691E).copy(alpha = viewAlpha)
        paint.color = android.graphics.Color.argb(
            (viewColor.alpha * 255).toInt(),
            (viewColor.red * 255).toInt(),
            (viewColor.green * 255).toInt(),
            (viewColor.blue * 255).toInt()
        )
        paint.textSize = if (selectedLegend == "view") 38f else 35f // 选中时加大文字
        canvas.nativeCanvas.drawText(
            "查阅次数/10",
            legendX + 245f,
            legendY + 12f,
            paint
        )
        
        // 绘制拼写练习图例文本（绿色）
        val spellingColor = Color(0xFF228B22).copy(alpha = spellingAlpha)
        paint.color = android.graphics.Color.argb(
            (spellingColor.alpha * 255).toInt(),
            (spellingColor.red * 255).toInt(),
            (spellingColor.green * 255).toInt(),
            (spellingColor.blue * 255).toInt()
        )
        paint.textSize = if (selectedLegend == "spelling") 38f else 35f // 选中时加大文字
        canvas.nativeCanvas.drawText(
            "拼写练习",
            legendX + 525f,
            legendY + 12f,
            paint
        )
    }
}
