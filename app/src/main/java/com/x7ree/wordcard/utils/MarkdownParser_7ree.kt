package com.x7ree.wordcard.utils

/**
 * Markdown解析工具类，用于从API返回的Markdown格式中提取结构化信息
 * 与现有的MarkdownRenderer_7ree保持兼容
 */
object MarkdownParser_7ree {
    
    /**
     * 从Markdown文本中解析中文释义
     * 使用与MarkdownRenderer_7ree相同的解析逻辑
     */
    fun parseChineseDefinition(markdown: String): String {
        val lines = markdown.split("\n")
        var foundChineseMeaningStarted = false
        var chineseMeaningLineCount = 0
        val chineseMeaning = StringBuilder()
        
        for (line in lines) {
            val trimmedLine = line.trim()

            if (foundChineseMeaningStarted) {
                // 如果遇到下一个标题，则停止处理
                if (trimmedLine.matches(Regex("^#+\\s+.+$"))) {
                    break
                }

                // 跳过空行，只处理非空行
                if (trimmedLine.isNotEmpty()) {
                    chineseMeaningLineCount += 1

                    // 只保留第一个非空行（标题后的第一行内容）
                    if (chineseMeaningLineCount == 1) {
                        chineseMeaning.append(line.trimStart())
                        break // 提前终止
                    }
                }
            }

            if (trimmedLine.matches(Regex("^#+\\s*中文词义.*$"))) {
                foundChineseMeaningStarted = true
            }
        }
        
        return chineseMeaning.toString().trim()
    }
    
    /**
     * 从Markdown文本中解析音标
     * 查找 "### 音标" 后面的第一行非空内容
     */
    fun parsePhonetic(markdown: String): String {
        val lines = markdown.split("\n")
        var foundPhoneticSection = false
        
        for (line in lines) {
            val trimmedLine = line.trim()
            
            if (foundPhoneticSection) {
                // 如果遇到下一个标题，则停止处理
                if (trimmedLine.matches(Regex("^#+\\s+.+$"))) {
                    break
                }
                
                // 找到第一个非空行
                if (trimmedLine.isNotEmpty()) {
                    // 清理可能的markdown格式符号
                    return trimmedLine.replace("**", "").replace("*", "").trim()
                }
            }
            
            if (trimmedLine.matches(Regex("^#+\\s*音标.*$"))) {
                foundPhoneticSection = true
            }
        }
        
        return ""
    }
    
    /**
     * 从Markdown文本中解析词性
     * 查找 "### 词性" 后面的第一行非空内容
     */
    fun parsePartOfSpeech(markdown: String): String {
        val lines = markdown.split("\n")
        var foundPartOfSpeechSection = false
        
        for (line in lines) {
            val trimmedLine = line.trim()
            
            if (foundPartOfSpeechSection) {
                // 如果遇到下一个标题，则停止处理
                if (trimmedLine.matches(Regex("^#+\\s+.+$"))) {
                    break
                }
                
                // 找到第一个非空行
                if (trimmedLine.isNotEmpty()) {
                    // 清理可能的markdown格式符号
                    return trimmedLine.replace("**", "").replace("*", "").trim()
                }
            }
            
            if (trimmedLine.matches(Regex("^#+\\s*词性.*$"))) {
                foundPartOfSpeechSection = true
            }
        }
        
        return ""
    }
    
    /**
     * 解析完整的单词信息
     * 返回包含中文释义、音标、词性的数据类
     */
    data class WordInfo(
        val chineseDefinition: String,
        val phonetic: String,
        val partOfSpeech: String
    )
    
    /**
     * 从Markdown文本中解析所有单词信息
     */
    fun parseWordInfo(markdown: String): WordInfo {
        return WordInfo(
            chineseDefinition = parseChineseDefinition(markdown),
            phonetic = parsePhonetic(markdown),
            partOfSpeech = parsePartOfSpeech(markdown)
        )
    }
    
    /**
     * 验证Markdown格式是否包含必要的章节
     */
    fun validateMarkdownFormat(markdown: String): Boolean {
        val requiredSections = listOf("中文词义", "音标", "词性")
        return requiredSections.all { section ->
            markdown.contains("### $section") || 
            markdown.contains("###$section") ||
            (section == "中文词义" && (markdown.contains("### 中文释义") || markdown.contains("### 词义")))
        }
    }
}