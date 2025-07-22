package com.x7ree.wordcard.widget

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

/**
 * Widget UI状态管理器
 * 负责管理Widget界面的各种状态切换
 */
class WidgetUIStateManager_7ree {
    
    /**
     * 切换到搜索状态
     */
    fun switchToSearchState_7ree(
        inputText: EditText,
        queryButton: Button,
        progressBar: ProgressBar,
        resultText: TextView,
        wordTitle: TextView,
        chineseMeaning: TextView,
        loadingText: TextView,
        resultButtons: LinearLayout,
        queryText: String
    ) {
        // 隐藏输入框和查询按钮
        inputText.visibility = View.GONE
        queryButton.visibility = View.GONE
        
        // 隐藏输入框容器以避免占用空间
        val inputContainer = inputText.parent as? android.view.View
        inputContainer?.visibility = View.GONE
        
        // 显示查询的单词标题
        wordTitle.text = queryText
        wordTitle.visibility = View.VISIBLE
        
        // 显示进度条和提示文字
        progressBar.visibility = View.VISIBLE
        loadingText.visibility = View.VISIBLE
        
        // 隐藏其他元素
        resultText.visibility = View.GONE
        chineseMeaning.visibility = View.GONE
        resultButtons.visibility = View.GONE
    }
    
    /**
     * 重置到初始状态
     */
    fun resetToInitialState_7ree(
        inputText: EditText,
        queryButton: Button,
        progressBar: ProgressBar,
        resultText: TextView,
        wordTitle: TextView,
        chineseMeaning: TextView,
        loadingText: TextView,
        resultButtons: LinearLayout
    ) {
        // 显示输入框和查询按钮
        inputText.visibility = View.VISIBLE
        queryButton.visibility = View.VISIBLE
        
        // 显示输入框容器
        val inputContainer = inputText.parent as? android.view.View
        inputContainer?.visibility = View.VISIBLE
        
        // 隐藏其他元素
        progressBar.visibility = View.GONE
        loadingText.visibility = View.GONE
        resultText.visibility = View.GONE
        wordTitle.visibility = View.GONE
        chineseMeaning.visibility = View.GONE
        resultButtons.visibility = View.GONE
    }
    
    /**
     * 切换到结果显示状态
     */
    fun switchToResultState_7ree(
        progressBar: ProgressBar,
        loadingText: TextView,
        resultText: TextView,
        wordTitle: TextView,
        chineseMeaning: TextView,
        inputText: EditText,
        queryButton: Button,
        resultButtons: LinearLayout,
        showButtons: Boolean = true
    ) {
        progressBar.visibility = View.GONE
        loadingText.visibility = View.GONE
        
        wordTitle.visibility = View.VISIBLE
        
        // 隐藏输入框和查询按钮
        inputText.visibility = View.GONE
        queryButton.visibility = View.GONE
        
        // 隐藏输入框容器以避免占用空间
        val inputContainer = inputText.parent as? android.view.View
        inputContainer?.visibility = View.GONE
        
        resultText.visibility = View.VISIBLE
        
        // 确保chineseMeaning初始状态为可见，后续由updateChineseMeaning_7ree方法控制
        chineseMeaning.visibility = View.VISIBLE
        
        // 根据showButtons参数决定是否显示结果按钮区域
        if (showButtons) {
            resultButtons.visibility = View.VISIBLE
        } else {
            resultButtons.visibility = View.GONE
        }
    }
    
    /**
     * 显示错误状态
     */
    fun showErrorState_7ree(
        progressBar: ProgressBar,
        loadingText: TextView,
        resultText: TextView,
        errorMessage: String
    ) {
        progressBar.visibility = View.GONE
        loadingText.visibility = View.GONE
        resultText.text = errorMessage
        resultText.visibility = View.VISIBLE
    }
    
    /**
     * 更新中文意思显示
     */
    fun updateChineseMeaning_7ree(
        chineseMeaning: TextView,
        meaning: String
    ) {
        if (meaning.isNotEmpty()) {
            chineseMeaning.text = meaning
            chineseMeaning.gravity = android.view.Gravity.CENTER
            chineseMeaning.visibility = View.VISIBLE
        } else {
            // 当没有中文意思时，显示提示信息而不是隐藏
            chineseMeaning.text = "(无法解析中文释义)"
            chineseMeaning.gravity = android.view.Gravity.CENTER
            chineseMeaning.visibility = View.VISIBLE
        }
    }
}