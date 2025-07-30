package com.x7ree.wordcard.article.utils

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 文章列表滚动位置管理器
 * 用于保存和恢复文章列表的滚动位置
 */
class ArticleScrollPositionManager_7ree {
    
    companion object {
        private const val TAG = "ArticleScrollPosition"
        
        // 全局单例实例，确保数据不会因为重组而丢失
        @Volatile
        private var INSTANCE: ArticleScrollPositionManager_7ree? = null
        
        fun getInstance(): ArticleScrollPositionManager_7ree {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ArticleScrollPositionManager_7ree().also { 
                    INSTANCE = it

                }
            }
        }
    }
    
    // 保存的滚动位置信息
    private var savedFirstVisibleItemIndex: Int = 0
    private var savedFirstVisibleItemScrollOffset: Int = 0
    private var hasSavedPosition: Boolean = false
    
    /**
     * 保存当前滚动位置
     */
    fun saveScrollPosition(listState: LazyListState) {
        savedFirstVisibleItemIndex = listState.firstVisibleItemIndex
        savedFirstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset
        hasSavedPosition = true
    }
    
    /**
     * 恢复滚动位置
     */
    suspend fun restoreScrollPosition(listState: LazyListState, scope: CoroutineScope) {
        if (hasSavedPosition) {
            try {
                // 检查目标索引是否有效
                if (savedFirstVisibleItemIndex >= listState.layoutInfo.totalItemsCount) {
                    val adjustedIndex = (listState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)
                    listState.scrollToItem(index = adjustedIndex, scrollOffset = 0)
                } else {
                    // 使用协程恢复滚动位置
                    scope.launch {
                        listState.scrollToItem(
                            index = savedFirstVisibleItemIndex,
                            scrollOffset = savedFirstVisibleItemScrollOffset
                        )
                    }.join()
                }
            } catch (e: Exception) {
                // 静默处理恢复失败的情况
            }
        }
    }
    
    /**
     * 清除保存的滚动位置
     */
    fun clearSavedPosition() {
        hasSavedPosition = false
        savedFirstVisibleItemIndex = 0
        savedFirstVisibleItemScrollOffset = 0
    }
    
    /**
     * 检查是否有保存的位置
     */
    fun hasSavedPosition(): Boolean = hasSavedPosition
    
    /**
     * 获取保存的位置信息（用于调试）
     */
    fun getSavedPositionInfo(): String {
        return if (hasSavedPosition) {
            "index=$savedFirstVisibleItemIndex, offset=$savedFirstVisibleItemScrollOffset"
        } else {
            "无保存位置"
        }
    }
}

/**
 * Composable函数：创建和管理文章列表滚动位置
 * 使用全局单例确保实例不会被重新创建
 */
@Composable
fun rememberArticleScrollPositionManager(): ArticleScrollPositionManager_7ree {
    return remember("ArticleScrollPositionManager") { 

        ArticleScrollPositionManager_7ree.getInstance()
    }
}

/**
 * Composable函数：自动恢复滚动位置
 * 在列表数据加载完成后自动恢复位置
 */
@Composable
fun AutoRestoreScrollPosition(
    listState: LazyListState,
    positionManager: ArticleScrollPositionManager_7ree,
    articlesCount: Int,
    isDataReady: Boolean = true
) {
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(articlesCount, isDataReady) {
        // 当文章数据准备好且有保存的位置时，恢复滚动位置
        if (isDataReady && articlesCount > 0 && positionManager.hasSavedPosition()) {
            // 延迟一小段时间确保列表完全渲染
            kotlinx.coroutines.delay(100)
            positionManager.restoreScrollPosition(listState, scope)
        }
    }
}