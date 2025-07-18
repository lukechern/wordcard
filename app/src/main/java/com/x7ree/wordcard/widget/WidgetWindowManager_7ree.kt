package com.x7ree.wordcard.widget

import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager

/**
 * Widget窗口配置管理器
 * 负责设置Widget Activity的窗口属性和位置
 */
class WidgetWindowManager_7ree {
    
    companion object {
        /**
         * 配置Widget窗口属性
         * @param activity 目标Activity
         */
        fun configureWindow_7ree(activity: AppCompatActivity) {
            // 隐藏标题栏
            activity.supportActionBar?.hide()
            
            // 设置窗口参数
            activity.window?.let { window ->
                // 设置软键盘模式
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                // 监听窗口外的触摸事件
                window.addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
                
                window.attributes?.let { params ->
                    params.width = WindowManager.LayoutParams.MATCH_PARENT
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT
                    
                    // 将窗口向上移动屏幕高度的25%
                    val displayMetrics = activity.resources.displayMetrics
                    val screenHeight = displayMetrics.heightPixels
                    params.y = -(screenHeight * 0.25).toInt()
                    
                    // 设置窗口重力，确保位置固定
                    params.gravity = android.view.Gravity.CENTER or android.view.Gravity.TOP
                    
                    window.attributes = params
                }
            }
        }
    }
}