package com.x7ree.wordcard.ui.SpellingPractice

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * 拼写练习对话框组件
 * 用于包装拼写练习内容的对话框容器
 */
@Composable
fun SpellingPracticeDialog_7ree(
    targetWord: String,
    chineseMeaning: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSpellingSuccess: () -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            SpellingPracticeContent_7ree(
                targetWord = targetWord,
                chineseMeaning = chineseMeaning,
                onDismiss = onDismiss,
                onSpellingSuccess = onSpellingSuccess
            )
        }
    }
}