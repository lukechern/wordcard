package com.x7ree.wordcard.ui

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.utils.DataStatistics_7ree
import kotlin.math.roundToInt

private enum class AnimationState_7ree { Initial, Final }

/**
 * 带小数部分缩放的数字文本组件
 * 小数部分字体大小为整数部分的60%（缩小40%）
 */
@Composable
fun DecimalNumberText_7ree(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight,
    color: Color,
    textAlign: TextAlign,
    letterSpacing: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier
) {
    val annotatedString = buildAnnotatedString {
        if (text.contains(".")) {
            val parts = text.split(".")
            // 整数部分
            withStyle(style = SpanStyle(
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = color,
                letterSpacing = letterSpacing
            )) {
                append(parts[0])
            }
            // 小数点
            withStyle(style = SpanStyle(
                fontSize = fontSize * 0.6f,
                fontWeight = fontWeight,
                color = color,
                letterSpacing = letterSpacing
            )) {
                append(".")
            }
            // 小数部分（缩小40%）
            withStyle(style = SpanStyle(
                fontSize = fontSize * 0.6f,
                fontWeight = fontWeight,
                color = color,
                letterSpacing = letterSpacing
            )) {
                append(parts[1])
            }
        } else {
            // 没有小数点的情况
            withStyle(style = SpanStyle(
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = color,
                letterSpacing = letterSpacing
            )) {
                append(text)
            }
        }
    }
    
    Text(
        text = annotatedString,
        textAlign = textAlign,
        modifier = modifier
    )
}

/**
 * 新的统计卡片组件
 * 采用更简单直接的布局方式，确保数字清晰显示
 */
@Composable
fun NewStatCard_7ree(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    gradient: Brush,
    numberColor: Color = Color(0xFF333333),
    labelColor: Color = Color(0xFF666666)
) {
    // 解析数字值用于动画
    val numericValue = remember(value) {
        value.replace("[^0-9.]".toRegex(), "").toFloatOrNull() ?: 0f
    }
    
    // Animation state management
    var animationState_7ree by rememberSaveable(key = label) { mutableStateOf(AnimationState_7ree.Initial) }
    LaunchedEffect(Unit) {
        animationState_7ree = AnimationState_7ree.Final
    }

    val transition_7ree = updateTransition(targetState = animationState_7ree, label = "${label}_transition")
    val animatedValue by transition_7ree.animateFloat(
        label = "${label}_value",
        transitionSpec = {
            when {
                AnimationState_7ree.Initial isTransitioningTo AnimationState_7ree.Final ->
                    tween(durationMillis = 1000)
                else ->
                    snap()
            }
        }
    ) { state ->
        when (state) {
            AnimationState_7ree.Initial -> 0f
            AnimationState_7ree.Final -> numericValue
        }
    }
    
    // 格式化显示值
    val displayValue = remember(animatedValue, value) {
        if (value.contains(".")) {
            // 如果原值包含小数点，保持小数格式
            String.format("%.1f", animatedValue)
        } else {
            // 如果原值是整数，显示整数
            animatedValue.roundToInt().toString()
        }
    }
    
    Card(
        modifier = modifier
            .height(78.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 数字区域 - 纯白色背景，高度降低30%
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
                    .background(Color.White)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                DecimalNumberText_7ree(
                    text = displayValue,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = numberColor,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.2.sp
                )
            }
            
            // 标题区域 - 渐变背景效果
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.34f)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                labelColor,
                                labelColor.copy(
                                    red = (labelColor.red * 0.7f).coerceAtLeast(0f),
                                    green = (labelColor.green * 0.7f).coerceAtLeast(0f),
                                    blue = (labelColor.blue * 0.7f).coerceAtLeast(0f)
                                )
                            )
                        )
                    )
                    .padding(vertical = 2.dp, horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

/**
 * 新的统计卡片网格组件
 * 用于显示9个统计数据卡片
 */
@Composable
fun NewStatisticsGrid_7ree(stats_7ree: DataStatistics_7ree.StatisticsData_7ree) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 第一行：3个卡片 - 蓝色主题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NewStatCard_7ree(
                modifier = Modifier.weight(1f),
                value = stats_7ree.totalWords.toString(),
                label = "单词总数",
                gradient = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                ),
                numberColor = Color(0xFF1976D2),
                labelColor = Color(0xFF1976D2)
            )
            
            NewStatCard_7ree(
                modifier = Modifier.weight(1f),
                value = stats_7ree.studyDays.toString(),
                label = "学习天数",
                gradient = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                ),
                numberColor = Color(0xFF1976D2),
                labelColor = Color(0xFF1976D2)
            )
            
            NewStatCard_7ree(
                modifier = Modifier.weight(1f),
                value = stats_7ree.favoriteWords.toString(),
                label = "收藏总数",
                gradient = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                ),
                numberColor = Color(0xFF1976D2),
                labelColor = Color(0xFF1976D2)
            )
        }
        
        // 第二行：3个卡片 - 橙色主题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NewStatCard_7ree(
                modifier = Modifier.weight(1f),
                value = stats_7ree.totalViews.toString(),
                label = "查阅总数",
                gradient = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                ),
                numberColor = Color(0xFFE65100),
                labelColor = Color(0xFFD3681D)
            )
            
            NewStatCard_7ree(
                modifier = Modifier.weight(1f),
                value = DataStatistics_7ree.formatReviewRatio_7ree(stats_7ree.reviewRatio),
                label = "查阅倍率",
                gradient = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                ),
                numberColor = Color(0xFFE65100),
                labelColor = Color(0xFFD3681D)
            )
            
            NewStatCard_7ree(
                modifier = Modifier.weight(1f),
                value = DataStatistics_7ree.formatDailyStudy_7ree(stats_7ree.dailyStudy),
                label = "每日查阅",
                gradient = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                ),
                numberColor = Color(0xFFE65100),
                labelColor = Color(0xFFD3681D)
            )
        }
        
        // 第三行：3个卡片 - 绿色主题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NewStatCard_7ree(
                modifier = Modifier.weight(1f),
                value = stats_7ree.totalSpelling.toString(),
                label = "拼写练习",
                gradient = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                ),
                numberColor = Color(0xFF2E7D32),
                labelColor = Color(0xFF2E7C32)
            )
            
            NewStatCard_7ree(
                modifier = Modifier.weight(1f),
                value = DataStatistics_7ree.formatSpellingRatio_7ree(stats_7ree.spellingRatio),
                label = "拼写倍率",
                gradient = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                ),
                numberColor = Color(0xFF2E7D32),
                labelColor = Color(0xFF2E7C32)
            )
            
            NewStatCard_7ree(
                modifier = Modifier.weight(1f),
                value = DataStatistics_7ree.formatDailySpelling_7ree(stats_7ree.dailySpelling),
                label = "每日拼写",
                gradient = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                ),
                numberColor = Color(0xFF2E7D32),
                labelColor = Color(0xFF2E7C32)
            )
        }
    }
}