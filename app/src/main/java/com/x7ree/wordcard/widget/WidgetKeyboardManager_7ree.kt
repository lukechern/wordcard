package com.x7ree.wordcard.widget

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.view.inputmethod.InputMethodManager
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.lifecycleScope
import com.x7ree.wordcard.R
import com.x7ree.wordcard.config.AppConfigManager_7ree
import com.x7ree.wordcard.config.GeneralConfig_7ree
import com.x7ree.wordcard.utils.CustomKeyboard_7ree
import com.x7ree.wordcard.utils.CustomKeyboardState_7ree
import com.x7ree.wordcard.utils.hideKeyboard_7ree
import kotlinx.coroutines.launch

/**
 * Widget键盘管理器
 * 负责管理桌面小组件的键盘切换功能（系统键盘和自定义键盘）
 */
class WidgetKeyboardManager_7ree(private val activity: Activity) {
    
    private val configManager_7ree = AppConfigManager_7ree(activity)
    private val customKeyboardState_7ree = CustomKeyboardState_7ree()
    private var currentConfig_7ree = GeneralConfig_7ree()
    private var customKeyboardContainer_7ree: LinearLayout? = null
    private var composeView_7ree: ComposeView? = null
    private val inputMethodManager_7ree = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    private var customCursor_7ree: WidgetCustomCursor_7ree? = null
    
    // 当前输入框和回调
    private var currentInputText_7ree: EditText? = null
    private var onTextChanged_7ree: ((String) -> Unit)? = null
    private var onSearchAction_7ree: (() -> Unit)? = null
    
    /**
     * 初始化键盘管理器
     */
    fun initialize_7ree() {
        // 获取屏幕宽度
        val displayMetrics = activity.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        // 加载配置
        loadConfig_7ree()
        
        // 获取自定义键盘容器
        if (customKeyboardContainer_7ree == null) {
            customKeyboardContainer_7ree = activity.findViewById(R.id.widget_custom_keyboard_container_7ree)
        }
        
        // 获取自定义光标组件
        if (customCursor_7ree == null) {
            customCursor_7ree = activity.findViewById(R.id.widget_custom_cursor_7ree)
        }
        
        // 初始化自定义键盘容器
        setupCustomKeyboardContainer_7ree()

        // 强制设置容器宽度为屏幕宽度
        customKeyboardContainer_7ree?.let {
            val layoutParams = it.layoutParams
            layoutParams.width = screenWidth
            it.layoutParams = layoutParams
        }
    }
    
    /**
     * 加载键盘配置
     */
    private fun loadConfig_7ree() {
        currentConfig_7ree = configManager_7ree.loadGeneralConfig_7ree()
    }
    
    /**
     * 设置自定义键盘容器
     */
    private fun setupCustomKeyboardContainer_7ree() {
        if (customKeyboardContainer_7ree != null && composeView_7ree == null) {
            // 清空容器，防止重复添加
            customKeyboardContainer_7ree?.removeAllViews()
            
            // 创建ComposeView来承载自定义键盘
            composeView_7ree = ComposeView(activity).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    // 监听键盘状态
                    val isVisible by customKeyboardState_7ree.isVisible_7ree
                    
                    if (isVisible && shouldUseCustomKeyboard_7ree()) {
                        CustomKeyboard_7ree(
                            onKeyPress_7ree = { key ->
                                handleKeyPress_7ree(key)
                            },
                            onBackspace_7ree = {
                                handleBackspace_7ree()
                            },
                            onSearch_7ree = {
                                handleSearch_7ree()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        )
                    }
                }
            }
            
            // 设置ComposeView的布局参数为MATCH_PARENT
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            composeView_7ree?.layoutParams = layoutParams
            
            customKeyboardContainer_7ree?.addView(composeView_7ree)
        }
    }
    
    /**
     * 设置自定义键盘容器
     */
    fun setCustomKeyboardContainer_7ree(container: LinearLayout) {
        customKeyboardContainer_7ree = container
        setupCustomKeyboardContainer_7ree()
    }
    
    /**
     * 绑定输入框
     */
    fun bindInputText_7ree(
        inputText: EditText,
        onTextChanged: (String) -> Unit,
        onSearchAction: () -> Unit
    ) {
        currentInputText_7ree = inputText
        onTextChanged_7ree = onTextChanged
        onSearchAction_7ree = onSearchAction
        
        // 如果使用自定义键盘，设置输入框为只读并禁用系统键盘
        if (shouldUseCustomKeyboard_7ree()) {
            inputText.setShowSoftInputOnFocus(false) // 强制禁用系统键盘
            inputText.isFocusable = true
            inputText.isFocusableInTouchMode = true
            inputText.isClickable = true
            inputText.isCursorVisible = false // 隐藏系统光标
            
            // 强制禁用系统键盘
            inputText.setRawInputType(0)
            inputText.setTextIsSelectable(false)
            
            // 绑定自定义光标到输入框
            customCursor_7ree?.bindEditText_7ree(inputText)
            
            // 设置输入框的焦点监听
             inputText.setOnFocusChangeListener { _, hasFocus ->
                 // 强制隐藏系统键盘
                 inputMethodManager_7ree.hideSoftInputFromWindow(inputText.windowToken, 0)
                 inputText.hideKeyboard_7ree()
                 if (hasFocus) {
                     // 显示自定义键盘和光标
                     showCustomKeyboard_7ree()
                     customCursor_7ree?.showCursor_7ree()
                 } else {
                     // 隐藏自定义键盘和光标
                     hideCustomKeyboard_7ree()
                     customCursor_7ree?.hideCursor_7ree()
                 }
             }
             
             // 设置点击监听
             inputText.setOnClickListener {
                 // 强制隐藏系统键盘
                 inputMethodManager_7ree.hideSoftInputFromWindow(inputText.windowToken, 0)
                 inputText.hideKeyboard_7ree()
                 // 如果没有焦点，则请求焦点
                 if (!inputText.isFocused) {
                     inputText.requestFocus()
                 }
                 showCustomKeyboard_7ree()
                 customCursor_7ree?.showCursor_7ree()
             }
        } else {
            // 使用系统键盘时的正常设置
            inputText.showSoftInputOnFocus = true
            inputText.isFocusable = true
            inputText.isFocusableInTouchMode = true
            inputText.isClickable = true
            inputText.isCursorVisible = true
        }
    }
    
    /**
     * 判断是否应该使用自定义键盘
     */
    private fun shouldUseCustomKeyboard_7ree(): Boolean {
        return currentConfig_7ree.keyboardType == "custom"
    }
    
    /**
     * 显示自定义键盘
     */
    internal fun showCustomKeyboard_7ree() {
        if (shouldUseCustomKeyboard_7ree()) {
            customKeyboardState_7ree.show_7ree()
            customKeyboardContainer_7ree?.visibility = View.VISIBLE
        }
    }
    
    /**
     * 隐藏自定义键盘
     */
    private fun hideCustomKeyboard_7ree() {
        customKeyboardState_7ree.hide_7ree()
        customKeyboardContainer_7ree?.visibility = View.GONE
    }
    
    /**
     * 隐藏键盘（公共方法）
     */
    fun hideKeyboard_7ree() {
        hideCustomKeyboard_7ree()
    }
    
    /**
     * 处理按键输入
     */
    private fun handleKeyPress_7ree(key: String) {
        currentInputText_7ree?.let { inputText ->
            val currentText = inputText.text.toString()
            val newText = currentText + key
            inputText.setText(newText)
            inputText.setSelection(newText.length)
            onTextChanged_7ree?.invoke(newText)
            // 更新自定义光标位置
            customCursor_7ree?.invalidate()
        }
    }
    
    /**
     * 处理退格键
     */
    private fun handleBackspace_7ree() {
        currentInputText_7ree?.let { inputText ->
            val currentText = inputText.text.toString()
            if (currentText.isNotEmpty()) {
                val newText = currentText.dropLast(1)
                inputText.setText(newText)
                inputText.setSelection(newText.length)
                onTextChanged_7ree?.invoke(newText)
                // 更新自定义光标位置
                customCursor_7ree?.invalidate()
            }
        }
    }
    
    /**
     * 处理搜索键
     */
    private fun handleSearch_7ree() {
        hideCustomKeyboard_7ree()
        onSearchAction_7ree?.invoke()
    }
    
    /**
     * 切换键盘类型
     */
    fun switchKeyboardType_7ree(keyboardType: String) {
        if (activity is AppCompatActivity) {
            activity.lifecycleScope.launch {
                val newConfig = GeneralConfig_7ree(keyboardType = keyboardType)
                val success = configManager_7ree.saveGeneralConfig_7ree(newConfig)
                
                if (success) {
                    currentConfig_7ree = newConfig
                    
                    // 重新配置当前输入框
                    currentInputText_7ree?.let { inputText ->
                        if (shouldUseCustomKeyboard_7ree()) {
                            inputText.showSoftInputOnFocus = false
                            inputText.hideKeyboard_7ree()
                            showCustomKeyboard_7ree()
                        } else {
                            inputText.showSoftInputOnFocus = true
                            hideCustomKeyboard_7ree()
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 获取当前键盘类型
     */
    fun getCurrentKeyboardType_7ree(): String {
        return currentConfig_7ree.keyboardType
    }
    
    /**
     * 释放资源
     */
    fun release_7ree() {
        hideCustomKeyboard_7ree()
        customCursor_7ree?.hideCursor_7ree()
        customKeyboardContainer_7ree?.removeAllViews()
        composeView_7ree = null
        customCursor_7ree = null
        currentInputText_7ree = null
        onTextChanged_7ree = null
        onSearchAction_7ree = null
    }
}