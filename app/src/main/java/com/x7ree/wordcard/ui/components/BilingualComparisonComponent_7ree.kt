package com.x7ree.wordcard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 中英对照组件
 * 将英文原文和中文翻译按句子对照显示
 */
@Composable
fun BilingualComparisonContent_7ree(
    englishContent: String,
    chineseContent: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (englishContent.isNotEmpty() && chineseContent.isNotEmpty()) {
            // 按句子分割内容
            val englishSentences = splitIntoSentences(englishContent)
            val chineseSentences = splitIntoSentences(chineseContent)
            
            // 取较短的句子数，避免数组越界
            val maxSentences = minOf(englishSentences.size, chineseSentences.size)
            
            for (i in 0 until maxSentences) {
                // 英文句子
                if (englishSentences[i].isNotBlank()) {
                    MarkdownText_7ree(
                        text = englishSentences[i].trim(),
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                // 中文句子
                if (chineseSentences[i].isNotBlank()) {
                    MarkdownText_7ree(
                        text = chineseSentences[i].trim(),
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                
                // 句子间空行（除了最后一句）
                if (i < maxSentences - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // 如果英文句子更多，显示剩余的英文句子
            if (englishSentences.size > maxSentences) {
                for (i in maxSentences until englishSentences.size) {
                    if (englishSentences[i].isNotBlank()) {
                        MarkdownText_7ree(
                            text = englishSentences[i].trim(),
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Text(
                            text = "（暂无对应中文翻译）",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
            
            // 如果中文句子更多，显示剩余的中文句子
            if (chineseSentences.size > maxSentences) {
                for (i in maxSentences until chineseSentences.size) {
                    if (chineseSentences[i].isNotBlank()) {
                        Text(
                            text = "（暂无对应英文原文）",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        MarkdownText_7ree(
                            text = chineseSentences[i].trim(),
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
        } else {
            // 内容不完整的提示
            Text(
                text = "中英对照功能需要同时具备英文原文和中文翻译内容",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
 * 检查是否是英文缩写词
 * 常见的英文缩写词不应该被当作句子结束
 */
private fun isAbbreviation(text: String, dotIndex: Int): Boolean {
    if (dotIndex <= 0) return false
    
    // 常见的英文缩写词列表
    val commonAbbreviations = listOf(
        "Mr", "Mrs", "Ms", "Dr", "Prof", "Sr", "Jr",
        "Inc", "Ltd", "Corp", "Co", "etc", "vs", "eg", "ie",
        "St", "Ave", "Rd", "Blvd", "Apt", "No", "Vol",
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
        "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun",
        "AM", "PM", "a.m", "p.m", "U.S", "U.K", "U.N"
    )
    
    // 向前查找可能的缩写词
    var start = dotIndex - 1
    while (start >= 0 && text[start].isLetter()) {
        start--
    }
    start++ // 调整到第一个字母位置
    
    if (start < dotIndex) {
        val possibleAbbr = text.substring(start, dotIndex)
        if (commonAbbreviations.any { it.equals(possibleAbbr, ignoreCase = true) }) {
            return true
        }
        
        // 检查是否是单个大写字母的缩写（如 A. B. C.）
        if (possibleAbbr.length == 1 && possibleAbbr[0].isUpperCase()) {
            return true
        }
        
        // 检查是否是全大写的短缩写（如 U.S.A.）
        if (possibleAbbr.length <= 4 && possibleAbbr.all { it.isUpperCase() }) {
            return true
        }
    }
    
    return false
}

/**
 * 检查句号后是否应该分割句子
 * 考虑数字、缩写等情况
 */
private fun shouldSplitAtDot(text: String, dotIndex: Int): Boolean {
    // 检查是否是缩写词
    if (isAbbreviation(text, dotIndex)) {
        return false
    }
    
    // 检查是否是数字中的小数点
    if (dotIndex > 0 && dotIndex < text.length - 1) {
        val prevChar = text[dotIndex - 1]
        val nextChar = text[dotIndex + 1]
        if (prevChar.isDigit() && nextChar.isDigit()) {
            return false
        }
    }
    
    // 检查是否是省略号的一部分
    if (dotIndex > 0 && dotIndex < text.length - 1) {
        val prevChar = text[dotIndex - 1]
        val nextChar = text[dotIndex + 1]
        if (prevChar == '.' || nextChar == '.') {
            return false
        }
    }
    
    return true
}

/**
 * 检查是否在引号内部
 * 用于避免在引号内的标点符号处分割句子
 */
private fun isInsideQuotes(text: String, index: Int): Boolean {
    var quoteCount = 0
    var chineseQuoteCount = 0
    
    // 从文本开始到当前位置，统计引号数量
    for (i in 0 until index) {
        when (text[i]) {
            '"' -> quoteCount++
            '"' -> chineseQuoteCount++
            '"' -> chineseQuoteCount++
        }
    }
    
    // 如果引号数量为奇数，说明在引号内部
    return (quoteCount % 2 == 1) || (chineseQuoteCount % 2 == 1)
}

/**
 * 检查句子结束标点后是否有需要包含的引号或其他符号
 * 返回需要跳过的字符数
 */
private fun getTrailingQuotesLength(text: String, startIndex: Int): Int {
    var skipCount = 0
    var currentIndex = startIndex + 1
    
    // 检查句子结束标点后的引号和其他符号
    while (currentIndex < text.length) {
        val char = text[currentIndex]
        when (char) {
            '"', '"', '"', '\'', '\'', '\'', '）', ')', '】', ']', '》', '>' -> {
                // 这些符号应该包含在前一句中
                skipCount++
                currentIndex++
            }
            ' ' -> {
                // 遇到空格，停止检查
                break
            }
            else -> {
                // 遇到其他字符，停止检查
                break
            }
        }
    }
    
    return skipCount
}

/**
 * 将文本内容按句子分割
 * 支持多种句子结束标点符号：句号、叹号、问号等
 * 针对中英文不同的标点使用习惯进行优化，并处理英文缩写词和引号
 */
private fun splitIntoSentences(content: String): List<String> {
    return try {
        val cleanContent = content
            .replace("\r\n", "\n") // 统一换行符
            .replace("\n", " ") // 将换行符替换为空格，避免句子被意外分割
            .replace("  ", " ") // 将双空格替换为单空格
            .trim()
        
        if (cleanContent.isEmpty()) {
            return emptyList()
        }
        
        // 使用简单的字符串分割方法，避免复杂正则表达式
        val sentences = mutableListOf<String>()
        var currentSentence = StringBuilder()
        var i = 0
        
        while (i < cleanContent.length) {
            val char = cleanContent[i]
            currentSentence.append(char)
            
            // 检查是否是句子结束标点
            when (char) {
                '.' -> {
                    // 英文句号：需要特殊处理缩写词和数字
                    if (shouldSplitAtDot(cleanContent, i)) {
                        // 检查后面的引号等符号
                        val trailingLength = getTrailingQuotesLength(cleanContent, i)
                        
                        // 将引号等符号也包含在当前句子中
                        for (j in 1..trailingLength) {
                            if (i + j < cleanContent.length) {
                                currentSentence.append(cleanContent[i + j])
                            }
                        }
                        
                        val nextCharIndex = i + trailingLength + 1
                        val nextChar = if (nextCharIndex < cleanContent.length) cleanContent[nextCharIndex] else ' '
                        
                        if (nextChar == ' ' || nextCharIndex >= cleanContent.length) {
                            val sentence = currentSentence.toString().trim()
                            if (sentence.length > 1) {
                                sentences.add(sentence)
                            }
                            currentSentence.clear()
                        }
                        
                        i += trailingLength // 跳过已处理的引号等符号
                    }
                }
                '!', '?' -> {
                    // 英文叹号和问号：检查后面是否有空格或结束，并处理引号
                    val trailingLength = getTrailingQuotesLength(cleanContent, i)
                    
                    // 将引号等符号也包含在当前句子中
                    for (j in 1..trailingLength) {
                        if (i + j < cleanContent.length) {
                            currentSentence.append(cleanContent[i + j])
                        }
                    }
                    
                    val nextCharIndex = i + trailingLength + 1
                    val nextChar = if (nextCharIndex < cleanContent.length) cleanContent[nextCharIndex] else ' '
                    
                    if (nextChar == ' ' || nextCharIndex >= cleanContent.length) {
                        val sentence = currentSentence.toString().trim()
                        if (sentence.length > 1) {
                            sentences.add(sentence)
                        }
                        currentSentence.clear()
                    }
                    
                    i += trailingLength // 跳过已处理的引号等符号
                }
                '。', '！', '？' -> {
                    // 中文标点：检查是否在引号内部，如果在引号内则不分割
                    if (!isInsideQuotes(cleanContent, i)) {
                        // 不在引号内，可以分割，但要处理后面的引号
                        val trailingLength = getTrailingQuotesLength(cleanContent, i)
                        
                        // 将引号等符号也包含在当前句子中
                        for (j in 1..trailingLength) {
                            if (i + j < cleanContent.length) {
                                currentSentence.append(cleanContent[i + j])
                            }
                        }
                        
                        val sentence = currentSentence.toString().trim()
                        if (sentence.length > 1) {
                            sentences.add(sentence)
                        }
                        currentSentence.clear()
                        
                        i += trailingLength // 跳过已处理的引号等符号
                    }
                }
                '；', ';' -> {
                    // 分号也可以作为句子分割点
                    val trailingLength = getTrailingQuotesLength(cleanContent, i)
                    
                    // 将引号等符号也包含在当前句子中
                    for (j in 1..trailingLength) {
                        if (i + j < cleanContent.length) {
                            currentSentence.append(cleanContent[i + j])
                        }
                    }
                    
                    val nextCharIndex = i + trailingLength + 1
                    val nextChar = if (nextCharIndex < cleanContent.length) cleanContent[nextCharIndex] else ' '
                    
                    if (nextChar == ' ' || nextCharIndex >= cleanContent.length || char == '；') {
                        val sentence = currentSentence.toString().trim()
                        if (sentence.length > 1) {
                            sentences.add(sentence)
                        }
                        currentSentence.clear()
                    }
                    
                    i += trailingLength // 跳过已处理的引号等符号
                }
            }
            
            i++
        }
        
        // 添加最后一个句子（如果没有结束标点）
        if (currentSentence.isNotEmpty()) {
            val sentence = currentSentence.toString().trim()
            if (sentence.length > 1) {
                sentences.add(sentence)
            }
        }
        
        sentences.toList()
    } catch (e: Exception) {
        // 如果分割失败，返回按段落分割的结果作为备选
        splitIntoParagraphs(content)
    }
}

/**
 * 将文本内容分割成段落（保留原函数以备后用）
 * 支持多种段落分隔符
 */
private fun splitIntoParagraphs(content: String): List<String> {
    return content
        .replace("\r\n", "\n") // 统一换行符
        .split("\n\n") // 按双换行符分割段落
        .flatMap { paragraph ->
            // 如果段落太长，按单换行符进一步分割
            if (paragraph.length > 200) {
                paragraph.split("\n").filter { it.isNotBlank() }
            } else {
                listOf(paragraph)
            }
        }
        .filter { it.isNotBlank() } // 过滤空段落
}