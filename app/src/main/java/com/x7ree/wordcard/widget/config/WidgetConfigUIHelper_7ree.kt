package com.x7ree.wordcard.widget.config

import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.x7ree.wordcard.R
import com.x7ree.wordcard.utils.hideKeyboard_7ree
import com.x7ree.wordcard.widget.WidgetInputValidator_7ree
import com.x7ree.wordcard.widget.WidgetKeyboardManager_7ree
import com.x7ree.wordcard.widget.WidgetOverlayManager_7ree

class WidgetConfigUIHelper_7ree(private val activity: WidgetConfigBaseActivity_7ree) {
    
    /**
     * 设置基础UI - 立即显示界面，提供基本交互
     */
    fun setupBasicUI_7ree() {
        val inputText = activity.findViewById<EditText>(R.id.widget_input_config_7ree)
        val queryButton = activity.findViewById<Button>(R.id.widget_query_button_config_7ree)
        val closeButton = activity.findViewById<ImageView>(R.id.widget_close_button_7ree)

        // 设置关闭按钮点击事件 - 这个不依赖管理器，可以立即设置
        closeButton.setOnClickListener {
            activity.finish()
        }
        
        // 设置输入框基本状态
        inputText.requestFocus()
        
        // 初始状态按钮为灰色无效
        queryButton.isEnabled = false
        queryButton.alpha = 0.5f
        
        // 设置基本的按钮点击事件（暂时禁用，等待初始化完成）
        // 在 setupAdvancedUI_7ree 中会重新设置，此处的逻辑可以简化
        queryButton.setOnClickListener {
            // 管理器此时可能未初始化，但按钮是禁用的。
            // 完整的逻辑在 setupAdvancedUI_7ree 中设置。
        }
    }
    
    /**
     * 设置高级UI功能 - 在管理器初始化完成后调用
     */
    fun setupAdvancedUI_7ree() {
        val inputText = activity.findViewById<EditText>(R.id.widget_input_config_7ree)
        val queryButton = activity.findViewById<Button>(R.id.widget_query_button_config_7ree)
        val customKeyboardContainer = activity.findViewById<LinearLayout>(R.id.widget_custom_keyboard_container_7ree)
        val customCursor = activity.findViewById<com.x7ree.wordcard.widget.WidgetCustomCursor_7ree>(R.id.widget_custom_cursor_7ree)
        val closeButton = activity.findViewById<ImageView>(R.id.widget_close_button_7ree)

        // 更新关闭按钮点击事件，现在可以使用管理器了
        // 假设此时管理器已初始化
        closeButton.setOnClickListener {
            // 隐藏键盘
            activity.getKeyboardManager_7reeInstance().hideKeyboard_7ree()
            inputText.hideKeyboard_7ree()
            // 隐藏蒙版
            activity.getOverlayManager_7reeInstance().hideOverlay_7ree()
            // 关闭Activity
            activity.finish()
        }
        
        // 设置自定义键盘容器
        activity.getKeyboardManager_7reeInstance().setCustomKeyboardContainer_7ree(customKeyboardContainer)
        
        // 手动设置自定义光标组件到键盘管理器
        activity.getKeyboardManager_7reeInstance().setCustomCursor_7ree(customCursor)
        
        // 设置蒙版管理器到键盘管理器
        activity.getKeyboardManager_7reeInstance().setOverlayManager_7ree(activity.getOverlayManager_7reeInstance())
        
        // 绑定键盘管理器到输入框
        activity.getKeyboardManager_7reeInstance().bindInputText_7ree(inputText, { _ -> }, {
            activity.performSearch_7ree(inputText, queryButton)
        })
        
        // 显示蒙版，为查询卡片提供视觉聚焦
        // 延迟显示蒙版，确保Activity完全加载
        inputText.post {
            activity.getOverlayManager_7reeInstance().showOverlay_7ree()
        }
        
        // 延迟自动弹出键盘，确保UI完全加载
        inputText.post {
            inputText.requestFocus()
            // 再次延迟确保键盘状态正确初始化
            inputText.postDelayed({
                activity.getKeyboardManager_7reeInstance().showCustomKeyboard_7ree()
                // 确保自定义光标在使用自定义键盘时显示
                if (activity.getKeyboardManager_7reeInstance().getCurrentKeyboardType_7ree() == "custom") {
                    customCursor.showCursor_7ree()
                }
            }, 100)
        }
        
        // 设置按钮状态管理
        activity.getButtonManager_7reeInstance().updateButtonState_7ree(queryButton, false)
        
        // 设置输入验证
        activity.getInputValidator_7reeInstance().setupInputFilter_7ree(inputText)
        activity.getInputValidator_7reeInstance().setupTextWatcher_7ree(inputText, queryButton)
        activity.getInputValidator_7reeInstance().setupEnterKeyListener_7ree(inputText) {
            activity.performSearch_7ree(inputText, queryButton)
        }
        
        // 更新按钮点击事件，现在所有管理器都已初始化
        queryButton.setOnClickListener {
            activity.performSearch_7ree(inputText, queryButton)
        }
    }
}
