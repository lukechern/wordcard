package com.x7ree.wordcard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.data.ArticleEntity_7ree
import com.x7ree.wordcard.article.utils.ArticlePullToRefreshComponent_7ree
import com.x7ree.wordcard.article.utils.ArticleFilterSideMenu_7ree
import com.x7ree.wordcard.article.utils.ArticleFilterState_7ree
import com.x7ree.wordcard.article.utils.PaginatedArticleList_7ree
import com.x7ree.wordcard.article.utils.ArticleCard_7ree
import com.x7ree.wordcard.article.utils.ArticleSearchBarComponent_7ree
import com.x7ree.wordcard.article.ArticleManagementBar_7ree
import com.x7ree.wordcard.article.ArticleEmptyState_7ree
import com.x7ree.wordcard.article.ArticleTwoColumnList_7ree
import com.x7ree.wordcard.article.utils.ArticleScrollPositionManager_7ree
import com.x7ree.wordcard.article.utils.rememberArticleScrollPositionManager
import androidx.compose.foundation.lazy.rememberLazyListState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen_7ree(
    articles: List<ArticleEntity_7ree> = emptyList(),
    onGenerateArticle: (String) -> Unit = {},
    onSmartGenerate: (SmartGenerationType_7ree) -> Unit = {},
    onSmartGenerateWithKeywords: (SmartGenerationType_7ree, String) -> Unit = { _, _ -> },
    onArticleClick: (ArticleEntity_7ree) -> Unit = {},
    onToggleFavorite: (Long) -> Unit = {},
    isGenerating: Boolean = false,
    showSmartGenerationCard: Boolean = false,
    smartGenerationStatus: String = "",
    @Suppress("UNUSED_PARAMETER") smartGenerationKeywords: List<String> = emptyList(),
    onCloseSmartGenerationCard: () -> Unit = {},
    currentSmartGenerationType: SmartGenerationType_7ree? = null,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    // 新增的筛选菜单相关参数
    showFilterMenu: Boolean = false,
    filterState: ArticleFilterState_7ree = ArticleFilterState_7ree(),
    onShowFilterMenu: () -> Unit = {},
    onHideFilterMenu: () -> Unit = {},
    onFilterStateChange: (ArticleFilterState_7ree) -> Unit = {},
    // 新增的管理模式相关参数
    isManagementMode: Boolean = false,
    selectedArticleIds: Set<Long> = emptySet(),
    onEnterManagementMode: () -> Unit = {},
    onExitManagementMode: () -> Unit = {},
    onToggleArticleSelection: (Long) -> Unit = {},
    @Suppress("UNUSED_PARAMETER") onToggleSelectAll: () -> Unit = {},
    onDeleteSelectedArticles: () -> Unit = {},
    // 新增的分页相关参数
    usePaginationMode: Boolean = true,
    isLoadingMore: Boolean = false,
    hasMoreData: Boolean = true,
    onLoadMore: () -> Unit = {},
    // 新增的搜索相关参数
    searchQuery: String = "",
    isSearchMode: Boolean = false,
    onSearchQueryChange: (String) -> Unit = {},
    onSearchModeToggle: (Boolean) -> Unit = {},
    // 新增的滚动位置管理相关参数
    shouldRestoreScrollPosition: Boolean = false,
    onScrollPositionSaved: () -> Unit = {},
    onScrollPositionRestored: () -> Unit = {}
) {
    var showGenerationDialog by remember { mutableStateOf(false) }
    var shouldCloseDialogAfterGeneration by remember { mutableStateOf(false) }
    
    // 滚动位置管理器和列表状态
    val scrollPositionManager = rememberArticleScrollPositionManager()
    val listState = rememberLazyListState()
    
    // 监听生成状态，如果正在生成文章，则在生成完成后关闭对话框
    LaunchedEffect(isGenerating) {
        if (!isGenerating && shouldCloseDialogAfterGeneration) {
            showGenerationDialog = false
            shouldCloseDialogAfterGeneration = false
        }
    }
    
    // 滚动位置恢复逻辑已移至 PaginatedArticleList_7ree 中处理，避免重复执行
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
        // 搜索栏组件 - 替换原有的标题栏
        ArticleSearchBarComponent_7ree(
            title = "读文章",
            searchQuery = searchQuery,
            isSearchMode = isSearchMode,
            onSearchQueryChange = onSearchQueryChange,
            onSearchModeToggle = onSearchModeToggle,
            onGenerateArticle = { showGenerationDialog = true },
            onShowFilterMenu = onShowFilterMenu,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 文章列表 - 根据模式选择使用分页组件或原有组件
        if (usePaginationMode) {
            // 使用分页组件
            PaginatedArticleList_7ree(
                articles = articles,
                isLoadingMore = isLoadingMore,
                hasMoreData = hasMoreData,
                onArticleClick = { article ->
                    // 在点击文章进入详情页前，保存当前滚动位置
                    scrollPositionManager.saveScrollPosition(listState)
                    onScrollPositionSaved()
                    onArticleClick(article)
                },
                onToggleFavorite = onToggleFavorite,
                onLoadMore = onLoadMore,
                listState = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                isManagementMode = isManagementMode,
                selectedArticleIds = selectedArticleIds,
                onToggleArticleSelection = onToggleArticleSelection,
                isSearchMode = isSearchMode,
                searchQuery = searchQuery,
                scrollPositionManager = scrollPositionManager,
                shouldRestoreScrollPosition = shouldRestoreScrollPosition,
                onScrollPositionRestored = onScrollPositionRestored
            )
        } else {
            // 使用原有的下拉刷新组件
            ArticlePullToRefreshComponent_7ree(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                if (articles.isEmpty()) {
                    // 使用新提取的空状态组件
                    ArticleEmptyState_7ree()
                } else {
                    // 使用新提取的两列布局文章列表组件
                    ArticleTwoColumnList_7ree(
                        articles = articles,
                        isManagementMode = isManagementMode,
                        selectedArticleIds = selectedArticleIds,
                        onArticleClick = onArticleClick,
                        onToggleFavorite = onToggleFavorite,
                        onToggleArticleSelection = onToggleArticleSelection
                    )
                }
            }
        }
        }
        
        // 使用新提取的管理模式底部删除操作条组件
        ArticleManagementBar_7ree(
            isManagementMode = isManagementMode,
            selectedArticleIds = selectedArticleIds,
            onExitManagementMode = onExitManagementMode,
            onDeleteSelectedArticles = onDeleteSelectedArticles
        )
    }
    
// 文章生成对话框
    ArticleGenerationDialog_7ree(
        isVisible = showGenerationDialog,
        onDismiss = { showGenerationDialog = false },
        onGenerate = { keyWords ->
            onGenerateArticle(keyWords)
            showGenerationDialog = false
        },
        onSmartGenerate = { type ->
            onSmartGenerate(type)
            // 不立即关闭对话框，等待生成完成后再关闭
            shouldCloseDialogAfterGeneration = true
        },
        onSmartGenerateWithKeywords = { type, keywords ->
            onSmartGenerateWithKeywords(type, keywords)
            // 对于手动输入，不立即关闭对话框，等待生成完成后再关闭
            shouldCloseDialogAfterGeneration = true
        },
        isGenerating = isGenerating
    )
    
    // 智能生成进度卡片
    SmartGenerationProgressCard_7ree(
        isVisible = showSmartGenerationCard,
        status = smartGenerationStatus,
        onDismiss = onCloseSmartGenerationCard,
        currentSmartGenerationType = currentSmartGenerationType
    )
    
    // 筛选菜单
    ArticleFilterSideMenu_7ree(
        isVisible = showFilterMenu,
        filterState = filterState,
        onFilterStateChange = onFilterStateChange,
        onDismiss = onHideFilterMenu,
        onManageClick = onEnterManagementMode
    )
}
