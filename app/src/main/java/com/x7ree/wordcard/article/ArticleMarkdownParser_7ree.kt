package com.x7ree.wordcard.article

import android.util.Log


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
        val bilingualComparison: String, // 新增：中英对照内容
        // 新增：用于朗读的清理文本
        val englishContentForTts: String,
        val chineseContentForTts: String,
        val englishTitleForTts: String,
        val chineseTitleForTts: String
    )
    
    /**
     * 解析文章Markdown内容
     * 使用新的模板格式进行解析，支持中英对照格式
     */
    fun parseArticleMarkdown(markdownContent: String): ArticleParseResult {
        Log.d(TAG, "==================== 开始解析文章Markdown内容 ====================")
        Log.d(TAG, "原始内容长度: ${markdownContent.length}")
        Log.d(TAG, "原始API返回内容:")
        Log.d(TAG, "---BEGIN API CONTENT---")
        Log.d(TAG, markdownContent)
        Log.d(TAG, "---END API CONTENT---")
        
        return try {
            // 使用正则表达式解析各个部分
            Log.d(TAG, "开始提取各个章节...")
            val englishTitle = extractSection(markdownContent, "英文标题")
            val chineseTitle = extractSection(markdownContent, "中文标题")
            val keywords = extractSection(markdownContent, "重点单词")
            val bilingualComparison = extractSection(markdownContent, "中英文章对照")
            
            Log.d(TAG, "章节提取完成，开始提取英文和中文内容...")
            // 从API返回内容中提取英文和中文内容
            val englishContent = extractEnglishContent(markdownContent)
            val chineseContent = extractChineseContent(markdownContent)
            
            Log.d(TAG, "==================== 原始解析结果 ====================")
            Log.d(TAG, "英文标题: '$englishTitle'")
            Log.d(TAG, "中文标题: '$chineseTitle'")
            Log.d(TAG, "关键词: '$keywords'")
            Log.d(TAG, "中英对照长度: ${bilingualComparison.length}")
            Log.d(TAG, "中英对照内容预览: ${bilingualComparison.take(200)}...")
            Log.d(TAG, "英文内容长度: ${englishContent.length}")
            Log.d(TAG, "英文内容: '$englishContent'")
            Log.d(TAG, "中文内容长度: ${chineseContent.length}")
            Log.d(TAG, "中文内容: '$chineseContent'")
            
            // 处理默认值
            val processedEnglishTitle = englishTitle.ifEmpty { "Generated Article" }
            val processedChineseTitle = chineseTitle.ifEmpty { generateChineseTitle(englishTitle) }
            val processedKeywords = filterKeywords(keywords).ifEmpty { "无关键词" }
            val processedBilingualComparison = if (bilingualComparison.isEmpty()) {
                "暂无中英对照内容"
            } else {
                cleanBilingualComparison(bilingualComparison)
            }
            val processedEnglishContent = englishContent.ifEmpty { "暂无英文内容" }
            val processedChineseContent = chineseContent.ifEmpty { "暂无中文内容" }
            
            Log.d(TAG, "==================== 处理后的内容 ====================")
            Log.d(TAG, "处理后英文内容: '$processedEnglishContent'")
            Log.d(TAG, "处理后中文内容: '$processedChineseContent'")
            
            val result = ArticleParseResult(
                englishTitle = processedEnglishTitle,
                chineseTitle = processedChineseTitle,
                englishContent = processedEnglishContent,
                chineseContent = processedChineseContent,
                keywords = processedKeywords,
                bilingualComparison = processedBilingualComparison,
                // 生成用于TTS的清理文本
                englishTitleForTts = cleanTextForTts(processedEnglishTitle),
                chineseTitleForTts = cleanTextForTts(processedChineseTitle),
                englishContentForTts = cleanTextForTts(processedEnglishContent),
                chineseContentForTts = cleanTextForTts(processedChineseContent)
            )
            
            Log.d(TAG, "==================== 最终解析结果 ====================")
            Log.d(TAG, "英文标题: ${result.englishTitle}")
            Log.d(TAG, "中文标题: ${result.chineseTitle}")
            Log.d(TAG, "英文内容长度: ${result.englishContent.length}")
            Log.d(TAG, "英文内容: ${result.englishContent}")
            Log.d(TAG, "中文内容长度: ${result.chineseContent.length}")
            Log.d(TAG, "中文内容: ${result.chineseContent}")
            Log.d(TAG, "中英对照长度: ${result.bilingualComparison.length}")
            Log.d(TAG, "关键词: ${result.keywords}")
            Log.d(TAG, "==================== 解析完成 ====================")
            
            result
            
        } catch (e: Exception) {
            Log.e(TAG, "解析失败: ${e.message}", e)
            Log.e(TAG, "异常堆栈:", e)
            // 解析失败时返回默认结果
            ArticleParseResult(
                englishTitle = "Generated Article",
                chineseTitle = "生成的文章",
                englishContent = "解析失败",
                chineseContent = "解析失败",
                keywords = "解析失败",
                bilingualComparison = markdownContent,
                englishTitleForTts = "Generated Article",
                chineseTitleForTts = "生成的文章",
                englishContentForTts = "解析失败",
                chineseContentForTts = "解析失败"
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
     * 从API返回内容中提取英文内容
     * 拼接全部[英文]开头的行
     * @param markdownContent API返回的完整内容
     * @return 拼接后的英文内容
     */
    private fun extractEnglishContent(markdownContent: String): String {
        Log.d(TAG, "开始提取英文内容")
        
        val englishSentences = mutableListOf<String>()
        
        try {
            // 修正正则表达式：匹配 [英文]内容 格式（没有大括号）
            val englishRegex = Regex("\\[英文\\](.+?)(?=\\s*\\[中文\\]|\\s*$)", RegexOption.DOT_MATCHES_ALL)
            
            // 提取所有英文句子
            englishRegex.findAll(markdownContent).forEach { matchResult ->
                val sentence = matchResult.groupValues[1].trim()
                if (sentence.isNotEmpty()) {
                    englishSentences.add(sentence)
                    Log.d(TAG, "找到英文句子: '$sentence'")
                }
            }
            
            Log.d(TAG, "提取到 ${englishSentences.size} 个英文句子")
            
            // 将句子拼接成完整内容，每行之间加换行符
            val englishContent = englishSentences.joinToString("\n")
            
            Log.d(TAG, "英文内容拼接完成，长度: ${englishContent.length}")
            Log.d(TAG, "英文内容: '$englishContent'")
            
            return englishContent
            
        } catch (e: Exception) {
            Log.e(TAG, "提取英文内容失败: ${e.message}", e)
            return ""
        }
    }
    
    /**
     * 从API返回内容中提取中文内容
     * 拼接全部[中文]开头的行
     * @param markdownContent API返回的完整内容
     * @return 拼接后的中文内容
     */
    private fun extractChineseContent(markdownContent: String): String {
        Log.d(TAG, "开始提取中文内容")
        
        val chineseSentences = mutableListOf<String>()
        
        try {
            // 修正正则表达式：匹配 [中文]内容 格式（没有大括号）
            val chineseRegex = Regex("\\[中文\\](.+?)(?=\\s*\\[英文\\]|\\s*$)", RegexOption.DOT_MATCHES_ALL)
            
            // 提取所有中文句子
            chineseRegex.findAll(markdownContent).forEach { matchResult ->
                val sentence = matchResult.groupValues[1].trim()
                if (sentence.isNotEmpty()) {
                    chineseSentences.add(sentence)
                    Log.d(TAG, "找到中文句子: '$sentence'")
                }
            }
            
            Log.d(TAG, "提取到 ${chineseSentences.size} 个中文句子")
            
            // 将句子拼接成完整内容，每行之间加换行符
            val chineseContent = chineseSentences.joinToString("\n")
            
            Log.d(TAG, "中文内容拼接完成，长度: ${chineseContent.length}")
            Log.d(TAG, "中文内容: '$chineseContent'")
            
            return chineseContent
            
        } catch (e: Exception) {
            Log.e(TAG, "提取中文内容失败: ${e.message}", e)
            return ""
        }
    }
    
    /**
     * 清理中英对照内容，删除每行开头的"[英文]"和"[中文]"
     * @param bilingualComparison 原始中英对照内容
     * @return 清理后的中英对照内容
     */
    private fun cleanBilingualComparison(bilingualComparison: String): String {
        Log.d(TAG, "开始清理中英对照内容")
        
        return try {
            val cleanedContent = bilingualComparison
                .replace(Regex("\\[英文\\]"), "") // 删除 [英文]
                .replace(Regex("\\[中文\\]"), "") // 删除 [中文]
                .replace(Regex("\\n\\s*\\n"), "\n") // 将多个连续换行符替换为单个换行符
                .trim()
            
            Log.d(TAG, "中英对照内容清理完成，长度: ${cleanedContent.length}")
            
            cleanedContent
        } catch (e: Exception) {
            Log.e(TAG, "清理中英对照内容失败: ${e.message}", e)
            bilingualComparison // 失败时返回原始内容
        }
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