package com.x7ree.wordcard.widget.config

import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.x7ree.wordcard.R

class WidgetConfigDisplayHelper_7ree(private val activity: WidgetConfigBaseActivity_7ree) {
    
    fun displayResult_7ree(
        extractedInfo: Pair<String, String>,
        queryText: String,
        progressBar: ProgressBar,
        resultText: TextView,
        wordTitle: TextView,
        chineseMeaning: TextView,
        inputText: EditText,
        queryButton: Button,
        showButtons: Boolean = true, // 新增参数控制是否显示按钮
        fullContent: String = "" // 新增参数传递完整内容用于判断流式状态
    ) {
        val loadingText = activity.findViewById<TextView>(R.id.widget_loading_text_7ree)
        val resultButtons = activity.findViewById<LinearLayout>(R.id.widget_result_buttons_7ree)
        
        // 切换到结果显示状态
        activity.getUiStateManager_7reeInstance().switchToResultState_7ree(
            progressBar, loadingText, resultText, wordTitle, chineseMeaning,
            inputText, queryButton, resultButtons, showButtons
        )
        
        // 从Pair中获取详细信息
        val detailInfo = extractedInfo.second
        
        // 解析完整内容获取单词和中文意思
        val parsedContent = com.x7ree.wordcard.widget.WidgetMarkdownParser_7ree.parseBasicInfo_7ree(detailInfo)
        
        // 显示单词标题
        wordTitle.text = if (parsedContent.word.isNotEmpty()) parsedContent.word else queryText
        
        // 检查流式输出是否完成
        val isStreamingComplete = activity.getUiStateManager_7reeInstance().isContentComplete_7ree(fullContent)
        
        // 更新中文意思显示，传递流式输出状态
        activity.getUiStateManager_7reeInstance().updateChineseMeaning_7ree(chineseMeaning, parsedContent.chineseMeaning, isStreamingComplete)
        
        // 使用完整的模板格式显示内容
        com.x7ree.wordcard.widget.WidgetMarkdownParser_7ree.renderToTextView_7ree(resultText, detailInfo)
        
        // 如果需要显示按钮，设置按钮点击事件
        if (showButtons) {
            // 使用解析出的单词或查询文本作为朗读内容
            val wordToSpeak = if (parsedContent.word.isNotEmpty()) parsedContent.word else queryText
            activity.getResultButtonManager_7reeInstance().setupResultButtons_7ree(queryText, wordToSpeak)
            
            // 在按钮设置完成后，延迟记录UI坐标以确保布局完成
            resultButtons.post {
                activity.logResultStateUICoordinates_7ree(wordTitle, chineseMeaning, resultText, resultButtons)
            }
        } else {
            // 即使不显示按钮，也记录UI坐标用于调试
            resultText.post {
                activity.logResultStateUICoordinates_7ree(wordTitle, chineseMeaning, resultText, resultButtons)
            }
        }
    }
}
