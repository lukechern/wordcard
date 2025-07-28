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
    smartGenerationKeywords: List<String> = emptyList(),
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
    onToggleSelectAll: () -> Unit = {},
    onDeleteSelectedArticles: () -> Unit = {},
    // 新增的分页相关参数
    usePaginationMode: Boolean = true,
    isLoadingMore: Boolean = false,
    hasMoreData: Boolean = true,
    onLoadMore: () -> Unit = {}
) {
    var showGenerationDialog by remember { mutableStateOf(false) }
    var shouldCloseDialogAfterGeneration by remember { mutableStateOf(false) }
    
    // 监听生成状态，如果正在生成文章，则在生成完成后关闭对话框
    LaunchedEffect(isGenerating) {
        if (!isGenerating && shouldCloseDialogAfterGeneration) {
            showGenerationDialog = false
            shouldCloseDialogAfterGeneration = false
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
        // 标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "读文章",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            // 右侧按钮组
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 生成文章按钮 - 背景减少30%，图标保持和单词本等大
                IconButton(
                    onClick = { showGenerationDialog = true },
                    modifier = Modifier
                        .size(30.dp) // 背景减少
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape // 改为正圆形
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "生成文章",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp) // 图标保持和单词本等大
                    )
                }
                
                // 汉堡菜单按钮
                IconButton(
                    onClick = { onShowFilterMenu() },
                    modifier = Modifier.size(30.dp) // 与加号按钮保持一致的尺寸
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "筛选与排序",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp) // 图标保持和单词本等大
                    )
                }
            }
        }
        
        // 文章列表 - 根据模式选择使用分页组件或原有组件
        if (usePaginationMode) {
            // 使用分页组件
            PaginatedArticleList_7ree(
                articles = articles,
                isLoadingMore = isLoadingMore,
                hasMoreData = hasMoreData,
                onArticleClick = onArticleClick,
                onToggleFavorite = onToggleFavorite,
                onLoadMore = onLoadMore,
                modifier = Modifier.fillMaxSize(),
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                isManagementMode = isManagementMode,
                selectedArticleIds = selectedArticleIds,
                onToggleArticleSelection = onToggleArticleSelection
            )
        } else {
            // 使用原有的下拉刷新组件
            ArticlePullToRefreshComponent_7ree(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                if (articles.isEmpty()) {
                    // 空状态
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "暂无文章",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "点击右上角的 + 按钮生成文章",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "或下拉刷新文章列表",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    // 使用两列布局，文章已经在ViewModel中进行了筛选和排序
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 直接按行处理，每行显示两个文章
                        items(
                            count = (articles.size + 1) / 2,
                            key = { rowIndex -> 
                                // 使用行中文章的ID作为key，确保稳定性
                                val leftIndex = rowIndex * 2
                                val rightIndex = leftIndex + 1
                                val leftId = if (leftIndex < articles.size) articles[leftIndex].id else -1L
                                val rightId = if (rightIndex < articles.size) articles[rightIndex].id else -2L
                                "${leftId}_${rightId}"
                            }
                        ) { rowIndex ->
                            val leftIndex = rowIndex * 2
                            val rightIndex = leftIndex + 1
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 左列 - 显示偶数索引的文章 (0, 2, 4, ...)
                                Box(modifier = Modifier.weight(1f)) {
                                    if (leftIndex < articles.size) {
                                        val leftArticle = articles[leftIndex]
                                        ArticleCard_7ree(
                                            article = leftArticle,
                                            onClick = { 
                                                if (isManagementMode) {
                                                    onToggleArticleSelection(leftArticle.id)
                                                } else {
                                                    onArticleClick(leftArticle)
                                                }
                                            },
                                            onToggleFavorite = { onToggleFavorite(leftArticle.id) },
                                            isManagementMode = isManagementMode,
                                            isSelected = selectedArticleIds.contains(leftArticle.id),
                                            onToggleSelection = { onToggleArticleSelection(leftArticle.id) }
                                        )
                                    }
                                }
                                
                                // 右列 - 显示奇数索引的文章 (1, 3, 5, ...)
                                Box(modifier = Modifier.weight(1f)) {
                                    if (rightIndex < articles.size) {
                                        val rightArticle = articles[rightIndex]
                                        ArticleCard_7ree(
                                            article = rightArticle,
                                            onClick = { 
                                                if (isManagementMode) {
                                                    onToggleArticleSelection(rightArticle.id)
                                                } else {
                                                    onArticleClick(rightArticle)
                                                }
                                            },
                                            onToggleFavorite = { onToggleFavorite(rightArticle.id) },
                                            isManagementMode = isManagementMode,
                                            isSelected = selectedArticleIds.contains(rightArticle.id),
                                            onToggleSelection = { onToggleArticleSelection(rightArticle.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        }
        
        // 管理模式下的底部删除操作条
        if (isManagementMode) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 完成按钮
                    TextButton(
                        onClick = onExitManagementMode
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "完成",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("完成")
                    }
                    
                    // 已选条目数量
                    Text(
                        text = "已选 ${selectedArticleIds.size} 项",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // 删除确认按钮
                    Button(
                        onClick = onDeleteSelectedArticles,
                        enabled = selectedArticleIds.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("删除")
                    }
                }
            }
        }
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
            showGenerationDialog = false
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
        keywords = smartGenerationKeywords,
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


