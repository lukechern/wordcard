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
    
    // 文章列表状态
    private val _articles = MutableStateFlow<List<ArticleEntity_7ree>>(emptyList())
    val articles: StateFlow<List<ArticleEntity_7ree>> = _articles.asStateFlow()
    
    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
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
    
    init {
        initializeArticleFlow()
    }
    
    /**
     * 初始化文章列表数据流
     */
    private fun initializeArticleFlow() {
        articleListHelper_7ree.initializeArticleFlow(
            viewModelScope,
            { loading -> _isLoading.value = loading },
            { articles -> _articles.value = articles },
            { error -> _operationResult.value = error }
        )
    }
    
    /**
     * 手动刷新文章列表（用于调试）
     */
    fun refreshArticles() {
        // 由于我们使用Flow，数据库的任何更改都应该自动反映到UI
        // 这个方法主要用于调试和强制刷新
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
                // 如果是智能生成文章，更新智能生成文章卡片的状态
                if (_showSmartGenerationCard.value) {
                    if (result == "文章生成成功！") {
                        // 获取最新生成的文章标题
                        val latestArticle = _articles.value.firstOrNull()
                        val title = latestArticle?.englishTitle ?: "无标题"
                        
                _smartGenerationStatus.value = "文章已生成，标题如下：\n$title"
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
        articleDetailHelper_7ree.handleArticleSelection(
            article,
            viewModelScope,
            { selectedArticle -> _selectedArticle.value = selectedArticle },
            { stats -> _keywordStats.value = stats },
            _articles.value
        )
        
        _showDetailScreen.value = true
        
        // 增加浏览次数
        articleDetailHelper_7ree.handleArticleSelection(
            article,
            viewModelScope,
            { selectedArticle -> incrementViewCount(selectedArticle.id) },
            { stats -> },
            _articles.value
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
     * 智能生成文章
     */
    fun smartGenerateArticle(type: com.x7ree.wordcard.ui.SmartGenerationType_7ree) {
        viewModelScope.launch {
            try {
                // 保存当前智能生成类型
                currentSmartGenerationType = type
                
                // 显示智能生成文章卡片
                _showSmartGenerationCard.value = true
                _isGenerating.value = true
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
                
                _smartGenerationStatus.value = "${apiName}正在生成文章中，请稍候……"
                
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
     * 释放资源
     */
    override fun onCleared() {
        super.onCleared()
        articleTtsHelper_7ree.release()
    }
}
