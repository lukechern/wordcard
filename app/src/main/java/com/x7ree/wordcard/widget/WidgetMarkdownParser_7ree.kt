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
         */
        fun parseBasicInfo_7ree(fullResult: String): WidgetMarkdownContent_7ree {
            if (fullResult.isBlank()) {
                return WidgetMarkdownContent_7ree("", "", "", "", "", "")
            }
            
            // 使用正则表达式提取各个部分
            val wordRegex = Regex("### 查询单词\\s*\\n([^#]+?)(?=\\n###|$)", RegexOption.DOT_MATCHES_ALL)
            val definitionRegex = Regex("### 查询单词\\s*\\n[^\\n]*\\n([^#]+?)(?=\\n###|$)", RegexOption.DOT_MATCHES_ALL)
            val phoneticRegex = Regex("### 音标\\s*\\n([^#]+?)(?=\\n###|$)", RegexOption.DOT_MATCHES_ALL)
            val partOfSpeechRegex = Regex("### 词性\\s*\\n([^#]+?)(?=\\n###|$)", RegexOption.DOT_MATCHES_ALL)
            val examplesRegex = Regex("### 英文例句\\s*\\n([^#]+?)(?=\\n###|$)", RegexOption.DOT_MATCHES_ALL)
            val translationsRegex = Regex("### 例句中文翻译\\s*\\n([^#]+?)(?=\\n###|$)", RegexOption.DOT_MATCHES_ALL)
            
            val word = wordRegex.find(fullResult)?.groupValues?.get(1)?.trim() ?: ""
            val chineseMeaning = definitionRegex.find(fullResult)?.groupValues?.get(1)?.trim() ?: ""
            val phonetic = phoneticRegex.find(fullResult)?.groupValues?.get(1)?.trim() ?: ""
            val partOfSpeech = partOfSpeechRegex.find(fullResult)?.groupValues?.get(1)?.trim() ?: ""
            val englishExamples = examplesRegex.find(fullResult)?.groupValues?.get(1)?.trim() ?: ""
            val chineseTranslations = translationsRegex.find(fullResult)?.groupValues?.get(1)?.trim() ?: ""
            
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
            
            // 过滤掉"查询单词"标题和单词内容
            val filteredContent = filterQueryWordSection_7ree(markdownContent)
            
            // 使用Markwon进行基本的Markdown渲染
            val markwon = Markwon.builder(textView.context).build()
            markwon.setMarkdown(textView, filteredContent)
            
            // 应用样式调整
            applyStyleAdjustments_7ree(textView)
        }
        
        /**
         * 过滤掉"查询单词"部分的内容
         */
        private fun filterQueryWordSection_7ree(content: String): String {
            // 使用正则表达式移除"查询单词"标题和其内容
            val regex = Regex("### 查询单词\\s*\\n[^#]*?(?=\\n###|$)", RegexOption.DOT_MATCHES_ALL)
            return regex.replace(content, "").trim()
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