package com.x7ree.wordcard.widget.config

import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.x7ree.wordcard.R
import com.x7ree.wordcard.utils.hideKeyboard_7ree
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WidgetConfigSearchHelper_7ree(private val activity: WidgetConfigBaseActivity_7ree) {
    
    fun performSearch_7ree(inputText: EditText, queryButton: Button) {
        // 收起键盘（包括自定义键盘）
        activity.getKeyboardManager_7reeInstance().hideKeyboard_7ree()
        inputText.hideKeyboard_7ree()
        
        val queryText = inputText.text.toString().trim()
        
        if (activity.getInputValidator_7reeInstance().isValidInput_7ree(queryText) && queryButton.isEnabled) {
            // 获取主界面的UI元素
            val progressBar = activity.findViewById<ProgressBar>(R.id.widget_progress_bar_7ree)
            val resultText = activity.findViewById<TextView>(R.id.widget_result_text_7ree)
            val wordTitle = activity.findViewById<TextView>(R.id.widget_word_title_7ree)
            val chineseMeaning = activity.findViewById<TextView>(R.id.widget_chinese_meaning_7ree)
            val loadingText = activity.findViewById<TextView>(R.id.widget_loading_text_7ree)
            val resultButtons = activity.findViewById<LinearLayout>(R.id.widget_result_buttons_7ree)
            
            // 切换到搜索状态
            activity.getUiStateManager_7reeInstance().switchToSearchState_7ree(
                inputText, queryButton, progressBar, resultText, wordTitle, 
                chineseMeaning, loadingText, resultButtons, queryText
            )
            
            // 添加查询状态下的UI坐标和间距日志输出
            activity.logSearchStateUICoordinates_7ree(progressBar, loadingText, wordTitle)
            
            // 禁用查询按钮防止重复查询
            queryButton.isEnabled = false
            
            // 执行查询
            activity.lifecycleScope.launch {
                try {
                    activity.getSearchManager_7reeInstance().searchWord_7ree(queryText).collect { searchResult ->
                        val extractedInfo = activity.getSearchManager_7reeInstance().extractBasicInfo_7ree(searchResult.content)
                        
                        if (extractedInfo.first.isNotBlank() || extractedInfo.second.isNotBlank()) {
                            activity.displayResult_7ree(
                                extractedInfo, queryText, progressBar, resultText, 
                                wordTitle, chineseMeaning, inputText, queryButton, 
                                searchResult.isComplete, searchResult.content
                            )
                        }
                        
                        // 如果搜索完成，保存当前查询的单词并检查是否需要自动朗读
                        if (searchResult.isComplete) {
                            activity.setCurrentQueryWord_7reeValue(queryText)
                            
                            // 自动朗读功能 - 当桌面小组件查询完成且配置启用时自动朗读单词
                            if (activity.getGeneralConfig_7reeInstance().autoReadAfterQuery && 
                                queryText.isNotBlank() && 
                                activity.getTtsManager_7reeInstance().isTtsReady_7ree()) {
                                // 延迟一小段时间确保结果完全显示
                                delay(500)
                                activity.getResultButtonManager_7reeInstance().autoSpeakWord_7ree(queryText)
                            }
                        }
                    }
                } catch (e: Exception) {
                    activity.getUiStateManager_7reeInstance().showErrorState_7ree(
                        progressBar, loadingText, resultText, 
                        "查询失败: ${e.localizedMessage}"
                    )
                } finally {
                    // 重新启用查询按钮
                    queryButton.isEnabled = true
                }
            }
        }
    }
}
