package com.x7ree.wordcard.test

import com.x7ree.wordcard.utils.MarkdownParser_7ree

/**
 * Markdown解析器测试工具
 */
object MarkdownParserTester_7ree {
    
    // 测试用的标准Markdown格式
    private val testMarkdown = """
### 查询单词
hello

### 中文词义
你好；哈喽；喂（用于问候或引起注意）

### 音标
/həˈloʊ/

### 词性
感叹词 (interjection)

### 英文例句
1. Hello, how are you today?
2. She said hello to everyone at the party.

### 例句中文翻译
1. 你好，你今天怎么样？
2. 她在聚会上向每个人问好。
    """.trimIndent()
    
    // 测试用的变体格式（缺少词性字段）
    private val testMarkdownVariant = """
### 查询单词
world

### 中文词义
世界；地球；领域

### 音标
/wɜːrld/

### 英文例句
The world is beautiful.

### 例句中文翻译
世界是美丽的。
    """.trimIndent()
    
    /**
     * 测试解析功能
     */
    fun testParsing(): String {
        val result = StringBuilder()
        
        result.appendLine("=== Markdown解析器测试 ===\n")
        
        // 测试标准格式
        result.appendLine("1. 测试标准格式:")
        val wordInfo1 = MarkdownParser_7ree.parseWordInfo(testMarkdown)
        result.appendLine("   中文释义: ${wordInfo1.chineseDefinition}")
        result.appendLine("   音标: ${wordInfo1.phonetic}")
        result.appendLine("   词性: ${wordInfo1.partOfSpeech}")
        result.appendLine()
        
        // 测试变体格式
        result.appendLine("2. 测试变体格式:")
        val wordInfo2 = MarkdownParser_7ree.parseWordInfo(testMarkdownVariant)
        result.appendLine("   中文释义: ${wordInfo2.chineseDefinition}")
        result.appendLine("   音标: ${wordInfo2.phonetic}")
        result.appendLine("   词性: ${wordInfo2.partOfSpeech}")
        result.appendLine()
        
        // 测试格式验证
        result.appendLine("3. 格式验证测试:")
        result.appendLine("   标准格式验证: ${MarkdownParser_7ree.validateMarkdownFormat(testMarkdown)}")
        result.appendLine("   变体格式验证: ${MarkdownParser_7ree.validateMarkdownFormat(testMarkdownVariant)}")
        result.appendLine()
        
        // 测试单独解析功能
        result.appendLine("4. 单独解析功能测试:")
        result.appendLine("   中文释义: '${MarkdownParser_7ree.parseChineseDefinition(testMarkdown)}'")
        result.appendLine("   音标: '${MarkdownParser_7ree.parsePhonetic(testMarkdown)}'")
        result.appendLine("   词性: '${MarkdownParser_7ree.parsePartOfSpeech(testMarkdown)}'")
        
        return result.toString()
    }
    
    /**
     * 测试边界情况
     */
    fun testEdgeCases(): String {
        val result = StringBuilder()
        
        result.appendLine("=== 边界情况测试 ===\n")
        
        // 测试空字符串
        val emptyInfo = MarkdownParser_7ree.parseWordInfo("")
        result.appendLine("1. 空字符串测试:")
        result.appendLine("   中文释义: '${emptyInfo.chineseDefinition}'")
        result.appendLine("   音标: '${emptyInfo.phonetic}'")
        result.appendLine("   词性: '${emptyInfo.partOfSpeech}'")
        result.appendLine()
        
        // 测试不完整格式
        val incompleteMarkdown = """
            ### 查询单词
            test
            
            ### 中文词义
            测试
        """.trimIndent()
        
        val incompleteInfo = MarkdownParser_7ree.parseWordInfo(incompleteMarkdown)
        result.appendLine("2. 不完整格式测试:")
        result.appendLine("   中文释义: '${incompleteInfo.chineseDefinition}'")
        result.appendLine("   音标: '${incompleteInfo.phonetic}'")
        result.appendLine("   词性: '${incompleteInfo.partOfSpeech}'")
        result.appendLine("   格式验证: ${MarkdownParser_7ree.validateMarkdownFormat(incompleteMarkdown)}")
        
        return result.toString()
    }
    
    /**
     * 获取测试用的Markdown示例
     */
    fun getTestMarkdown(): String = testMarkdown
    
    /**
     * 获取变体格式的Markdown示例
     */
    fun getTestMarkdownVariant(): String = testMarkdownVariant
}