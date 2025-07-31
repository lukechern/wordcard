package com.x7ree.wordcard.article.utils

import com.x7ree.wordcard.R

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

import com.x7ree.wordcard.article.utils.SearchModeUI_7ree
import com.x7ree.wordcard.article.utils.TitleModeUI_7ree

/**
 * 文章搜索栏组件
 * 支持在标题栏和搜索框之间切换，专门用于文章搜索
 * UI样式与单词本搜索组件完全一致
 */
@Composable
fun ArticleSearchBarComponent_7ree(
    title: String,
    searchQuery: String,
    isSearchMode: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchModeToggle: (Boolean) -> Unit,
    onGenerateArticle: () -> Unit,
    onShowFilterMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    
    // 使用TextFieldValue来管理文本和光标位置
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = searchQuery, selection = TextRange(searchQuery.length)))
    }
    
    // 监听searchQuery变化，同步更新TextFieldValue
    LaunchedEffect(searchQuery) {
        if (textFieldValue.text != searchQuery) {
            textFieldValue = TextFieldValue(
                text = searchQuery,
                selection = TextRange(searchQuery.length)
            )
        }
    }
    
    // 焦点状态
    var isFocused by remember { mutableStateOf(false) }
    
    // 当进入搜索模式时自动获取焦点
    LaunchedEffect(isSearchMode) {
        if (isSearchMode) {
            focusRequester.requestFocus()
        }
    }
    
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp) // 固定标题栏高度，确保两种模式下高度一致
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSearchMode) {
                // 搜索模式：显示搜索框
                SearchModeUI_7ree(
                    textFieldValue = textFieldValue,
                    onTextFieldValueChange = { textFieldValue = it },
                    focusRequester = focusRequester,
                    isFocused = isFocused,
                    onFocusedChange = { isFocused = it },
                    onSearchQueryChange = onSearchQueryChange,
                    onSearchModeToggle = onSearchModeToggle
                )
            } else {
                // 标题模式：显示标题和操作按钮
                TitleModeUI_7ree(
                    title = title,
                    onGenerateArticle = onGenerateArticle,
                    onSearchModeToggle = onSearchModeToggle,
                    onShowFilterMenu = onShowFilterMenu
                )
            }
        }
    }
}
