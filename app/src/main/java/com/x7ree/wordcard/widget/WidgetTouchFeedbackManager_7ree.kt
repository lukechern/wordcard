package com.x7ree.wordcard.widget

import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView

/**
 * Widget触摸反馈管理器
 * 负责处理按钮的触摸动画效果
 */
class WidgetTouchFeedbackManager_7ree {
    
    /**
     * 为ImageView添加触摸反馈效果
     * @param imageView 目标ImageView
     */
    fun addTouchFeedback_7ree(imageView: ImageView) {
        imageView.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // 只监控第一个触摸动作，按顺序播放动画
                view.animate()
                    .scaleX(0.7f)
                    .scaleY(0.7f)
                    .setDuration(100)
                    .withEndAction {
                        // 等待200毫秒后还原
                        view.postDelayed({
                            view.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start()
                        }, 200)
                    }
                    .start()
            }
            false // 返回false让其他点击事件继续处理
        }
    }
    
    /**
     * 为TextView添加触摸反馈效果，触摸时缩放对应的ImageView
     * @param textView 目标TextView
     * @param targetImageView 要缩放的ImageView
     */
    fun addTextTouchFeedback_7ree(textView: TextView, targetImageView: ImageView) {
        textView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // 点击文字时缩放对应的图标
                targetImageView.animate()
                    .scaleX(0.7f)
                    .scaleY(0.7f)
                    .setDuration(100)
                    .withEndAction {
                        // 等待200毫秒后还原
                        targetImageView.postDelayed({
                            targetImageView.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start()
                        }, 200)
                    }
                    .start()
            }
            false // 返回false让其他点击事件继续处理
        }
    }
}