package com.x7ree.wordcard.ui

import com.x7ree.wordcard.data.WordEntity_7ree
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.x7ree.wordcard.ui.DashBoard.components.MonthlyChartComponent.MonthlyChartComponent_7ree as MonthlyChartComponentImpl

@Composable
fun MonthlyChartComponent_7ree(
    words_7ree: List<WordEntity_7ree>,
    articles_7ree: List<com.x7ree.wordcard.data.ArticleEntity_7ree>,
    modifier: Modifier = Modifier
) {
    MonthlyChartComponentImpl(
        words_7ree = words_7ree,
        articles_7ree = articles_7ree,
        modifier = modifier
    )
}
