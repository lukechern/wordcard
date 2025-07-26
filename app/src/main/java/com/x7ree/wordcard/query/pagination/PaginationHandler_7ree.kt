package com.x7ree.wordcard.query.pagination

import com.x7ree.wordcard.query.manager.DataManager_7ree
import com.x7ree.wordcard.query.state.PaginationState_7ree

/**
 * 分页和搜索功能模块
 */
class PaginationHandler_7ree(
    private val dataManager_7ree: DataManager_7ree,
    private val paginationState_7ree: PaginationState_7ree
) {
    
    /**
     * 加载单词计数
     */
    fun loadWordCount_7ree() {
        dataManager_7ree.loadWordCount_7ree()
    }
    
    /**
     * 加载总浏览量
     */
    fun loadTotalViews_7ree() {
        dataManager_7ree.loadTotalViews_7ree()
    }
    
    /**
     * 加载初始单词
     */
    fun loadInitialWords_7ree() {
        dataManager_7ree.loadInitialWords_7ree()
    }
    
    /**
     * 加载更多单词
     */
    fun loadMoreWords_7ree() {
        dataManager_7ree.loadMoreWords_7ree()
    }
    
    /**
     * 重置分页
     */
    fun resetPagination_7ree() {
        paginationState_7ree.resetPagination_7ree()
    }
    
    /**
     * 切换收藏过滤器
     */
    fun toggleFavoriteFilter_7ree() {
        paginationState_7ree.toggleFavoriteFilter_7ree()
        resetPagination_7ree()
        loadInitialWords_7ree()
        // println("DEBUG: 切换单词过滤")
    }
    
    /**
     * 更新搜索查询
     */
    fun updateSearchQuery_7ree(query: String) {
        paginationState_7ree.updateSearchQuery_7ree(query)
        dataManager_7ree.searchWords_7ree(query)
    }
    
    /**
     * 切换搜索模式
     */
    fun toggleSearchMode_7ree() {
        paginationState_7ree.toggleSearchMode_7ree()
        if (!paginationState_7ree.isSearchMode_7ree.value) {
            // 退出搜索模式时清空搜索查询并重新加载初始数据
            paginationState_7ree.updateSearchQuery_7ree("")
            resetPagination_7ree()
            loadInitialWords_7ree()
        }
    }
    
    /**
     * 设置搜索模式
     */
    fun setSearchMode_7ree(isSearchMode: Boolean) {
        paginationState_7ree.updateSearchMode_7ree(isSearchMode)
        if (!isSearchMode) {
            // 退出搜索模式时清空搜索查询并重新加载初始数据
            paginationState_7ree.updateSearchQuery_7ree("")
            resetPagination_7ree()
            loadInitialWords_7ree()
        }
    }
    
    /**
     * 清空搜索
     */
    fun clearSearch_7ree() {
        paginationState_7ree.clearSearch_7ree()
        resetPagination_7ree()
        loadInitialWords_7ree()
    }
    
    /**
     * 设置排序类型
     */
    fun setSortType_7ree(sortType: String?) {
        paginationState_7ree.updateSortType_7ree(sortType)
        resetPagination_7ree()
        loadInitialWords_7ree()
    }
    
    /**
     * 清空排序
     */
    fun clearSort_7ree() {
        paginationState_7ree.clearSort_7ree()
        resetPagination_7ree()
        loadInitialWords_7ree()
    }
}
