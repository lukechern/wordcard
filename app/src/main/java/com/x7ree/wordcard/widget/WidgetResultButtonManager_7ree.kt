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
    
    // TTS按钮状态
    private var currentTtsState = WidgetTtsButtonState.IDLE
    private var currentSpeakingWord = ""
    
    /**
     * Widget TTS按钮状态枚举
     */
    enum class WidgetTtsButtonState {
        IDLE,       // 默认状态 - 三角形播放图标
        LOADING,    // 加载状态 - 转圈载入图标
        PLAYING     // 播放状态 - 暂停图标
    }
    
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
     * 外部调用的自动朗读方法 - 用于自动朗读时更新按钮状态
     * @param word 要朗读的单词
     */
    fun autoSpeakWord_7ree(word: String) {
        val speakButton = activity.findViewById<ImageView>(R.id.widget_speak_button_7ree)
        // val speakText = activity.findViewById<TextView>(R.id.widget_speak_text_7ree)
        // val speakContainer = activity.findViewById<LinearLayout>(R.id.widget_speak_container_7ree)
        
        if (currentTtsState == WidgetTtsButtonState.IDLE) {
            // 开始朗读
            currentSpeakingWord = word
            updateSpeakButtonState(speakButton, WidgetTtsButtonState.LOADING)
            currentTtsState = WidgetTtsButtonState.LOADING
            
            // 调用TTS朗读，使用真实的状态回调
            ttsManager_7ree.speakWord_7ree(
                word = word,
                onPlayStart = {
                    // TTS音频开始播放时切换到播放状态
                    updateSpeakButtonState(speakButton, WidgetTtsButtonState.PLAYING)
                    currentTtsState = WidgetTtsButtonState.PLAYING
                },
                onPlayComplete = {
                    // TTS音频播放完成时恢复空闲状态
                    updateSpeakButtonState(speakButton, WidgetTtsButtonState.IDLE)
                    currentTtsState = WidgetTtsButtonState.IDLE
                    currentSpeakingWord = ""
                },
                onError = { _ ->
                    // TTS出错时恢复空闲状态
                    updateSpeakButtonState(speakButton, WidgetTtsButtonState.IDLE)
                    currentTtsState = WidgetTtsButtonState.IDLE
                    currentSpeakingWord = ""
                }
            )
        }
    }
    
    /**
     * 设置朗读按钮 - 支持状态切换
     */
    private fun setupSpeakButton_7ree(currentQueryWord: String) {
        val speakButton = activity.findViewById<ImageView>(R.id.widget_speak_button_7ree)
        val speakText = activity.findViewById<TextView>(R.id.widget_speak_text_7ree)
        val speakContainer = activity.findViewById<LinearLayout>(R.id.widget_speak_container_7ree)
        
        // 添加触摸反馈效果
        touchFeedbackManager_7ree.addTouchFeedback_7ree(speakButton)
        touchFeedbackManager_7ree.addTextTouchFeedback_7ree(speakText, speakButton)
        
        // 初始化按钮状态
        updateSpeakButtonState(speakButton, WidgetTtsButtonState.IDLE)
        
        speakContainer.setOnClickListener {
            when (currentTtsState) {
                WidgetTtsButtonState.IDLE -> {
                    // 开始朗读
                    currentSpeakingWord = currentQueryWord
                    updateSpeakButtonState(speakButton, WidgetTtsButtonState.LOADING)
                    currentTtsState = WidgetTtsButtonState.LOADING
                    
                    // 调用TTS朗读，使用真实的状态回调
                    ttsManager_7ree.speakWord_7ree(
                        word = currentQueryWord,
                        onPlayStart = {
                            // TTS音频开始播放时切换到播放状态
                            updateSpeakButtonState(speakButton, WidgetTtsButtonState.PLAYING)
                            currentTtsState = WidgetTtsButtonState.PLAYING
                        },
                        onPlayComplete = {
                            // TTS音频播放完成时恢复空闲状态
                            updateSpeakButtonState(speakButton, WidgetTtsButtonState.IDLE)
                            currentTtsState = WidgetTtsButtonState.IDLE
                            currentSpeakingWord = ""
                        },
                        onError = { _ ->
                            // TTS出错时恢复空闲状态
                            updateSpeakButtonState(speakButton, WidgetTtsButtonState.IDLE)
                            currentTtsState = WidgetTtsButtonState.IDLE
                            currentSpeakingWord = ""
                        }
                    )
                }
                WidgetTtsButtonState.LOADING -> {
                    // 加载中不响应点击
                }
                WidgetTtsButtonState.PLAYING -> {
                    // 停止朗读
                    ttsManager_7ree.stopSpeaking_7ree()
                    updateSpeakButtonState(speakButton, WidgetTtsButtonState.IDLE)
                    currentTtsState = WidgetTtsButtonState.IDLE
                    currentSpeakingWord = ""
                }
            }
        }
    }
    
    /**
     * 更新朗读按钮的状态显示
     */
    private fun updateSpeakButtonState(speakButton: ImageView, state: WidgetTtsButtonState) {
        when (state) {
            WidgetTtsButtonState.IDLE -> {
                speakButton.clearAnimation()
                speakButton.setImageResource(R.drawable.ic_speaker_7ree)
                speakButton.alpha = 1.0f
                speakButton.isEnabled = true
            }
            WidgetTtsButtonState.LOADING -> {
                // 使用自定义的加载图标
                speakButton.setImageResource(R.drawable.ic_loading_7ree)
                speakButton.alpha = 0.8f
                speakButton.isEnabled = false
                
                // 添加旋转动画
                val rotateAnimation = android.view.animation.RotateAnimation(
                    0f, 360f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
                ).apply {
                    duration = 1000
                    repeatCount = android.view.animation.Animation.INFINITE
                    interpolator = android.view.animation.LinearInterpolator()
                }
                speakButton.startAnimation(rotateAnimation)
            }
            WidgetTtsButtonState.PLAYING -> {
                speakButton.clearAnimation()
                speakButton.setImageResource(R.drawable.ic_pause_custom_7ree)
                speakButton.alpha = 1.0f
                speakButton.isEnabled = true
            }
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
