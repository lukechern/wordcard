package com.x7ree.wordcard.article.utils

import com.x7ree.wordcard.data.ArticleRepository_7ree
import com.x7ree.wordcard.data.ArticleEntity_7ree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay

/**
 * 文章分页处理器 - 参考单词本分页逻辑实现
 */
class ArticlePaginationHandler_7ree(
    private val articleRepository_7ree: ArticleRepository_7ree
) {
    
    // 分页加载相关状态
    private val _pagedArticles = MutableStateFlow<List<ArticleEntity_7ree>>(emptyList())
    val pagedArticles: StateFlow<List<ArticleEntity_7ree>> = _pagedArticles.asStateFlow()
    
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()
    
    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData.asStateFlow()
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    var currentPage = 0
        private set
    val pageSize = 10 // 每页10篇文章
    
    // 筛选状态
    private var currentFilterState: ArticleFilterState_7ree = ArticleFilterState_7ree()
    
    /**
     * 加载初始文章
     */
    suspend fun loadInitialArticles(filterState: ArticleFilterState_7ree = ArticleFilterState_7ree()) {
        try {
            currentFilterState = filterState
            currentPage = 0
            _pagedArticles.value = emptyList()
            _hasMoreData.value = true
            
            val articles = loadArticlesWithFilter(0, pageSize, filterState)
            _pagedArticles.value = articles
            _hasMoreData.value = articles.size == pageSize
            
        } catch (e: Exception) {
            // 处理错误
        }
    }
    
    /**
     * 加载更多文章
     */
    suspend fun loadMoreArticles() {
        if (_isLoadingMore.value || !_hasMoreData.value) return
        
        try {
            _isLoadingMore.value = true
            currentPage++
            
            val newArticles = loadArticlesWithFilter(currentPage, pageSize, currentFilterState)
            
            if (newArticles.isNotEmpty()) {
                val currentArticles = _pagedArticles.value.toMutableList()
                currentArticles.addAll(newArticles)
                _pagedArticles.value = currentArticles
                
                // 如果返回的文章数量少于页面大小，说明没有更多数据了
                _hasMoreData.value = newArticles.size == pageSize
            } else {
                _hasMoreData.value = false
            }
            
        } catch (e: Exception) {
            // 处理错误，回退页码
            currentPage--
        } finally {
            _isLoadingMore.value = false
        }
    }
    
    /**
     * 设置刷新状态 - 供外部调用
     */
    fun setRefreshing(isRefreshing: Boolean) {
        _isRefreshing.value = isRefreshing
    }
    
    /**
     * 下拉刷新 - 简化版本，主要逻辑由ViewModel控制
     */
    suspend fun refreshArticles() {
        // 这个方法保留用于兼容性，但主要逻辑已移到ViewModel
        try {
            _isRefreshing.value = true
            
            // 重置分页状态
            currentPage = 0
            _pagedArticles.value = emptyList()
            _hasMoreData.value = true
            
            // 加载第一页数据
            val articles = loadArticlesWithFilter(0, pageSize, currentFilterState)
            _pagedArticles.value = articles
            _hasMoreData.value = articles.size == pageSize
            
        } catch (e: Exception) {
            // 处理错误，但不影响刷新状态重置
        } finally {
            _isRefreshing.value = false
        }
    }
    
    /**
     * 更新筛选状态并重新加载
     */
    suspend fun updateFilterAndReload(filterState: ArticleFilterState_7ree) {
        currentFilterState = filterState
        loadInitialArticles(filterState)
    }
    
    /**
     * 根据筛选条件加载文章
     */
    private suspend fun loadArticlesWithFilter(
        page: Int, 
        size: Int, 
        filterState: ArticleFilterState_7ree
    ): List<ArticleEntity_7ree> {
        val offset = page * size
        
        return when {
            // 只显示收藏文章
            filterState.showFavoritesOnly -> {
                when (filterState.sortType) {
                    ArticleSortType_7ree.PUBLISH_TIME_ASC -> 
                        articleRepository_7ree.getFavoriteArticlesSortedByTimeAsc(size, offset)
                    ArticleSortType_7ree.PUBLISH_TIME_DESC -> 
                        articleRepository_7ree.getFavoriteArticlesSortedByTimeDesc(size, offset)
                    ArticleSortType_7ree.VIEW_COUNT_ASC -> 
                        articleRepository_7ree.getFavoriteArticlesSortedByViewCountAsc(size, offset)
                    ArticleSortType_7ree.VIEW_COUNT_DESC -> 
                        articleRepository_7ree.getFavoriteArticlesSortedByViewCountDesc(size, offset)
                    null -> 
                        articleRepository_7ree.getFavoriteArticlesSortedByTimeDesc(size, offset) // 默认按时间降序
                }
            }
            // 显示所有文章
            else -> {
                when (filterState.sortType) {
                    ArticleSortType_7ree.PUBLISH_TIME_ASC -> 
                        articleRepository_7ree.getAllArticlesSortedByTimeAsc(size, offset)
                    ArticleSortType_7ree.PUBLISH_TIME_DESC -> 
                        articleRepository_7ree.getAllArticlesSortedByTimeDesc(size, offset)
                    ArticleSortType_7ree.VIEW_COUNT_ASC -> 
                        articleRepository_7ree.getAllArticlesSortedByViewCountAsc(size, offset)
                    ArticleSortType_7ree.VIEW_COUNT_DESC -> 
                        articleRepository_7ree.getAllArticlesSortedByViewCountDesc(size, offset)
                    null -> 
                        articleRepository_7ree.getAllArticlesSortedByTimeDesc(size, offset) // 默认按时间降序
                }
            }
        }
    }
    
    /**
     * 重置分页状态
     */
    fun resetPagination() {
        currentPage = 0
        _pagedArticles.value = emptyList()
        _hasMoreData.value = true
        _isLoadingMore.value = false
    }
    
    /**
     * 添加新文章到顶部（用于新生成的文章）
     */
    fun addNewArticleToTop(newArticle: ArticleEntity_7ree) {
        val currentArticles = _pagedArticles.value.toMutableList()
        val existingIndex = currentArticles.indexOfFirst { it.id == newArticle.id }
        if (existingIndex >= 0) {
            currentArticles[existingIndex] = newArticle
        } else {
            currentArticles.add(0, newArticle)
        }
        _pagedArticles.value = currentArticles
    }
    
    /**
     * 移除文章（用于删除操作）
     */
    fun removeArticle(articleId: Long) {
        val currentArticles = _pagedArticles.value.toMutableList()
        currentArticles.removeAll { it.id == articleId }
        _pagedArticles.value = currentArticles
    }
    
    /**
     * 更新文章（用于收藏状态变更等）
     */
    fun updateArticle(updatedArticle: ArticleEntity_7ree) {
        val currentArticles = _pagedArticles.value.toMutableList()
        val index = currentArticles.indexOfFirst { it.id == updatedArticle.id }
        if (index >= 0) {
            currentArticles[index] = updatedArticle
            _pagedArticles.value = currentArticles
        }
    }
}