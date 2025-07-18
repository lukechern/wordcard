package com.x7ree.wordcard.ui.SpellingPractice

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.x7ree.wordcard.query.WordQueryViewModel_7ree

/**
 * 拼写练习对话框组件
 * 用于包装拼写练习内容的对话框容器
 */
@Composable
fun SpellingPracticeDialog_7ree(
    targetWord: String,
    chineseMeaning: String,
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSpellingSuccess: () -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,  // 关闭点击外部关闭，由内容组件自己处理
                usePlatformDefaultWidth = false  // 允许Dialog占满屏幕宽度
            )
        ) {
            SpellingPracticeContent_7ree(
                targetWord = targetWord,
                chineseMeaning = chineseMeaning,
                wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                onDismiss = onDismiss,
                onSpellingSuccess = onSpellingSuccess
            )
        }
    }
}