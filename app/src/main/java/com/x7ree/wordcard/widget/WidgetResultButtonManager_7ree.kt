package com.x7ree.wordcard.widget

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.x7ree.wordcard.MainActivity
import com.x7ree.wordcard.R

/**
 * Widget结果按钮管理器
 * 负责处理结果页面的各种按钮功能
 */
class WidgetResultButtonManager_7ree(
    private val activity: Activity,
    private val ttsManager_7ree: WidgetTTSManager_7ree,
    private val touchFeedbackManager_7ree: WidgetTouchFeedbackManager_7ree
) {
    
    /**
     * 设置结果按钮的点击事件
     * @param queryText 查询的单词
     * @param currentQueryWord 当前查询的单词
     */
    fun setupResultButtons_7ree(queryText: String, currentQueryWord: String) {
        setupSpeakButton_7ree(currentQueryWord)
        setupWordbookButton_7ree()
        setupDetailButton_7ree(queryText)
    }
    
    /**
     * 设置朗读按钮
     */
    private fun setupSpeakButton_7ree(currentQueryWord: String) {
        val speakButton = activity.findViewById<ImageView>(R.id.widget_speak_button_7ree)
        val speakText = activity.findViewById<TextView>(R.id.widget_speak_text_7ree)
        val speakContainer = activity.findViewById<LinearLayout>(R.id.widget_speak_container_7ree)
        
        // 添加触摸反馈效果
        touchFeedbackManager_7ree.addTouchFeedback_7ree(speakButton)
        touchFeedbackManager_7ree.addTextTouchFeedback_7ree(speakText, speakButton)
        
        speakContainer.setOnClickListener {
            ttsManager_7ree.speakWord_7ree(currentQueryWord)
        }
    }
    
    /**
     * 设置单词本按钮
     */
    private fun setupWordbookButton_7ree() {
        val wordbookButton = activity.findViewById<ImageView>(R.id.widget_wordbook_button_7ree)
        val wordbookText = activity.findViewById<TextView>(R.id.widget_wordbook_text_7ree)
        val wordbookContainer = activity.findViewById<LinearLayout>(R.id.widget_wordbook_container_7ree)
        
        // 添加触摸反馈效果
        touchFeedbackManager_7ree.addTouchFeedback_7ree(wordbookButton)
        touchFeedbackManager_7ree.addTextTouchFeedback_7ree(wordbookText, wordbookButton)
        
        wordbookContainer.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java).apply {
                action = WordQueryWidgetProvider_7ree.ACTION_WIDGET_WORDBOOK_7ree
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            activity.startActivity(intent)
            activity.finish()
        }
    }
    
    /**
     * 设置详情页按钮
     */
    private fun setupDetailButton_7ree(queryText: String) {
        val detailButton = activity.findViewById<ImageView>(R.id.widget_detail_button_7ree)
        val detailText = activity.findViewById<TextView>(R.id.widget_detail_text_7ree)
        val detailContainer = activity.findViewById<LinearLayout>(R.id.widget_detail_container_7ree)
        
        // 添加触摸反馈效果
        touchFeedbackManager_7ree.addTouchFeedback_7ree(detailButton)
        touchFeedbackManager_7ree.addTextTouchFeedback_7ree(detailText, detailButton)
        
        detailContainer.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java).apply {
                putExtra("query_word", queryText)
                putExtra("show_detail", true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            activity.startActivity(intent)
            activity.finish()
        }
    }
}