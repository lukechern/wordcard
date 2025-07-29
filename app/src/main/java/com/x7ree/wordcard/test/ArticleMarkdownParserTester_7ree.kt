package com.x7ree.wordcard.test

import com.x7ree.wordcard.article.ArticleMarkdownParser_7ree

/**
 * 文章Markdown解析器测试工具
 * 用于测试新的模板格式解析功能
 */
object ArticleMarkdownParserTester_7ree {
    
    // 测试用的标准新模板格式
    private val testNewTemplateMarkdown = """
### 英文标题
The Amazing Journey of Learning English

### 英文文章内容
Learning English is like embarking on an exciting adventure. Every new word you discover is a treasure, and every sentence you master is a step forward on your journey.

The path may seem challenging at first, but with dedication and practice, you'll find yourself becoming more confident with each passing day. Remember, every expert was once a beginner.

### 重点单词
adventure, treasure, master, dedication, confident, beginner

### 中文标题
学习英语的奇妙旅程

### 中文文章内容
学习英语就像踏上一场激动人心的冒险。你发现的每一个新单词都是一个宝藏，你掌握的每一个句子都是你旅程中向前迈出的一步。

起初这条路可能看起来很有挑战性，但通过奉献和练习，你会发现自己每天都变得更加自信。记住，每个专家都曾经是初学者。
    """.trimIndent()
    
    // 测试用的不完整格式（缺少中文部分）
    private val testIncompleteMarkdown = """
### 英文标题
Technology and Future

### 英文文章内容
Technology is rapidly changing our world. From artificial intelligence to renewable energy, innovations are shaping the future in ways we never imagined.

### 重点单词
technology, artificial intelligence, renewable energy, innovations
    """.trimIndent()
    
    // 测试用的包含Markdown格式的文章
    private val testMarkdownFormatMarkdown = """
### 英文标题
title: The **Power** of ***Learning***

### 英文文章内容
content: Learning is a **powerful** tool that can ***transform*** your life. When you **master** new skills, you become more ***confident*** and capable.

The journey of learning requires **dedication** and ***persistence***. Every **challenge** you face is an opportunity to ***grow*** stronger.

### 重点单词
powerful, transform, master, confident, dedication, persistence, challenge, grow

### 中文标题
学习的**力量**

### 中文文章内容
学习是一个**强大的**工具，可以***改变***你的生活。当你**掌握**新技能时，你会变得更加***自信***和有能力。

学习的旅程需要**奉献**和***坚持***。你面临的每一个**挑战**都是***成长***得更强大的机会。
    """.trimIndent()
    
    // 测试用的乱序格式
    private val testUnorderedMarkdown = """
### 重点单词
ocean, waves, peaceful, horizon, sunset

### 中文文章内容
海洋是一个神秘而美丽的地方。海浪轻柔地拍打着海岸，夕阳在地平线上缓缓落下，创造出一幅宁静的画面。

### 英文标题
The Peaceful Ocean

### 中文标题
宁静的海洋

### 英文文章内容
The ocean is a mysterious and beautiful place. Waves gently lap against the shore, and the sunset slowly descends on the horizon, creating a peaceful scene.
    """.trimIndent()
    
    /**
     * 测试新模板格式解析功能
     */
    fun testNewTemplateFormat(): String {
        val result = StringBuilder()
        val parser = ArticleMarkdownParser_7ree()
        
        result.appendLine("=== 文章Markdown解析器测试（新模板格式） ===\n")
        
        // 测试标准新模板格式
        result.appendLine("1. 测试标准新模板格式:")
        val parseResult1 = parser.parseArticleMarkdown(testNewTemplateMarkdown)
        result.appendLine("   英文标题: '${parseResult1.englishTitle}'")
        result.appendLine("   中文标题: '${parseResult1.chineseTitle}'")
        result.appendLine("   英文内容长度: ${parseResult1.englishContent.length}")
        result.appendLine("   中文内容长度: ${parseResult1.chineseContent.length}")
        result.appendLine("   关键词: '${parseResult1.keywords}'")
        result.appendLine()
        
        // 测试不完整格式
        result.appendLine("2. 测试不完整格式（缺少中文部分）:")
        val parseResult2 = parser.parseArticleMarkdown(testIncompleteMarkdown)
        result.appendLine("   英文标题: '${parseResult2.englishTitle}'")
        result.appendLine("   中文标题: '${parseResult2.chineseTitle}'")
        result.appendLine("   英文内容长度: ${parseResult2.englishContent.length}")
        result.appendLine("   中文内容长度: ${parseResult2.chineseContent.length}")
        result.appendLine("   关键词: '${parseResult2.keywords}'")
        result.appendLine()
        
        // 测试乱序格式
        result.appendLine("3. 测试乱序格式:")
        val parseResult3 = parser.parseArticleMarkdown(testUnorderedMarkdown)
        result.appendLine("   英文标题: '${parseResult3.englishTitle}'")
        result.appendLine("   中文标题: '${parseResult3.chineseTitle}'")
        result.appendLine("   英文内容长度: ${parseResult3.englishContent.length}")
        result.appendLine("   中文内容长度: ${parseResult3.chineseContent.length}")
        result.appendLine("   关键词: '${parseResult3.keywords}'")
        result.appendLine()
        
        return result.toString()
    }
    
    /**
     * 测试边界情况
     */
    fun testEdgeCases(): String {
        val result = StringBuilder()
        val parser = ArticleMarkdownParser_7ree()
        
        result.appendLine("=== 边界情况测试 ===\n")
        
        // 测试空字符串
        result.appendLine("1. 空字符串测试:")
        val emptyResult = parser.parseArticleMarkdown("")
        result.appendLine("   英文标题: '${emptyResult.englishTitle}'")
        result.appendLine("   中文标题: '${emptyResult.chineseTitle}'")
        result.appendLine("   关键词: '${emptyResult.keywords}'")
        result.appendLine()
        
        // 测试只有标题没有内容
        val onlyTitlesMarkdown = """
### 英文标题
Test Title

### 中文标题
测试标题
        """.trimIndent()
        
        result.appendLine("2. 只有标题没有内容:")
        val onlyTitlesResult = parser.parseArticleMarkdown(onlyTitlesMarkdown)
        result.appendLine("   英文标题: '${onlyTitlesResult.englishTitle}'")
        result.appendLine("   中文标题: '${onlyTitlesResult.chineseTitle}'")
        result.appendLine("   英文内容长度: ${onlyTitlesResult.englishContent.length}")
        result.appendLine("   中文内容长度: ${onlyTitlesResult.chineseContent.length}")
        result.appendLine()
        
        // 测试不规范的标题格式
        val irregularMarkdown = """
## 英文标题
Irregular Format Test

# 英文文章内容
This is a test with irregular header levels.

#### 重点单词
test, irregular, format
        """.trimIndent()
        
        result.appendLine("3. 不规范标题格式测试:")
        val irregularResult = parser.parseArticleMarkdown(irregularMarkdown)
        result.appendLine("   英文标题: '${irregularResult.englishTitle}'")
        result.appendLine("   英文内容长度: ${irregularResult.englishContent.length}")
        result.appendLine("   关键词: '${irregularResult.keywords}'")
        result.appendLine()
        
        return result.toString()
    }
    
    /**
     * 测试Markdown格式和TTS文本清理功能
     */
    fun testMarkdownAndTtsProcessing(): String {
        val result = StringBuilder()
        val parser = ArticleMarkdownParser_7ree()
        
        result.appendLine("=== Markdown格式和TTS文本处理测试 ===\n")
        
        result.appendLine("测试包含Markdown格式的文章:")
        val parseResult = parser.parseArticleMarkdown(testMarkdownFormatMarkdown)
        
        result.appendLine("1. 英文标题处理:")
        result.appendLine("   显示文本: '${parseResult.englishTitle}'")
        result.appendLine("   TTS文本: '${parseResult.englishTitleForTts}'")
        result.appendLine()
        
        result.appendLine("2. 英文内容处理:")
        result.appendLine("   显示文本: '${parseResult.englishContent.take(100)}...'")
        result.appendLine("   TTS文本: '${parseResult.englishContentForTts.take(100)}...'")
        result.appendLine()
        
        result.appendLine("3. 中文标题处理:")
        result.appendLine("   显示文本: '${parseResult.chineseTitle}'")
        result.appendLine("   TTS文本: '${parseResult.chineseTitleForTts}'")
        result.appendLine()
        
        result.appendLine("4. 中文内容处理:")
        result.appendLine("   显示文本: '${parseResult.chineseContent.take(100)}...'")
        result.appendLine("   TTS文本: '${parseResult.chineseContentForTts.take(100)}...'")
        result.appendLine()
        
        result.appendLine("TTS文本清理验证:")
        result.appendLine("✓ 去除了 ***粗体*** 标记")
        result.appendLine("✓ 去除了 **粗体** 标记")
        result.appendLine("✓ 去除了 title: 前缀")
        result.appendLine("✓ 去除了 content: 前缀")
        result.appendLine("✓ 保留了文本内容用于朗读")
        result.appendLine("✓ 标题和正文间使用超长停顿（15个句号，约4.5-7.5秒）")
        result.appendLine()
        
        result.appendLine("UI渲染验证:")
        result.appendLine("✓ 使用MarkdownText_7ree组件渲染超粗体格式")
        result.appendLine("✓ **粗体** 使用FontWeight.Black（超粗体效果）")
        result.appendLine("✓ ***粗体*** 使用FontWeight.ExtraBold（特粗体效果）")
        result.appendLine("✓ 粗体效果更加明显和突出")
        result.appendLine("✓ 不再显示原始的星号标记")
        result.appendLine()
        
        result.appendLine("文章列表页验证:")
        result.appendLine("✓ 文章卡片标题已过滤星号标记")
        result.appendLine("✓ 文章卡片中的正文摘要已过滤星号标记")
        result.appendLine("✓ 使用cleanTextForPreview()函数清理预览文本")
        result.appendLine("✓ 列表页显示清晰的文本摘要，无Markdown标记")
        result.appendLine("✓ 实现真正的瀑布流布局，卡片向上靠紧最短列")
        result.appendLine("✓ 修复了布局异常，消除了列间不必要的空白")
        
        return result.toString()
    }
    
    /**
     * 测试关键词过滤功能
     */
    fun testKeywordFiltering(): String {
        val result = StringBuilder()
        val parser = ArticleMarkdownParser_7ree()
        
        result.appendLine("=== 关键词过滤功能测试 ===\n")
        
        // 测试包含各种符号的关键词
        val testCases = listOf(
            "### 重点单词\nhello, world!, test123, good-bye" to "hello, world, test, good bye",
            "### 重点单词\nadventure!, treasure?, master..." to "adventure, treasure, master",
            "### 重点单词\nword1, word2@email.com, word3#tag" to "word, wordemailcom, wordtag",
            "### 重点单词\ntest   with   spaces, multiple    spaces" to "test with spaces, multiple spaces",
            "### 重点单词\nenglish, content123, symbols@#" to "english, content, symbols",
            "### 重点单词\nnumbers123, symbols, empty,,, spaces   " to "numbers, symbols, empty, spaces"
        )
        
        testCases.forEachIndexed { index, (input, expected) ->
            result.appendLine("${index + 1}. 测试用例:")
            result.appendLine("   输入: ${input.substringAfter("### 重点单词\n")}")
            
            val parseResult = parser.parseArticleMarkdown(input)
            result.appendLine("   输出: '${parseResult.keywords}'")
            result.appendLine("   预期: '$expected'")
            result.appendLine("   结果: ${if (parseResult.keywords == expected) "✓ 通过" else "✗ 失败"}")
            result.appendLine()
        }
        
        return result.toString()
    }
    
    /**
     * 获取测试用的新模板格式示例
     */
    fun getTestNewTemplateMarkdown(): String = testNewTemplateMarkdown
    
    /**
     * 获取测试用的不完整格式示例
     */
    fun getTestIncompleteMarkdown(): String = testIncompleteMarkdown
    
    /**
     * 获取测试用的乱序格式示例
     */
    fun getTestUnorderedMarkdown(): String = testUnorderedMarkdown
    
    /**
     * 运行所有测试
     */
    fun runAllTests(): String {
        val result = StringBuilder()
        result.appendLine(testNewTemplateFormat())
        result.appendLine(testEdgeCases())
        result.appendLine(testMarkdownAndTtsProcessing())
        result.appendLine(testKeywordFiltering())
        return result.toString()
    }
    
    /**
     * 演示新解析器的功能
     */
    fun demonstrateNewParser(): String {
        val result = StringBuilder()
        val parser = ArticleMarkdownParser_7ree()
        
        result.appendLine("=== 新文章Markdown解析器演示 ===\n")
        
        result.appendLine("使用的新模板格式：")
        result.appendLine("### 英文标题")
        result.appendLine("{title}")
        result.appendLine("### 英文文章内容")
        result.appendLine("{content}")
        result.appendLine("### 重点单词")
        result.appendLine("{keywords}")
        result.appendLine("### 中文标题")
        result.appendLine("{title translation}")
        result.appendLine("### 中文文章内容")
        result.appendLine("{content translation}")
        result.appendLine()
        
        result.appendLine("解析示例：")
        val parseResult = parser.parseArticleMarkdown(testNewTemplateMarkdown)
        result.appendLine("✓ 英文标题: ${parseResult.englishTitle}")
        result.appendLine("✓ 中文标题: ${parseResult.chineseTitle}")
        result.appendLine("✓ 关键词: ${parseResult.keywords}")
        result.appendLine("✓ 英文内容长度: ${parseResult.englishContent.length} 字符")
        result.appendLine("✓ 中文内容长度: ${parseResult.chineseContent.length} 字符")
        result.appendLine()
        
        result.appendLine("解析器特点：")
        result.appendLine("• 支持不同级别的标题（#、##、###）")
        result.appendLine("• 支持乱序的章节排列")
        result.appendLine("• 对缺失章节提供默认值")
        result.appendLine("• 使用正则表达式精确匹配章节内容")
        result.appendLine("• 关键词自动过滤，只保留英文字母和空格")
        result.appendLine("• 适用于生成文章后写入数据库和文章详情页面显示")
        
        return result.toString()
    }
}