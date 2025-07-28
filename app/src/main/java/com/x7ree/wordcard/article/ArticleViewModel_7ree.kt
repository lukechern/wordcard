package com.x7ree.wordcard.article

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.config.AppConfigManager_7ree
import com.x7ree.wordcard.config.PromptConfig_7ree
import com.x7ree.wordcard.data.ArticleEntity_7ree
import com.x7ree.wordcard.data.ArticleRepository_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.article.utils.ArticleGenerationHelper_7ree
import com.x7ree.wordcard.article.utils.ArticleDataHelper_7ree
import com.x7ree.wordcard.article.utils.ArticleTtsHelper_7ree
import com.x7ree.wordcard.article.utils.SmartWordSelectionHelper_7ree
import com.x7ree.wordcard.article.utils.ArticleFavoriteHelper_7ree
import com.x7ree.wordcard.article.utils.ArticleDetailHelper_7ree
import com.x7ree.wordcard.article.utils.ArticleListHelper_7ree
import com.x7ree.wordcard.article.utils.ArticleDeleteHelper_7ree
import com.x7ree.wordcard.article.utils.ArticleGenerationHelper2_7ree
import com.x7ree.wordcard.article.utils.ArticleFilterState_7ree
import com.x7ree.wordcard.article.utils.ArticleSortType_7ree
import com.x7ree.wordcard.article.utils.ArticlePaginationHandler_7ree
import kotlinx.coroutines.delay

/**
 * 文章功能的ViewModel
 */
class ArticleViewModel_7ree(
    private val articleRepository_7ree: ArticleRepository_7ree,
    private val apiService_7ree: OpenAiApiService_7ree,
    private val context: Context,
    private val wordRepository_7ree: WordRepository_7ree? = null
) : ViewModel() {
    
    private val appConfigManager_7ree = AppConfigManager_7ree(context)
    private val articleGenerationHelper_7ree = ArticleGenerationHelper_7ree(wordRepository_7ree, articleRepository_7ree, apiService_7ree)
    private val articleDataHelper_7ree = ArticleDataHelper_7ree(wordRepository_7ree)
    private val articleTtsHelper_7ree = ArticleTtsHelper_7ree(context)
    private val smartWordSelectionHelper_7ree = SmartWordSelectionHelper_7ree(wordRepository_7ree)
    private val articleFavoriteHelper_7ree = ArticleFavoriteHelper_7ree(articleRepository_7ree)
    private val articleDetailHelper_7ree = ArticleDetailHelper_7ree(articleRepository_7ree, wordRepository_7ree)
    private val articleListHelper_7ree = ArticleListHelper_7ree(articleRepository_7ree)
    private val articleDeleteHelper_7ree = ArticleDeleteHelper_7ree(articleRepository_7ree)
    private val articleGenerationHelper2_7ree = ArticleGenerationHelper2_7ree(apiService_7ree, appConfigManager_7ree, articleRepository_7ree, articleGenerationHelper_7ree)
    
    // 分页处理器
    private val articlePaginationHandler_7ree = ArticlePaginationHandler_7ree(articleRepository_7ree)
    
    // 文章列表状态
    private val _articles = MutableStateFlow<List<ArticleEntity_7ree>>(emptyList())
    val articles: StateFlow<List<ArticleEntity_7ree>> = _articles.asStateFlow()
    
    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // 下拉刷新状态
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    // 操作结果
    private val _operationResult = MutableStateFlow<String?>(null)
    val operationResult: StateFlow<String?> = _operationResult.asStateFlow()
    
    // 文章生成状态
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()
    
    // 当前选中的文章（用于详情页）
    private val _selectedArticle = MutableStateFlow<ArticleEntity_7ree?>(null)
    val selectedArticle: StateFlow<ArticleEntity_7ree?> = _selectedArticle.asStateFlow()
    
    // 是否显示详情页
    private val _showDetailScreen = MutableStateFlow(false)
    val showDetailScreen: StateFlow<Boolean> = _showDetailScreen.asStateFlow()
    
    // 朗读状态
    val isReading: StateFlow<Boolean> = articleTtsHelper_7ree.isReading
    val isTtsInitializing: StateFlow<Boolean> = articleTtsHelper_7ree.isInitializing
    val ttsErrorMessage: StateFlow<String?> = articleTtsHelper_7ree.errorMessage
    val ttsButtonState: StateFlow<ArticleTtsManager_7ree.TtsButtonState> = articleTtsHelper_7ree.buttonState
    val currentTtsEngine: StateFlow<String> = articleTtsHelper_7ree.currentEngine
    
    // 关键词统计
    private val _keywordStats = MutableStateFlow<Map<String, Int>>(emptyMap())
    val keywordStats: StateFlow<Map<String, Int>> = _keywordStats.asStateFlow()
    
    // 智能生成文章状态
    private val _smartGenerationStatus = MutableStateFlow<String>("")
    val smartGenerationStatus: StateFlow<String> = _smartGenerationStatus.asStateFlow()
    
    // 智能生成文章关键词
    private val _smartGenerationKeywords = MutableStateFlow<List<String>>(emptyList())
    val smartGenerationKeywords: StateFlow<List<String>> = _smartGenerationKeywords.asStateFlow()
    
    // 是否显示智能生成文章卡片
    private val _showSmartGenerationCard = MutableStateFlow(false)
    val showSmartGenerationCard: StateFlow<Boolean> = _showSmartGenerationCard.asStateFlow()
    
    // 智能生成类型
    private var currentSmartGenerationType: com.x7ree.wordcard.ui.SmartGenerationType_7ree? = null
    fun getCurrentSmartGenerationType(): com.x7ree.wordcard.ui.SmartGenerationType_7ree? = currentSmartGenerationType
    
    // 筛选和排序状态
    private val _filterState = MutableStateFlow(ArticleFilterState_7ree())
    val filterState: StateFlow<ArticleFilterState_7ree> = _filterState.asStateFlow()
    
    // 原始文章列表（未经筛选和排序）
    private val _rawArticles = MutableStateFlow<List<ArticleEntity_7ree>>(emptyList())
    
    // 筛选菜单显示状态
    private val _showFilterMenu = MutableStateFlow(false)
    val showFilterMenu: StateFlow<Boolean> = _showFilterMenu.asStateFlow()
    
    // 管理模式状态
    private val _isManagementMode = MutableStateFlow(false)
    val isManagementMode: StateFlow<Boolean> = _isManagementMode.asStateFlow()
    
    // 选中的文章ID列表
    private val _selectedArticleIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedArticleIds: StateFlow<Set<Long>> = _selectedArticleIds.asStateFlow()
    
    // 分页相关状态 - 从分页处理器获取
    val pagedArticles: StateFlow<List<ArticleEntity_7ree>> = articlePaginationHandler_7ree.pagedArticles
    val isLoadingMore: StateFlow<Boolean> = articlePaginationHandler_7ree.isLoadingMore
    val hasMoreData: StateFlow<Boolean> = articlePaginationHandler_7ree.hasMoreData
    val isPaginationRefreshing: StateFlow<Boolean> = articlePaginationHandler_7ree.isRefreshing
    
    // 是否使用分页模式
    private val _usePaginationMode = MutableStateFlow(true)
    val usePaginationMode: StateFlow<Boolean> = _usePaginationMode.asStateFlow()
    
    // 搜索相关状态
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isSearchMode = MutableStateFlow(false)
    val isSearchMode: StateFlow<Boolean> = _isSearchMode.asStateFlow()
    
    // 搜索结果（用于非分页模式）
    private val _searchResults = MutableStateFlow<List<ArticleEntity_7ree>>(emptyList())
    val searchResults: StateFlow<List<ArticleEntity_7ree>> = _searchResults.asStateFlow()
    
    init {
        // 初始化分页数据
        loadInitialArticles()
        
        // 如果不使用分页模式，则使用原有的流式数据
        if (!_usePaginationMode.value) {
            initializeArticleFlow()
            // 监听筛选状态变化，重新应用筛选和排序
            viewModelScope.launch {
                _filterState.collect { filterState ->
                    applyFilterAndSort()
                }
            }
        } else {
            // 分页模式下，监听筛选状态变化，重新加载分页数据
            viewModelScope.launch {
                _filterState.collect { filterState ->
                    loadInitialArticles()
                }
            }
        }
    }
    
    /**
     * 初始化文章列表数据流
     */
    private fun initializeArticleFlow() {
        articleListHelper_7ree.initializeArticleFlow(
            viewModelScope,
            { loading -> _isLoading.value = loading },
            { articles -> 
                _rawArticles.value = articles
                applyFilterAndSort()
            },
            { error -> _operationResult.value = error }
        )
    }
    
    /**
     * 应用筛选和排序
     */
    private fun applyFilterAndSort() {
        val rawArticles = _rawArticles.value
        val filterState = _filterState.value
        
        // 应用筛选
        var filteredArticles = rawArticles
        if (filterState.showFavoritesOnly) {
            filteredArticles = filteredArticles.filter { it.isFavorite }
        }
        
        // 应用排序
        val sortedArticles = when (filterState.sortType) {
            ArticleSortType_7ree.PUBLISH_TIME_ASC -> {
                filteredArticles.sortedBy { it.generationTimestamp }
            }
            ArticleSortType_7ree.PUBLISH_TIME_DESC -> {
                filteredArticles.sortedByDescending { it.generationTimestamp }
            }
            ArticleSortType_7ree.VIEW_COUNT_ASC -> {
                filteredArticles.sortedBy { it.viewCount }
            }
            ArticleSortType_7ree.VIEW_COUNT_DESC -> {
                filteredArticles.sortedByDescending { it.viewCount }
            }
            null -> {
                // 默认排序：按生成时间降序
                filteredArticles.sortedByDescending { it.generationTimestamp }
            }
        }
        
        _articles.value = sortedArticles
    }
    
    /**
     * 手动刷新文章列表（用于调试）
     */
    fun refreshArticles() {
        // 由于我们使用Flow，数据库的任何更改都应该自动反映到UI
        // 这个方法主要用于调试和强制刷新
    }
    
    /**
     * 下拉刷新文章列表
     */
    fun pullToRefreshArticles() {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true
                
                // 模拟刷新延迟，提供更好的用户体验
                delay(800)
                
                // 重新初始化文章流，这会触发数据库查询并更新UI
                initializeArticleFlow()
                
            } catch (e: Exception) {
                _operationResult.value = "刷新失败: ${e.message}"
            } finally {
                _isRefreshing.value = false
            }
        }
    }
    
    /**
     * 生成新文章
     */
    fun generateArticle(keyWords: String) {
        articleGenerationHelper2_7ree.generateArticle(
            keyWords,
            viewModelScope,
            { generating -> _isGenerating.value = generating },
            { result -> 
                _operationResult.value = result
                
                // 如果生成成功，处理新文章
                if (result == "文章生成成功！") {
                    handleNewArticleGenerated()
                }
                
                // 如果是智能生成文章，更新智能生成文章卡片的状态
                if (_showSmartGenerationCard.value) {
                    if (result == "文章生成成功！") {
                        // 延迟一下再获取最新文章，确保数据库更新完成
                        viewModelScope.launch {
                            delay(100) // 等待数据库更新
                            // 根据模式获取最新文章
                            val latestArticle = if (_usePaginationMode.value) {
                                articlePaginationHandler_7ree.pagedArticles.value
                                    .sortedByDescending { it.generationTimestamp }
                                    .firstOrNull()
                            } else {
                                _articles.value
                                    .sortedByDescending { it.generationTimestamp }
                                    .firstOrNull()
                            }
                            val title = latestArticle?.englishTitle ?: "无标题"
                            
                            _smartGenerationStatus.value = "文章已生成，标题如下：\n$title"
                        }
                    } else if (result.startsWith("文章生成失败")) {
                        _smartGenerationStatus.value = result
                    }
                }
            }
        )
    }
    
    /**
     * 切换文章收藏状态
     */
    fun toggleFavorite(articleId: Long) {
        articleFavoriteHelper_7ree.toggleFavorite(
            articleId,
            viewModelScope
        ) { result ->
            _operationResult.value = result
        }
    }
    
    /**
     * 增加文章浏览次数
     */
    fun incrementViewCount(articleId: Long) {
        articleListHelper_7ree.incrementViewCount(articleId, viewModelScope)
    }
    
    /**
     * 删除文章
     */
    fun deleteArticle(articleId: Long) {
        articleDeleteHelper_7ree.deleteArticle(
            articleId,
            viewModelScope
        ) { result ->
            _operationResult.value = result
        }
    }
    
    /**
     * 清除操作结果
     */
    fun clearOperationResult() {
        _operationResult.value = null
    }
    
    /**
     * 选择文章并显示详情页
     */
    fun selectArticle(article: ArticleEntity_7ree) {
        // 根据是否使用分页模式选择正确的文章列表
        val articlesToUse = if (_usePaginationMode.value) {
            // 在分页模式下，使用分页处理器获取的所有文章
            articlePaginationHandler_7ree.pagedArticles.value
        } else {
            // 在非分页模式下，使用原始文章列表
            _articles.value
        }
        
        articleDetailHelper_7ree.handleArticleSelection(
            article,
            viewModelScope,
            { selectedArticle -> _selectedArticle.value = selectedArticle },
            { stats -> _keywordStats.value = stats },
            articlesToUse
        )
        
        _showDetailScreen.value = true
        
        // 增加浏览次数
        articleDetailHelper_7ree.handleArticleSelection(
            article,
            viewModelScope,
            { selectedArticle -> incrementViewCount(selectedArticle.id) },
            { stats -> },
            articlesToUse
        )
    }
    
    /**
     * 关闭详情页
     */
    fun closeDetailScreen() {
        _showDetailScreen.value = false
        _selectedArticle.value = null
    }
    
    /**
     * 切换当前选中文章的收藏状态
     */
    fun toggleSelectedArticleFavorite() {
        _selectedArticle.value?.let { article ->
            toggleFavorite(article.id)
            // 更新选中文章的收藏状态
            _selectedArticle.value = article.copy(isFavorite = !article.isFavorite)
        }
    }
    
    /**
     * 朗读文章
     */
    fun readArticle(article: ArticleEntity_7ree) {
        articleTtsHelper_7ree.readArticle(article.englishContent, article.englishTitle)
    }
    
    /**
     * 停止朗读
     */
    fun stopReading() {
        articleTtsHelper_7ree.stopReading()
    }
    
    /**
     * 切换朗读状态（朗读/停止）
     */
    fun toggleReading() {
        _selectedArticle.value?.let { article ->
            articleTtsHelper_7ree.toggleReading(article.englishContent, article.englishTitle)
        }
    }
    
    /**
     * 清除TTS错误信息
     */
    fun clearTtsError() {
        articleTtsHelper_7ree.clearError()
    }
    
    /**
     * 智能生成文章（使用预定义的智能选词类型）
     */
    fun smartGenerateArticle(type: com.x7ree.wordcard.ui.SmartGenerationType_7ree) {
        smartGenerateArticle(type, "")
    }
    
    /**
     * 智能生成文章（支持手动输入关键词）
     */
    fun smartGenerateArticle(type: com.x7ree.wordcard.ui.SmartGenerationType_7ree, manualKeywords: String = "") {
        viewModelScope.launch {
            try {
                // 保存当前智能生成类型
                currentSmartGenerationType = type
                
                // 显示智能生成文章卡片
                _showSmartGenerationCard.value = true
                _isGenerating.value = true
                
                // 对于手动输入，直接使用已输入的关键词
                if (type == com.x7ree.wordcard.ui.SmartGenerationType_7ree.MANUAL_INPUT) {
                    _operationResult.value = "正在生成文章..."
                    _smartGenerationStatus.value = "正在生成文章，请稍候..."
                    
                    // 延迟一小段时间以显示状态
                    delay(500)
                    
                    // 显示正在生成文章的状态（包含API名称）
                    val apiConfig = appConfigManager_7ree.loadApiConfig_7ree()
                    val apiName = apiConfig.getActiveTranslationApi().apiName
                    _smartGenerationStatus.value = "${apiName}正在生成文章，请稍候……"
                    
                    // 使用手动输入的关键词生成文章
                    generateArticle(manualKeywords)
                    return@launch
                }
                
                _operationResult.value = "正在智能选择关键词..."
                
                if (wordRepository_7ree == null) {
                    _operationResult.value = "单词库未初始化，无法智能选词"
                    _smartGenerationStatus.value = "单词库未初始化，无法智能选词"
                    _isGenerating.value = false
                    return@launch
                }
                
                // 根据类型设置初始状态
                val initialStatus = when (type) {
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_VIEW_COUNT -> "开始查找单词本中查阅量最少的5个单词"
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_REFERENCE_COUNT -> "开始查找单词本中引用次数最少的5个单词"
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_SPELLING_COUNT -> "开始查找单词本中拼写练习次数最少的5个单词"
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.NEWEST_WORDS -> "开始查找单词本中最新加入的5个单词"
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.RANDOM_WORDS -> "开始随机选择5个单词"
                    else -> "开始查找单词"
                }
                
                _smartGenerationStatus.value = initialStatus
                
                // 至少显示1秒钟的初始状态
                delay(1000)
                
                // 根据类型获取关键词
                val keywords = when (type) {
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_VIEW_COUNT -> {
                        smartWordSelectionHelper_7ree.getWordsWithLowViewCount()
                    }
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_REFERENCE_COUNT -> {
                        smartWordSelectionHelper_7ree.getWordsWithLowReferenceCount()
                    }
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_SPELLING_COUNT -> {
                        smartWordSelectionHelper_7ree.getWordsWithLowSpellingCount()
                    }
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.NEWEST_WORDS -> {
                        smartWordSelectionHelper_7ree.getNewestWords()
                    }
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.RANDOM_WORDS -> {
                        smartWordSelectionHelper_7ree.getRandomWords()
                    }
                    else -> {
                        emptyList()
                    }
                }
                
                if (keywords.isEmpty()) {
                    _operationResult.value = "单词库中没有找到合适的单词"
                    _smartGenerationStatus.value = "单词库中没有找到合适的单词"
                    _isGenerating.value = false
                    return@launch
                }
                
                // 显示找到的关键词
                _smartGenerationKeywords.value = keywords
                
                // 根据类型设置找到关键词的状态
                val foundStatus = when (type) {
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_VIEW_COUNT -> "已找到以下低查询单词：" + keywords.joinToString("，")
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_REFERENCE_COUNT -> "已找到以下低引用单词：" + keywords.joinToString("，")
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_SPELLING_COUNT -> "已找到以下低拼写单词：" + keywords.joinToString("，")
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.NEWEST_WORDS -> "已找到以下新收录单词：" + keywords.joinToString("，")
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.RANDOM_WORDS -> "已找到以下随机单词：" + keywords.joinToString("，")
                    else -> "已找到以下单词：" + keywords.joinToString("，")
                }
                
                _smartGenerationStatus.value = foundStatus
                
                // 至少显示1秒钟的找到关键词状态
                delay(1000)
                
                val keyWordsString = keywords.joinToString(", ")
                
                // 显示正在生成文章的状态（包含API名称和单词类型）
                val apiConfig = appConfigManager_7ree.loadApiConfig_7ree()
                val apiName = apiConfig.getActiveTranslationApi().apiName
                
                val wordTypeText = when (type) {
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_VIEW_COUNT -> "5个低查询单词"
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_REFERENCE_COUNT -> "5个低引用单词"
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_SPELLING_COUNT -> "5个低拼写单词"
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.NEWEST_WORDS -> "5个新收录单词"
                    com.x7ree.wordcard.ui.SmartGenerationType_7ree.RANDOM_WORDS -> "5个随机单词"
                    else -> "5个单词"
                }
                
                _smartGenerationStatus.value = "${apiName}正在生成文章，请稍候……"
                
                // 使用选择的关键词生成文章
                generateArticle(keyWordsString)
                
            } catch (e: Exception) {
                _operationResult.value = "智能生成失败: ${e.message}"
                _smartGenerationStatus.value = "智能生成失败: ${e.message}"
                _isGenerating.value = false
            }
        }
    }
    
    /**
     * 关闭智能生成文章卡片
     */
    fun closeSmartGenerationCard() {
        _showSmartGenerationCard.value = false
        _smartGenerationStatus.value = ""
        _smartGenerationKeywords.value = emptyList()
        _isGenerating.value = false
    }
    
    /**
     * 显示筛选菜单
     */
    fun showFilterMenu() {
        _showFilterMenu.value = true
    }
    
    /**
     * 隐藏筛选菜单
     */
    fun hideFilterMenu() {
        _showFilterMenu.value = false
    }
    
    /**
     * 更新筛选状态
     */
    fun updateFilterState(newFilterState: ArticleFilterState_7ree) {
        _filterState.value = newFilterState
    }
    
    /**
     * 进入管理模式
     */
    fun enterManagementMode() {
        _isManagementMode.value = true
        _selectedArticleIds.value = emptySet()
    }
    
    /**
     * 退出管理模式
     */
    fun exitManagementMode() {
        _isManagementMode.value = false
        _selectedArticleIds.value = emptySet()
    }
    
    /**
     * 切换文章选中状态
     */
    fun toggleArticleSelection(articleId: Long) {
        val currentSelected = _selectedArticleIds.value
        _selectedArticleIds.value = if (currentSelected.contains(articleId)) {
            currentSelected - articleId
        } else {
            currentSelected + articleId
        }
    }
    
    /**
     * 全选/取消全选
     */
    fun toggleSelectAll() {
        val currentSelected = _selectedArticleIds.value
        val allArticleIds = _articles.value.map { it.id }.toSet()
        
        _selectedArticleIds.value = if (currentSelected.size == allArticleIds.size) {
            emptySet() // 如果已全选，则取消全选
        } else {
            allArticleIds // 否则全选
        }
    }
    
    /**
     * 批量删除选中的文章
     */
    fun deleteSelectedArticles() {
        val selectedIds = _selectedArticleIds.value
        if (selectedIds.isEmpty()) {
            _operationResult.value = "请先选择要删除的文章"
            return
        }
        
        viewModelScope.launch {
            try {
                var successCount = 0
                var failCount = 0
                
                selectedIds.forEach { articleId ->
                    try {
                        articleDeleteHelper_7ree.deleteArticle(
                            articleId,
                            viewModelScope
                        ) { result ->
                            if (result.contains("成功")) {
                                successCount++
                            } else {
                                failCount++
                            }
                        }
                    } catch (e: Exception) {
                        failCount++
                    }
                }
                
                // 等待删除操作完成
                delay(500)
                
                val resultMessage = when {
                    failCount == 0 -> "成功删除 ${successCount} 篇文章"
                    successCount == 0 -> "删除失败，共 ${failCount} 篇文章删除失败"
                    else -> "删除完成，成功 ${successCount} 篇，失败 ${failCount} 篇"
                }
                
                _operationResult.value = resultMessage
                
                // 清空选中状态并退出管理模式
                _selectedArticleIds.value = emptySet()
                _isManagementMode.value = false
                
            } catch (e: Exception) {
                _operationResult.value = "批量删除失败: ${e.message}"
            }
        }
    }
    
    // ========== 分页相关方法 ==========
    
    /**
     * 加载初始文章（分页模式）
     */
    fun loadInitialArticles() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                articlePaginationHandler_7ree.loadInitialArticles(_filterState.value)
            } catch (e: Exception) {
                _operationResult.value = "加载文章失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 加载更多文章（分页模式）
     */
    fun loadMoreArticles() {
        viewModelScope.launch {
            try {
                if (articlePaginationHandler_7ree.isInSearchMode()) {
                    // 搜索模式下加载更多搜索结果
                    articlePaginationHandler_7ree.loadMoreSearchResults()
                } else {
                    // 正常模式下加载更多文章
                    articlePaginationHandler_7ree.loadMoreArticles()
                }
            } catch (e: Exception) {
                _operationResult.value = "加载更多文章失败: ${e.message}"
            }
        }
    }
    
    /**
     * 分页模式下的下拉刷新 - 完全参考单词本的DataLoadingManager实现
     */
    fun paginationRefreshArticles() {
        viewModelScope.launch {
            try {
                // 设置刷新状态
                articlePaginationHandler_7ree.setRefreshing(true)
                
                // 完全重置分页状态
                articlePaginationHandler_7ree.resetPagination()
                
                // 重新加载初始数据
                loadInitialArticles()
                
                // 延迟以显示刷新动画 - 与单词本保持一致
                delay(500)
                
            } catch (e: Exception) {
                _operationResult.value = "刷新失败: ${e.message}"
            } finally {
                // 确保刷新状态被重置
                articlePaginationHandler_7ree.setRefreshing(false)
            }
        }
    }
    
    /**
     * 切换分页模式
     */
    fun togglePaginationMode() {
        _usePaginationMode.value = !_usePaginationMode.value
        if (_usePaginationMode.value) {
            // 切换到分页模式
            loadInitialArticles()
        } else {
            // 切换到流式模式
            initializeArticleFlow()
        }
    }
    
    /**
     * 分页模式下的文章收藏切换
     */
    fun paginationToggleFavorite(articleId: Long) {
        viewModelScope.launch {
            try {
                // 先更新数据库
                articleFavoriteHelper_7ree.toggleFavorite(articleId, viewModelScope) { result ->
                    _operationResult.value = result
                }
                
                // 然后更新分页列表中的文章状态
                val currentArticles = articlePaginationHandler_7ree.pagedArticles.value
                val updatedArticle = currentArticles.find { it.id == articleId }?.let { article ->
                    article.copy(isFavorite = !article.isFavorite)
                }
                
                updatedArticle?.let {
                    articlePaginationHandler_7ree.updateArticle(it)
                }
                
            } catch (e: Exception) {
                _operationResult.value = "收藏操作失败: ${e.message}"
            }
        }
    }
    
    /**
     * 分页模式下的文章删除
     */
    fun paginationDeleteArticle(articleId: Long) {
        viewModelScope.launch {
            try {
                // 先从分页列表中移除
                articlePaginationHandler_7ree.removeArticle(articleId)
                
                // 然后删除数据库记录
                articleDeleteHelper_7ree.deleteArticle(articleId, viewModelScope) { result ->
                    _operationResult.value = result
                }
                
            } catch (e: Exception) {
                _operationResult.value = "删除文章失败: ${e.message}"
            }
        }
    }
    
    /**
     * 分页模式下的批量删除
     */
    fun paginationDeleteSelectedArticles() {
        val selectedIds = _selectedArticleIds.value
        if (selectedIds.isEmpty()) {
            _operationResult.value = "请先选择要删除的文章"
            return
        }
        
        viewModelScope.launch {
            try {
                var successCount = 0
                var failCount = 0
                
                // 先从分页列表中移除选中的文章
                selectedIds.forEach { articleId ->
                    articlePaginationHandler_7ree.removeArticle(articleId)
                }
                
                // 然后删除数据库记录
                selectedIds.forEach { articleId ->
                    try {
                        articleDeleteHelper_7ree.deleteArticle(
                            articleId,
                            viewModelScope
                        ) { result ->
                            if (result.contains("成功")) {
                                successCount++
                            } else {
                                failCount++
                            }
                        }
                    } catch (e: Exception) {
                        failCount++
                    }
                }
                
                // 等待删除操作完成
                delay(500)
                
                val resultMessage = when {
                    failCount == 0 -> "成功删除 ${successCount} 篇文章"
                    successCount == 0 -> "删除失败，共 ${failCount} 篇文章删除失败"
                    else -> "删除完成，成功 ${successCount} 篇，失败 ${failCount} 篇"
                }
                
                _operationResult.value = resultMessage
                
                // 清空选中状态并退出管理模式
                _selectedArticleIds.value = emptySet()
                _isManagementMode.value = false
                
            } catch (e: Exception) {
                _operationResult.value = "批量删除失败: ${e.message}"
            }
        }
    }
    
    /**
     * 分页模式下的文章生成后处理
     */
    private fun handleNewArticleGenerated() {
        if (_usePaginationMode.value) {
            // 分页模式下，重新加载第一页以显示新生成的文章
            loadInitialArticles()
        }
    }
    
    // ========== 搜索相关方法 ==========
    
    /**
     * 切换搜索模式
     */
    fun toggleSearchMode(isSearchMode: Boolean) {
        _isSearchMode.value = isSearchMode
        if (!isSearchMode) {
            // 退出搜索模式时清空搜索查询
            _searchQuery.value = ""
            _searchResults.value = emptyList()
            
            // 如果是分页模式，清空分页处理器的搜索状态并重新加载
            if (_usePaginationMode.value) {
                viewModelScope.launch {
                    articlePaginationHandler_7ree.clearSearch()
                    loadInitialArticles()
                }
            }
        }
    }
    
    /**
     * 更新搜索查询
     */
    fun updateSearchQuery(query: String) {
        println("DEBUG: updateSearchQuery called with query: '$query'")
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        // 执行搜索
        performSearch(query)
    }
    
    /**
     * 执行搜索
     * 搜索文章标题和关键词，支持分页搜索
     */
    private fun performSearch(query: String) {
        viewModelScope.launch {
            try {
                val searchQuery = query.trim()
                println("DEBUG: performSearch called with query: '$searchQuery'")
                println("DEBUG: usePaginationMode: ${_usePaginationMode.value}")
                
                if (_usePaginationMode.value) {
                    // 分页模式下，通过分页处理器执行搜索
                    println("DEBUG: Using pagination mode for search")
                    articlePaginationHandler_7ree.searchArticles(searchQuery, _filterState.value)
                } else {
                    // 非分页模式下，在当前文章列表中搜索
                    val searchQueryLower = searchQuery.lowercase()
                    val articlesToSearch = _articles.value
                    println("DEBUG: Using non-pagination mode, searching in ${articlesToSearch.size} articles")
                    
                    // 执行搜索过滤
                    val filteredArticles = articlesToSearch.filter { article ->
                        // 搜索英文标题
                        article.englishTitle.lowercase().contains(searchQueryLower) ||
                        // 搜索标题翻译
                        article.titleTranslation.lowercase().contains(searchQueryLower) ||
                        // 搜索关键词
                        article.keyWords.lowercase().contains(searchQueryLower) ||
                        // 搜索英文内容（部分匹配）
                        article.englishContent.lowercase().contains(searchQueryLower) ||
                        // 搜索中文内容（部分匹配）
                        article.chineseContent.lowercase().contains(searchQueryLower)
                    }
                    
                    println("DEBUG: Non-pagination search found ${filteredArticles.size} results")
                    _searchResults.value = filteredArticles
                }
                
            } catch (e: Exception) {
                println("DEBUG: Search failed with exception: ${e.message}")
                _operationResult.value = "搜索失败: ${e.message}"
            }
        }
    }
    
    /**
     * 获取当前显示的文章列表
     * 根据搜索模式返回搜索结果或原始列表
     */
    fun getCurrentDisplayArticles(): StateFlow<List<ArticleEntity_7ree>> {
        return if (_isSearchMode.value && _searchQuery.value.isNotBlank()) {
            _searchResults
        } else if (_usePaginationMode.value) {
            pagedArticles
        } else {
            articles
        }
    }
    
    /**
     * 清空搜索结果
     */
    fun clearSearchResults() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _isSearchMode.value = false
    }
    
    /**
     * 测试搜索功能 - 直接搜索所有文章
     */
    fun testSearch(query: String) {
        viewModelScope.launch {
            try {
                println("DEBUG: testSearch called with query: '$query'")
                
                // 获取所有文章进行测试
                val allArticles = if (_usePaginationMode.value) {
                    articlePaginationHandler_7ree.pagedArticles.value
                } else {
                    _articles.value
                }
                
                println("DEBUG: Total articles available: ${allArticles.size}")
                
                // 打印前几篇文章的标题用于调试
                allArticles.take(3).forEach { article ->
                    println("DEBUG: Article title: '${article.englishTitle}', keywords: '${article.keyWords}'")
                }
                
                // 简单搜索
                val searchQueryLower = query.lowercase()
                val results = allArticles.filter { article ->
                    article.englishTitle.lowercase().contains(searchQueryLower)
                }
                
                println("DEBUG: Simple search found ${results.size} results")
                
                // 更新搜索结果
                _searchResults.value = results
                
                // 如果是分页模式，我们需要通过正确的方式更新结果
                if (_usePaginationMode.value) {
                    // 在分页模式下，我们应该通过搜索方法来更新结果
                    println("DEBUG: In pagination mode, should use search method instead")
                }
                
            } catch (e: Exception) {
                println("DEBUG: testSearch failed: ${e.message}")
            }
        }
    }
    
    /**
     * 释放资源
     */
    override fun onCleared() {
        super.onCleared()
        articleTtsHelper_7ree.release()
    }
}
