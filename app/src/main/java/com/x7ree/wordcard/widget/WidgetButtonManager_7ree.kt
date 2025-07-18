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
        } else {
            button.background = ContextCompat.getDrawable(context, R.drawable.widget_gray_button_background_7ree)
        }
        
        // 创建带图标的文字
        val buttonText = "用AI查询"
        val spannableString = SpannableString(" $buttonText")
        
        // 获取放大镜图标
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_search_magnifier_7ree)
        drawable?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            // 使用ALIGN_BOTTOM来降低图标位置
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BOTTOM)
            spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        
        button.text = spannableString
    }
}