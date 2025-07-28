package com.x7ree.wordcard.ui.MainScreen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.ArticleDetailScreen_7ree
import com.x7ree.wordcard.ui.ArticleScreen_7ree
import com.x7ree.wordcard.ui.BottomNavigationBar_7ree
import com.x7ree.wordcard.ui.DashboardScreen_7ree
import com.x7ree.wordcard.ui.MainScreen.HistoryScreen_7ree
import com.x7ree.wordcard.ui.SplashScreen_7ree
import com.x7ree.wordcard.ui.WordCardScreen_7ree
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree?,
    isInitializationComplete_7ree: Boolean = false,
    onImportFile_7ree: () -> Unit = {}
) {
    // 从ViewModel获取当前屏幕状态
    val currentScreenString_7ree by wordQueryViewModel_7ree?.currentScreen_7ree?.collectAsState() ?: mutableStateOf("SEARCH")
    val currentScreen_7ree = when (currentScreenString_7ree) {
        "HISTORY" -> Screen_7ree.HISTORY
        "ARTICLE" -> Screen_7ree.ARTICLE
        "SETTINGS" -> Screen_7ree.SETTINGS
        else -> Screen_7ree.SEARCH
    }
    var showSplash_7ree by remember { mutableStateOf(true) }
    var showCustomToast_7ree by remember { mutableStateOf(false) }
    var toastMessage_7ree by remember { mutableStateOf("") }
    val operationResult_7ree by wordQueryViewModel_7ree?.operationResult_7ree?.collectAsState() ?: mutableStateOf(null)

    // 监听操作结果，显示自定义提示条
    LaunchedEffect(operationResult_7ree) {
        operationResult_7ree?.let { result ->
            toastMessage_7ree = result
            showCustomToast_7ree = true
            // 清除操作结果
            wordQueryViewModel_7ree?.clearOperationResult_7ree()
        }
    }

    // 智能启动画面控制 - 改进逻辑，确保不会卡住
    LaunchedEffect(isInitializationComplete_7ree) {
        if (isInitializationComplete_7ree) {
            // 如果初始化已完成，只显示500毫秒启动画面给用户视觉反馈
            delay(500)
            showSplash_7ree = false
        }
    }
    
    // 强制超时机制 - 确保启动画面不会无限显示
    LaunchedEffect(Unit) {
        delay(5000) // 最多显示5秒启动画面，给冷启动更多时间
        if (showSplash_7ree) {
            // Log.d("MainScreen_7ree", "启动画面超时，强制关闭")
            showSplash_7ree = false
        }
    }
    
    // 额外的安全机制 - 如果ViewModel可用但初始化标志未设置，也关闭启动画面
    LaunchedEffect(wordQueryViewModel_7ree) {
        if (wordQueryViewModel_7ree != null && showSplash_7ree) {
            delay(1000) // 给一点时间让初始化标志更新
            if (showSplash_7ree) {
                // Log.d("MainScreen_7ree", "ViewModel已可用，关闭启动画面")
                showSplash_7ree = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                // 只有在初始化完成且不在启动画面时才显示底部导航
                if (!showSplash_7ree && wordQueryViewModel_7ree != null) {
                    BottomNavigationBar_7ree(
                        currentScreen_7ree = currentScreen_7ree,
                        onScreenSelected_7ree = { screen -> 
                            val screenString = when (screen) {
                                Screen_7ree.HISTORY -> "HISTORY"
                                Screen_7ree.ARTICLE -> "ARTICLE"
                                Screen_7ree.SETTINGS -> "SETTINGS"
                                else -> "SEARCH"
                            }
                            wordQueryViewModel_7ree.setCurrentScreen_7ree(screenString)
                        },
                        onSearchReset_7ree = { wordQueryViewModel_7ree.resetQueryState_7ree() }
                    )
                }
            }
        ) { paddingValues_7ree ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues_7ree)
            ) {
                if (showSplash_7ree) {
                    SplashScreen_7ree()
                } else {
                    // 只有在初始化完成且ViewModel可用时才显示主界面
                    if (wordQueryViewModel_7ree != null) {
                        when (currentScreen_7ree) {
                            Screen_7ree.SEARCH -> {
                                WordCardScreen_7ree(
                                    wordQueryViewModel_7ree = wordQueryViewModel_7ree
                                )
                            }
                            Screen_7ree.HISTORY -> {
                                HistoryScreen_7ree(
                                    wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                                    onWordClick_7ree = { word ->
                                        wordQueryViewModel_7ree.loadWordFromHistory_7ree(word)
                                        wordQueryViewModel_7ree.setCurrentScreen_7ree("SEARCH")
                                    }
                                )
                            }
                            Screen_7ree.ARTICLE -> {
                                val articleViewModel = wordQueryViewModel_7ree.articleViewModel_7ree
                                if (articleViewModel != null) {
                                    // 收集分页相关状态
                                    val usePaginationMode by articleViewModel.usePaginationMode.collectAsState()
                                    val articles by if (usePaginationMode) {
                                        articleViewModel.pagedArticles.collectAsState()
                                    } else {
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
                                    
                                    selectedArticle?.let { article ->
                                        if (showDetailScreen) {
                                            val isReading by articleViewModel.isReading.collectAsState()
                                            val ttsButtonState by articleViewModel.ttsButtonState.collectAsState()
                                            val keywordStats by articleViewModel.keywordStats.collectAsState()
                                            
                                            // 显示文章详情页
                                            ArticleDetailScreen_7ree(
                                                article = article,
                                                onBackClick = {
                                                    articleViewModel.closeDetailScreen()
                                                },
                                                onToggleFavorite = {
                                                    articleViewModel.toggleSelectedArticleFavorite()
                                                },
                                                onShareClick = {
                                                    articleViewModel.toggleReading()
                                                },
                                                isReading = isReading,
                                                ttsButtonState = ttsButtonState,
                                                keywordStats = keywordStats
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
                                                onLoadMore = { articleViewModel.loadMoreArticles() }
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
                                            onLoadMore = { articleViewModel.loadMoreArticles() }
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
                            Screen_7ree.SETTINGS -> {
                                DashboardScreen_7ree(
                                    wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                                    onImportFile_7ree = onImportFile_7ree
                                )
                            }
                        }
                    } else {
                        // 如果ViewModel还未初始化完成，显示加载状态，但添加超时保护
                        var showLoadingTimeout by remember { mutableStateOf(false) }
                        
                        // 超时保护机制 - 如果10秒后还在加载状态，显示错误信息
                        LaunchedEffect(Unit) {
                            delay(10000) // 10秒超时
                            Log.e("MainScreen_7ree", "应用初始化超时")
                            showLoadingTimeout = true
                        }
                        
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (showLoadingTimeout) {
                                Text(
                                    text = "应用启动异常，请重新打开应用",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text(
                                    text = "正在加载...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // 自定义提示条显示在最顶层
        CustomToast_7ree(
            message = toastMessage_7ree,
            isVisible = showCustomToast_7ree,
            onDismiss = { showCustomToast_7ree = false }
        )
    }
}
