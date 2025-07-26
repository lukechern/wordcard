package com.x7ree.wordcard.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.utils.CustomKeyboard.rememberCustomKeyboardState_7ree

@Composable
fun CustomKeyboardManager_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    isSearchMode_7ree: Boolean,
    showCustomKeyboard_7ree: Boolean,
    searchQuery_7ree: String,
    onShowCustomKeyboardChange: (Boolean) -> Unit,
    onUseCustomKeyboardChange: (Boolean) -> Unit,
    onCustomKeyboardStateChange: (com.x7ree.wordcard.utils.CustomKeyboard.CustomKeyboardState_7ree) -> Unit
) {
    // 自定义键盘状态
    val customKeyboardState_7ree = rememberCustomKeyboardState_7ree()
    val generalConfig_7ree by wordQueryViewModel_7ree.generalConfig_7ree.collectAsState()
    val useCustomKeyboard = generalConfig_7ree.keyboardType == "custom"
    var showCustomKeyboardInternal by remember { mutableStateOf(showCustomKeyboard_7ree) }
    
    // 监听搜索模式变化
    LaunchedEffect(isSearchMode_7ree) {
        if (!isSearchMode_7ree && useCustomKeyboard) {
            showCustomKeyboardInternal = false
            customKeyboardState_7ree.hide_7ree()
        }
    }
    
    // 更新外部状态
    LaunchedEffect(showCustomKeyboardInternal) {
        onShowCustomKeyboardChange(showCustomKeyboardInternal)
    }
    
    // 更新外部使用自定义键盘状态
    LaunchedEffect(useCustomKeyboard) {
        onUseCustomKeyboardChange(useCustomKeyboard)
    }
    
    // 更新外部自定义键盘状态
    LaunchedEffect(customKeyboardState_7ree) {
        onCustomKeyboardStateChange(customKeyboardState_7ree)
    }
}
