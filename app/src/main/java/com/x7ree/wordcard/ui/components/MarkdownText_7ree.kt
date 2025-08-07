package com.x7ree.wordcard.ui.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * 简单的Markdown文本渲染组件
 * 支持 **粗体** 和 ***粗体*** 格式
 * 使用多种视觉效果确保加粗效果可见
 */
@Composable
fun MarkdownText_7ree(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified
) {
    val annotatedString = remember(text, style) {
        parseMarkdownToAnnotatedString(text, style)
    }
    
    BasicText(
        text = annotatedString,
        modifier = modifier,
        style = style.copy(
            color = if (color != Color.Unspecified) color else style.color,
            fontSize = if (fontSize != TextUnit.Unspecified) fontSize else style.fontSize,
            lineHeight = if (lineHeight != TextUnit.Unspecified) lineHeight else style.lineHeight
        )
    )
}

/**
 * 将Markdown文本解析为AnnotatedString
 * 支持 **粗体** 和 ***粗体*** 格式
 * 使用多种视觉效果确保加粗效果可见
 */
private fun parseMarkdownToAnnotatedString(text: String, baseStyle: TextStyle): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        val length = text.length
        
        while (currentIndex < length) {
            // 查找三个星号的粗体标记 ***text***
            val tripleStarStart = text.indexOf("***", currentIndex)
            if (tripleStarStart != -1) {
                val tripleStarEnd = text.indexOf("***", tripleStarStart + 3)
                if (tripleStarEnd != -1) {
                    // 添加三个星号之前的普通文本
                    if (tripleStarStart > currentIndex) {
                        val beforeText = text.substring(currentIndex, tripleStarStart)
                        append(parseDoubleStarText(beforeText, baseStyle))
                    }
                    
                    // 添加超粗体文本（三个星号使用最强效果）
                    val boldText = text.substring(tripleStarStart + 3, tripleStarEnd)
                    withStyle(style = SpanStyle(
                        fontWeight = FontWeight.Black, // 使用最粗的Black字重
                        fontSize = baseStyle.fontSize * 1.08f, // 更大的字体
                        letterSpacing = 0.8.sp // 更大的字符间距
                    )) {
                        append(boldText)
                    }
                    
                    currentIndex = tripleStarEnd + 3
                    continue
                }
            }
            
            // 查找两个星号的粗体标记 **text**
            val doubleStarStart = text.indexOf("**", currentIndex)
            if (doubleStarStart != -1) {
                val doubleStarEnd = text.indexOf("**", doubleStarStart + 2)
                if (doubleStarEnd != -1) {
                    // 添加两个星号之前的普通文本
                    if (doubleStarStart > currentIndex) {
                        append(text.substring(currentIndex, doubleStarStart))
                    }
                    
                    // 添加粗体文本（两个星号使用更强的粗体效果）
                    val boldText = text.substring(doubleStarStart + 2, doubleStarEnd)
                    withStyle(style = SpanStyle(
                        fontWeight = FontWeight.ExtraBold, // 使用ExtraBold而不是Bold
                        letterSpacing = 0.5.sp, // 增加字符间距
                        fontSize = baseStyle.fontSize * 1.02f // 稍微增大字体
                    )) {
                        append(boldText)
                    }
                    
                    currentIndex = doubleStarEnd + 2
                    continue
                }
            }
            
            // 没有找到更多的Markdown标记，添加剩余的文本
            append(text.substring(currentIndex))
            break
        }
    }
}

/**
 * 解析包含双星号的文本
 */
private fun parseDoubleStarText(text: String, baseStyle: TextStyle): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        val length = text.length
        
        while (currentIndex < length) {
            val doubleStarStart = text.indexOf("**", currentIndex)
            if (doubleStarStart != -1) {
                val doubleStarEnd = text.indexOf("**", doubleStarStart + 2)
                if (doubleStarEnd != -1) {
                    // 添加两个星号之前的普通文本
                    if (doubleStarStart > currentIndex) {
                        append(text.substring(currentIndex, doubleStarStart))
                    }
                    
                    // 添加粗体文本（使用更强的粗体效果）
                    val boldText = text.substring(doubleStarStart + 2, doubleStarEnd)
                    withStyle(style = SpanStyle(
                        fontWeight = FontWeight.ExtraBold, // 使用ExtraBold
                        letterSpacing = 0.5.sp, // 增加字符间距
                        fontSize = baseStyle.fontSize * 1.02f // 稍微增大字体
                    )) {
                        append(boldText)
                    }
                    
                    currentIndex = doubleStarEnd + 2
                    continue
                }
            }
            
            // 没有找到更多的标记，添加剩余的文本
            append(text.substring(currentIndex))
            break
        }
    }
}