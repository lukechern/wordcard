package com.x7ree.wordcard.core

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.widget.WordQueryWidgetProvider_7ree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IntentHandler_7ree(private val context: Context, private val lifecycleOwner: LifecycleOwner) {
    private val TAG_7ree = "IntentHandler_7ree"
    
    private var wordQueryViewModel_7ree: WordQueryViewModel_7ree? = null
    private var isInitializationComplete_7ree: Boolean = false
    
    fun setViewModel(viewModel: WordQueryViewModel_7ree) {
        wordQueryViewModel_7ree = viewModel
    }
    
    fun setInitializationStatus(isComplete: Boolean) {
        isInitializationComplete_7ree = isComplete
    }
    
    fun handleWidgetIntent_7ree(intent: Intent?) {
        // Log.d(TAG_7ree, "handleWidgetIntent_7ree called with action: ${intent?.action}")
        
        // 处理来自WidgetConfigActivity_7ree的查看详情请求
        val queryWord = intent?.getStringExtra("query_word")
        val showDetail = intent?.getBooleanExtra("show_detail", false)
        
        if (!queryWord.isNullOrBlank() && (showDetail == true)) {
            // Log.d(TAG_7ree, "收到查看详情请求: $queryWord")
            // 等待ViewModel初始化完成后执行查询并显示详情
            lifecycleOwner.lifecycleScope.launch {
                // 等待初始化完成，改进超时机制
                var waitTime = 0
                val maxWaitTime = 15000 // 增加到15秒
                val checkInterval = 50 // 减少检查间隔到50ms，提高响应性
                
                while ((!isInitializationComplete_7ree || wordQueryViewModel_7ree == null) && waitTime < maxWaitTime) {
                    kotlinx.coroutines.delay(checkInterval.toLong())
                    waitTime += checkInterval
                    
                    // 每秒记录一次等待状态
                    if (waitTime % 1000 == 0) {
                        Log.d(TAG_7ree, "等待初始化中... ${waitTime/1000}s, isComplete: $isInitializationComplete_7ree, viewModel: ${wordQueryViewModel_7ree != null}")
                    }
                }
                
                if (waitTime >= maxWaitTime) {
                    Log.e(TAG_7ree, "等待初始化超时，无法处理查看详情请求: $queryWord")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "应用启动中，请稍后重试", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }
                
                // 在主线程执行查询并切换到查询页面
                withContext(Dispatchers.Main) {
                    try {
                        // 设置查询文本
                        wordQueryViewModel_7ree?.onWordInputChanged_7ree(queryWord)
                        // 先切换到查询页面
                        wordQueryViewModel_7ree?.setCurrentScreen_7ree("SEARCH")
                        // 然后加载单词详情
                        wordQueryViewModel_7ree?.loadWordFromHistory_7ree(queryWord)
                        // Log.d(TAG_7ree, "查看详情已执行: $queryWord，已切换到查询页面")
                        
                        // 确保隐藏键盘（特别是从小组件进入时）
                        val activity = context as? android.app.Activity
                        activity?.let { act ->
                            val imm = act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(act.findViewById<android.view.View>(android.R.id.content).windowToken, 0)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG_7ree, "处理查看详情请求时出错: ${e.message}", e)
                        Toast.makeText(context, "处理请求时出错，请重试", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            return
        }
        
        when (intent?.action) {
            WordQueryWidgetProvider_7ree.ACTION_WIDGET_QUERY_7ree -> {
                val queryText = intent.getStringExtra(WordQueryWidgetProvider_7ree.EXTRA_QUERY_TEXT_7ree)
                Log.d(TAG_7ree, "收到小组件查询请求: $queryText")
                
                if (!queryText.isNullOrBlank()) {
                    // 等待ViewModel初始化完成后执行查询
                    lifecycleOwner.lifecycleScope.launch {
                        // 等待初始化完成，改进超时机制
                        var waitTime = 0
                        val maxWaitTime = 15000 // 增加到15秒
                        val checkInterval = 50 // 减少检查间隔到50ms
                        
                        while ((!isInitializationComplete_7ree || wordQueryViewModel_7ree == null) && waitTime < maxWaitTime) {
                            kotlinx.coroutines.delay(checkInterval.toLong())
                            waitTime += checkInterval
                            
                            // 每秒记录一次等待状态
                            if (waitTime % 1000 == 0) {
                                Log.d(TAG_7ree, "等待初始化中... ${waitTime/1000}s, isComplete: $isInitializationComplete_7ree, viewModel: ${wordQueryViewModel_7ree != null}")
                            }
                        }
                        
                        if (waitTime >= maxWaitTime) {
                            Log.e(TAG_7ree, "等待初始化超时，无法处理小组件查询请求: $queryText")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "应用启动中，请稍后重试", Toast.LENGTH_LONG).show()
                            }
                            return@launch
                        }
                        
                        // 在主线程执行查询
                        withContext(Dispatchers.Main) {
                            try {
                                wordQueryViewModel_7ree?.onWordInputChanged_7ree(queryText)
                                wordQueryViewModel_7ree?.queryWord_7ree()
                                Log.d(TAG_7ree, "小组件查询已执行: $queryText")
                            } catch (e: Exception) {
                                Log.e(TAG_7ree, "处理小组件查询请求时出错: ${e.message}", e)
                                Toast.makeText(context, "处理查询请求时出错，请重试", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            WordQueryWidgetProvider_7ree.ACTION_WIDGET_WORDBOOK_7ree -> {
                // Log.d(TAG_7ree, "收到小组件单词本请求")
                lifecycleOwner.lifecycleScope.launch {
                    // 等待ViewModel初始化完成后切换到单词本页面
                    var waitTime = 0
                    val maxWaitTime = 15000 // 增加到15秒
                    val checkInterval = 50
                    
                    while ((!isInitializationComplete_7ree || wordQueryViewModel_7ree == null) && waitTime < maxWaitTime) {
                        kotlinx.coroutines.delay(checkInterval.toLong())
                        waitTime += checkInterval
                        
                        if (waitTime % 1000 == 0) {
                            Log.d(TAG_7ree, "等待初始化中... ${waitTime/1000}s")
                        }
                    }
                    
                    if (waitTime >= maxWaitTime) {
                        Log.e(TAG_7ree, "等待初始化超时，无法处理单词本请求")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "应用启动中，请稍后重试", Toast.LENGTH_LONG).show()
                        }
                        return@launch
                    }
                    
                    // 在主线程切换到单词本页面
                    withContext(Dispatchers.Main) {
                        try {
                            wordQueryViewModel_7ree?.setCurrentScreen_7ree("HISTORY")
                            // Log.d(TAG_7ree, "已切换到单词本页面")
                        } catch (e: Exception) {
                            Log.e(TAG_7ree, "处理单词本请求时出错: ${e.message}", e)
                            Toast.makeText(context, "处理请求时出错，请重试", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
