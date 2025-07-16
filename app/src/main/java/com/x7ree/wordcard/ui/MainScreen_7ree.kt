package com.x7ree.wordcard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Keyboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import com.x7ree.wordcard.ui.BottomNavigationBar_7ree // 导入新的组件

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.x7ree.wordcard.data.WordEntity_7ree
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
语言包定义

    'pl_search_word_7r' => '查单词',
    'pl_history_7r' => '历史',
    'pl_settings_7r' => '配置',
    'pl_config_saved_7r' => '配置保存成功',
    'pl_config_save_failed_7r' => '配置保存失败',
    'pl_history_placeholder_7r' => '历史功能开发中...',
    'pl_settings_placeholder_7r' => '配置功能开发中...',
**/

// 自定义提示条组件
@Composable
fun CustomToast_7ree(
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = isVisible,
                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(),
                exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically()
            ) {
                Card(
                    modifier = Modifier
                        .padding(10.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
        
        // 自动隐藏提示条
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000) // 2秒后自动隐藏
            onDismiss()
        }
    }
}

enum class Screen_7ree {
    SEARCH,
    HISTORY,
    SETTINGS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree?,
    isInitializationComplete_7ree: Boolean = false,
    speak_7ree: (String, String) -> Unit,
    stopSpeaking_7ree: () -> Unit,
    onImportFile_7ree: () -> Unit = {}
) {
    // 从ViewModel获取当前屏幕状态
    val currentScreenString_7ree by wordQueryViewModel_7ree?.currentScreen_7ree?.collectAsState() ?: mutableStateOf("SEARCH")
    val currentScreen_7ree = when (currentScreenString_7ree) {
        "HISTORY" -> Screen_7ree.HISTORY
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

    // 智能启动画面控制 - 并行执行，不增加总等待时间
    LaunchedEffect(isInitializationComplete_7ree) {
        if (isInitializationComplete_7ree) {
            // 如果初始化已完成，只显示500毫秒启动画面给用户视觉反馈
            delay(500)
            showSplash_7ree = false
        }
    }
    
    // 如果初始化时间过长，确保启动画面不会无限显示
    LaunchedEffect(Unit) {
        delay(3000) // 最多显示3秒启动画面
        if (showSplash_7ree) {
            showSplash_7ree = false
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
                                Screen_7ree.SETTINGS -> "SETTINGS"
                                else -> "SEARCH"
                            }
                            wordQueryViewModel_7ree?.setCurrentScreen_7ree(screenString)
                        },
                        onSearchReset_7ree = { wordQueryViewModel_7ree?.resetQueryState_7ree() }
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
                                    wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                                    speak_7ree = speak_7ree,
                                    stopSpeaking_7ree = stopSpeaking_7ree
                                )
                            }
                            Screen_7ree.HISTORY -> {
                                HistoryScreen_7ree(
                                    wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                                    onWordClick_7ree = { word ->
                                        wordQueryViewModel_7ree.loadWordFromHistory_7ree(word)
                                        wordQueryViewModel_7ree.setCurrentScreen_7ree("SEARCH")
                                    },
                                    speak_7ree = speak_7ree
                                )
                            }
                                                    Screen_7ree.SETTINGS -> {
                            DashboardScreen_7ree(
                                wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                                onImportFile_7ree = onImportFile_7ree
                            )
                        }
                        }
                    } else {
                        // 如果ViewModel还未初始化完成，显示加载状态
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
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
        
        // 自定义提示条显示在最顶层
        CustomToast_7ree(
            message = toastMessage_7ree,
            isVisible = showCustomToast_7ree,
            onDismiss = { showCustomToast_7ree = false }
        )
    }
}

@Composable
fun HistoryScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onWordClick_7ree: (String) -> Unit,
    speak_7ree: (String, String) -> Unit = { _, _ -> }
) {
    val pagedWords_7ree by wordQueryViewModel_7ree.pagedWords_7ree.collectAsState()
    val isLoadingMore_7ree by wordQueryViewModel_7ree.isLoadingMore_7ree.collectAsState()
    val hasMoreData_7ree by wordQueryViewModel_7ree.hasMoreData_7ree.collectAsState()
    val showFavoritesOnly_7ree by wordQueryViewModel_7ree.showFavoritesOnly_7ree.collectAsState()
    var deletedWords_7ree by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isInitialLoading_7ree by remember { mutableStateOf(true) }
    
    // 只过滤掉已删除的单词，收藏过滤由ViewModel在数据库层面处理
    val filteredWords_7ree = remember(pagedWords_7ree, deletedWords_7ree) {
        pagedWords_7ree.filter { it.word !in deletedWords_7ree }
    }
    
    // 初始加载
    LaunchedEffect(Unit) {
        // 重置分页状态并加载初始数据
        wordQueryViewModel_7ree.resetPagination_7ree()
        wordQueryViewModel_7ree.loadInitialWords_7ree()
        
        // 按需加载单词计数
        wordQueryViewModel_7ree.loadWordCount_7ree()
        
        // 等待初始数据加载完成
        delay(500)
        isInitialLoading_7ree = false
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 标题行，包含标题和收藏过滤按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "单词本",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = { wordQueryViewModel_7ree.toggleFavoriteFilter_7ree() }
            ) {
                Icon(
                    imageVector = if (showFavoritesOnly_7ree) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (showFavoritesOnly_7ree) "显示全部单词" else "只显示收藏单词",
                    tint = if (showFavoritesOnly_7ree) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (isInitialLoading_7ree) {
            // 显示初始加载状态
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "单词本加载中...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (filteredWords_7ree.isEmpty() && !hasMoreData_7ree) {
            // 显示空状态
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (showFavoritesOnly_7ree) "暂无收藏的单词" else "单词本暂无记录",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            PaginatedWordList_7ree(
                words = filteredWords_7ree,
                isLoadingMore = isLoadingMore_7ree,
                hasMoreData = hasMoreData_7ree,
                onWordClick = onWordClick_7ree,
                onFavoriteToggle = { entity ->
                    wordQueryViewModel_7ree.setFavorite_7ree(entity.word, !entity.isFavorite)
                    // 如果当前在收藏过滤模式下，需要重新加载数据
                    if (showFavoritesOnly_7ree) {
                        wordQueryViewModel_7ree.resetPagination_7ree()
                        wordQueryViewModel_7ree.loadInitialWords_7ree()
                    }
                },
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
                    speak_7ree(word, "word")
                }
            )
        }
    }
}

@Composable
fun HistoryWordItem_7ree(
    wordEntity_7ree: WordEntity_7ree,
    onWordClick_7ree: (String) -> Unit,
    onFavoriteToggle_7ree: (WordEntity_7ree) -> Unit,
    onDismiss_7ree: () -> Unit,
    onWordSpeak_7ree: (String) -> Unit = {}
) {
    val dateFormat_7ree = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val dateStr_7ree = dateFormat_7ree.format(Date(wordEntity_7ree.queryTimestamp))

    SwipeableRevealItem_7ree(
        onDeleteClick = onDismiss_7ree,
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onWordClick_7ree(wordEntity_7ree.word) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                 text = wordEntity_7ree.word,
                                 style = MaterialTheme.typography.titleLarge,
                                 fontWeight = FontWeight.Bold
                             )
                            
                            // 收藏状态显示（仅显示，不可点击）
                            if (wordEntity_7ree.isFavorite) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                     imageVector = Icons.Filled.Favorite,
                                     contentDescription = "已收藏",
                                     tint = MaterialTheme.colorScheme.primary,
                                     modifier = Modifier.size(13.6.dp)
                                 )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // 时间显示 - 固定宽度
                            Text(
                                text = dateStr_7ree,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.width(120.dp)
                            )
                            
                            // 查看次数 - 固定宽度
                             Row(
                                 verticalAlignment = Alignment.CenterVertically,
                                 modifier = Modifier.width(50.dp)
                             ) {
                                 Icon(
                                     imageVector = Icons.Filled.Visibility,
                                     contentDescription = "浏览次数",
                                     modifier = Modifier.size(16.dp),
                                     tint = Color.Gray
                                 )
                                 Spacer(modifier = Modifier.width(2.dp))
                                 Text(
                                     text = if (wordEntity_7ree.viewCount > 99) "99+" else "${wordEntity_7ree.viewCount}",
                                     style = MaterialTheme.typography.bodySmall,
                                     color = Color.Gray
                                 )
                             }
                            
                            // 拼写练习次数 - 固定宽度（如果大于0才显示）
                             if (wordEntity_7ree.spellingCount > 0) {
                                 Row(
                                     verticalAlignment = Alignment.CenterVertically,
                                     modifier = Modifier.width(60.dp)
                                 ) {
                                     Icon(
                                         imageVector = Icons.Filled.Keyboard,
                                         contentDescription = "拼写练习次数",
                                         modifier = Modifier.size(16.dp),
                                         tint = Color.Gray
                                     )
                                     Spacer(modifier = Modifier.width(2.dp))
                                     Text(
                                         text = if (wordEntity_7ree.spellingCount > 99) "99+" else "${wordEntity_7ree.spellingCount}",
                                         style = MaterialTheme.typography.bodySmall,
                                         color = Color.Gray
                                     )
                                 }
                             }
                        }
                    }

                    // 朗读按钮（喇叭图标）
                    IconButton(
                        onClick = { onWordSpeak_7ree(wordEntity_7ree.word) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "朗读单词",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    )
}







 

 