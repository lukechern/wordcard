package com.x7ree.wordcard.widget

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.x7ree.wordcard.widget.config.*
import kotlinx.coroutines.launch

class WidgetConfigActivity_7ree : WidgetConfigBaseActivity_7ree() {
    
    private lateinit var uiHelper: WidgetConfigUIHelper_7ree
    private lateinit var searchHelper: WidgetConfigSearchHelper_7ree
    private lateinit var displayHelper: WidgetConfigDisplayHelper_7ree
    private lateinit var logHelper: WidgetConfigLogHelper_7ree
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化辅助类
        uiHelper = WidgetConfigUIHelper_7ree(this)
        searchHelper = WidgetConfigSearchHelper_7ree(this)
        displayHelper = WidgetConfigDisplayHelper_7ree(this)
        logHelper = WidgetConfigLogHelper_7ree(this)
        
        // 先设置基础UI，让界面立即可见
        uiHelper.setupBasicUI_7ree()
        
        // 在后台异步初始化管理器，避免阻塞UI
        lifecycleScope.launch {
            initializeManagers_7ree()
            // 初始化完成后设置完整UI功能
            uiHelper.setupAdvancedUI_7ree()
        }
    }
}
