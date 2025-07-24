package com.x7ree.wordcard.widget

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

/**
 * Widget输入验证器
 * 负责处理输入框的文本过滤和验证逻辑
 */
class WidgetInputValidator_7ree(private val buttonManager_7ree: WidgetButtonManager_7ree) {
    
    /**
     * 设置输入框过滤器，只允许英文字母
     */
    fun setupInputFilter_7ree(inputText: EditText) {
        inputText.filters = arrayOf(InputFilter { source, start, end, _, _, _ ->
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
    }
    
    /**
     * 设置文本变化监听器
     */
    fun setupTextWatcher_7ree(inputText: EditText, queryButton: Button) {
        inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()?.trim() ?: ""
                buttonManager_7ree.updateButtonState_7ree(queryButton, text.length >= 3)
            }
        })
    }
    
    /**
     * 设置回车键监听器
     */
    fun setupEnterKeyListener_7ree(inputText: EditText, onSearchAction: () -> Unit) {
        inputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                onSearchAction()
                true
            } else {
                false
            }
        }
    }
    
    /**
     * 验证输入文本是否有效
     */
    fun isValidInput_7ree(text: String): Boolean {
        return text.trim().length >= 3
    }
}
