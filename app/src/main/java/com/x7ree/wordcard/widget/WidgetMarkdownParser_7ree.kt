package com.x7ree.wordcard.widget

import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.TextView
import io.noties.markwon.Markwon

/**
 * 专为小组件弹出卡片设计的简化版Markdown解析工具
 * 适用于传统Android View系统
 */
class WidgetMarkdownParser_7ree {
    
    companion object {
        
        /**
         * 解析基本信息，使用正则表达式提取各个部分的内容
         * 针对流式输出优化：在检测到"音标"两个字之前不解析中文词义
         */
        fun parseBasicInfo_7ree(fullResult: String): WidgetMarkdownContent_7ree {
            // android.util.Log.d("WidgetMarkdownParser", "开始解析API返回内容，长度: ${fullResult.length}")
            // android.util.Log.d("WidgetMarkdownParser", "API返回内容: $fullResult")
            
            if (fullResult.isBlank()) {
                // android.util.Log.d("WidgetMarkdownParser", "API返回内容为空")
                return WidgetMarkdownContent_7ree("", "", "", "", "", "")
            }
            
            // 检查是否包含"音标"关键字，用于判断流式输出是否足够完整
            val hasPhoneticSection = fullResult.contains("音标")
            // android.util.Log.d("WidgetMarkdownParser", "是否检测到音标关键字: $hasPhoneticSection")
            
            // 使用正则表达式提取各个部分
            val wordRegex = Regex("### 查询单词\\s*\\n([^#]+?)(?=\\n###|$)", RegexOption.DOT_MATCHES_ALL)
            val chineseMeaningRegex = Regex("### 中文词义\\s*\\n([^#]+?)(?=\\n###|$)", RegexOption.DOT_MATCHES_ALL)
            val phoneticRegex = Regex("### 音标\\s*\\n([^#]+?)(?=\\n###|$)", RegexOption.DOT_MATCHES_ALL)
            val partOfSpeechRegex = Regex("### 词性\\s*\\n([^#]+?)(?=\\n###|$)", RegexOption.DOT_MATCHES_ALL)
            val examplesRegex = Regex("### 英文例句\\s*\\n([^#]+?)(?=\\n###|$)", RegexOption.DOT_MATCHES_ALL)
            val translationsRegex = Regex("### 例句中文翻译\\s*\\n([^#]+?)(?=\\n###|$)", RegexOption.DOT_MATCHES_ALL)
            
            val word = wordRegex.find(fullResult)?.groupValues?.get(1)?.trim() ?: ""
            
            // 只有在检测到"音标"关键字后才解析中文词义，避免流式输出时内容不完整
            val chineseMeaning = if (hasPhoneticSection) {
                chineseMeaningRegex.find(fullResult)?.groupValues?.get(1)?.trim() ?: ""
            } else {
                // android.util.Log.d("WidgetMarkdownParser", "未检测到音标关键字，跳过中文词义解析")
                ""
            }
            
            val phonetic = phoneticRegex.find(fullResult)?.groupValues?.get(1)?.trim() ?: ""
            val partOfSpeech = partOfSpeechRegex.find(fullResult)?.groupValues?.get(1)?.trim() ?: ""
            val englishExamples = examplesRegex.find(fullResult)?.groupValues?.get(1)?.trim() ?: ""
            val chineseTranslations = translationsRegex.find(fullResult)?.groupValues?.get(1)?.trim() ?: ""
            
            // android.util.Log.d("WidgetMarkdownParser", "解析结果 - 单词: '$word'")
            // android.util.Log.d("WidgetMarkdownParser", "解析结果 - 中文意思: '$chineseMeaning'")
            // android.util.Log.d("WidgetMarkdownParser", "解析结果 - 音标: '$phonetic'")
            // android.util.Log.d("WidgetMarkdownParser", "解析结果 - 词性: '$partOfSpeech'")
            
            return WidgetMarkdownContent_7ree(word, chineseMeaning, phonetic, partOfSpeech, englishExamples, chineseTranslations)
        }
        
        /**
         * 渲染Markdown内容到TextView（简化版）
         */
        fun renderToTextView_7ree(textView: TextView, markdownContent: String) {
            if (markdownContent.isBlank()) {
                textView.text = ""
                return
            }
            
            // 过滤掉"查询单词"和"中文词义"标题和内容
            val filteredContent = filterQueryWordAndChineseMeaningSection_7ree(markdownContent)
            
            // 使用Markwon进行基本的Markdown渲染
            val markwon = Markwon.builder(textView.context).build()
            markwon.setMarkdown(textView, filteredContent)
            
            // 应用样式调整
            applyStyleAdjustments_7ree(textView)
        }
        
        /**
         * 过滤掉"查询单词"和"中文词义"部分的内容
         * 针对流式输出，确保在完整接收后再进行过滤
         */
        private fun filterQueryWordAndChineseMeaningSection_7ree(content: String): String {
            var filteredContent = content
            
            // 使用正则表达式移除"查询单词"标题和其内容
            val queryWordRegex = Regex("### 查询单词\\s*\\n[^#]*?(?=\\n###|$)", RegexOption.DOT_MATCHES_ALL)
            filteredContent = queryWordRegex.replace(filteredContent, "")
            
            // 使用正则表达式移除"中文词义"标题和其内容
            val chineseMeaningRegex = Regex("### 中文词义\\s*\\n[^#]*?(?=\\n###|$)", RegexOption.DOT_MATCHES_ALL)
            filteredContent = chineseMeaningRegex.replace(filteredContent, "")
            
            return filteredContent.trim()
        }
        
        /**
         * 应用文本样式调整
         * 将粗体样式改为下划线样式
         */
        private fun applyStyleAdjustments_7ree(textView: TextView) {
            val spannable = textView.text as? Spannable ?: return
            
            // 处理粗体样式，将其改为下划线样式
            val boldSpans = spannable.getSpans(0, spannable.length, StyleSpan::class.java)
            boldSpans.forEach { span ->
                if (span.style == android.graphics.Typeface.BOLD) {
                    val start = spannable.getSpanStart(span)
                    val end = spannable.getSpanEnd(span)
                    // 移除原有的粗体样式
                    spannable.removeSpan(span)
                    // 应用下划线样式
                    spannable.setSpan(
                        UnderlineSpan(),
                        start,
                        end,
                        android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
        
        /**
         * 构建详细信息字符串
         */
        fun buildDetailInfo_7ree(content: WidgetMarkdownContent_7ree): String {
            val result = StringBuilder()
            
            if (content.phonetic.isNotEmpty()) {
                result.append("音标: ${content.phonetic}\n")
            }
            
            if (content.partOfSpeech.isNotEmpty()) {
                result.append("词性: ${content.partOfSpeech}\n")
            }
            
            return result.toString().trim()
        }
        
        /**
         * 按照指定模板格式化完整内容
         */
        fun formatCompleteContent_7ree(content: WidgetMarkdownContent_7ree): String {
            val result = StringBuilder()
            
            if (content.phonetic.isNotEmpty()) {
                result.append("### 音标\n${content.phonetic}\n\n")
            }
            
            if (content.partOfSpeech.isNotEmpty()) {
                result.append("### 词性\n${content.partOfSpeech}\n\n")
            }
            
            if (content.englishExamples.isNotEmpty()) {
                result.append("### 英文例句\n${content.englishExamples}\n\n")
            }
            
            if (content.chineseTranslations.isNotEmpty()) {
                result.append("### 例句中文翻译\n${content.chineseTranslations}\n\n")
            }
            
            return result.toString().trim()
        }
    }
}

/**
 * 小组件Markdown内容数据类
 */
data class WidgetMarkdownContent_7ree(
    val word: String,
    val chineseMeaning: String,
    val phonetic: String,
    val partOfSpeech: String,
    val englishExamples: String,
    val chineseTranslations: String
)