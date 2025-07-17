package com.x7ree.wordcard.ui

import android.text.Html
import android.widget.TextView
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
// 移除不存在的滚动条导入
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import io.noties.markwon.Markwon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.PlayArrow // Import PlayArrow for play icon
import androidx.compose.material.icons.filled.Pause // Import Pause for pause icon
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.graphics.graphicsLayer
import android.util.Log
import java.util.regex.Pattern // Import Pattern for regex
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import androidx.compose.runtime.collectAsState // 导入 collectAsState
import com.x7ree.wordcard.ui.SwipeNavigationComponent_7ree // 导入滑动导航组件
import kotlinx.coroutines.delay // 导入delay函数
import com.x7ree.wordcard.R
import com.x7ree.wordcard.utils.MarkdownRenderer_7ree
import com.x7ree.wordcard.utils.DataStatistics_7ree
import androidx.compose.ui.platform.LocalContext
import com.x7ree.wordcard.utils.CacheManager_7ree
import kotlinx.coroutines.flow.first
import com.x7ree.wordcard.ui.SpellingCard_7ree
import com.x7ree.wordcard.ui.SpellingPracticeDialog_7ree
import com.x7ree.wordcard.ui.ScrollIndicator_7ree
import androidx.compose.foundation.layout.fillMaxHeight

// 信息卡片组件
@Composable
fun InfoCard_7ree(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .graphicsLayer {
                // 设置较低的z轴层级，避免遮挡手势检测
                shadowElevation = 0f
            },
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            // 添加额外的空间来匹配收藏卡片的高度
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// 收藏卡片组件
@Composable
fun FavoriteCard_7ree(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onToggle() }
            .graphicsLayer {
                // 设置较低的z轴层级，避免遮挡手势检测
                shadowElevation = 0f
            },
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isFavorite) "取消收藏" else "收藏",
                modifier = Modifier.size(24.dp),
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isFavorite) "已收藏" else "收藏",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            // 添加额外的空间来匹配其他卡片的高度
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// 格式化日期函数
private fun formatDate_7ree(timestamp: Long): String {
    val dateFormat_7ree = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    return dateFormat_7ree.format(Date(timestamp))
}



private const val TAG_7ree = "WordCardScreen_7ree"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordCardScreen_7ree(wordQueryViewModel_7ree: WordQueryViewModel_7ree, speak_7ree: (String, String) -> Unit, stopSpeaking_7ree: () -> Unit) {
    
    // 用于显示输入提示的状态
    var showInputWarning_7ree by remember { mutableStateOf(false) }
    
    // 拼写练习对话框状态
    var showSpellingDialog_7ree by remember { mutableStateOf(false) }
    
    // 自动隐藏提示的效果
    LaunchedEffect(showInputWarning_7ree) {
        if (showInputWarning_7ree) {
            delay(2000) // 2秒后自动隐藏
            showInputWarning_7ree = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(animationSpec = tween(durationMillis = 300)),
        contentAlignment = Alignment.Center
    ) {
        // 如果正在加载且没有查询结果，显示加载动画和正在查询的单词
        if (wordQueryViewModel_7ree.isLoading_7ree && wordQueryViewModel_7ree.queryResult_7ree.isBlank()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 显示正在查询的单词
                Text(
                    text = wordQueryViewModel_7ree.wordInput_7ree,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "查询中...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else if (!wordQueryViewModel_7ree.isWordConfirmed_7ree || wordQueryViewModel_7ree.queryResult_7ree.isBlank()) {
            // 未开始查询时，标题、输入框、按钮向上移动15%的距离，但要为图标留出空间
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = (-0.15f * 100 + 50).dp) // 调整偏移量
            ) {
                // App图标
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.wordcardicon),
                    contentDescription = "App图标",
                    contentScale = ContentScale.Fit,
                    alignment = androidx.compose.ui.Alignment.Center,
                    modifier = Modifier
                        .size(90.dp)
                        .padding(bottom = 16.dp)
                )
                
                // 标题
                Text(
                    text = "AI查单词",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                
                // 输入框 - 统一设计风格，限制只能输入英文字母
                OutlinedTextField(
                    value = wordQueryViewModel_7ree.wordInput_7ree,
                    onValueChange = { newValue ->
                        // 检查是否包含非英文字符
                        val hasInvalidChars = newValue.any { !it.isLetter() || (it !in 'a'..'z' && it !in 'A'..'Z') }
                        if (hasInvalidChars) {
                            showInputWarning_7ree = true
                        }
                        
                        // 过滤输入，只允许英文字母
                        val filteredValue = newValue.filter { it.isLetter() && (it in 'a'..'z' || it in 'A'..'Z') }
                        wordQueryViewModel_7ree.onWordInputChanged_7ree(filteredValue)
                    },
                    label = { Text("请输入英文单词") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(64.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            // 点击回车时直接提交查询
                            if (wordQueryViewModel_7ree.wordInput_7ree.length >= 3) {
                                wordQueryViewModel_7ree.queryWord_7ree()
                             }
                         }
                    )
                )
                
                // 输入提示条
                if (showInputWarning_7ree) {
                    Text(
                        text = "⚠️ 只能输入英文字母",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 查询按钮 - 统一设计风格，添加放大镜图标
                Button(
                    onClick = { wordQueryViewModel_7ree.queryWord_7ree() },
                    enabled = !wordQueryViewModel_7ree.isLoading_7ree && wordQueryViewModel_7ree.wordInput_7ree.length >= 3,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 2.dp,
                        disabledElevation = 0.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "查询",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "开始查",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // 统计数据 - 使用缓存机制
                val context = LocalContext.current
                val cacheManager_7ree = remember { CacheManager_7ree(context) }
                var cachedStats_7ree by remember { mutableStateOf(DataStatistics_7ree.StatisticsData_7ree(0, 0, 0, 0, 0.0f, 0.0f, 0, 0.0f, 0.0f)) }
                
                // 加载缓存的统计数据
                LaunchedEffect(Unit) {
                    // 延迟加载统计数据，不阻塞UI显示
                    delay(100) // 短暂延迟，让UI先渲染
                    
                    // 检查是否需要更新缓存
                    if (cacheManager_7ree.shouldUpdateCache_7ree()) {
                        // 需要更新缓存时，获取最新数据
                        val allWords_7ree = wordQueryViewModel_7ree.getHistoryWords_7ree().first()
                        cachedStats_7ree = DataStatistics_7ree.calculateStatistics_7ree(allWords_7ree)
                        cacheManager_7ree.updateCacheTimestamp_7ree()
                    } else {
                        // 使用缓存数据，快速获取一次性数据
                        val allWords_7ree = wordQueryViewModel_7ree.getHistoryWords_7ree().first()
                        cachedStats_7ree = DataStatistics_7ree.calculateStatistics_7ree(allWords_7ree)
                    }
                }
                Spacer(modifier = Modifier.height(112.dp)) // 在按钮和统计数据之间增加一些间距
                Text(
                    text = "已收集${cachedStats_7ree.totalWords}个单词",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp)) // 两个统计数据之间的间距
                Text(
                    text = "已累计查阅${cachedStats_7ree.totalViews}次",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp)) // 两个统计数据之间的间距
                Text(
                    text = "已持续学习${cachedStats_7ree.studyDays}天",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp)) // 在统计数据和底部之间增加一些间距
            }
        } else {
            // 有查询结果时，显示完整内容
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), // 将内边距移到这里
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 标题栏：左边"单词卡片"，右边收藏桃心图标
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "单词卡片",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // 右上角收藏桃心图标
                    if (wordQueryViewModel_7ree.currentWordInfo_7ree != null) {
                        IconButton(
                            onClick = { wordQueryViewModel_7ree.toggleFavorite_7ree() }
                        ) {
                            Icon(
                                imageVector = if (wordQueryViewModel_7ree.currentWordInfo_7ree!!.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (wordQueryViewModel_7ree.currentWordInfo_7ree!!.isFavorite) "取消收藏" else "收藏",
                                modifier = Modifier.size(24.dp),
                                tint = if (wordQueryViewModel_7ree.currentWordInfo_7ree!!.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // 查询结果详情页顶部单词标题
                Text(
                    text = if (wordQueryViewModel_7ree.wordInput_7ree.isNotBlank()) wordQueryViewModel_7ree.wordInput_7ree else "AI查单词",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize * 1.4f // 放大40%（原来是1.3f，现在增加1号）
                    ),
                    fontWeight = FontWeight.ExtraBold, // 从Bold改为ExtraBold，更加粗
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                if (wordQueryViewModel_7ree.queryResult_7ree.isNotBlank()) {
                    // 添加状态变量来跟踪导航是否可用
                    var canNavigate_7ree by remember { mutableStateOf(false) }
                    
                    // 使用LaunchedEffect在单词详情页面显示后短暂延迟，然后评估canNavigate状态
                    LaunchedEffect(wordQueryViewModel_7ree.wordInput_7ree) {
                        // 给单词列表加载留出时间
                        delay(300)
                        canNavigate_7ree = wordQueryViewModel_7ree.canNavigate_7ree()
                    }
                    
                    // 创建滚动状态
                    val scrollState_7ree = rememberScrollState()
                    
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(
                                    state = scrollState_7ree,
                                    enabled = true // 确保滚动功能正常
                                )
                                .padding(bottom = 16.dp) // 添加底部间距
                        ) {
                            // 使用新的MarkdownRenderer_7ree组件来处理Markdown内容
                            MarkdownRenderer_7ree(
                                queryResult = wordQueryViewModel_7ree.queryResult_7ree,
                                onWordSpeak = { speak_7ree(wordQueryViewModel_7ree.getWordSpeechText_7ree(), "word") },
                                onExamplesSpeak = { speak_7ree(wordQueryViewModel_7ree.getExamplesSpeechText_7ree(), "examples") },
                                isSpeakingWord = wordQueryViewModel_7ree.isSpeakingWord_7ree,
                                isSpeakingExamples = wordQueryViewModel_7ree.isSpeakingExamples_7ree,
                                isTtsReady = wordQueryViewModel_7ree.isTtsReady_7ree
                            )
                            
                            // 添加3个并排的信息卡片（删除收藏卡片）
                            if (wordQueryViewModel_7ree.currentWordInfo_7ree != null) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    // 卡片1：初次查询时间
                                    InfoCard_7ree(
                                        title = "初次查询",
                                        value = formatDate_7ree(wordQueryViewModel_7ree.currentWordInfo_7ree!!.queryTimestamp),
                                        icon = Icons.Filled.History,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    // 卡片2：查阅次数
                                    InfoCard_7ree(
                                        title = "查阅次数",
                                        value = "查阅${wordQueryViewModel_7ree.currentWordInfo_7ree!!.viewCount}次",
                                        icon = Icons.Filled.Visibility,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    // 卡片3：拼写练习
                                    SpellingCard_7ree(
                                        spellingCount = wordQueryViewModel_7ree.getCurrentSpellingCount_7ree(),
                                        onSpellingClick = { showSpellingDialog_7ree = true },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            
                                // 在底部添加滑动提示信息
                                if (wordQueryViewModel_7ree.canNavigate_7ree()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "上下滑动切换单词",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                        
                        // 添加自定义滚动指示器
                        ScrollIndicator_7ree(
                            scrollState = scrollState_7ree,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight()
                        )
                        
                        // 添加滑动导航组件作为覆盖层，对齐到左侧
                        Box(
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            SwipeNavigationComponent_7ree(
                                canNavigate = canNavigate_7ree,
                                onNavigateToPrevious = { wordQueryViewModel_7ree.navigateToPreviousWord_7ree() },
                                onNavigateToNext = { wordQueryViewModel_7ree.navigateToNextWord_7ree() }
                            )
                        }
                    }
                }
            }
        }
        }
        
        // 拼写练习对话框
        val chineseMeaning = if (wordQueryViewModel_7ree.queryResult_7ree.isNotBlank()) {
            // 直接使用MarkdownRenderer_7ree的解析结果获取中文意思
            com.x7ree.wordcard.utils.MarkdownRenderer_7ree.parseMarkdownContent_7ree(wordQueryViewModel_7ree.queryResult_7ree).chineseMeaning.trim()
        } else {
            ""
        }
        Log.d(TAG_7ree, "拼写练习对话框 - 获取到的中文词义: '$chineseMeaning'")
        
        SpellingPracticeDialog_7ree(
            targetWord = wordQueryViewModel_7ree.wordInput_7ree,
            chineseMeaning = chineseMeaning,
            isVisible = showSpellingDialog_7ree,
            onDismiss = { showSpellingDialog_7ree = false },
            onSpellingSuccess = {
                wordQueryViewModel_7ree.onSpellingSuccess_7ree()
            }
        )
    }