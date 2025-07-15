package com.x7ree.wordcard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 统计卡片组件
 * 用于显示仪表盘中的各种统计数据
 */
@Composable
fun StatCard_7ree(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    gradient: Brush,
    numberColor: Color
) {
    val lightGrayColor = Color(0xFFF5F5F5)
    
    Card(
        modifier = modifier
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = lightGrayColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
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
                // 检查是否包含小数点，如果有则使用特殊显示
                if (value.contains(".")) {
                    DecimalNumberText_7ree(
                        value = value,
                        color = numberColor
                    )
                } else {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = numberColor,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // 标签部分 - 渐变背景
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(gradient),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * 小数数字文本组件
 * 用于显示带小数的数字，整数部分和小数部分使用不同字号
 */
@Composable
fun DecimalNumberText_7ree(
    value: String,
    color: Color
) {
    val parts = value.split(".")
    if (parts.size == 2) {
        val integerPart = parts[0]
        var decimalPart = parts[1]
        
        // 确保小数部分为两位，不足时补零
        decimalPart = decimalPart.padEnd(2, '0').take(2)
        
        val annotatedString = buildAnnotatedString {
            // 整数部分 - 22sp
            withStyle(style = SpanStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )) {
                append(integerPart)
            }
            
            // 小数点 - 16sp
            withStyle(style = SpanStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )) {
                append(".")
            }
            
            // 小数部分 - 14sp
            withStyle(style = SpanStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )) {
                append(decimalPart)
            }
        }
        
        Text(
            text = annotatedString,
            textAlign = TextAlign.Center
        )
    } else {
        // 如果不是标准的小数格式，使用普通显示
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}