package com.x7ree.wordcard.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.R
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.LocalSwipeState_7ree
import com.x7ree.wordcard.ui.SpellingCard_7ree
import com.x7ree.wordcard.ui.WordCardScrollIndicator_7ree
import com.x7ree.wordcard.ui.SpellingPractice.SpellingPracticeDialog_7ree
import com.x7ree.wordcard.ui.SwipeArrowIndicator_7ree
import com.x7ree.wordcard.ui.SwipeDirection_7ree
import com.x7ree.wordcard.ui.SwipeNavigationComponent_7ree
import com.x7ree.wordcard.ui.SwipeState_7ree
import com.x7ree.wordcard.utils.MarkdownRenderer_7ree
import kotlinx.coroutines.delay

private const val TAG_7ree = "WordResultComponent_7ree"

// 查询结果显示组件
@Composable
fun WordResultComponent_7ree(
    wordQueryViewModel: WordQueryViewModel_7ree,
    showSpellingDialog: Boolean,
    onShowSpellingDialogChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // 获取通用配置
    val generalConfig_7ree by wordQueryViewModel.generalConfig_7ree.collectAsState()
    
    // 监听当前单词信息的变化，确保收藏状态能够及时更新UI
    val currentWordInfo by remember { 
        derivedStateOf { wordQueryViewModel.currentWordInfo_7ree }
    }
    
    // 在组件显示时刷新单词信息，确保引用次数是最新的
    LaunchedEffect(wordQueryViewModel.wordInput_7ree) {
        if (wordQueryViewModel.wordInput_7ree.isNotBlank()) {
            wordQueryViewModel.refreshCurrentWordInfo_7ree()
        }
    }
    
    // 添加调试日志来跟踪收藏状态变化
    LaunchedEffect(currentWordInfo?.isFavorite) {
        currentWordInfo?.let { _ ->
            // Log.d(TAG_7ree, "当前单词收藏状态: ${wordInfo.word} -> ${wordInfo.isFavorite}")
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 2.dp, vertical = 16.dp), // 左右对称2.dp，上下保持16.dp
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 标题栏：左边"单词卡片"，右边收藏桃心图标
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp) // 调整标题栏高度
                .padding(horizontal = 0.dp, vertical = 0.dp)
                .padding(bottom = 8.dp), // 添加底部边距，与单词本列表页保持一致
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "单词卡片",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            // 右上角收藏桃心图标
            if (wordQueryViewModel.currentWordInfo_7ree != null) {
                val wordInfo = wordQueryViewModel.currentWordInfo_7ree!!
                IconButton(
                    onClick = { 
                        // Log.d(TAG_7ree, "收藏按钮被点击，当前状态: ${wordInfo.isFavorite}")
                        wordQueryViewModel.toggleFavorite_7ree() 
                    }
                ) {
                    Icon(
                        imageVector = if (wordInfo.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (wordInfo.isFavorite) "取消收藏" else "收藏",
                        modifier = Modifier.size(24.dp),
                        tint = if (wordInfo.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // 查询结果详情页顶部单词标题
        Text(
            text = if (wordQueryViewModel.wordInput_7ree.isNotBlank()) wordQueryViewModel.wordInput_7ree else "AI查单词",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = MaterialTheme.typography.headlineMedium.fontSize * 1.4f // 放大40%（原来是1.3f，现在增加1号）
            ),
            fontWeight = FontWeight.ExtraBold, // 从Bold改为ExtraBold，更加粗
            modifier = Modifier.padding(bottom = 16.dp) // 减少底部边距
        )

        if (wordQueryViewModel.queryResult_7ree.isNotBlank()) {
            // 添加状态变量来跟踪导航是否可用
            var canNavigate_7ree by remember { mutableStateOf(false) }
            
            // 滑动状态管理
            var swipeState_7ree by remember { mutableStateOf(SwipeState_7ree()) }
            
            // 使用LaunchedEffect在单词详情页面显示后短暂延迟，然后评估canNavigate状态
            LaunchedEffect(wordQueryViewModel.wordInput_7ree) {
                // 给单词列表加载留出时间
                delay(300)
                canNavigate_7ree = wordQueryViewModel.canNavigate_7ree()
            }
            
            // 自动朗读功能 - 当单词查询完成且配置启用时自动朗读
            LaunchedEffect(wordQueryViewModel.queryResult_7ree, generalConfig_7ree.autoReadAfterQuery) {
                if (generalConfig_7ree.autoReadAfterQuery && 
                    wordQueryViewModel.queryResult_7ree.isNotBlank() && 
                    wordQueryViewModel.wordInput_7ree.isNotBlank() &&
                    wordQueryViewModel.isTtsReady_7ree) {
                    // 延迟一小段时间确保页面完全加载
                    delay(500)
                    wordQueryViewModel.speakWord_7ree(wordQueryViewModel.getWordSpeechText_7ree())
                }
            }
            
            // 创建滚动状态
            val scrollState_7ree = rememberScrollState()
            
            // 使用Column布局，将内容分为可滚动的正文部分和固定的底部卡片部分
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 可滚动的正文内容区域
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // 占据剩余空间
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(
                                state = scrollState_7ree,
                                enabled = true // 确保滚动功能正常
                            )
                            .padding(bottom = 16.dp) // 只保留底部间距
                    ) {
                        // TTS状态管理 - 独立管理音标和例句按钮状态
                        val isSpeakingWord_7ree = wordQueryViewModel.isSpeakingWord_7ree
                        val isSpeakingExamples_7ree = wordQueryViewModel.isSpeakingExamples_7ree
                        
                        // 独立的状态管理
                        var wordTtsState_7ree by remember { mutableStateOf(com.x7ree.wordcard.ui.components.TtsButtonState_7ree.IDLE) }
                        var examplesTtsState_7ree by remember { mutableStateOf(com.x7ree.wordcard.ui.components.TtsButtonState_7ree.IDLE) }
                        
                        // 获取协程作用域
                        val coroutineScope = rememberCoroutineScope()
                        
                        // 调试日志
                        // android.util.Log.d("TTS_EXAMPLES_DEBUG", "当前状态 - isSpeaking: $isSpeaking_7ree, isSpeakingWord: $isSpeakingWord_7ree, isSpeakingExamples: $isSpeakingExamples_7ree")
                        // android.util.Log.d("TTS_EXAMPLES_DEBUG", "当前按钮状态 - wordTtsState: $wordTtsState_7ree, examplesTtsState: $examplesTtsState_7ree")
                        
                        // 监听音标朗读状态变化
                        LaunchedEffect(isSpeakingWord_7ree) {
                            if (isSpeakingWord_7ree) {
                                // 当开始播放时，如果当前是LOADING状态，延迟一下再切换到PLAYING，让用户看到加载动画
                                if (wordTtsState_7ree == com.x7ree.wordcard.ui.components.TtsButtonState_7ree.LOADING) {
                                    delay(500) // 延迟500ms让用户看到加载动画
                                }
                                wordTtsState_7ree = com.x7ree.wordcard.ui.components.TtsButtonState_7ree.PLAYING
                            } else {
                                wordTtsState_7ree = com.x7ree.wordcard.ui.components.TtsButtonState_7ree.IDLE
                            }
                        }
                        
                        // 监听例句朗读状态变化
                        LaunchedEffect(isSpeakingExamples_7ree) {
                            if (isSpeakingExamples_7ree) {
                                // 当开始播放时，如果当前是LOADING状态，延迟一下再切换到PLAYING，让用户看到加载动画
                                if (examplesTtsState_7ree == com.x7ree.wordcard.ui.components.TtsButtonState_7ree.LOADING) {
                                    delay(500) // 延迟500ms让用户看到加载动画
                                }
                                examplesTtsState_7ree = com.x7ree.wordcard.ui.components.TtsButtonState_7ree.PLAYING
                            } else {
                                examplesTtsState_7ree = com.x7ree.wordcard.ui.components.TtsButtonState_7ree.IDLE
                            }
                        }
                        
                        // 使用新的MarkdownRenderer_7ree组件来处理Markdown内容
                        MarkdownRenderer_7ree(
                            queryResult = wordQueryViewModel.queryResult_7ree,
                            onWordSpeak = { 
                                // 设置音标按钮为加载状态
                                // android.util.Log.d("TTS_WORD_DEBUG", "点击音标朗读，设置为LOADING状态")
                                wordTtsState_7ree = com.x7ree.wordcard.ui.components.TtsButtonState_7ree.LOADING
                                
                                // 启动超时处理协程
                                coroutineScope.launch {
                                    delay(5000) // 5秒超时
                                    if (wordTtsState_7ree == com.x7ree.wordcard.ui.components.TtsButtonState_7ree.LOADING) {
                                        // android.util.Log.d("TTS_WORD_DEBUG", "音标朗读超时，切换回IDLE状态")
                                        wordTtsState_7ree = com.x7ree.wordcard.ui.components.TtsButtonState_7ree.IDLE
                                    }
                                }
                                
                                // 直接调用单词朗读方法，状态变化由LaunchedEffect监听处理
                                wordQueryViewModel.speakWord_7ree(wordQueryViewModel.wordInput_7ree) 
                            },
                            onExamplesSpeak = { 
                                // 设置例句按钮为加载状态
                                // android.util.Log.d("TTS_EXAMPLES_DEBUG", "点击例句朗读，设置为LOADING状态")
                                examplesTtsState_7ree = com.x7ree.wordcard.ui.components.TtsButtonState_7ree.LOADING
                                
                                // 启动超时处理协程
                                coroutineScope.launch {
                                    delay(5000) // 5秒超时
                                    if (examplesTtsState_7ree == com.x7ree.wordcard.ui.components.TtsButtonState_7ree.LOADING) {
                                        // android.util.Log.d("TTS_EXAMPLES_DEBUG", "例句朗读超时，切换回IDLE状态")
                                        examplesTtsState_7ree = com.x7ree.wordcard.ui.components.TtsButtonState_7ree.IDLE
                                    }
                                }
                                
                                // 直接调用例句朗读方法，状态变化由LaunchedEffect监听处理
                                wordQueryViewModel.speakExamples_7ree() 
                            },
                            onWordStopSpeak = { 
                                // android.util.Log.d("TTS_WORD_DEBUG", "手动停止音标朗读，设置为IDLE状态")
                                wordQueryViewModel.stopSpeaking_7ree()
                                wordTtsState_7ree = com.x7ree.wordcard.ui.components.TtsButtonState_7ree.IDLE
                            },
                            onExamplesStopSpeak = { 
                                // android.util.Log.d("TTS_EXAMPLES_DEBUG", "手动停止例句朗读，设置为IDLE状态")
                                wordQueryViewModel.stopSpeaking_7ree()
                                examplesTtsState_7ree = com.x7ree.wordcard.ui.components.TtsButtonState_7ree.IDLE
                            },
                            wordTtsState = wordTtsState_7ree,
                            examplesTtsState = examplesTtsState_7ree
                        )
                    }
                    
                    // 添加自定义滚动指示器，定位在正文内容右侧
                    WordCardScrollIndicator_7ree(
                        scrollState = scrollState_7ree,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .padding(end = 0.dp) // 紧贴屏幕右边缘
                    )
                    
                    // 使用CompositionLocalProvider提供滑动状态
                    CompositionLocalProvider(LocalSwipeState_7ree provides swipeState_7ree) {
                        // 添加滑动导航组件作为覆盖层，对齐到左侧
                        Box(
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            SwipeNavigationComponent_7ree(
                                canNavigate = canNavigate_7ree,
                                onNavigateToPrevious = { wordQueryViewModel.navigateToPreviousWord_7ree() },
                                onNavigateToNext = { wordQueryViewModel.navigateToNextWord_7ree() },
                                onSwipeStateChange = { isSwipping, direction, distance ->
                                    swipeState_7ree = SwipeState_7ree(
                                        showSwipeFeedback = isSwipping,
                                        swipeDirection = when (direction) {
                                            "up" -> SwipeDirection_7ree.UP
                                            "down" -> SwipeDirection_7ree.DOWN
                                            else -> SwipeDirection_7ree.NONE
                                        },
                                        totalDragDistance = distance
                                    )
                                }
                            )
                        }
                        
                        // 添加箭头指示器覆盖层，覆盖整个屏幕宽度，箭头显示在屏幕中心
                        SwipeArrowIndicator_7ree(
                            canNavigate = canNavigate_7ree,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                
                // 固定在底部的3个信息卡片，不参与滚动
                if (wordQueryViewModel.currentWordInfo_7ree != null) {
                    // 使用Box居中对齐，减少卡片总宽度15%
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // 卡片1：初次查询时间
                            InfoCard_7ree(
                                title = "初次查询",
                                value = formatDate_7ree(wordQueryViewModel.currentWordInfo_7ree!!.queryTimestamp),
                                icon = Icons.Filled.History,
                                modifier = Modifier.weight(1f)
                            )
                            
                            Spacer(modifier = Modifier.width(6.dp)) // 减少间距以适应4个卡片
                            
                            // 卡片2：查阅次数
                            InfoCard_7ree(
                                title = "查阅次数",
                                value = "查阅${wordQueryViewModel.currentWordInfo_7ree!!.viewCount}次",
                                icon = Icons.Filled.Visibility,
                                modifier = Modifier.weight(1f)
                            )
                            
                            Spacer(modifier = Modifier.width(6.dp))
                            
// 卡片3：引用次数 - 添加点击事件跳转到文章页面
                            InfoCard_7ree(
                                title = "引用次数",
                                value = "引用${wordQueryViewModel.currentWordInfo_7ree!!.referenceCount}次",
                                drawableRes = R.drawable.ic_article_custom_7ree,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    // 点击引用次数卡片，跳转到文章页面并搜索当前单词
                                    wordQueryViewModel.navigateToArticleAndSearch_7ree()
                                }
                            )
                            
                            Spacer(modifier = Modifier.width(6.dp))
                            
                            // 卡片4：拼写练习
                            SpellingCard_7ree(
                                spellingCount = wordQueryViewModel.getCurrentSpellingCount_7ree(),
                                onSpellingClick = { onShowSpellingDialogChange(true) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    // 在底部添加滑动提示信息
                     if (wordQueryViewModel.canNavigate_7ree()) {
                         Text(
                             text = "上下滑动切换单词",
                             style = MaterialTheme.typography.bodySmall.copy(
                                 lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 0.8f // 减少行高
                             ),
                             color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), // 更灰一些
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .padding(bottom = 4.dp), // 从8.dp减少到4.dp
                             textAlign = TextAlign.Center
                         )
                     }
                }
            }
        }
    }
    
    // 拼写练习对话框
    val chineseMeaning = if (wordQueryViewModel.queryResult_7ree.isNotBlank()) {
        // 直接使用MarkdownRenderer_7ree的解析结果获取中文意思
        MarkdownRenderer_7ree.parseMarkdownContent_7ree(wordQueryViewModel.queryResult_7ree).chineseMeaning.trim()
    } else {
        ""
    }
    // Log.d(TAG_7ree, "拼写练习对话框 - 获取到的中文词义: '$chineseMeaning'")
    
    SpellingPracticeDialog_7ree(
        targetWord = wordQueryViewModel.wordInput_7ree,
        chineseMeaning = chineseMeaning,
        wordQueryViewModel_7ree = wordQueryViewModel,
        isVisible = showSpellingDialog,
        onDismiss = { onShowSpellingDialogChange(false) },
        onSpellingSuccess = {
            wordQueryViewModel.onSpellingSuccess_7ree()
        }
    )
}
