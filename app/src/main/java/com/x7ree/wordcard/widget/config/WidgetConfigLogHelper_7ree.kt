package com.x7ree.wordcard.widget.config

import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

class WidgetConfigLogHelper_7ree(private val activity: WidgetConfigBaseActivity_7ree) {
    
    /**
     * 记录查询状态下UI元素的坐标和间距信息
     */
    fun logSearchStateUICoordinates_7ree(progressBar: ProgressBar, loadingText: TextView, wordTitle: TextView) {
        // 功能已移到主Activity中实现
        activity.logSearchStateUICoordinates_7ree(progressBar, loadingText, wordTitle)
    }
    
    /**
     * 记录结果状态下UI元素的坐标和间距信息
     */
    fun logResultStateUICoordinates_7ree(wordTitle: TextView, chineseMeaning: TextView, resultText: TextView, resultButtons: LinearLayout) {
        // 功能已移到主Activity中实现
        activity.logResultStateUICoordinates_7ree(wordTitle, chineseMeaning, resultText, resultButtons)
    }
}