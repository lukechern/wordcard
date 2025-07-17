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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.LocalSwipeState_7ree
import com.x7ree.wordcard.ui.ScrollIndicator_7ree
import com.x7ree.wordcard.ui.SpellingCard_7ree
import com.x7ree.wordcard.ui.SpellingPracticeDialog_7ree
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
    speak: (String, String) -> Unit,
    showSpellingDialog: Boolean,
    onShowSpellingDialogChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp), // 左右对称24.dp，上下保持16.dp
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
            if (wordQueryViewModel.currentWordInfo_7ree != null) {
                IconButton(
                    onClick = { wordQueryViewModel.toggleFavorite_7ree() }
                ) {
                    Icon(
                        imageVector = if (wordQueryViewModel.currentWordInfo_7ree!!.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (wordQueryViewModel.currentWordInfo_7ree!!.isFavorite) "取消收藏" else "收藏",
                        modifier = Modifier.size(24.dp),
                        tint = if (wordQueryViewModel.currentWordInfo_7ree!!.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
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
            modifier = Modifier.padding(bottom = 24.dp)
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
                        .padding(bottom = 16.dp) // 只保留底部间距，左右padding已在外层设置
                ) {
                    // 使用新的MarkdownRenderer_7ree组件来处理Markdown内容
                    MarkdownRenderer_7ree(
                        queryResult = wordQueryViewModel.queryResult_7ree,
                        onWordSpeak = { speak(wordQueryViewModel.getWordSpeechText_7ree(), "word") },
                        onExamplesSpeak = { speak(wordQueryViewModel.getExamplesSpeechText_7ree(), "examples") },
                        isSpeakingWord = wordQueryViewModel.isSpeakingWord_7ree,
                        isSpeakingExamples = wordQueryViewModel.isSpeakingExamples_7ree,
                        isTtsReady = wordQueryViewModel.isTtsReady_7ree
                    )
                    
                    // 添加3个并排的信息卡片（删除收藏卡片）
                    if (wordQueryViewModel.currentWordInfo_7ree != null) {
                        Spacer(modifier = Modifier.height(24.dp))
                        // 使用Box居中对齐，减少卡片总宽度15%
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(0.85f), // 减少15%宽度
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // 卡片1：初次查询时间
                                InfoCard_7ree(
                                    title = "初次查询",
                                    value = formatDate_7ree(wordQueryViewModel.currentWordInfo_7ree!!.queryTimestamp),
                                    icon = Icons.Filled.History,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                // 卡片2：查阅次数
                                InfoCard_7ree(
                                    title = "查阅次数",
                                    value = "查阅${wordQueryViewModel.currentWordInfo_7ree!!.viewCount}次",
                                    icon = Icons.Filled.Visibility,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                // 卡片3：拼写练习
                                SpellingCard_7ree(
                                    spellingCount = wordQueryViewModel.getCurrentSpellingCount_7ree(),
                                    onSpellingClick = { onShowSpellingDialogChange(true) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    
                        // 在底部添加滑动提示信息
                        if (wordQueryViewModel.canNavigate_7ree()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "上下滑动切换单词",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                // 添加自定义滚动指示器，定位在正文内容右侧
                ScrollIndicator_7ree(
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
        }
    }
    
    // 拼写练习对话框
    val chineseMeaning = if (wordQueryViewModel.queryResult_7ree.isNotBlank()) {
        // 直接使用MarkdownRenderer_7ree的解析结果获取中文意思
        MarkdownRenderer_7ree.parseMarkdownContent_7ree(wordQueryViewModel.queryResult_7ree).chineseMeaning.trim()
    } else {
        ""
    }
    Log.d(TAG_7ree, "拼写练习对话框 - 获取到的中文词义: '$chineseMeaning'")
    
    SpellingPracticeDialog_7ree(
        targetWord = wordQueryViewModel.wordInput_7ree,
        chineseMeaning = chineseMeaning,
        isVisible = showSpellingDialog,
        onDismiss = { onShowSpellingDialogChange(false) },
        onSpellingSuccess = {
            wordQueryViewModel.onSpellingSuccess_7ree()
        }
    )
}