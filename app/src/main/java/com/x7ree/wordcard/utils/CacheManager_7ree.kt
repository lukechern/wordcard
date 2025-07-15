package com.x7ree.wordcard.utils

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

/**
 * 缓存管理器
 * 负责管理统计数据的缓存和更新机制
 */
class CacheManager_7ree(context: Context) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "wordcard_cache_7ree",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_LAST_UPDATE_TIME = "last_update_time_7ree"
        private const val CACHE_DURATION_MS = 10 * 60 * 1000L // 10分钟
    }
    
    // 缓存更新状态
    private val _cacheUpdateTime_7ree = MutableStateFlow(getLastUpdateTimestamp_7ree())
    val cacheUpdateTime_7ree: StateFlow<Long> = _cacheUpdateTime_7ree.asStateFlow()
    
    // 手动更新状态
    private val _isManualUpdating_7ree = MutableStateFlow(false)
    val isManualUpdating_7ree: StateFlow<Boolean> = _isManualUpdating_7ree.asStateFlow()
    
    /**
     * 检查缓存是否需要更新
     */
    fun shouldUpdateCache_7ree(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = getLastUpdateTimestamp_7ree()
        return (currentTime - lastUpdateTime) > CACHE_DURATION_MS
    }
    
    /**
     * 更新缓存时间戳
     */
    fun updateCacheTimestamp_7ree() {
        val currentTime = System.currentTimeMillis()
        sharedPreferences.edit()
            .putLong(KEY_LAST_UPDATE_TIME, currentTime)
            .apply()
        _cacheUpdateTime_7ree.value = currentTime
    }
    
    /**
     * 获取上次更新时间戳
     */
    private fun getLastUpdateTimestamp_7ree(): Long {
        return sharedPreferences.getLong(KEY_LAST_UPDATE_TIME, 0L)
    }
    
    /**
     * 获取格式化的上次更新时间字符串
     */
    fun getLastUpdateTime_7ree(): String {
        val lastUpdateTime = getLastUpdateTimestamp_7ree()
        if (lastUpdateTime == 0L) {
            return "暂无数据"
        }
        
        val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        return dateFormat.format(Date(lastUpdateTime))
    }
    
    /**
     * 获取距离下次自动更新的剩余时间（毫秒）
     */
    fun getTimeUntilNextUpdate_7ree(): Long {
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = getLastUpdateTimestamp_7ree()
        val nextUpdateTime = lastUpdateTime + CACHE_DURATION_MS
        return maxOf(0L, nextUpdateTime - currentTime)
    }
    
    /**
     * 格式化剩余时间为可读字符串
     */
    fun formatTimeUntilNextUpdate_7ree(): String {
        val remainingMs = getTimeUntilNextUpdate_7ree()
        if (remainingMs <= 0) {
            return "即将更新"
        }
        
        val minutes = remainingMs / (60 * 1000)
        val seconds = (remainingMs % (60 * 1000)) / 1000
        
        return when {
            minutes > 0 -> "${minutes}分${seconds}秒后更新"
            else -> "${seconds}秒后更新"
        }
    }
    
    /**
     * 设置手动更新状态
     */
    fun setManualUpdating_7ree(isUpdating: Boolean) {
        _isManualUpdating_7ree.value = isUpdating
    }
    
    /**
     * 强制更新缓存
     */
    suspend fun forceUpdateCache_7ree(onUpdate: suspend () -> Unit) {
        setManualUpdating_7ree(true)
        try {
            onUpdate()
            updateCacheTimestamp_7ree()
        } finally {
            setManualUpdating_7ree(false)
        }
    }
}