package com.x7ree.wordcard.article

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.config.AppConfigManager_7ree
import com.x7ree.wordcard.data.ArticleEntity_7ree
import com.x7ree.wordcard.data.ArticleRepository_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.article.utils.*
import com.x7ree.wordcard.article.viewmodel.ArticlePaginationHandlerWrapper
import com.x7ree.wordcard.article.viewmodel.ArticleState
import com.x7ree.wordcard.article.viewmodel.ArticleListHandler
import com.x7ree.wordcard.article.viewmodel.ArticleGenerationHandler
import com.x7ree.wordcard.article.viewmodel.ArticleDetailHandler
import com.x7ree.wordcard.article.viewmodel.ArticleTtsHandler
import com.x7ree.wordcard.article.viewmodel.ArticleManagementHandler
import com.x7ree.wordcard.article.viewmodel.ArticleSearchHandler
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArticleViewModel_7ree(
    private val articleRepository_7ree: ArticleRepository_7ree,
    private val apiService_7ree: OpenAiApiService_7ree,
    private val context: Context,
    private val wordRepository_7ree: WordRepository_7ree? = null
) : ViewModel() {

private val state = ArticleState()

    private val articleListHelper = ArticleListHelper_7ree(articleRepository_7ree)
    private val articleGenerationHelper = ArticleGenerationHelper2_7ree(apiService_7ree, AppConfigManager_7ree(context), articleRepository_7ree, ArticleGenerationHelper_7ree(wordRepository_7ree, articleRepository_7ree, apiService_7ree))
    private val articleDetailHelper = ArticleDetailHelper_7ree(articleRepository_7ree, wordRepository_7ree)
    private val articleTtsHelper = ArticleTtsHelper_7ree(context)
    private val articleDeleteHelper = ArticleDeleteHelper_7ree(articleRepository_7ree)
    private val articlePaginationHandler = ArticlePaginationHandler_7ree(articleRepository_7ree)

    private val listHandler = ArticleListHandler(state, articleListHelper)
    private val generationHandler = ArticleGenerationHandler(state, articleGenerationHelper, articleRepository_7ree)
    private val detailHandler = ArticleDetailHandler(state, articleDetailHelper, articleRepository_7ree)
    private val ttsHandler = ArticleTtsHandler(state, articleTtsHelper)
    private val managementHandler = ArticleManagementHandler(state, articleDeleteHelper)
    private val paginationHandler = ArticlePaginationHandlerWrapper(state, articlePaginationHandler)
    private val searchHandler = ArticleSearchHandler(state, articlePaginationHandler)

    val articles: StateFlow<List<ArticleEntity_7ree>> = state.articles
    val isLoading: StateFlow<Boolean> = state.isLoading
    val isRefreshing: StateFlow<Boolean> = state.isRefreshing
    val operationResult: StateFlow<String?> = state.operationResult
    val isGenerating: StateFlow<Boolean> = state.isGenerating
    val selectedArticle: StateFlow<ArticleEntity_7ree?> = state.selectedArticle
    val showDetailScreen: StateFlow<Boolean> = state.showDetailScreen
    val isReading: StateFlow<Boolean> = articleTtsHelper.isReading
    val isTtsInitializing: StateFlow<Boolean> = articleTtsHelper.isInitializing
    val ttsErrorMessage: StateFlow<String?> = articleTtsHelper.errorMessage
    val ttsButtonState: StateFlow<ArticleTtsManager_7ree.TtsButtonState> = articleTtsHelper.buttonState
    val currentTtsEngine: StateFlow<String> = articleTtsHelper.currentEngine
    val keywordStats: StateFlow<Map<String, Int>> = state.keywordStats
    val smartGenerationStatus: StateFlow<String> = state.smartGenerationStatus
    val smartGenerationKeywords: StateFlow<List<String>> = state.smartGenerationKeywords
    val showSmartGenerationCard: StateFlow<Boolean> = state.showSmartGenerationCard
    val filterState: StateFlow<ArticleFilterState_7ree> = state.filterState
    val showFilterMenu: StateFlow<Boolean> = state.showFilterMenu
    val isManagementMode: StateFlow<Boolean> = state.isManagementMode
    val selectedArticleIds: StateFlow<Set<Long>> = state.selectedArticleIds
    val pagedArticles: StateFlow<List<ArticleEntity_7ree>> = paginationHandler.pagedArticles
    val isLoadingMore: StateFlow<Boolean> = paginationHandler.isLoadingMore
    val hasMoreData: StateFlow<Boolean> = paginationHandler.hasMoreData
    val isPaginationRefreshing: StateFlow<Boolean> = paginationHandler.isPaginationRefreshing
    val usePaginationMode: StateFlow<Boolean> = state.usePaginationMode
val searchQuery: StateFlow<String> = state.searchQuery
    val isSearchMode: StateFlow<Boolean> = state.isSearchMode
    val searchResults: StateFlow<List<ArticleEntity_7ree>> = state.searchResults
    val relatedArticles: StateFlow<List<ArticleEntity_7ree>> = state.relatedArticles
    val isFromArticleList: StateFlow<Boolean> = state.isFromArticleList
    val shouldRestoreScrollPosition: StateFlow<Boolean> = state.shouldRestoreScrollPosition
    private var currentSmartGenerationType: com.x7ree.wordcard.ui.SmartGenerationType_7ree? = null

    init {
        if (!usePaginationMode.value) {
            listHandler.initializeArticleFlow(viewModelScope)
            viewModelScope.launch {
                filterState.collect { listHandler.applyFilterAndSort() }
            }
        } else {
            paginationHandler.loadInitialArticles(viewModelScope)
            viewModelScope.launch {
                filterState.collect { paginationHandler.loadInitialArticles(viewModelScope) }
            }
        }
    }

    fun getCurrentSmartGenerationType(): com.x7ree.wordcard.ui.SmartGenerationType_7ree? = currentSmartGenerationType

    fun pullToRefreshArticles() {
        viewModelScope.launch {
            try {
                state._isRefreshing.value = true
                kotlinx.coroutines.delay(800)
                listHandler.initializeArticleFlow(viewModelScope)
            } catch (e: Exception) {
                state._operationResult.value = "刷新失败: ${e.message}"
            } finally {
                state._isRefreshing.value = false
            }
        }
    }

    fun generateArticle(keyWords: String) = generationHandler.generateArticle(keyWords, viewModelScope) { handleNewArticleGenerated() }
    fun toggleFavorite(articleId: Long) = ArticleFavoriteHelper_7ree(articleRepository_7ree).toggleFavorite(articleId, viewModelScope) { result -> state._operationResult.value = result }
    fun incrementViewCount(articleId: Long) = listHandler.incrementViewCount(articleId, viewModelScope)
    fun deleteArticle(articleId: Long) = articleDeleteHelper.deleteArticle(articleId, viewModelScope) { result -> state._operationResult.value = result }
    fun clearOperationResult() {
        state._operationResult.value = null
    }
    fun selectArticle(article: ArticleEntity_7ree) = detailHandler.selectArticle(article, viewModelScope) { incrementViewCount(it) }
    fun closeDetailScreen() = detailHandler.closeDetailScreen()
    fun returnToArticleList() = detailHandler.returnToArticleList()
    fun toggleSelectedArticleFavorite() = detailHandler.toggleSelectedArticleFavorite { toggleFavorite(it) }
    fun readArticle(article: ArticleEntity_7ree) = ttsHandler.readArticle(article)
    fun stopReading() = ttsHandler.stopReading()
    fun toggleReading() = ttsHandler.toggleReading()
    fun clearTtsError() = ttsHandler.clearTtsError()
    fun smartGenerateArticle(type: com.x7ree.wordcard.ui.SmartGenerationType_7ree, manualKeywords: String = "") {
        currentSmartGenerationType = type
        state._showSmartGenerationCard.value = true
        
        viewModelScope.launch {
            try {
                state._smartGenerationStatus.value = "正在选择单词..."
                
                val smartWordHelper = SmartWordSelectionHelper_7ree(wordRepository_7ree)
                val keywords = when (type) {
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_VIEW_COUNT -> {
                        state._smartGenerationStatus.value = "正在获取查阅次数最少的单词..."
                        smartWordHelper.getWordsWithLowViewCount()
                    }
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_REFERENCE_COUNT -> {
                        state._smartGenerationStatus.value = "正在获取引用次数最少的单词..."
                        smartWordHelper.getWordsWithLowReferenceCount()
                    }
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_SPELLING_COUNT -> {
                        state._smartGenerationStatus.value = "正在获取拼写练习最少的单词..."
                        smartWordHelper.getWordsWithLowSpellingCount()
                    }
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.NEWEST_WORDS -> {
                        state._smartGenerationStatus.value = "正在获取最新收录的单词..."
                        smartWordHelper.getNewestWords()
                    }
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.RANDOM_WORDS -> {
                        state._smartGenerationStatus.value = "正在随机选择单词..."
                        smartWordHelper.getRandomWords()
                    }
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.MANUAL_INPUT -> {
                        state._smartGenerationStatus.value = "正在处理手动输入的单词..."
                        manualKeywords.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    }
                }
                
                if (keywords.isEmpty()) {
                    state._smartGenerationStatus.value = "未找到合适的单词，请稍后重试"
                    return@launch
                }
                
                state._smartGenerationKeywords.value = keywords
                
                // 获取API引擎名
                val appConfigManager = AppConfigManager_7ree(context)
                val apiConfig = appConfigManager.loadApiConfig_7ree()
                val activeApi = apiConfig.getActiveTranslationApi()
                val engineName = activeApi.apiName.ifEmpty { "AI引擎" }
                
                state._smartGenerationStatus.value = "${keywords.joinToString(", ")}\n${engineName}写作中，请稍候…"
                
                // 调用生成文章的方法
                generationHandler.generateArticle(keywords.joinToString(", "), viewModelScope) { handleNewArticleGenerated() }
                
            } catch (e: Exception) {
                state._smartGenerationStatus.value = "智能生成失败: ${e.message}"
            }
        }
    }
    
    fun closeSmartGenerationCard() {
        state._showSmartGenerationCard.value = false
        state._smartGenerationStatus.value = ""
        state._smartGenerationKeywords.value = emptyList()
        currentSmartGenerationType = null
    }
    fun showFilterMenu() {
        state._showFilterMenu.value = true
    }
    fun hideFilterMenu() {
        state._showFilterMenu.value = false
    }
    fun updateFilterState(newFilterState: ArticleFilterState_7ree) {
        state._filterState.value = newFilterState
    }
    fun enterManagementMode() = managementHandler.enterManagementMode()
    fun exitManagementMode() = managementHandler.exitManagementMode()
    fun toggleArticleSelection(articleId: Long) = managementHandler.toggleArticleSelection(articleId)
    fun toggleSelectAll() = managementHandler.toggleSelectAll()
    fun deleteSelectedArticles() = managementHandler.deleteSelectedArticles(viewModelScope)
    fun loadInitialArticles() = paginationHandler.loadInitialArticles(viewModelScope)
    fun loadMoreArticles() = paginationHandler.loadMoreArticles(viewModelScope)
    fun paginationRefreshArticles() = paginationHandler.paginationRefreshArticles(viewModelScope)
    fun togglePaginationMode() {
        state._usePaginationMode.value = !state.usePaginationMode.value
        if (usePaginationMode.value) {
            paginationHandler.loadInitialArticles(viewModelScope)
        } else {
            listHandler.initializeArticleFlow(viewModelScope)
        }
    }
    fun paginationToggleFavorite(articleId: Long) {
        viewModelScope.launch {
            try {
                ArticleFavoriteHelper_7ree(articleRepository_7ree).toggleFavorite(articleId, viewModelScope) { result -> 
                    state._operationResult.value = result 
                }
                // 刷新分页数据
                paginationHandler.loadInitialArticles(viewModelScope)
            } catch (e: Exception) {
                state._operationResult.value = "操作失败: ${e.message}"
            }
        }
    }
    
    fun paginationDeleteArticle(articleId: Long) {
        viewModelScope.launch {
            try {
                articleDeleteHelper.deleteArticleSync(articleId)
                state._operationResult.value = "文章已删除"
                // 刷新分页数据
                paginationHandler.loadInitialArticles(viewModelScope)
            } catch (e: Exception) {
                state._operationResult.value = "删除失败: ${e.message}"
            }
        }
    }
    
    fun paginationDeleteSelectedArticles() {
        managementHandler.deleteSelectedArticles(viewModelScope)
        // 删除完成后刷新分页数据
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000) // 等待删除操作完成
            paginationHandler.loadInitialArticles(viewModelScope)
        }
    }
    private fun handleNewArticleGenerated() {
        if (usePaginationMode.value) {
            paginationHandler.loadInitialArticles(viewModelScope)
        } else {
            // 非分页模式下也需要刷新文章列表
            listHandler.initializeArticleFlow(viewModelScope)
        }
    }
    fun toggleSearchMode(isSearchMode: Boolean) = searchHandler.toggleSearchMode(isSearchMode, viewModelScope) { loadInitialArticles() }
    fun updateSearchQuery(query: String) = searchHandler.updateSearchQuery(query, viewModelScope)
    fun getCurrentDisplayArticles(): StateFlow<List<ArticleEntity_7ree>> {
        return if (isSearchMode.value && searchQuery.value.isNotBlank()) {
            searchResults
        } else if (usePaginationMode.value) {
            pagedArticles
        } else {
            articles
        }
    }
    fun clearSearchResults() = searchHandler.clearSearchResults()
    
    // 滚动位置管理方法
    fun saveScrollPositionBeforeNavigation() {
        // 标记从文章列表进入详情页
        state._isFromArticleList.value = true
    }
    
    fun markScrollPositionForRestore() {
        // 标记需要恢复滚动位置
        state._shouldRestoreScrollPosition.value = true
    }
    
    fun clearScrollPositionRestore() {
        // 清除滚动位置恢复标记
        state._shouldRestoreScrollPosition.value = false
        state._isFromArticleList.value = false
    }

    override fun onCleared() {
        super.onCleared()
        ttsHandler.release()
    }
}
