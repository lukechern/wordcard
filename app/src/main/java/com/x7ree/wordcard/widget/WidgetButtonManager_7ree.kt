package com.x7ree.wordcard.widget

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.widget.Button
import androidx.core.content.ContextCompat
import com.x7ree.wordcard.R

/**
 * Widget按钮管理器
 * 负责管理查询按钮的状态和样式
 */
class WidgetButtonManager_7ree(private val context: Context) {
    
    /**
     * 更新按钮状态
     * @param button 目标按钮
     * @param enabled 是否启用
     */
    fun updateButtonState_7ree(button: Button, enabled: Boolean) {
        button.isEnabled = enabled
        if (enabled) {
            button.background = ContextCompat.getDrawable(context, R.drawable.widget_green_button_background_7ree)
            button.alpha = 1.0f // 确保启用状态下完全不透明
        } else {
            button.background = ContextCompat.getDrawable(context, R.drawable.widget_gray_button_background_7ree)
            button.alpha = 0.5f // 禁用状态下半透明
        }
        
        // 创建带图标的文字，增加间距
        val buttonText = "用AI查询"
        val spannableString = SpannableString("   $buttonText") // 增加3个空格作为间距
        
        // 获取星星图标
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_auto_awesome_7ree)
        drawable?.let {
            // 根据按钮状态设置图标颜色
            val iconColor = if (enabled) {
                ContextCompat.getColor(context, R.color.white) // 生效状态：白色图标
            } else {
                ContextCompat.getColor(context, R.color.dark_gray) // 未生效状态：深灰色图标
            }
            it.setTint(iconColor)
            // 调整图标大小，使其与文字更好地对齐（增加15%：15 * 1.15 ≈ 17dp）
            val iconSize = (17 * context.resources.displayMetrics.density).toInt() // 17dp转换为像素
            it.setBounds(0, 0, iconSize, iconSize)
            // 使用ALIGN_CENTER来垂直居中对齐
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_CENTER)
            spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        
        // 设置文字颜色
        val textColor = if (enabled) {
            ContextCompat.getColor(context, R.color.white) // 生效状态：白色文字
        } else {
            ContextCompat.getColor(context, R.color.dark_gray) // 未生效状态：深灰色文字
        }
        button.setTextColor(textColor)
        
        button.text = spannableString
    }
}
