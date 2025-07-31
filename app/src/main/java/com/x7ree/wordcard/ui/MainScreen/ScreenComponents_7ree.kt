package com.x7ree.wordcard.ui.MainScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.ArticleDetailScreen_7ree
import com.x7ree.wordcard.ui.ArticleScreen_7ree
import com.x7ree.wordcard.ui.DashboardScreen_7ree
import com.x7ree.wordcard.ui.WordCardScreen_7ree

/**
 * 显示搜索屏幕
 */
@Composable
fun ShowSearchScreen_7ree(wordQueryViewModel_7ree: WordQueryViewModel_7ree) {
    WordCardScreen_7ree(
        wordQueryViewModel_7ree = wordQueryViewModel_7ree
    )
}

/**
 * 显示历史记录屏幕
 */
@Composable
fun ShowHistoryScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onWordClick_7ree: (String) -> Unit
) {
    HistoryScreen_7ree(
        wordQueryViewModel_7ree = wordQueryViewModel_7ree,
        onWordClick_7ree = onWordClick_7ree
    )
}

/**
 * 显示文章屏幕
 */
@Composable
fun ShowArticleScreen_7ree(wordQueryViewModel_7ree: WordQueryViewModel_7ree) {
    val articleViewModel = wordQueryViewModel_7ree.articleViewModel_7ree
    if (articleViewModel != null) {
        // 收集分页相关状态
        val usePaginationMode by articleViewModel.usePaginationMode.collectAsState()
        val isSearchMode by articleViewModel.isSearchMode.collectAsState()
        val searchQuery by articleViewModel.searchQuery.collectAsState()
        val searchResults by articleViewModel.searchResults.collectAsState()
        
        // 根据搜索模式选择显示的文章列表
        val articles by if (usePaginationMode) {
            // 分页模式下，搜索结果也通过pagedArticles获取
            articleViewModel.pagedArticles.collectAsState()
        } else if (isSearchMode && searchQuery.isNotBlank()) {
            // 非分页模式下，搜索结果通过searchResults获取
            articleViewModel.searchResults.collectAsState()
        } else {
            // 非分页模式下的正常文章列表
            articleViewModel.articles.collectAsState()
        }
        val isLoadingMore by articleViewModel.isLoadingMore.collectAsState()
        val hasMoreData by articleViewModel.hasMoreData.collectAsState()
        val isRefreshing by if (usePaginationMode) {
            articleViewModel.isPaginationRefreshing.collectAsState()
        } else {
            articleViewModel.isRefreshing.collectAsState()
        }
        
        val isGenerating by articleViewModel.isGenerating.collectAsState()
        val showDetailScreen by articleViewModel.showDetailScreen.collectAsState()
        val selectedArticle by articleViewModel.selectedArticle.collectAsState()
        val shouldRestoreScrollPosition by articleViewModel.shouldRestoreScrollPosition.collectAsState()
        
selectedArticle?.let { article ->
            if (showDetailScreen) {
                val isReading by articleViewModel.isReading.collectAsState()
                val ttsButtonState by articleViewModel.ttsButtonState.collectAsState()
                val keywordStats by articleViewModel.keywordStats.collectAsState()
                val relatedArticles by articleViewModel.relatedArticles.collectAsState()
                
// 创建一个包装的返回点击函数，用于处理TTS停止逻辑
                val handleBackClick = {
                    // 如果正在朗读，则先停止朗读再返回
                    if (isReading) {
                        // 直接调用停止朗读的方法
                        articleViewModel.stopReading()
                    }
                    // 返回文章列表时，标记需要恢复滚动位置
                    articleViewModel.markScrollPositionForRestore()
                    articleViewModel.closeDetailScreen()
                }
                
                // 显示文章详情页
                ArticleDetailScreen_7ree(
                    article = article,
                    relatedArticles = relatedArticles,
                    onBackClick = handleBackClick,
                    onToggleFavorite = {
                        articleViewModel.toggleSelectedArticleFavorite()
                    },
                    onShareClick = {
                        articleViewModel.toggleReading()
                    },
                    onKeywordClick = { keyword ->
                        // 处理关键词点击事件
                        wordQueryViewModel_7ree?.onWordInputChanged_7ree(keyword)
                        wordQueryViewModel_7ree?.queryWord_7ree()
                        wordQueryViewModel_7ree?.setCurrentScreen_7ree("SEARCH")
                    },
                    onRelatedArticleClick = { relatedArticle ->
                        articleViewModel.selectArticle(relatedArticle)
                    },
                    isReading = isReading,
                    ttsButtonState = ttsButtonState,
                    keywordStats = keywordStats,
                    // 新增参数：传递TTS停止逻辑给边缘滑动组件
                    onEdgeSwipeBack = handleBackClick
                )
            } else {
                // 显示文章列表页
                ArticleScreen_7ree(
                    articles = articles,
                    onGenerateArticle = { keyWords ->
                        articleViewModel.generateArticle(keyWords)
                    },
                    onSmartGenerate = { type ->
                        articleViewModel.smartGenerateArticle(type)
                    },
                    onSmartGenerateWithKeywords = { type, keywords ->
                        articleViewModel.smartGenerateArticle(type, keywords)
                    },
                    onArticleClick = { article ->
                        articleViewModel.selectArticle(article)
                    },
                    onToggleFavorite = { articleId ->
                        if (usePaginationMode) {
                            articleViewModel.paginationToggleFavorite(articleId)
                        } else {
                            articleViewModel.toggleFavorite(articleId)
                        }
                    },
                    isGenerating = isGenerating,
                    showSmartGenerationCard = articleViewModel.showSmartGenerationCard.collectAsState().value,
                    smartGenerationStatus = articleViewModel.smartGenerationStatus.collectAsState().value,
                    smartGenerationKeywords = articleViewModel.smartGenerationKeywords.collectAsState().value,
                    onCloseSmartGenerationCard = { articleViewModel.closeSmartGenerationCard() },
                    currentSmartGenerationType = articleViewModel.getCurrentSmartGenerationType(),
                    isRefreshing = isRefreshing,
                    onRefresh = { 
                        if (usePaginationMode) {
                            articleViewModel.paginationRefreshArticles()
                        } else {
                            articleViewModel.pullToRefreshArticles()
                        }
                    },
                    // 新增的筛选菜单参数
                    showFilterMenu = articleViewModel.showFilterMenu.collectAsState().value,
                    filterState = articleViewModel.filterState.collectAsState().value,
                    onShowFilterMenu = { articleViewModel.showFilterMenu() },
                    onHideFilterMenu = { articleViewModel.hideFilterMenu() },
                    onFilterStateChange = { filterState -> articleViewModel.updateFilterState(filterState) },
                    // 新增的管理模式参数
                    isManagementMode = articleViewModel.isManagementMode.collectAsState().value,
                    selectedArticleIds = articleViewModel.selectedArticleIds.collectAsState().value,
                    onEnterManagementMode = { articleViewModel.enterManagementMode() },
                    onExitManagementMode = { articleViewModel.exitManagementMode() },
                    onToggleArticleSelection = { articleId -> articleViewModel.toggleArticleSelection(articleId) },
                    onToggleSelectAll = { articleViewModel.toggleSelectAll() },
                    onDeleteSelectedArticles = { 
                        if (usePaginationMode) {
                            articleViewModel.paginationDeleteSelectedArticles()
                        } else {
                            articleViewModel.deleteSelectedArticles()
                        }
                    },
                    // 新增的分页参数
                    usePaginationMode = usePaginationMode,
                    isLoadingMore = isLoadingMore,
                    hasMoreData = hasMoreData,
                    onLoadMore = { articleViewModel.loadMoreArticles() },
                    // 新增的搜索参数
                    searchQuery = articleViewModel.searchQuery.collectAsState().value,
                    isSearchMode = articleViewModel.isSearchMode.collectAsState().value,
                    onSearchQueryChange = { query -> 
                        articleViewModel.updateSearchQuery(query)
                    },
                    onSearchModeToggle = { isSearchMode -> articleViewModel.toggleSearchMode(isSearchMode) },
                    // 新增的滚动位置管理参数
                    shouldRestoreScrollPosition = shouldRestoreScrollPosition,
                    onScrollPositionSaved = { articleViewModel.saveScrollPositionBeforeNavigation() },
                    onScrollPositionRestored = { articleViewModel.clearScrollPositionRestore() }
                )
            }
        } ?: run {
            // 显示文章列表页
            ArticleScreen_7ree(
                articles = articles,
                onGenerateArticle = { keyWords ->
                    articleViewModel.generateArticle(keyWords)
                },
                onSmartGenerate = { type ->
                    articleViewModel.smartGenerateArticle(type)
                },
                onSmartGenerateWithKeywords = { type, keywords ->
                    articleViewModel.smartGenerateArticle(type, keywords)
                },
                onArticleClick = { article ->
                    articleViewModel.selectArticle(article)
                },
                onToggleFavorite = { articleId ->
                    if (usePaginationMode) {
                        articleViewModel.paginationToggleFavorite(articleId)
                    } else {
                        articleViewModel.toggleFavorite(articleId)
                    }
                },
                isGenerating = isGenerating,
                showSmartGenerationCard = articleViewModel.showSmartGenerationCard.collectAsState().value,
                smartGenerationStatus = articleViewModel.smartGenerationStatus.collectAsState().value,
                smartGenerationKeywords = articleViewModel.smartGenerationKeywords.collectAsState().value,
                onCloseSmartGenerationCard = { articleViewModel.closeSmartGenerationCard() },
                currentSmartGenerationType = articleViewModel.getCurrentSmartGenerationType(),
                isRefreshing = isRefreshing,
                onRefresh = { 
                    if (usePaginationMode) {
                        articleViewModel.paginationRefreshArticles()
                    } else {
                        articleViewModel.pullToRefreshArticles()
                    }
                },
                // 新增的筛选菜单参数
                showFilterMenu = articleViewModel.showFilterMenu.collectAsState().value,
                filterState = articleViewModel.filterState.collectAsState().value,
                onShowFilterMenu = { articleViewModel.showFilterMenu() },
                onHideFilterMenu = { articleViewModel.hideFilterMenu() },
                onFilterStateChange = { filterState -> articleViewModel.updateFilterState(filterState) },
                // 新增的管理模式参数
                isManagementMode = articleViewModel.isManagementMode.collectAsState().value,
                selectedArticleIds = articleViewModel.selectedArticleIds.collectAsState().value,
                onEnterManagementMode = { articleViewModel.enterManagementMode() },
                onExitManagementMode = { articleViewModel.exitManagementMode() },
                onToggleArticleSelection = { articleId -> articleViewModel.toggleArticleSelection(articleId) },
                onToggleSelectAll = { articleViewModel.toggleSelectAll() },
                onDeleteSelectedArticles = { 
                    if (usePaginationMode) {
                        articleViewModel.paginationDeleteSelectedArticles()
                    } else {
                        articleViewModel.deleteSelectedArticles()
                    }
                },
                // 新增的分页参数
                usePaginationMode = usePaginationMode,
                isLoadingMore = isLoadingMore,
                hasMoreData = hasMoreData,
                onLoadMore = { articleViewModel.loadMoreArticles() },
                // 新增的搜索参数
                searchQuery = articleViewModel.searchQuery.collectAsState().value,
                isSearchMode = articleViewModel.isSearchMode.collectAsState().value,
                onSearchQueryChange = { query -> 
                    articleViewModel.updateSearchQuery(query)
                },
                onSearchModeToggle = { isSearchMode -> articleViewModel.toggleSearchMode(isSearchMode) },
                // 新增的滚动位置管理参数
                shouldRestoreScrollPosition = shouldRestoreScrollPosition,
                onScrollPositionSaved = { articleViewModel.saveScrollPositionBeforeNavigation() },
                onScrollPositionRestored = { articleViewModel.clearScrollPositionRestore() }
            )
        }
    } else {
        // 如果ArticleViewModel未初始化，显示错误信息
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "文章功能初始化失败",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * 显示设置屏幕
 */
@Composable
fun ShowSettingsScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onImportWordFile_7ree: () -> Unit,
    onImportArticleFile_7ree: () -> Unit
) {
    DashboardScreen_7ree(
        wordQueryViewModel_7ree = wordQueryViewModel_7ree,
        onImportWordFile_7ree = onImportWordFile_7ree,
        onImportArticleFile_7ree = onImportArticleFile_7ree
    )
}
