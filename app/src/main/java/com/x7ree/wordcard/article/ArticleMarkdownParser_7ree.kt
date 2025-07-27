package com.x7ree.wordcard.article

import android.util.Log

/**
 * 文章Markdown解析器
 * 专门用于解析API返回的文章Markdown格式数据
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
        val keywords: String
    )
    
    /**
     * 解析文章Markdown内容
     */
    fun parseArticleMarkdown(markdownContent: String): ArticleParseResult {
        Log.d(TAG, "开始解析文章Markdown内容")
        Log.d(TAG, "原始内容长度: ${markdownContent.length}")
        Log.d(TAG, "原始内容: $markdownContent")
        
        return try {
            val lines = markdownContent.split("\n")
            var englishTitle = ""
            var chineseTitle = ""
            var englishContent = ""
            var chineseContent = ""
            var keywords = ""
            
            var currentSection = ""
            val englishContentBuilder = StringBuilder()
            val chineseContentBuilder = StringBuilder()
            
            for (line in lines) {
                val trimmedLine = line.trim()
                Log.d(TAG, "处理行: $trimmedLine")
                
                when {
                    // 检测各个章节标题
                    trimmedLine.startsWith("### 文章标题") || 
                    trimmedLine.startsWith("## 文章标题") ||
                    trimmedLine.startsWith("# 文章标题") -> {
                        // 如果当前在中文翻译章节中，这应该是中文标题章节
                        if (currentSection == "chineseTranslation") {
                            currentSection = "chineseTitle"
                            Log.d(TAG, "进入中文标题章节（在翻译章节中）")
                        } else {
                            currentSection = "englishTitle"
                            Log.d(TAG, "进入英文标题章节")
                        }
                    }
                    
                    trimmedLine.startsWith("### 文章内容") || 
                    trimmedLine.startsWith("## 文章内容") ||
                    trimmedLine.startsWith("# 文章内容") -> {
                        // 如果当前在中文翻译章节中，这应该是中文内容章节
                        if (currentSection == "chineseTranslation" || currentSection == "chineseTitle") {
                            currentSection = "chineseContent"
                            Log.d(TAG, "进入中文内容章节（在翻译章节中）")
                        } else {
                            currentSection = "englishContent"
                            Log.d(TAG, "进入英文内容章节")
                        }
                    }
                    
                    trimmedLine.startsWith("### 重点单词") || 
                    trimmedLine.startsWith("## 重点单词") ||
                    trimmedLine.startsWith("# 重点单词") -> {
                        currentSection = "keywords"
                        Log.d(TAG, "进入关键词章节")
                    }
                    
                    trimmedLine.startsWith("### 文章翻译") || 
                    trimmedLine.startsWith("## 文章翻译") ||
                    trimmedLine.startsWith("# 文章翻译") -> {
                        currentSection = "chineseTranslation"
                        Log.d(TAG, "进入中文翻译章节")
                    }
                    
                    // 在中文翻译章节中，如果遇到任何三级标题，且不是已知的标准标题，则视为中文标题
                    currentSection == "chineseTranslation" && trimmedLine.startsWith("###") -> {
                        // 提取标题内容（去掉###前缀）
                        val titleContent = trimmedLine.removePrefix("###").trim()
                        if (chineseTitle.isEmpty() && titleContent.isNotEmpty()) {
                            chineseTitle = titleContent
                            Log.d(TAG, "解析到中文标题（从###行）: $chineseTitle")
                        }
                        // 识别到中文标题后，下一步应该是中文内容
                        currentSection = "chineseContent"
                        Log.d(TAG, "进入中文内容章节（中文标题后自动切换）")
                    }
                    
                    // 处理内容行（非标题行）
                    trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#") -> {
                        when (currentSection) {
                            "englishTitle" -> {
                                if (englishTitle.isEmpty()) {
                                    englishTitle = trimmedLine
                                    Log.d(TAG, "解析到英文标题: $englishTitle")
                                }
                            }
                            "chineseTitle" -> {
                                if (chineseTitle.isEmpty()) {
                                    chineseTitle = trimmedLine
                                    Log.d(TAG, "解析到中文标题: $chineseTitle")
                                }
                            }
                            "englishContent" -> {
                                englishContentBuilder.appendLine(trimmedLine)
                                Log.d(TAG, "添加英文内容行: $trimmedLine")
                            }
                            "chineseContent" -> {
                                chineseContentBuilder.appendLine(trimmedLine)
                                Log.d(TAG, "添加中文内容行: $trimmedLine")
                            }
                            "keywords" -> {
                                if (keywords.isEmpty()) {
                                    keywords = trimmedLine
                                    Log.d(TAG, "解析到关键词: $keywords")
                                }
                            }
                        }
                    }
                }
            }
            
            // 清理内容
            englishContent = englishContentBuilder.toString().trim()
            chineseContent = chineseContentBuilder.toString().trim()
            
            // 应用默认值
            if (englishTitle.isEmpty()) {
                englishTitle = "Generated Article"
                Log.d(TAG, "使用默认英文标题")
            }
            
            if (chineseTitle.isEmpty()) {
                chineseTitle = generateChineseTitle(englishTitle)
                Log.d(TAG, "生成默认中文标题: $chineseTitle")
            }
            
            if (englishContent.isEmpty()) {
                englishContent = markdownContent
                Log.d(TAG, "使用原始内容作为英文内容")
            }
            
            if (chineseContent.isEmpty()) {
                chineseContent = "翻译暂不可用"
                Log.d(TAG, "使用默认中文内容")
            }
            
            if (keywords.isEmpty()) {
                keywords = "无关键词"
                Log.d(TAG, "使用默认关键词")
            }
            
            val result = ArticleParseResult(
                englishTitle = englishTitle,
                chineseTitle = chineseTitle,
                englishContent = englishContent,
                chineseContent = chineseContent,
                keywords = keywords
            )
            
            Log.d(TAG, "解析完成:")
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
                keywords = "解析失败"
            )
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