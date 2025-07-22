package com.x7ree.wordcard.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import com.x7ree.wordcard.R
import kotlinx.coroutines.*
import kotlinx.coroutines.TimeoutCancellationException

/**
 * 小组件加载Activity - 立即显示加载动画，后台进行初始化
 */
class WidgetLoadingActivity_7ree : AppCompatActivity() {
    
    private lateinit var progressBar: ProgressBar
    private lateinit var loadingText: TextView
    private lateinit var loadingHint: TextView
    private var initializationJob: Job? = null
    private var startTime: Long = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 配置窗口 - 使用与查询界面相同的配置
        WidgetWindowManager_7ree.configureWindow_7ree(this)
        setFinishOnTouchOutside(true)
        
        setContentView(R.layout.activity_widget_loading_7ree)
        
        // 初始化UI元素
        progressBar = findViewById(R.id.widget_loading_progress_7ree)
        loadingText = findViewById(R.id.widget_loading_text_7ree)
        loadingHint = findViewById(R.id.widget_loading_hint_7ree)
        
        // 记录启动时间
        startTime = System.currentTimeMillis()
        
        // 立即显示加载动画
        showLoadingAnimation()
        
        // 在后台进行初始化工作
        startBackgroundInitialization()
    }
    
    private fun showLoadingAnimation() {
        progressBar.visibility = android.view.View.VISIBLE
        loadingText.visibility = android.view.View.VISIBLE
        loadingText.text = "正在启动查询..."
        
        // 添加一些动态效果
        startLoadingTextAnimation()
        
        // 如果加载时间超过1.5秒，显示提示信息
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                loadingHint.visibility = android.view.View.VISIBLE
            }
        }, 1500)
    }
    
    private fun startLoadingTextAnimation() {
        val handler = Handler(Looper.getMainLooper())
        val loadingTexts = arrayOf("正在启动查询.", "正在启动查询..", "正在启动查询...")
        var currentIndex = 0
        
        val runnable = object : Runnable {
            override fun run() {
                if (!isFinishing) {
                    loadingText.text = loadingTexts[currentIndex]
                    currentIndex = (currentIndex + 1) % loadingTexts.size
                    handler.postDelayed(this, 500)
                }
            }
        }
        handler.post(runnable)
    }
    
    private fun startBackgroundInitialization() {
        initializationJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                // 使用超时机制，确保不会无限等待
                withTimeout(5000) { // 5秒超时
                    // 执行一些预加载工作以提升后续体验
                    preloadResources()
                }
                
                // 最小显示时间，确保用户看到加载动画，避免闪烁
                val elapsedTime = System.currentTimeMillis() - startTime
                if (elapsedTime < 200) {
                    delay(200 - elapsedTime)
                }
                
                // 切换到主线程启动真正的查询Activity
                withContext(Dispatchers.Main) {
                    if (!isFinishing) {
                        startQueryActivity()
                    }
                }
                
            } catch (e: TimeoutCancellationException) {
                // 超时情况下直接启动查询Activity
                withContext(Dispatchers.Main) {
                    if (!isFinishing) {
                        startQueryActivity()
                    }
                }
            } catch (e: Exception) {
                // 如果初始化失败，仍然尝试启动查询Activity
                withContext(Dispatchers.Main) {
                    if (!isFinishing) {
                        startQueryActivity()
                    }
                }
            }
        }
    }
    
    /**
     * 预加载资源，提升后续Activity的启动速度
     */
    private suspend fun preloadResources() {
        WidgetPreloader_7ree.preloadSync(this)
    }
    
    private fun startQueryActivity() {
        val intent = Intent(this, WidgetConfigActivity_7ree::class.java)
        // 传递原始Intent的所有数据
        intent.putExtras(getIntent().extras ?: Bundle())
        
        // 设置Activity转场动画为淡入淡出，提供流畅的过渡效果
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        
        // 关闭加载Activity
        finish()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        initializationJob?.cancel()
    }
    
    override fun onBackPressed() {
        // 允许用户取消加载
        super.onBackPressed()
        initializationJob?.cancel()
    }
}