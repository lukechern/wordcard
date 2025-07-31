package com.x7ree.wordcard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.data.ArticleEntity_7ree
import com.x7ree.wordcard.article.ArticleDetailScreen.ArticleDetailAppBar_7ree
import com.x7ree.wordcard.article.ArticleDetailScreen.ArticleTitleCard_7ree
import com.x7ree.wordcard.article.ArticleDetailScreen.ArticleContentTabs_7ree
import com.x7ree.wordcard.article.ArticleDetailScreen.ArticleKeywordsCard_7ree
import com.x7ree.wordcard.article.ArticleDetailScreen.RelatedArticlesCard_7ree
import com.x7ree.wordcard.article.ArticleDetailScreen.filterMarkdownStars
import com.x7ree.wordcard.article.ArticleDetailScreen.formatTimestamp
import com.x7ree.wordcard.article.utils.ArticleEdgeSwipeComponent_7ree
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen_7ree(
    article: ArticleEntity_7ree,
    relatedArticles: List<ArticleEntity_7ree> = emptyList(),
    onBackClick: () -> Unit = {},
    onToggleFavorite: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onKeywordClick: (String) -> Unit = {},
    onRelatedArticleClick: (ArticleEntity_7ree) -> Unit = {},
    isReading: Boolean = false,
    ttsButtonState: com.x7ree.wordcard.article.ArticleTtsManager_7ree.TtsButtonState = com.x7ree.wordcard.article.ArticleTtsManager_7ree.TtsButtonState.READY,
    keywordStats: Map<String, Int> = emptyMap(),
    onEdgeSwipeBack: (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var isSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 主要内容
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ArticleDetailAppBar_7ree(
                article = article,
                onToggleFavorite = onToggleFavorite,
                onShareClick = onShareClick,
                onBackClick = onBackClick,
                ttsButtonState = ttsButtonState
            )
            
            // 文章内容
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                ArticleTitleCard_7ree(
                    article = article,
                    filterMarkdownStars = { filterMarkdownStars(it) },
                    formatTimestamp = { formatTimestamp(it) }
                )
                
                ArticleContentTabs_7ree(article = article)
                
                ArticleKeywordsCard_7ree(
                    article = article,
                    keywordStats = keywordStats,
                    onKeywordClick = onKeywordClick
                )
                
                RelatedArticlesCard_7ree(
                    relatedArticles = relatedArticles,
                    onRelatedArticleClick = onRelatedArticleClick,
                    scrollState = scrollState
                )
                
                // 底部间距
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        
        // 边缘滑动返回组件
        ArticleEdgeSwipeComponent_7ree(
            onBackNavigation = onEdgeSwipeBack ?: onBackClick
        )
    }
}
