package com.x7ree.wordcard.widget

import android.content.Context
import kotlinx.coroutines.*

/**
 * 小组件预加载器 - 用于在后台预加载常用资源
 */
object WidgetPreloader_7ree {
    
    private var preloadJob: Job? = null
    private var isPreloaded = false
    
    /**
     * 异步预加载资源
     */
    fun preloadAsync(context: Context) {
        if (isPreloaded || preloadJob?.isActive == true) {
            return
        }
        
        preloadJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                preloadResources(context)
                isPreloaded = true
            } catch (e: Exception) {
                // 预加载失败不影响主流程
            }
        }
    }
    
    /**
     * 同步预加载资源（用于加载Activity中）
     */
    suspend fun preloadSync(context: Context) {
        if (isPreloaded) return
        
        try {
            preloadResources(context)
            isPreloaded = true
        } catch (e: Exception) {
            // 预加载失败不影响主流程
        }
    }
    
    private suspend fun preloadResources(context: Context) {
        // 预热数据库连接
        withContext(Dispatchers.IO) {
            com.x7ree.wordcard.data.WordDatabase_7ree.getDatabase_7ree(context)
        }
        
        // 预加载配置管理器
        withContext(Dispatchers.IO) {
            com.x7ree.wordcard.config.AppConfigManager_7ree(context)
        }
        
        // 预加载API服务
        withContext(Dispatchers.IO) {
            com.x7ree.wordcard.api.OpenAiApiService_7ree()
        }
    }
    
    fun cancelPreload() {
        preloadJob?.cancel()
        preloadJob = null
    }
    
    fun isResourcesPreloaded(): Boolean = isPreloaded
}