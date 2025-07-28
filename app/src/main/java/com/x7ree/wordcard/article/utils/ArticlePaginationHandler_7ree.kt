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
    
    // 搜索状态
    private var currentSearchQuery: String = ""
    private var isSearchMode: Boolean = false
    
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
    
    /**
     * 搜索文章 - 支持分页搜索
     */
    suspend fun searchArticles(query: String, filterState: ArticleFilterState_7ree) {
        try {
            println("DEBUG: ArticlePaginationHandler.searchArticles called with query: '$query'")
            currentSearchQuery = query
            isSearchMode = query.isNotBlank()
            currentFilterState = filterState
            currentPage = 0
            _pagedArticles.value = emptyList()
            _hasMoreData.value = true
            
            if (query.isBlank()) {
                // 如果搜索查询为空，加载正常的文章列表
                println("DEBUG: Query is blank, loading normal articles")
                loadInitialArticles(filterState)
                return
            }
            
            // 执行搜索并加载第一页结果
            println("DEBUG: Executing search with filter")
            
            // 先尝试使用测试搜索方法
            val testArticles = articleRepository_7ree.testSearchArticles(query, pageSize, 0)
            println("DEBUG: Test search returned ${testArticles.size} articles")
            
            // 如果测试搜索有结果，直接使用测试结果
            if (testArticles.isNotEmpty()) {
                println("DEBUG: Using test search results")
                _pagedArticles.value = testArticles
                _hasMoreData.value = testArticles.size == pageSize
            } else {
                // 否则使用原来的搜索方法
                val articles = searchArticlesWithFilter(0, pageSize, query, filterState)
                println("DEBUG: Filter search returned ${articles.size} articles")
                _pagedArticles.value = articles
                _hasMoreData.value = articles.size == pageSize
            }
            
        } catch (e: Exception) {
            println("DEBUG: Search failed with exception: ${e.message}")
            // 处理搜索错误
        }
    }
    
    /**
     * 加载更多搜索结果
     */
    suspend fun loadMoreSearchResults() {
        if (!isSearchMode || currentSearchQuery.isBlank()) {
            // 如果不在搜索模式，使用普通的加载更多
            loadMoreArticles()
            return
        }
        
        if (_isLoadingMore.value || !_hasMoreData.value) return
        
        try {
            _isLoadingMore.value = true
            currentPage++
            
            val newArticles = searchArticlesWithFilter(currentPage, pageSize, currentSearchQuery, currentFilterState)
            
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
     * 根据搜索查询和筛选条件加载文章
     */
    private suspend fun searchArticlesWithFilter(
        page: Int,
        size: Int,
        query: String,
        filterState: ArticleFilterState_7ree
    ): List<ArticleEntity_7ree> {
        val offset = page * size
        println("DEBUG: searchArticlesWithFilter called with query='$query', page=$page, size=$size, offset=$offset")
        println("DEBUG: filterState.showFavoritesOnly=${filterState.showFavoritesOnly}, sortType=${filterState.sortType}")
        
        return when {
            // 在收藏文章中搜索
            filterState.showFavoritesOnly -> {
                when (filterState.sortType) {
                    ArticleSortType_7ree.PUBLISH_TIME_ASC -> 
                        articleRepository_7ree.searchFavoriteArticlesSortedByTimeAsc(query, size, offset)
                    ArticleSortType_7ree.PUBLISH_TIME_DESC -> 
                        articleRepository_7ree.searchFavoriteArticlesSortedByTimeDesc(query, size, offset)
                    ArticleSortType_7ree.VIEW_COUNT_ASC -> 
                        articleRepository_7ree.searchFavoriteArticlesSortedByViewCountAsc(query, size, offset)
                    ArticleSortType_7ree.VIEW_COUNT_DESC -> 
                        articleRepository_7ree.searchFavoriteArticlesSortedByViewCountDesc(query, size, offset)
                    null -> 
                        articleRepository_7ree.searchFavoriteArticlesSortedByTimeDesc(query, size, offset) // 默认按时间降序
                }
            }
            // 在所有文章中搜索
            else -> {
                when (filterState.sortType) {
                    ArticleSortType_7ree.PUBLISH_TIME_ASC -> 
                        articleRepository_7ree.searchAllArticlesSortedByTimeAsc(query, size, offset)
                    ArticleSortType_7ree.PUBLISH_TIME_DESC -> 
                        articleRepository_7ree.searchAllArticlesSortedByTimeDesc(query, size, offset)
                    ArticleSortType_7ree.VIEW_COUNT_ASC -> 
                        articleRepository_7ree.searchAllArticlesSortedByViewCountAsc(query, size, offset)
                    ArticleSortType_7ree.VIEW_COUNT_DESC -> 
                        articleRepository_7ree.searchAllArticlesSortedByViewCountDesc(query, size, offset)
                    null -> 
                        articleRepository_7ree.searchAllArticlesSortedByTimeDesc(query, size, offset) // 默认按时间降序
                }
            }
        }
    }
    
    /**
     * 清空搜索状态
     */
    fun clearSearch() {
        currentSearchQuery = ""
        isSearchMode = false
    }
    
    /**
     * 获取当前搜索查询
     */
    fun getCurrentSearchQuery(): String = currentSearchQuery
    
    /**
     * 检查是否在搜索模式
     */
    fun isInSearchMode(): Boolean = isSearchMode
}