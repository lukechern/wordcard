package com.x7ree.wordcard.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.x7ree.wordcard.ui.components.FilterState_7ree

@Composable
fun FilterMenuManager_7ree(
    showFavoritesOnly_7ree: Boolean,
    isFilterMenuVisible: Boolean,
    onFilterMenuVisibilityChange: (Boolean) -> Unit,
    filterState: FilterState_7ree,
    onFilterStateChange: (FilterState_7ree) -> Unit
) {
    // 筛选菜单状态
    var isFilterMenuVisibleInternal by remember { mutableStateOf(isFilterMenuVisible) }
    var filterStateInternal by remember { 
        mutableStateOf(filterState)
    }
    
    // 同步收藏状态
    LaunchedEffect(showFavoritesOnly_7ree) {
        filterStateInternal = filterStateInternal.copy(showFavoritesOnly = showFavoritesOnly_7ree)
    }
    
    // 更新外部状态
    LaunchedEffect(isFilterMenuVisibleInternal) {
        onFilterMenuVisibilityChange(isFilterMenuVisibleInternal)
    }
    
    // 更新外部筛选状态
    LaunchedEffect(filterStateInternal) {
        onFilterStateChange(filterStateInternal)
    }
}
