package com.x7ree.wordcard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape

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
import com.x7ree.wordcard.ui.BottomNavigationBar_7ree // 导入新的组件

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    'pl_history_placeholder_7r' => '历史功能开发中...',
    'pl_settings_placeholder_7r' => '配置功能开发中...',
**/

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
    var currentScreen_7ree by remember { mutableStateOf(Screen_7ree.SEARCH) }
    var showSplash_7ree by remember { mutableStateOf(true) }
    val snackbarHostState_7ree = remember { SnackbarHostState() }
    val operationResult_7ree by wordQueryViewModel_7ree?.operationResult_7ree?.collectAsState() ?: mutableStateOf(null)

    // 监听操作结果，显示Snackbar
    LaunchedEffect(operationResult_7ree) {
        operationResult_7ree?.let { result ->
            snackbarHostState_7ree.showSnackbar(
                message = result,
                duration = androidx.compose.material3.SnackbarDuration.Short
            )
            // 清除操作结果
            wordQueryViewModel_7ree?.clearOperationResult_7ree()
        }
    }

    // 智能启动画面控制 - 并行执行，不增加总等待时间
    LaunchedEffect(isInitializationComplete_7ree) {
        if (isInitializationComplete_7ree) {
            // 如果初始化已完成，至少显示1秒启动画面给用户视觉反馈
            delay(1000)
            showSplash_7ree = false
        }
    }
    
    // 如果初始化时间过长，确保启动画面不会无限显示
    LaunchedEffect(Unit) {
        delay(5000) // 最多显示5秒启动画面
        if (showSplash_7ree) {
            showSplash_7ree = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState_7ree) },
        bottomBar = {
            // 只有在初始化完成且不在启动画面时才显示底部导航
            if (!showSplash_7ree && wordQueryViewModel_7ree != null) {
                BottomNavigationBar_7ree(
                    currentScreen_7ree = currentScreen_7ree,
                    onScreenSelected_7ree = { screen -> currentScreen_7ree = screen },
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
                                    currentScreen_7ree = Screen_7ree.SEARCH
                                }
                            )
                        }
                        Screen_7ree.SETTINGS -> {
                            SettingsScreen_7ree(
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
}

@Composable
fun HistoryScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onWordClick_7ree: (String) -> Unit
) {
    var historyWords_7ree by remember { mutableStateOf<List<WordEntity_7ree>>(emptyList()) }
    var deletedWords_7ree by remember { mutableStateOf<Set<String>>(emptySet()) }
    
    // 加载历史单词列表
    LaunchedEffect(Unit) {
        wordQueryViewModel_7ree.getHistoryWords_7ree().collect { words_7ree ->
            // 过滤掉已删除的单词，确保UI状态同步
            historyWords_7ree = words_7ree
                .filter { it.word !in deletedWords_7ree }
                .sortedByDescending { it.queryTimestamp }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "历史记录",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (historyWords_7ree.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无历史记录",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn {
                items(historyWords_7ree) { wordEntity_7ree ->
                    HistoryWordItem_7ree(
                        wordEntity_7ree = wordEntity_7ree,
                        onWordClick_7ree = onWordClick_7ree,
                        onFavoriteToggle_7ree = { entity ->
                            wordQueryViewModel_7ree.setFavorite_7ree(entity.word, !entity.isFavorite)
                        },
                        onDismiss_7ree = {
                            // 立即添加到删除集合，从UI中移除
                            deletedWords_7ree = deletedWords_7ree + wordEntity_7ree.word
                            // 然后执行实际的删除操作
                            wordQueryViewModel_7ree.deleteWord_7ree(wordEntity_7ree.word)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun HistoryWordItem_7ree(
    wordEntity_7ree: WordEntity_7ree,
    onWordClick_7ree: (String) -> Unit,
    onFavoriteToggle_7ree: (WordEntity_7ree) -> Unit,
    onDismiss_7ree: () -> Unit
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
                        Text(
                            text = wordEntity_7ree.word,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Visibility,
                                contentDescription = "浏览次数",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${wordEntity_7ree.viewCount}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = dateStr_7ree,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = { onFavoriteToggle_7ree(wordEntity_7ree) }
                    ) {
                        Icon(
                            imageVector = if (wordEntity_7ree.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (wordEntity_7ree.isFavorite) "取消收藏" else "收藏",
                            tint = if (wordEntity_7ree.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    )
}

enum class SettingsTab_7ree {
    API_CONFIG,
    PROMPT_CONFIG,
    DATA_MANAGEMENT
}

@Composable
fun SettingsScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onImportFile_7ree: () -> Unit = {}
) {
    var selectedTab_7ree by remember { mutableStateOf(SettingsTab_7ree.API_CONFIG) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "配置",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        TabRow(selectedTabIndex = selectedTab_7ree.ordinal) {
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.API_CONFIG,
                onClick = { selectedTab_7ree = SettingsTab_7ree.API_CONFIG },
                text = { Text("API配置") }
            )
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.PROMPT_CONFIG,
                onClick = { selectedTab_7ree = SettingsTab_7ree.PROMPT_CONFIG },
                text = { Text("提示词配置") }
            )
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.DATA_MANAGEMENT,
                onClick = { selectedTab_7ree = SettingsTab_7ree.DATA_MANAGEMENT },
                text = { Text("数据管理") }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (selectedTab_7ree) {
            SettingsTab_7ree.API_CONFIG -> {
                ApiConfigTab_7ree(wordQueryViewModel_7ree)
            }
            SettingsTab_7ree.PROMPT_CONFIG -> {
                PromptConfigTab_7ree(wordQueryViewModel_7ree)
            }
            SettingsTab_7ree.DATA_MANAGEMENT -> {
                DataManagementTab_7ree(
                    wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                    onImportFile_7ree = onImportFile_7ree
                )
            }
        }
    }
}

@Composable
fun ApiConfigTab_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree
) {
    val apiConfig_7ree by wordQueryViewModel_7ree.apiConfig_7ree.collectAsState()
    val operationResult_7ree by wordQueryViewModel_7ree.operationResult_7ree.collectAsState()
    
    var apiKey_7ree by remember { mutableStateOf(apiConfig_7ree.apiKey) }
    var apiUrl_7ree by remember { mutableStateOf(apiConfig_7ree.apiUrl) }
    var modelName_7ree by remember { mutableStateOf(apiConfig_7ree.modelName) }
    
    // 当配置更新时，同步到输入框
    LaunchedEffect(apiConfig_7ree) {
        apiKey_7ree = apiConfig_7ree.apiKey
        apiUrl_7ree = apiConfig_7ree.apiUrl
        modelName_7ree = apiConfig_7ree.modelName
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "API连接参数配置",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = apiUrl_7ree,
            onValueChange = { apiUrl_7ree = it },
            label = { Text("API URL") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true
        )
        
        OutlinedTextField(
            value = apiKey_7ree,
            onValueChange = { apiKey_7ree = it },
            label = { Text("API Key") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), // 确保有底部间距
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(), // 将API Key显示为星号
            textStyle = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 0.sp) // 调整字符间距，使其更密集
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = modelName_7ree,
            onValueChange = { modelName_7ree = it },
            label = { Text("模型名称") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )
        
        Button(
            onClick = {
                wordQueryViewModel_7ree.saveApiConfig_7ree(apiKey_7ree, apiUrl_7ree, modelName_7ree)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("保存配置")
        }
        
        // 操作结果通过Snackbar显示，这里不再显示文本
    }
}

@Composable
fun DataManagementTab_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onImportFile_7ree: () -> Unit = {}
) {
    val operationResult_7ree by wordQueryViewModel_7ree.operationResult_7ree.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "查询历史单词数据导出与导入",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "数据导出",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "将查询历史导出为JSON文件，包含单词、查询结果、时间戳等信息",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Button(
                    onClick = {
                        wordQueryViewModel_7ree.exportHistoryData_7ree()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = "导出",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("导出历史数据")
                }
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
            modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "数据导入",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "从JSON文件导入查询历史数据，支持批量恢复历史记录",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Button(
                    onClick = {
                        onImportFile_7ree()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Upload,
                        contentDescription = "导入",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("导入历史数据")
                }
            }
        }

        // 显示数据导出默认路径
        val exportPath_7ree by wordQueryViewModel_7ree.exportPath_7ree.collectAsState()
        Spacer(modifier = Modifier.height(16.dp)) // 在卡片和路径之间增加间距
        Text(
            text = "数据将默认导出到: \n$exportPath_7ree",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Start // 修改为左对齐
        )
        Spacer(modifier = Modifier.height(16.dp)) // 在路径和底部之间增加间距
        
        // 操作结果通过Snackbar显示，这里不再显示文本
    }
} 

@Composable
fun PromptConfigTab_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree
) {
    val promptConfig_7ree by wordQueryViewModel_7ree.promptConfig_7ree.collectAsState()
    val operationResult_7ree by wordQueryViewModel_7ree.operationResult_7ree.collectAsState()
    
    var queryPrompt_7ree by remember { mutableStateOf(promptConfig_7ree.queryPrompt_7ree) }
    var outputTemplate_7ree by remember { mutableStateOf(promptConfig_7ree.outputTemplate_7ree) }
    
    // 当配置更新时，同步到输入框
    LaunchedEffect(promptConfig_7ree) {
        queryPrompt_7ree = promptConfig_7ree.queryPrompt_7ree
        outputTemplate_7ree = promptConfig_7ree.outputTemplate_7ree
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "查询提示词和输出模板配置",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "查询提示词",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = queryPrompt_7ree,
            onValueChange = { queryPrompt_7ree = it },
            label = { Text("查询提示词") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 16.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
        )
        
        Text(
            text = "输出模板",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = outputTemplate_7ree,
            onValueChange = { outputTemplate_7ree = it },
            label = { Text("输出模板") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 16.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
        )
        
        Button(
            onClick = {
                wordQueryViewModel_7ree.savePromptConfig_7ree(queryPrompt_7ree, outputTemplate_7ree)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("保存配置")
        }
        
        // 操作结果通过Snackbar显示，这里不再显示文本
    }
} 