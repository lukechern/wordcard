package com.x7ree.wordcard.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.components.LoadingComponent_7ree
import com.x7ree.wordcard.ui.components.WordInputComponent_7ree
import com.x7ree.wordcard.ui.components.WordResultComponent_7ree
import kotlinx.coroutines.delay

private const val TAG_7ree = "WordCardScreen_7ree"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordCardScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree, 
    speak_7ree: (String, String) -> Unit, 
    stopSpeaking_7ree: () -> Unit
) {
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
        when {
            // 如果正在加载且没有查询结果，显示加载动画
            wordQueryViewModel_7ree.isLoading_7ree && wordQueryViewModel_7ree.queryResult_7ree.isBlank() -> {
                LoadingComponent_7ree(
                    wordInput = wordQueryViewModel_7ree.wordInput_7ree
                )
            }
            // 未开始查询时，显示输入界面
            !wordQueryViewModel_7ree.isWordConfirmed_7ree || wordQueryViewModel_7ree.queryResult_7ree.isBlank() -> {
                WordInputComponent_7ree(
                    wordQueryViewModel = wordQueryViewModel_7ree,
                    showInputWarning = showInputWarning_7ree,
                    onInputWarningChange = { showInputWarning_7ree = it }
                )
            }
            // 有查询结果时，显示结果界面
            else -> {
                WordResultComponent_7ree(
                    wordQueryViewModel = wordQueryViewModel_7ree,
                    speak = speak_7ree,
                    showSpellingDialog = showSpellingDialog_7ree,
                    onShowSpellingDialogChange = { showSpellingDialog_7ree = it }
                )
            }
        }
    }
}