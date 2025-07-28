package com.x7ree.wordcard.ui.MainScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.state.ScrollPosition_7ree
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.components.SearchBarComponent_7ree
import com.x7ree.wordcard.ui.components.TtsButtonState_7ree
import com.x7ree.wordcard.ui.components.FilterSideMenu_7ree
import com.x7ree.wordcard.ui.components.FilterState_7ree
import com.x7ree.wordcard.ui.components.SortType_7ree
import com.x7ree.wordcard.ui.components.CustomScrollbar_7ree
import com.x7ree.wordcard.ui.components.KeyboardControlButton_7ree
import com.x7ree.wordcard.ui.components.InitialLoadingComponent_7ree
import com.x7ree.wordcard.ui.components.EmptyStateComponent_7ree
import com.x7ree.wordcard.ui.components.HistoryContentComponent_7ree
import com.x7ree.wordcard.ui.components.FilterMenuManager_7ree
import com.x7ree.wordcard.ui.components.TtsStateManager_7ree
import com.x7ree.wordcard.ui.components.ScrollPositionManager_7ree
import com.x7ree.wordcard.ui.components.DataLoadingManager_7ree
import com.x7ree.wordcard.ui.components.CustomKeyboardManager_7ree
import com.x7ree.wordcard.utils.CustomKeyboard.CustomKeyboard_7ree

@Composable
fun HistoryScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onWordClick_7ree: (String) -> Unit
) {
    val pagedWords_7ree by wordQueryViewModel_7ree.pagedWords_7ree.collectAsState()
    val isLoadingMore_7ree by wordQueryViewModel_7ree.isLoadingMore_7ree.collectAsState()
    val hasMoreData_7ree by wordQueryViewModel_7ree.hasMoreData_7ree.collectAsState()
    val showFavoritesOnly_7ree by wordQueryViewModel_7ree.showFavoritesOnly_7ree.collectAsState()
    val searchQuery_7ree by wordQueryViewModel_7ree.searchQuery_7ree.collectAsState()
    val isSearchMode_7ree by wordQueryViewModel_7ree.isSearchMode_7ree.collectAsState()
    val isSpeaking_7ree = wordQueryViewModel_7ree.isSpeaking_7ree
    val isSpeakingWord_7ree = wordQueryViewModel_7ree.isSpeakingWord_7ree
    
    var deletedWords_7ree by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isInitialLoading_7ree by remember { mutableStateOf(true) }
    var isRefreshing_7ree by remember { mutableStateOf(false) }
    var currentSpeakingWord_7ree by remember { mutableStateOf("") }
    var ttsState_7ree by remember { mutableStateOf(TtsButtonState_7ree.IDLE) }
    var filteredWords_7ree by remember { mutableStateOf<List<com.x7ree.wordcard.data.WordEntity_7ree>>(emptyList()) }
    var handleRefresh by remember { mutableStateOf<(() -> Unit)?>(null) }
    
    // 筛选菜单状态
    var isFilterMenuVisible by remember { mutableStateOf(false) }
    var filterState by remember { 
        mutableStateOf(FilterState_7ree(showFavoritesOnly = showFavoritesOnly_7ree))
    }
    
    // 创建或获取保存的滚动状态
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = wordQueryViewModel_7ree.savedWordBookScrollPosition_7ree.firstVisibleItemIndex,
        initialFirstVisibleItemScrollOffset = wordQueryViewModel_7ree.savedWordBookScrollPosition_7ree.firstVisibleItemScrollOffset
    )
    
    // 使用筛选菜单管理器
    FilterMenuManager_7ree(
        showFavoritesOnly_7ree = showFavoritesOnly_7ree,
        isFilterMenuVisible = isFilterMenuVisible,
        onFilterMenuVisibilityChange = { isVisible -> isFilterMenuVisible = isVisible },
        filterState = filterState,
        onFilterStateChange = { newState -> filterState = newState }
    )
    
    // 使用TTS状态管理器
    TtsStateManager_7ree(
        isSpeakingWord_7ree = isSpeakingWord_7ree,
        isSpeaking_7ree = isSpeaking_7ree,
        currentSpeakingWord_7ree = currentSpeakingWord_7ree,
        onTtsStateChange = { newState -> ttsState_7ree = newState },
        onCurrentSpeakingWordChange = { newWord -> currentSpeakingWord_7ree = newWord }
    )
    
    // 使用滚动位置管理器
    ScrollPositionManager_7ree(
        wordQueryViewModel_7ree = wordQueryViewModel_7ree,
        pagedWords_7ree = pagedWords_7ree,
        listState = listState,
        onSavedScrollPositionChange = { newPosition -> 
            wordQueryViewModel_7ree.savedWordBookScrollPosition_7ree = newPosition 
        }
    )
    
    // 使用数据加载管理器
    DataLoadingManager_7ree(
        wordQueryViewModel_7ree = wordQueryViewModel_7ree,
        pagedWords_7ree = pagedWords_7ree,
        deletedWords_7ree = deletedWords_7ree,
        onDeletedWordsChange = { newDeletedWords -> deletedWords_7ree = newDeletedWords },
        onInitialLoadingChange = { isLoading -> isInitialLoading_7ree = isLoading },
        onRefreshingChange = { isRefreshing -> isRefreshing_7ree = isRefreshing },
        onFilteredWordsChange = { newFilteredWords -> filteredWords_7ree = newFilteredWords },
        onHandleRefreshChange = { newHandleRefresh -> handleRefresh = newHandleRefresh }
    )
    
    // 自定义键盘状态
    var showCustomKeyboard_7ree by remember { mutableStateOf(false) }
    var useCustomKeyboard by remember { mutableStateOf(false) }
    var customKeyboardState_7ree by remember { mutableStateOf<com.x7ree.wordcard.utils.CustomKeyboard.CustomKeyboardState_7ree?>(null) }
    
    // 使用自定义键盘管理器
    CustomKeyboardManager_7ree(
        wordQueryViewModel_7ree = wordQueryViewModel_7ree,
        isSearchMode_7ree = isSearchMode_7ree,
        showCustomKeyboard_7ree = showCustomKeyboard_7ree,
        onShowCustomKeyboardChange = { shouldShow -> showCustomKeyboard_7ree = shouldShow },
        onUseCustomKeyboardChange = { shouldUse -> useCustomKeyboard = shouldUse },
        onCustomKeyboardStateChange = { newState -> customKeyboardState_7ree = newState }
    )
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 搜索栏组件，包含标题、搜索功能和汉堡菜单按钮
            SearchBarComponent_7ree(
                title = "单词本",
                searchQuery = searchQuery_7ree,
                isSearchMode = isSearchMode_7ree,
                onSearchQueryChange = { query ->
                    wordQueryViewModel_7ree.updateSearchQuery_7ree(query)
                },
                onSearchModeToggle = { isSearchMode ->
                    wordQueryViewModel_7ree.setSearchMode_7ree(isSearchMode)
                    if (!isSearchMode && useCustomKeyboard) {
                        showCustomKeyboard_7ree = false
                        customKeyboardState_7ree?.hide_7ree()
                    }
                    // 退出搜索模式时关闭筛选菜单
                    if (!isSearchMode) {
                        isFilterMenuVisible = false
                    }
                },
                wordQueryViewModel = wordQueryViewModel_7ree,
                onCustomKeyboardStateChange = { shouldShow ->
                    showCustomKeyboard_7ree = shouldShow
                    if (shouldShow) {
                        // 确保键盘状态同步
                        customKeyboardState_7ree?.show_7ree()
                    }
                },
                // 使用汉堡菜单
                showMenuButton = true,
                isMenuOpen = isFilterMenuVisible,
                onMenuToggle = {
                    isFilterMenuVisible = !isFilterMenuVisible
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // 内容区域 - 包含滚动条
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                if (isInitialLoading_7ree) {
                    // 显示初始加载状态
                    InitialLoadingComponent_7ree(
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (filteredWords_7ree.isEmpty() && !hasMoreData_7ree) {
                    // 显示空状态
                    EmptyStateComponent_7ree(
                        showFavoritesOnly_7ree = showFavoritesOnly_7ree,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // 显示内容区域
                    HistoryContentComponent_7ree(
                        filteredWords_7ree = filteredWords_7ree,
                        isLoadingMore_7ree = isLoadingMore_7ree,
                        hasMoreData_7ree = hasMoreData_7ree,
                        isRefreshing_7ree = isRefreshing_7ree,
                        onWordClick = onWordClick_7ree,
                        onWordDelete = { wordEntity_7ree ->
                            // 立即添加到删除集合，从UI中移除
                            deletedWords_7ree = deletedWords_7ree + wordEntity_7ree.word
                            // 执行实际的删除操作
                            wordQueryViewModel_7ree.deleteWord_7ree(wordEntity_7ree.word)
                            // 重新加载数据以保持分页正确性
                            wordQueryViewModel_7ree.resetPagination_7ree()
                            wordQueryViewModel_7ree.loadInitialWords_7ree()
                        },
                        onLoadMore = {
                            wordQueryViewModel_7ree.loadMoreWords_7ree()
                        },
onWordSpeak = { word ->
    // 防止重复点击
    if (currentSpeakingWord_7ree.isEmpty() || currentSpeakingWord_7ree != word) {
        currentSpeakingWord_7ree = word
        // 调用TTS播放
        wordQueryViewModel_7ree.speakWord_7ree(word)
    }
},
onWordStopSpeak = {
    wordQueryViewModel_7ree.stopSpeaking_7ree()
    currentSpeakingWord_7ree = ""
},
                        onRefresh = handleRefresh,
                        ttsState = ttsState_7ree,
                        currentSpeakingWord = currentSpeakingWord_7ree,
                        listState = listState,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
        }
        
        // 筛选侧边菜单
        FilterSideMenu_7ree(
            isVisible = isFilterMenuVisible,
            filterState = filterState,
            onFilterStateChange = { newFilterState ->
                filterState = newFilterState
                
                // 处理收藏筛选变化
                if (newFilterState.showFavoritesOnly != showFavoritesOnly_7ree) {
                    wordQueryViewModel_7ree.toggleFavoriteFilter_7ree()
                }
                
                // 处理排序变化
                val sortTypeString = newFilterState.sortType?.name
                wordQueryViewModel_7ree.setSortType_7ree(sortTypeString)
                
                // 重新加载数据
                wordQueryViewModel_7ree.resetPagination_7ree()
                wordQueryViewModel_7ree.loadInitialWords_7ree()
            },
            onDismiss = {
                isFilterMenuVisible = false
            }
        )
        
        // 自定义键盘 - 固定在屏幕底部，全屏宽度
        if (useCustomKeyboard && showCustomKeyboard_7ree && isSearchMode_7ree) {
            CustomKeyboard_7ree(
                onKeyPress_7ree = { key ->
                    when (key) {
                        "BACKSPACE" -> {
                            if (searchQuery_7ree.isNotEmpty()) {
                                wordQueryViewModel_7ree.updateSearchQuery_7ree(searchQuery_7ree.dropLast(1))
                            }
                        }
                        "SEARCH" -> {
                            // 搜索功能 - 在搜索模式下不需要特殊处理，实时搜索已经在进行
                        }
                        else -> {
                            // 添加字母
                            wordQueryViewModel_7ree.updateSearchQuery_7ree(searchQuery_7ree + key)
                        }
                    }
                },
                onBackspace_7ree = {
                    if (searchQuery_7ree.isNotEmpty()) {
                        wordQueryViewModel_7ree.updateSearchQuery_7ree(searchQuery_7ree.dropLast(1))
                    }
                },
                onSearch_7ree = {
                    // 搜索功能 - 在搜索模式下不需要特殊处理，实时搜索已经在进行
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        
        // 键盘控制按钮 - 在搜索模式下显示，根据键盘状态切换图标
        if (useCustomKeyboard && isSearchMode_7ree) {
            KeyboardControlButton_7ree(
                showCustomKeyboard_7ree = showCustomKeyboard_7ree,
                onKeyboardToggle = {
                    if (showCustomKeyboard_7ree) {
                        // 收起键盘
                        showCustomKeyboard_7ree = false
                        customKeyboardState_7ree?.hide_7ree()
                    } else {
                        // 展开键盘
                        showCustomKeyboard_7ree = true
                        customKeyboardState_7ree?.show_7ree()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(
                        x = (-16).dp, 
                        y = if (showCustomKeyboard_7ree) (-228).dp else (-32).dp // 展开图标向下移动48.dp (一个按钮直径)
                    )
            )
        }
    }
}
