package com.x7ree.wordcard.widget

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ImageSpan
import android.view.KeyEvent
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.x7ree.wordcard.MainActivity
import com.x7ree.wordcard.R

class WidgetConfigActivity_7ree : Activity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 设置窗口参数，确保内容不被裁剪
        window?.let { window ->
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            window.attributes?.let { params ->
                params.width = WindowManager.LayoutParams.WRAP_CONTENT
                params.height = WindowManager.LayoutParams.WRAP_CONTENT
                window.attributes = params
            }
        }
        
        setContentView(R.layout.activity_widget_config_7ree)
        
        val inputText = findViewById<EditText>(R.id.widget_input_config_7ree)
        val queryButton = findViewById<Button>(R.id.widget_query_button_config_7ree)
        
        // 1. 自动聚焦到输入框并打开键盘
        inputText.requestFocus()
        inputText.post {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(inputText, InputMethodManager.SHOW_IMPLICIT)
        }
        
        // 2. 初始状态按钮为灰色无效
        updateButtonState_7ree(queryButton, false)
        
        // 3. 限制只能输入英文字母
        inputText.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            val filtered = StringBuilder()
            for (i in start until end) {
                val char = source[i]
                // 只允许英文字母 (a-z, A-Z)
                if ((char in 'a'..'z') || (char in 'A'..'Z')) {
                    filtered.append(char)
                }
            }
            if (filtered.length == end - start) {
                null // 接受所有字符
            } else {
                filtered.toString() // 返回过滤后的字符
            }
        })
        
        // 监听文本变化，动态更新按钮状态
        inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()?.trim() ?: ""
                updateButtonState_7ree(queryButton, text.length >= 3)
            }
        })
        
        // 5. 回车键触发搜索
        inputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                performSearch_7ree(inputText, queryButton)
                true
            } else {
                false
            }
        }
        
        // 按钮点击事件
        queryButton.setOnClickListener {
            performSearch_7ree(inputText, queryButton)
        }
    }
    
    private fun updateButtonState_7ree(button: Button, enabled: Boolean) {
        button.isEnabled = enabled
        if (enabled) {
            button.background = ContextCompat.getDrawable(this, R.drawable.widget_green_button_background_7ree)
        } else {
            button.background = ContextCompat.getDrawable(this, R.drawable.widget_gray_button_background_7ree)
        }
        
        // 创建带图标的文字
        val buttonText = "用AI查询"
        val spannableString = SpannableString(" $buttonText")
        
        // 获取放大镜图标
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_search_magnifier_7ree)
        drawable?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            // 使用ALIGN_BOTTOM来降低图标位置
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BOTTOM)
            spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        
        button.text = spannableString
    }
    
    private fun performSearch_7ree(inputText: EditText, queryButton: Button) {
        val queryText = inputText.text.toString().trim()
        
        if (queryText.length >= 3 && queryButton.isEnabled) {
            // 启动MainActivity并传递查询文本
            val intent = Intent(this, MainActivity::class.java).apply {
                action = WordQueryWidgetProvider_7ree.ACTION_WIDGET_QUERY_7ree
                putExtra(WordQueryWidgetProvider_7ree.EXTRA_QUERY_TEXT_7ree, queryText)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            finish()
        }
    }
}