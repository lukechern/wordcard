package com.x7ree.wordcard.article

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.config.AppConfigManager_7ree
import com.x7ree.wordcard.config.PromptConfig_7ree
import com.x7ree.wordcard.data.ArticleEntity_7ree
import com.x7ree.wordcard.data.ArticleRepository_7ree

/**
 * 文章功能的ViewModel
 */
class ArticleViewModel_7ree(
    private val articleRepository_7ree: ArticleRepository_7ree,
    private val apiService_7ree: OpenAiApiService_7ree,
    private val context: Context
) : ViewModel() {
    
    private val appConfigManager_7ree = AppConfigManager_7ree(context)
    
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
    
    init {
        loadArticles()
    }
    
    /**
     * 加载文章列表
     */
    fun loadArticles() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                articleRepository_7ree.getAllArticles_7ree().collect { articleList ->
                    _articles.value = articleList
                }
            } catch (e: Exception) {
                _operationResult.value = "加载文章失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 生成新文章
     */
    fun generateArticle(keyWords: String) {
        viewModelScope.launch {
            try {
                _isGenerating.value = true
                _operationResult.value = "正在生成文章..."
                
                // 获取提示词配置
                val promptConfig = appConfigManager_7ree.loadPromptConfig_7ree()
                
                // 构建生成文章的提示词
                val prompt = buildArticlePrompt(keyWords, promptConfig)
                
                // 调用API生成文章
                val apiResultWrapper = apiService_7ree.queryWord_7ree(prompt)
                
                // 检查API调用结果
                val apiResult = apiResultWrapper.getOrElse { error ->
                    throw Exception("API调用失败: ${error.message}")
                }
                
                // 解析API结果
                val parsedResult = parseArticleResult(apiResult)
                
                // 保存到数据库
                val articleId = articleRepository_7ree.saveArticle_7ree(
                    keyWords = parsedResult.keywords, // 使用解析出的关键词
                    apiResult = apiResult,
                    englishTitle = parsedResult.title,
                    titleTranslation = parsedResult.titleTranslation,
                    englishContent = parsedResult.content,
                    chineseContent = parsedResult.translation
                )
                
                _operationResult.value = "文章生成成功！"
                
                // 重新加载文章列表
                loadArticles()
                
            } catch (e: Exception) {
                _operationResult.value = "文章生成失败: ${e.message}"
            } finally {
                _isGenerating.value = false
            }
        }
    }
    
    /**
     * 切换文章收藏状态
     */
    fun toggleFavorite(articleId: Long) {
        viewModelScope.launch {
            try {
                articleRepository_7ree.toggleFavorite_7ree(articleId)
                _operationResult.value = "收藏状态已更新"
                // 重新加载文章列表以更新UI
                loadArticles()
            } catch (e: Exception) {
                _operationResult.value = "更新收藏状态失败: ${e.message}"
            }
        }
    }
    
    /**
     * 增加文章浏览次数
     */
    fun incrementViewCount(articleId: Long) {
        viewModelScope.launch {
            try {
                articleRepository_7ree.incrementViewCount_7ree(articleId)
            } catch (e: Exception) {
                // 浏览次数更新失败不需要显示错误信息
            }
        }
    }
    
    /**
     * 删除文章
     */
    fun deleteArticle(articleId: Long) {
        viewModelScope.launch {
            try {
                articleRepository_7ree.deleteArticle_7ree(articleId)
                _operationResult.value = "文章已删除"
                loadArticles()
            } catch (e: Exception) {
                _operationResult.value = "删除文章失败: ${e.message}"
            }
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
        _selectedArticle.value = article
        _showDetailScreen.value = true
        // 增加浏览次数
        incrementViewCount(article.id)
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
     * 分享文章
     */
    fun shareArticle(article: ArticleEntity_7ree) {
        // 暂时简化分享功能
        _operationResult.value = "分享功能开发中..."
    }
    
    /**
     * 构建文章生成提示词
     */
    private fun buildArticlePrompt(keyWords: String, promptConfig: PromptConfig_7ree): String {
        return """
            ${promptConfig.articleGenerationPrompt_7ree}
            
            请基于以下关键词生成文章：$keyWords
            
            请严格按照以下模板格式输出：
            ${promptConfig.articleOutputTemplate_7ree}
        """.trimIndent()
    }
    
    /**
     * 解析文章生成结果
     */
    private fun parseArticleResult(apiResult: String): ArticleParseResult {
        return try {
            val lines = apiResult.split("\n")
            var englishTitle = ""
            var chineseTitle = ""
            var content = ""
            var keywords = ""
            var translation = ""
            
            var currentSection = ""
            val contentBuilder = StringBuilder()
            val translationBuilder = StringBuilder()
            
            for (line in lines) {
                when {
                    line.startsWith("# 文章标题") || line.startsWith("# English Title") -> {
                        currentSection = "title"
                    }
                    line.startsWith("# 标题翻译") || line.startsWith("# Chinese Title") -> {
                        currentSection = "chineseTitle"
                    }
                    line.startsWith("# 文章内容") || line.startsWith("# Article Content") -> {
                        currentSection = "content"
                    }
                    line.startsWith("# 重点单词") || line.startsWith("# Keywords") -> {
                        currentSection = "keywords"
                    }
                    line.startsWith("# 文章翻译") || line.startsWith("# Chinese Translation") -> {
                        currentSection = "translation"
                    }
                    line.isNotBlank() && !line.startsWith("#") -> {
                        when (currentSection) {
                            "title" -> englishTitle = line.trim()
                            "chineseTitle" -> chineseTitle = line.trim()
                            "content" -> contentBuilder.appendLine(line)
                            "keywords" -> keywords = line.trim()
                            "translation" -> translationBuilder.appendLine(line)
                        }
                    }
                }
            }
            
            content = contentBuilder.toString().trim()
            translation = translationBuilder.toString().trim()
            
            // 如果解析失败，使用默认值
            if (englishTitle.isEmpty()) englishTitle = "Generated Article"
            if (chineseTitle.isEmpty()) chineseTitle = generateChineseTitle(englishTitle)
            if (content.isEmpty()) content = apiResult
            if (translation.isEmpty()) translation = "翻译暂不可用"
            if (keywords.isEmpty()) keywords = "无关键词"
            
            ArticleParseResult(
                title = englishTitle,
                titleTranslation = chineseTitle,
                content = content,
                translation = translation,
                keywords = keywords
            )
        } catch (e: Exception) {
            // 解析失败时返回默认结果
            ArticleParseResult(
                title = "Generated Article",
                titleTranslation = "生成的文章",
                content = apiResult,
                translation = "翻译暂不可用",
                keywords = "解析失败"
            )
        }
    }
    
    /**
     * 生成简单的中文标题
     */
    private fun generateChineseTitle(englishTitle: String): String {
        return when {
            englishTitle.contains("story", ignoreCase = true) -> "故事"
            englishTitle.contains("adventure", ignoreCase = true) -> "冒险"
            englishTitle.contains("journey", ignoreCase = true) -> "旅程"
            englishTitle.contains("life", ignoreCase = true) -> "生活"
            englishTitle.contains("nature", ignoreCase = true) -> "自然"
            englishTitle.contains("technology", ignoreCase = true) -> "科技"
            englishTitle.contains("education", ignoreCase = true) -> "教育"
            englishTitle.contains("health", ignoreCase = true) -> "健康"
            else -> "英语文章"
        }
    }
    
    /**
     * 文章解析结果数据类
     */
    private data class ArticleParseResult(
        val title: String,
        val titleTranslation: String,
        val content: String,
        val translation: String,
        val keywords: String
    )
}