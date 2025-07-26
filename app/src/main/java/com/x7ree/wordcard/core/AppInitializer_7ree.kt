package com.x7ree.wordcard.core

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.data.WordDatabase_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppInitializer_7ree(private val context: Context, private val lifecycleOwner: LifecycleOwner) {
    private val TAG_7ree = "AppInitializer_7ree"
    
    // 初始化状态跟踪
    var isInitializationComplete_7ree by mutableStateOf(false)
    private var isDatabaseInitialized_7ree = false
    
    // 创建数据库和仓库实例 - 改为异步初始化
    private var database_7ree: WordDatabase_7ree? = null
    private var wordRepository_7ree: WordRepository_7ree? = null
    var wordQueryViewModel_7ree: WordQueryViewModel_7ree? by mutableStateOf(null)
    
    fun initializeAppAsync_7ree(onInitializationComplete: (WordQueryViewModel_7ree?) -> Unit) {
        // 记录启动开始时间
        val startTime = System.currentTimeMillis()
        
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Log.d(TAG_7ree, "开始异步初始化应用组件")
                
                // 初始化数据库
                initializeDatabaseAsync_7ree()
                
                // 初始化ViewModel
                initializeViewModel_7ree()
                
                // 标记初始化完成 - 只有在ViewModel真正初始化成功后才标记完成
                withContext(Dispatchers.Main) {
                    if (wordQueryViewModel_7ree != null) {
                        isInitializationComplete_7ree = true
                        
                        // 计算并记录启动时间
                        val endTime = System.currentTimeMillis()
                        @Suppress("UNUSED_VARIABLE")
                        val startupTime = endTime - startTime
                        // Log.d(TAG_7ree, "应用初始化完成，耗时: ${startupTime}ms")
                        
                        // 回调通知初始化完成
                        onInitializationComplete(wordQueryViewModel_7ree)
                    } else {
                        Log.e(TAG_7ree, "ViewModel初始化失败，无法标记初始化完成")
                        
                        // 计算并记录启动时间
                        val endTime = System.currentTimeMillis()
                        val startupTime = endTime - startTime
                        Log.e(TAG_7ree, "应用初始化失败，耗时: ${startupTime}ms")
                        
                        // 回调通知初始化失败
                        onInitializationComplete(null)
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG_7ree, "异步初始化失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    // 记录启动失败时间
                    val endTime = System.currentTimeMillis()
                    val startupTime = endTime - startTime
                    Log.e(TAG_7ree, "应用初始化失败，耗时: ${startupTime}ms")
                    
                    // 回调通知初始化失败
                    onInitializationComplete(null)
                }
            }
        }
    }
    
    private suspend fun initializeDatabaseAsync_7ree() {
        try {
            // Log.d(TAG_7ree, "开始异步初始化数据库")
            database_7ree = WordDatabase_7ree.getDatabase_7ree(context)
            wordRepository_7ree = WordRepository_7ree(database_7ree!!.wordDao_7ree())
            isDatabaseInitialized_7ree = true
            // Log.d(TAG_7ree, "数据库初始化完成")
        } catch (e: Exception) {
            Log.e(TAG_7ree, "数据库异步初始化失败: ${e.message}", e)
        }
    }
    
    private suspend fun initializeViewModel_7ree() {
        try {
            // Log.d(TAG_7ree, "开始初始化ViewModel")
            if (wordRepository_7ree != null) {
                wordQueryViewModel_7ree = WordQueryViewModel_7ree(OpenAiApiService_7ree(), wordRepository_7ree!!, context)
            } else {
                Log.e(TAG_7ree, "WordRepository未初始化，无法创建ViewModel")
            }
        } catch (e: Exception) {
            Log.e(TAG_7ree, "ViewModel初始化失败: ${e.message}", e)
        }
    }
}
