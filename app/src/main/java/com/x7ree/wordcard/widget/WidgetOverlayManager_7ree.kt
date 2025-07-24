package com.x7ree.wordcard.widget

import android.app.Activity
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat

/**
 * Widget蒙版管理器
 * 负责管理桌面小组件的黑色蒙版显示和隐藏
 */
class WidgetOverlayManager_7ree(private val activity: Activity) {
    
    private var overlayView_7ree: View? = null
    private var rootContainer_7ree: ViewGroup? = null
    private var decorView_7ree: ViewGroup? = null
    
    /**
     * 初始化蒙版管理器
     */
    fun initialize_7ree() {
        // 获取根容器和DecorView
        rootContainer_7ree = activity.findViewById(android.R.id.content)
        decorView_7ree = activity.window.decorView as? ViewGroup
        
        // 创建蒙版视图
        createOverlayView_7ree()
    }
    
    /**
     * 创建蒙版视图
     */
    private fun createOverlayView_7ree() {
        // 优先使用DecorView，如果不可用则使用ContentFrameLayout
        val targetContainer = decorView_7ree ?: rootContainer_7ree
        
        if (overlayView_7ree == null && targetContainer != null) {
            // 获取真实屏幕尺寸
            val displayMetrics = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics = activity.windowManager.currentWindowMetrics
                val displayMetrics = DisplayMetrics()
                windowMetrics.bounds
                displayMetrics
            } else {
                @Suppress("DEPRECATION")
                val displayMetrics = DisplayMetrics()
                @Suppress("DEPRECATION")
                activity.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
                displayMetrics
            }
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels
            
            overlayView_7ree = View(activity).apply {
                // 设置70%不透明度的黑色背景
                setBackgroundColor(ContextCompat.getColor(activity, android.R.color.black))
                alpha = 0.7f
                
                // 计算容器位置偏移
                val containerLocation = IntArray(2)
                targetContainer.getLocationOnScreen(containerLocation)
                
                // 使用FrameLayout.LayoutParams来确保支持边距
                layoutParams = FrameLayout.LayoutParams(screenWidth, screenHeight).apply {
                    // 设置负边距来补偿容器的位置偏移，让蒙版从屏幕(0,0)开始
                    topMargin = -containerLocation[1]
                    leftMargin = -containerLocation[0]
                }
                
                // 初始状态为隐藏
                visibility = View.GONE
                
                // 设置触摸事件不拦截，让触摸事件可以穿透到下层视图
                // 这样蒙版只提供视觉效果，不会阻止用户与查询卡片的交互
                isClickable = false
                isFocusable = false
            }
            
            // 将蒙版添加到目标容器的最底层
            targetContainer.addView(overlayView_7ree, 0)
        }
    }
    
    /**
     * 显示蒙版
     */
    fun showOverlay_7ree() {
        overlayView_7ree?.let { overlay ->
            if (overlay.visibility != View.VISIBLE) {
                overlay.visibility = View.VISIBLE
                // 添加淡入动画效果
                overlay.animate()
                    .alpha(0.7f)
                    .setDuration(200)
                    .start()
            }
        }
    }
    
    /**
     * 隐藏蒙版
     */
    fun hideOverlay_7ree() {
        overlayView_7ree?.let { overlay ->
            if (overlay.visibility == View.VISIBLE) {
                // 添加淡出动画效果
                overlay.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction {
                        overlay.visibility = View.GONE
                        overlay.alpha = 0.7f // 重置透明度为下次显示准备
                    }
                    .start()
            }
        }
    }
    
    /**
     * 检查蒙版是否正在显示
     */
    fun isOverlayVisible_7ree(): Boolean {
        return overlayView_7ree?.visibility == View.VISIBLE
    }
    
    /**
     * 释放资源
     */
    fun release_7ree() {
        overlayView_7ree?.let { overlay ->
            // 从正确的容器中移除蒙版
            val targetContainer = decorView_7ree ?: rootContainer_7ree
            targetContainer?.removeView(overlay)
        }
        overlayView_7ree = null
        rootContainer_7ree = null
        decorView_7ree = null
    }
}
