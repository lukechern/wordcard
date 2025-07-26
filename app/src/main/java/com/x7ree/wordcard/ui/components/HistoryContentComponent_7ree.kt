package com.x7ree.wordcard.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.offset
import com.x7ree.wordcard.data.WordEntity_7ree
import com.x7ree.wordcard.ui.PaginatedWordList_7ree
import com.x7ree.wordcard.ui.components.TtsButtonState_7ree

@Composable
fun HistoryContentComponent_7ree(
    filteredWords_7ree: List<WordEntity_7ree>,
    isLoadingMore_7ree: Boolean,
    hasMoreData_7ree: Boolean,
    isRefreshing_7ree: Boolean,
    onWordClick: (String) -> Unit,
    onWordDelete: (WordEntity_7ree) -> Unit,
    onLoadMore: () -> Unit,
    onWordSpeak: (String) -> Unit,
    onWordStopSpeak: () -> Unit,
    onRefresh: (() -> Unit)?,
    ttsState: TtsButtonState_7ree,
    currentSpeakingWord: String,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        PaginatedWordList_7ree(
            words = filteredWords_7ree,
            isLoadingMore = isLoadingMore_7ree,
            hasMoreData = hasMoreData_7ree,
            onWordClick = onWordClick,
            onWordDelete = onWordDelete,
            onLoadMore = onLoadMore,
            onWordSpeak = onWordSpeak,
            onWordStopSpeak = onWordStopSpeak,
            ttsState = ttsState,
            currentSpeakingWord = currentSpeakingWord,
            listState = listState,
            isRefreshing = isRefreshing_7ree,
            onRefresh = onRefresh ?: {}
        )

        // 自定义滚动条 - 位于内容区域右侧，从标题栏下方开始
        if (filteredWords_7ree.isNotEmpty()) {
            CustomScrollbar_7ree(
                listState = listState,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .padding(top = 0.dp, bottom = 8.dp)
                    .offset(x = 8.dp)
            )
        }
    }
}
