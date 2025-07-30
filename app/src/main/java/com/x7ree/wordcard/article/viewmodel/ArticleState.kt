package com.x7ree.wordcard.article.viewmodel

import com.x7ree.wordcard.article.utils.ArticleFilterState_7ree
import com.x7ree.wordcard.data.ArticleEntity_7ree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ArticleState {
    // Article list status
    val _articles = MutableStateFlow<List<ArticleEntity_7ree>>(emptyList())
    val articles: StateFlow<List<ArticleEntity_7ree>> = _articles

    // Loading status
    val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Pull-to-refresh status
    val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    // Operation result
    val _operationResult = MutableStateFlow<String?>(null)
    val operationResult: StateFlow<String?> = _operationResult

    // Article generation status
    val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    // Currently selected article (for details page)
    val _selectedArticle = MutableStateFlow<ArticleEntity_7ree?>(null)
    val selectedArticle: StateFlow<ArticleEntity_7ree?> = _selectedArticle

    // Whether to show the details screen
    val _showDetailScreen = MutableStateFlow(false)
    val showDetailScreen: StateFlow<Boolean> = _showDetailScreen

    // Keyword statistics
    val _keywordStats = MutableStateFlow<Map<String, Int>>(emptyMap())
    val keywordStats: StateFlow<Map<String, Int>> = _keywordStats

    // Smart generation article status
    val _smartGenerationStatus = MutableStateFlow<String>("")
    val smartGenerationStatus: StateFlow<String> = _smartGenerationStatus

    // Smart generation article keywords
    val _smartGenerationKeywords = MutableStateFlow<List<String>>(emptyList())
    val smartGenerationKeywords: StateFlow<List<String>> = _smartGenerationKeywords

    // Whether to show the smart generation article card
    val _showSmartGenerationCard = MutableStateFlow(false)
    val showSmartGenerationCard: StateFlow<Boolean> = _showSmartGenerationCard

    // Filter and sort status
    val _filterState = MutableStateFlow(ArticleFilterState_7ree())
    val filterState: StateFlow<ArticleFilterState_7ree> = _filterState

    // Raw article list (before filtering and sorting)
    val _rawArticles = MutableStateFlow<List<ArticleEntity_7ree>>(emptyList())

    // Filter menu display status
    val _showFilterMenu = MutableStateFlow(false)
    val showFilterMenu: StateFlow<Boolean> = _showFilterMenu

    // Management mode status
    val _isManagementMode = MutableStateFlow(false)
    val isManagementMode: StateFlow<Boolean> = _isManagementMode

    // List of selected article IDs
    val _selectedArticleIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedArticleIds: StateFlow<Set<Long>> = _selectedArticleIds

    // Whether to use pagination mode
    val _usePaginationMode = MutableStateFlow(true)
    val usePaginationMode: StateFlow<Boolean> = _usePaginationMode

    // Search related status
    val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val _isSearchMode = MutableStateFlow(false)
    val isSearchMode: StateFlow<Boolean> = _isSearchMode

// Search results (for non-pagination mode)
    val _searchResults = MutableStateFlow<List<ArticleEntity_7ree>>(emptyList())
    val searchResults: StateFlow<List<ArticleEntity_7ree>> = _searchResults
    
    // Related articles
    val _relatedArticles = MutableStateFlow<List<ArticleEntity_7ree>>(emptyList())
    val relatedArticles: StateFlow<List<ArticleEntity_7ree>> = _relatedArticles
    
    // Whether the article detail page was entered from the article list
    val _isFromArticleList = MutableStateFlow(false)
    val isFromArticleList: StateFlow<Boolean> = _isFromArticleList
}
