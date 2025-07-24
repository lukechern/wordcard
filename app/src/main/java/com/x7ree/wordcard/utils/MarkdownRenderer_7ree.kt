package com.x7ree.wordcard.utils

import android.content.Context
import android.text.Spannable
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.x7ree.wordcard.R
import com.x7ree.wordcard.ui.components.TtsButton_7ree
import com.x7ree.wordcard.ui.components.TtsButtonState_7ree
import io.noties.markwon.Markwon
import android.util.Log

/**
 * Markdown渲染工具类，用于处理API查询结果的Markdown格式化和显示
 */
class MarkdownRenderer_7ree {
    
    companion object {
        
        const val TAG_7ree = "MarkdownRenderer_7ree"
        
        /**
         * 应用文本样式调整
         * 将粗体样式改为下划线样式
         */
        fun applyTextStyleAdjustments_7ree(textView: TextView) {
            val spannable_7ree = textView.text as Spannable
            // 处理粗体样式，将其改为下划线样式
            val boldSpans_7ree = spannable_7ree.getSpans(0, spannable_7ree.length, StyleSpan::class.java)
            boldSpans_7ree.forEach { span_7ree ->
                if (span_7ree.style == android.graphics.Typeface.BOLD) {
                    val start_7ree = spannable_7ree.getSpanStart(span_7ree)
                    val end_7ree = spannable_7ree.getSpanEnd(span_7ree)
                    // 移除原有的粗体样式
                    spannable_7ree.removeSpan(span_7ree)
                    // 应用下划线样式
                    spannable_7ree.setSpan(
                        UnderlineSpan(),
                        start_7ree,
                        end_7ree,
                        android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
        
        /**
         * 渲染Markdown内容到TextView
         */
        fun renderMarkdownToTextView_7ree(textView: TextView, markdownContent: String) {
            val markwon_7ree = Markwon.builder(textView.context).build()
            markwon_7ree.setMarkdown(textView, markdownContent)
            applyTextStyleAdjustments_7ree(textView)
        }
        
        /**
         * 解析Markdown内容并提取各个部分
         */
        fun parseMarkdownContent_7ree(queryResult: String): MarkdownContent_7ree {
            // Log.d(TAG_7ree, "开始解析Markdown内容，输入长度: ${queryResult.length}")
            // Log.d(TAG_7ree, "输入内容前200字符: ${queryResult.take(200)}")
            val lines_7ree = queryResult.split("\n")
            // Log.d(TAG_7ree, "分割后行数: ${lines_7ree.size}")
            
            val beforePhonetic_7ree = StringBuilder()
            val afterPhonetic_7ree = StringBuilder()
            val afterExamples_7ree = StringBuilder()
            val chineseMeaning_7ree = StringBuilder()
            var foundPhoneticSection_7ree = false
            var foundExamplesSection_7ree = false
            var foundChineseMeaningSection_7ree = false
            var phoneticTitleLine_7ree = ""
            var examplesTitleLine_7ree = ""
            
            // 过滤掉"查询单词"标题行、单词英文行和"中文词义"标题行
            val filteredLines_7ree = mutableListOf<String>()
            var skipNextLine_7ree = false
            
            for (line_7ree in lines_7ree) {
                val trimmedLine_7ree = line_7ree.trim()

                // 检查是否是"查询单词"标题行
                if (trimmedLine_7ree.matches(Regex("^#+\\s*查询单词.*$"))) {
                    skipNextLine_7ree = true // 标记跳过下一行（单词英文）
                    continue // 跳过当前行
                }
                
                // 如果标记了跳过下一行，则跳过当前行
                if (skipNextLine_7ree) {
                    skipNextLine_7ree = false
                    continue
                }
                
                // 检查是否是"中文词义"标题行，如果是则跳过
                if (trimmedLine_7ree.matches(Regex("^#+\\s*中文词义.*$"))) {
                    continue // 跳过中文词义标题行
                }
                
                // 保留其他行
                filteredLines_7ree.add(line_7ree)
            }
            
            // 第一遍：查找音标标题，同时完全排除中文词义内容
            var inChineseMeaningSection_7ree = false
            for (line_7ree in filteredLines_7ree) {
                val trimmedLine_7ree = line_7ree.trim()

                if (trimmedLine_7ree.matches(Regex("^#+\\s*音标.*$"))) {
                    foundPhoneticSection_7ree = true
                    phoneticTitleLine_7ree = line_7ree
                    break
                } else if (trimmedLine_7ree.matches(Regex("^#+\\s*中文词义.*$"))) {
                    inChineseMeaningSection_7ree = true
                    continue
                } else if (inChineseMeaningSection_7ree && trimmedLine_7ree.matches(Regex("^#+\\s+.+$"))) {
                    inChineseMeaningSection_7ree = false
                    continue
                } else if (inChineseMeaningSection_7ree) {
                    // 跳过中文词义段落中的所有内容行
                    continue
                }

                if (!inChineseMeaningSection_7ree) {
                    beforePhonetic_7ree.append(line_7ree).append("\n")
                }
            }
            
            // 第二遍：查找英文例句标题
            for (line_7ree in filteredLines_7ree) {
                if (line_7ree.trim().matches(Regex("^#+\\s*英文例句.*$"))) {
                    foundExamplesSection_7ree = true
                    examplesTitleLine_7ree = line_7ree
                    break
                }
            }
            
            // 第三遍：查找中文词义的"第二行"
            var foundChineseMeaningStarted_7ree = false
            var chineseMeaningLineCount_7ree = 0
            for (line_7ree in lines_7ree) {
                val trimmedLine_7ree = line_7ree.trim()

                if (foundChineseMeaningStarted_7ree) {
                    // 如果遇到下一个标题，则停止处理
                    if (trimmedLine_7ree.matches(Regex("^#+\\s+.+$"))) {
                        break
                    }

                    // 跳过空行，只处理非空行
                    if (trimmedLine_7ree.isNotEmpty()) {
                        chineseMeaningLineCount_7ree += 1

                        // 只保留第一个非空行（标题后的第一行内容）
                        if (chineseMeaningLineCount_7ree == 1) {
                            chineseMeaning_7ree.append(line_7ree.trimStart()).append("\n")
                            // Log.d(TAG_7ree, "提取到中文词义内容: ${line_7ree.trimStart()}")
                            break // 提前终止
                        }
                    }
                }

                if (trimmedLine_7ree.matches(Regex("^#+\\s*中文词义.*$"))) {
                    // Log.d(TAG_7ree, "找到中文词义标题行: $trimmedLine_7ree")
                    foundChineseMeaningStarted_7ree = true
                    foundChineseMeaningSection_7ree = true
                }
            }
            
            // 收集音标标题之后到英文例句标题之前的内容
            if (foundPhoneticSection_7ree) {
                var afterPhoneticStarted_7ree = false
                for (line_7ree in filteredLines_7ree) {
                    if (afterPhoneticStarted_7ree && !line_7ree.trim().matches(Regex("^#+\\s*英文例句.*$"))) {
                        afterPhonetic_7ree.append(line_7ree).append("\n")
                    } else if (line_7ree.trim().matches(Regex("^#+\\s*音标.*$"))) {
                        afterPhoneticStarted_7ree = true
                    } else if (line_7ree.trim().matches(Regex("^#+\\s*英文例句.*$"))) {
                        break
                    }
                }
            }
            
            // 收集英文例句标题之后的内容
            if (foundExamplesSection_7ree) {
                var afterExamplesStarted_7ree = false
                for (line_7ree in filteredLines_7ree) {
                    if (afterExamplesStarted_7ree) {
                        // 去除行首的额外空格，确保对齐
                        val trimmedLine_7ree = line_7ree.trimStart()
                        afterExamples_7ree.append(trimmedLine_7ree).append("\n")
                    } else if (line_7ree.trim().matches(Regex("^#+\\s*英文例句.*$"))) {
                        afterExamplesStarted_7ree = true
                    }
                }
            }
            
            // Log.d(TAG_7ree, "解析完成 - 中文词义: '${chineseMeaning_7ree.toString()}', 找到中文词义段落: $foundChineseMeaningSection_7ree")
            
            return MarkdownContent_7ree(
                beforePhonetic = beforePhonetic_7ree.toString(),
                afterPhonetic = afterPhonetic_7ree.toString(),
                afterExamples = afterExamples_7ree.toString(),
                chineseMeaning = chineseMeaning_7ree.toString(),
                phoneticTitleLine = phoneticTitleLine_7ree,
                examplesTitleLine = examplesTitleLine_7ree,
                foundPhoneticSection = foundPhoneticSection_7ree,
                foundExamplesSection = foundExamplesSection_7ree,
                foundChineseMeaningSection = foundChineseMeaningSection_7ree,
                filteredLines = filteredLines_7ree
            )
        }
    }
}

/**
 * Markdown内容数据类
 */
data class MarkdownContent_7ree(
    val beforePhonetic: String,
    val afterPhonetic: String,
    val afterExamples: String,
    val chineseMeaning: String,
    val phoneticTitleLine: String,
    val examplesTitleLine: String,
    val foundPhoneticSection: Boolean,
    val foundExamplesSection: Boolean,
    val foundChineseMeaningSection: Boolean,
    val filteredLines: List<String>
)

/**
 * Markdown渲染组件
 */
@Composable
fun MarkdownRenderer_7ree(
    queryResult: String,
    onWordSpeak: () -> Unit = {},
    onExamplesSpeak: () -> Unit = {},
    onWordStopSpeak: () -> Unit = {},
    onExamplesStopSpeak: () -> Unit = {},
    wordTtsState: TtsButtonState_7ree = TtsButtonState_7ree.IDLE,
    examplesTtsState: TtsButtonState_7ree = TtsButtonState_7ree.IDLE,
    modifier: Modifier = Modifier
) {
    val content = remember(queryResult) {
        MarkdownRenderer_7ree.parseMarkdownContent_7ree(queryResult)
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // 显示音标标题之前的内容
        if (content.beforePhonetic.isNotEmpty()) {
            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        textSize = 20f
                        gravity = android.view.Gravity.CENTER
                        setTypeface(android.graphics.Typeface.DEFAULT_BOLD)
                        setTextColor(context.getColor(R.color.green_500_7ree))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                update = { textView ->
                    val markwon_7ree = Markwon.builder(textView.context).build()
                    markwon_7ree.setMarkdown(textView, content.beforePhonetic)
                    MarkdownRenderer_7ree.applyTextStyleAdjustments_7ree(textView)
                }
            )
        }
        
        // 中文词义已在标题栏下方单独显示，此处不再重复显示
        
        // 显示音标标题行（带单词朗读喇叭按钮）
        if (content.foundPhoneticSection) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 音标标题
                AndroidView(
                    factory = { context ->
                        TextView(context)
                    },
                    update = { textView ->
                        val markwon_7ree = Markwon.builder(textView.context).build()
                        markwon_7ree.setMarkdown(textView, content.phoneticTitleLine)
                    }
                )
                
                // 单词朗读按钮（带状态切换）
                TtsButton_7ree(
                    state = wordTtsState,
                    onPlayClick = onWordSpeak,
                    onPauseClick = onWordStopSpeak,
                    contentDescription = "朗读单词"
                )
            }
            
            // 显示音标标题之后的内容
            if (content.afterPhonetic.isNotEmpty()) {
                AndroidView(
                    factory = { context ->
                        TextView(context).apply {
                            textSize = 14f
                        }
                    },
                    update = { textView ->
                        val markwon_7ree = Markwon.builder(textView.context).build()
                        markwon_7ree.setMarkdown(textView, content.afterPhonetic)
                        MarkdownRenderer_7ree.applyTextStyleAdjustments_7ree(textView)
                    }
                )
            }
        }
        
        // 显示英文例句标题行（带例句朗读喇叭按钮）
        if (content.foundExamplesSection) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 英文例句标题
                AndroidView(
                    factory = { context ->
                        TextView(context)
                    },
                    update = { textView ->
                        val markwon_7ree = Markwon.builder(textView.context).build()
                        markwon_7ree.setMarkdown(textView, content.examplesTitleLine)
                    }
                )
                
                // 例句朗读按钮（带状态切换）
                TtsButton_7ree(
                    state = examplesTtsState,
                    onPlayClick = onExamplesSpeak,
                    onPauseClick = onExamplesStopSpeak,
                    contentDescription = "朗读例句"
                )
            }
            
            // 显示英文例句标题之后的内容
            if (content.afterExamples.isNotEmpty()) {
                AndroidView(
                    factory = { context ->
                        TextView(context).apply {
                            textSize = 14f
                        }
                    },
                    update = { textView ->
                        val markwon_7ree = Markwon.builder(textView.context).build()
                        markwon_7ree.setMarkdown(textView, content.afterExamples)
                        MarkdownRenderer_7ree.applyTextStyleAdjustments_7ree(textView)
                    }
                )
            }
        } else if (!content.foundPhoneticSection) {
            // 如果既没有找到音标部分也没有找到英文例句部分，直接显示过滤后的内容
            val filteredContent_7ree = content.filteredLines.joinToString("\n")
            if (filteredContent_7ree.isNotBlank()) {
                AndroidView(
                    factory = { context ->
                        TextView(context).apply {
                            textSize = 14f
                        }
                    },
                    update = { textView ->
                        val markwon_7ree = Markwon.builder(textView.context).build()
                        markwon_7ree.setMarkdown(textView, filteredContent_7ree)
                        MarkdownRenderer_7ree.applyTextStyleAdjustments_7ree(textView)
                    }
                )
            }
        }
    }
}
