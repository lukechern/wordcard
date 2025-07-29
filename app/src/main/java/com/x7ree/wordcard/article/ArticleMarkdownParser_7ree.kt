package com.x7ree.wordcard.article

import android.util.Log

/**
 * 文章Markdown解析器
 * 专门用于解析API返回的文章Markdown格式数据
 * 
 * 支持的新模板格式：
 * ### 英文标题
 * {title}
 * ### 英文文章内容
 * {content}
 * ### 重点单词
 * {keywords}
 * ### 中文标题
 * {title translation}
 * ### 中文文章内容
 * {content translation}
 */
class ArticleMarkdownParser_7ree {
    
    companion object {
        private const val TAG = "ArticleMarkdownParser"
    }
    
    /**
     * 文章解析结果数据类
     */
    data class ArticleParseResult(
        val englishTitle: String,
        val chineseTitle: String,
        val englishContent: String,
        val chineseContent: String,
        val keywords: String,
        // 新增：用于朗读的清理文本
        val englishContentForTts: String,
        val chineseContentForTts: String,
        val englishTitleForTts: String,
        val chineseTitleForTts: String
    )
    
    /**
     * 解析文章Markdown内容
     * 使用新的模板格式进行解析
     */
    fun parseArticleMarkdown(markdownContent: String): ArticleParseResult {
        Log.d(TAG, "开始解析文章Markdown内容")
        Log.d(TAG, "原始内容长度: ${markdownContent.length}")
        Log.d(TAG, "原始内容: $markdownContent")
        
        return try {
            // 使用正则表达式解析各个部分
            val englishTitle = extractSection(markdownContent, "英文标题")
            val englishContent = extractSection(markdownContent, "英文文章内容")
            val keywords = extractSection(markdownContent, "重点单词")
            val chineseTitle = extractSection(markdownContent, "中文标题")
            val chineseContent = extractSection(markdownContent, "中文文章内容")
            
            Log.d(TAG, "解析结果:")
            Log.d(TAG, "英文标题: '$englishTitle'")
            Log.d(TAG, "英文内容长度: ${englishContent.length}")
            Log.d(TAG, "关键词: '$keywords'")
            Log.d(TAG, "中文标题: '$chineseTitle'")
            Log.d(TAG, "中文内容长度: ${chineseContent.length}")
            
            // 处理默认值
            val finalEnglishTitle = englishTitle.ifEmpty { "Generated Article" }
            val finalChineseTitle = chineseTitle.ifEmpty { generateChineseTitle(englishTitle) }
            val finalEnglishContent = englishContent.ifEmpty { markdownContent }
            val finalChineseContent = chineseContent.ifEmpty { "翻译暂不可用" }
            val finalKeywords = filterKeywords(keywords).ifEmpty { "无关键词" }
            
            val result = ArticleParseResult(
                englishTitle = finalEnglishTitle,
                chineseTitle = finalChineseTitle,
                englishContent = finalEnglishContent,
                chineseContent = finalChineseContent,
                keywords = finalKeywords,
                // 生成用于TTS的清理文本
                englishTitleForTts = cleanTextForTts(finalEnglishTitle),
                chineseTitleForTts = cleanTextForTts(finalChineseTitle),
                englishContentForTts = cleanTextForTts(finalEnglishContent),
                chineseContentForTts = cleanTextForTts(finalChineseContent)
            )
            
            Log.d(TAG, "最终解析结果:")
            Log.d(TAG, "英文标题: ${result.englishTitle}")
            Log.d(TAG, "中文标题: ${result.chineseTitle}")
            Log.d(TAG, "英文内容长度: ${result.englishContent.length}")
            Log.d(TAG, "中文内容长度: ${result.chineseContent.length}")
            Log.d(TAG, "关键词: ${result.keywords}")
            
            result
            
        } catch (e: Exception) {
            Log.e(TAG, "解析失败: ${e.message}", e)
            // 解析失败时返回默认结果
            ArticleParseResult(
                englishTitle = "Generated Article",
                chineseTitle = "生成的文章",
                englishContent = markdownContent,
                chineseContent = "翻译暂不可用",
                keywords = "解析失败",
                englishTitleForTts = "Generated Article",
                chineseTitleForTts = "生成的文章",
                englishContentForTts = cleanTextForTts(markdownContent),
                chineseContentForTts = "翻译暂不可用"
            )
        }
    }
    
    /**
     * 提取指定章节的内容
     * @param markdownContent 完整的markdown内容
     * @param sectionTitle 章节标题（不包含###前缀）
     * @return 章节内容，如果未找到则返回空字符串
     */
    private fun extractSection(markdownContent: String, sectionTitle: String): String {
        Log.d(TAG, "提取章节: $sectionTitle")
        
        // 构建正则表达式，匹配章节标题和内容
        // 支持不同级别的标题（#、##、###）
        val regex = Regex(
            "^#+\\s*$sectionTitle\\s*$\\n([\\s\\S]*?)(?=^#+\\s|\\z)",
            setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE)
        )
        
        val matchResult = regex.find(markdownContent)
        val content = matchResult?.groupValues?.get(1)?.trim() ?: ""
        
        Log.d(TAG, "章节 '$sectionTitle' 内容长度: ${content.length}")
        if (content.isNotEmpty()) {
            Log.d(TAG, "章节 '$sectionTitle' 内容预览: ${content.take(100)}...")
        }
        
        return content
    }
    
    /**
     * 过滤关键词，只保留英文字母和空格，去掉其他符号
     * @param rawKeywords 原始关键词字符串
     * @return 过滤后的关键词字符串
     */
    private fun filterKeywords(rawKeywords: String): String {
        Log.d(TAG, "开始过滤关键词: '$rawKeywords'")
        
        if (rawKeywords.isEmpty()) {
            return ""
        }
        
        // 使用正则表达式只保留英文字母和空格，去掉其他符号
        val filteredKeywords = rawKeywords
            .replace(Regex("[^a-zA-Z\\s,]"), "") // 先保留逗号用于分割
            .split(",") // 按逗号分割
            .map { keyword ->
                keyword.trim() // 去掉前后空格
                    .replace(Regex("[^a-zA-Z\\s]"), "") // 去掉除英文字母和空格外的所有字符
                    .replace(Regex("\\s+"), " ") // 将多个连续空格替换为单个空格
                    .trim() // 再次去掉前后空格
            }
            .filter { it.isNotEmpty() } // 过滤掉空字符串
            .joinToString(", ") // 重新用逗号和空格连接
        
        Log.d(TAG, "关键词过滤结果: '$filteredKeywords'")
        
        return filteredKeywords
    }
    
    /**
     * 清理文本用于TTS朗读
     * 去除Markdown格式标记和语音说明
     * @param text 原始文本
     * @return 清理后的朗读文本
     */
    fun cleanTextForTts(text: String): String {
        Log.d(TAG, "开始清理TTS文本: '${text.take(50)}...'")
        
        if (text.isEmpty()) {
            return ""
        }
        
        var cleanedText = text
        
        // 去除三个星号包裹的粗体标记 ***text*** -> text
        cleanedText = cleanedText.replace(Regex("\\*\\*\\*([^*]+)\\*\\*\\*"), "$1")
        
        // 去除两个星号包裹的粗体标记 **text** -> text
        cleanedText = cleanedText.replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1")
        
        // 去除单个星号包裹的斜体标记 *text* -> text
        cleanedText = cleanedText.replace(Regex("\\*([^*]+)\\*"), "$1")
        
        // 去除标题前的"title"语音说明
        cleanedText = cleanedText.replace(Regex("^title\\s*:?\\s*", RegexOption.IGNORE_CASE), "")
        
        // 去除文章前的"content"语音说明
        cleanedText = cleanedText.replace(Regex("^content\\s*:?\\s*", RegexOption.IGNORE_CASE), "")
        
        // 去除其他可能的语音说明前缀
        cleanedText = cleanedText.replace(Regex("^(article|text|story)\\s*:?\\s*", RegexOption.IGNORE_CASE), "")
        
        // 清理多余的空白字符
        cleanedText = cleanedText.replace(Regex("\\s+"), " ").trim()
        
        Log.d(TAG, "TTS文本清理完成: '${cleanedText.take(50)}...'")
        
        return cleanedText
    }
    
    /**
     * 为显示渲染处理Markdown格式
     * 保留Markdown标记用于UI渲染
     * @param text 原始文本
     * @return 用于显示的文本（保留Markdown格式）
     */
    fun formatTextForDisplay(text: String): String {
        // 这个方法保留原始的Markdown格式，供UI组件渲染使用
        // UI组件（如Compose的Text或MarkdownText）会自动处理**和***标记
        return text
    }
    
    /**
     * 生成简单的中文标题
     */
    private fun generateChineseTitle(englishTitle: String): String {
        return when {
            englishTitle.contains("story", ignoreCase = true) -> "故事"
            englishTitle.contains("adventure", ignoreCase = true) -> "冒险"
            englishTitle.contains("journey", ignoreCase = true) -> "旅程"
            englishTitle.contains("life", ignoreCase = true) -> "生活"
            englishTitle.contains("nature", ignoreCase = true) -> "自然"
            englishTitle.contains("technology", ignoreCase = true) -> "科技"
            englishTitle.contains("education", ignoreCase = true) -> "教育"
            englishTitle.contains("health", ignoreCase = true) -> "健康"
            englishTitle.contains("sky", ignoreCase = true) -> "天空"
            englishTitle.contains("sea", ignoreCase = true) -> "海洋"
            else -> "英语文章"
        }
    }
}