package com.x7ree.wordcard.ui

// 重新导出重构后的组件，保持向后兼容性
import com.x7ree.wordcard.ui.MainScreen.MainScreen_7ree as MainScreenImpl
import com.x7ree.wordcard.ui.MainScreen.Screen_7ree as ScreenImpl
import com.x7ree.wordcard.ui.MainScreen.CustomToast_7ree as CustomToastImpl
import com.x7ree.wordcard.ui.MainScreen.HistoryScreen_7ree as HistoryScreenImpl
import com.x7ree.wordcard.ui.MainScreen.HistoryWordItem_7ree as HistoryWordItemImpl

import androidx.compose.runtime.Composable
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.data.WordEntity_7ree

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

// 重新导出类型别名，保持向后兼容性
typealias Screen_7ree = ScreenImpl

// 重新导出组件函数，保持向后兼容性
@Composable
fun MainScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree?,
    isInitializationComplete_7ree: Boolean = false,
    onImportFile_7ree: () -> Unit = {}
) = MainScreenImpl(
    wordQueryViewModel_7ree = wordQueryViewModel_7ree,
    isInitializationComplete_7ree = isInitializationComplete_7ree,
    onImportFile_7ree = onImportFile_7ree
)

@Composable
fun CustomToast_7ree(
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit
) = CustomToastImpl(
    message = message,
    isVisible = isVisible,
    onDismiss = onDismiss
)

@Composable
fun HistoryScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onWordClick_7ree: (String) -> Unit
) = HistoryScreenImpl(
    wordQueryViewModel_7ree = wordQueryViewModel_7ree,
    onWordClick_7ree = onWordClick_7ree
)

@Composable
fun HistoryWordItem_7ree(
    wordEntity_7ree: WordEntity_7ree,
    onWordClick_7ree: (String) -> Unit,
    onDismiss_7ree: () -> Unit,
    onWordSpeak_7ree: (String) -> Unit = {}
) = HistoryWordItemImpl(
    wordEntity_7ree = wordEntity_7ree,
    onWordClick_7ree = onWordClick_7ree,
    onDismiss_7ree = onDismiss_7ree,
    onWordSpeak_7ree = onWordSpeak_7ree
)
