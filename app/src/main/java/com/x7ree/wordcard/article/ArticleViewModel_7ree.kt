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
    private val articleTtsManager_7ree = ArticleTtsManager_7ree(context)
    
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
    val isReading: StateFlow<Boolean> = articleTtsManager_7ree.isReading
    val isTtsInitializing: StateFlow<Boolean> = articleTtsManager_7ree.isInitializing
    val ttsErrorMessage: StateFlow<String?> = articleTtsManager_7ree.errorMessage
    val ttsButtonState: StateFlow<ArticleTtsManager_7ree.TtsButtonState> = articleTtsManager_7ree.buttonState
    val currentTtsEngine: StateFlow<String> = articleTtsManager_7ree.currentEngine
    
    init {
        initializeArticleFlow()
    }
    
    /**
     * 初始化文章列表数据流
     */
    private fun initializeArticleFlow() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                articleRepository_7ree.getAllArticles_7ree().collect { articleList ->
                    android.util.Log.d("ArticleViewModel", "文章列表更新，共 ${articleList.size} 篇文章")
                    _articles.value = articleList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                android.util.Log.e("ArticleViewModel", "加载文章失败: ${e.message}", e)
                _operationResult.value = "加载文章失败: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 手动刷新文章列表（用于调试）
     */
    fun refreshArticles() {
        android.util.Log.d("ArticleViewModel", "手动刷新文章列表")
        // 由于我们使用Flow，数据库的任何更改都应该自动反映到UI
        // 这个方法主要用于调试和强制刷新
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
                val parser = ArticleMarkdownParser_7ree()
                val parsedResult = parser.parseArticleMarkdown(apiResult)
                
                // 保存到数据库
                val articleId = articleRepository_7ree.saveArticle_7ree(
                    keyWords = parsedResult.keywords, // 使用解析出的关键词
                    apiResult = apiResult,
                    englishTitle = parsedResult.englishTitle,
                    titleTranslation = parsedResult.chineseTitle,
                    englishContent = parsedResult.englishContent,
                    chineseContent = parsedResult.chineseContent
                )
                
                _operationResult.value = "文章生成成功！"
                
                // 文章生成成功，Flow会自动更新列表
                android.util.Log.d("ArticleViewModel", "文章生成成功，等待Flow自动更新")
                
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
                // Flow会自动更新文章列表
                android.util.Log.d("ArticleViewModel", "收藏状态已更新，等待Flow自动更新")
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
                // Flow会自动更新文章列表
                android.util.Log.d("ArticleViewModel", "文章已删除，等待Flow自动更新")
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
        viewModelScope.launch {
            try {
                // 检查文章内容是否包含 ### 符号，如果有说明解析失败
                val needsReparse = article.englishContent.contains("###") || 
                                 article.chineseContent.contains("###") ||
                                 article.englishTitle == "Generated Article" ||
                                 article.chineseContent == "翻译暂不可用"
                
                if (needsReparse && article.apiResult.isNotEmpty()) {
                    android.util.Log.d("ArticleDetail", "检测到文章解析失败，开始重新解析...")
                    android.util.Log.d("ArticleDetail", "原因: englishContent包含###=${article.englishContent.contains("###")}, chineseContent包含###=${article.chineseContent.contains("###")}, 默认标题=${article.englishTitle == "Generated Article"}, 默认翻译=${article.chineseContent == "翻译暂不可用"}")
                    
                    // 重新解析文章
                    val parser = ArticleMarkdownParser_7ree()
                    val parsedResult = parser.parseArticleMarkdown(article.apiResult)
                    
                    // 更新数据库
                    val updatedArticle = article.copy(
                        englishTitle = parsedResult.englishTitle,
                        titleTranslation = parsedResult.chineseTitle,
                        englishContent = parsedResult.englishContent,
                        chineseContent = parsedResult.chineseContent,
                        keyWords = parsedResult.keywords
                    )
                    
                    // 保存更新后的文章到数据库
                    android.util.Log.d("ArticleDetail", "准备更新数据库，文章ID: ${updatedArticle.id}")
                    android.util.Log.d("ArticleDetail", "更新前英文标题: ${article.englishTitle}")
                    android.util.Log.d("ArticleDetail", "更新后英文标题: ${updatedArticle.englishTitle}")
                    android.util.Log.d("ArticleDetail", "更新前中文内容: ${article.chineseContent}")
                    android.util.Log.d("ArticleDetail", "更新后中文内容: ${updatedArticle.chineseContent}")
                    
                    articleRepository_7ree.updateArticle_7ree(updatedArticle)
                    android.util.Log.d("ArticleDetail", "数据库更新操作已执行")
                    
                    // 验证数据库更新是否成功
                    val verifyArticle = articleRepository_7ree.getArticle_7ree(updatedArticle.id)
                    if (verifyArticle != null) {
                        android.util.Log.d("ArticleDetail", "数据库验证成功")
                        android.util.Log.d("ArticleDetail", "验证后英文标题: ${verifyArticle.englishTitle}")
                        android.util.Log.d("ArticleDetail", "验证后中文标题: ${verifyArticle.titleTranslation}")
                        android.util.Log.d("ArticleDetail", "验证后中文内容: ${verifyArticle.chineseContent}")
                        android.util.Log.d("ArticleDetail", "验证后关键词: ${verifyArticle.keyWords}")
                        
                        // 使用数据库中验证后的文章
                        _selectedArticle.value = verifyArticle
                        
                        android.util.Log.d("ArticleDetail", "数据库更新完成，Flow应该会自动更新文章列表")
                    } else {
                        android.util.Log.e("ArticleDetail", "数据库验证失败，文章未找到")
                        // 使用更新后的文章作为备选
                        _selectedArticle.value = updatedArticle
                    }
                } else {
                    // 文章解析正常，直接使用
                    _selectedArticle.value = article
                }
                
                _showDetailScreen.value = true
                
                // 输出文章详情日志
                val currentArticle = _selectedArticle.value!!
                android.util.Log.d("ArticleDetail", "=== 文章详情页打开 ===")
                android.util.Log.d("ArticleDetail", "文章ID: ${currentArticle.id}")
                android.util.Log.d("ArticleDetail", "生成时间戳: ${currentArticle.generationTimestamp}")
                android.util.Log.d("ArticleDetail", "生成时间: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(currentArticle.generationTimestamp))}")
                android.util.Log.d("ArticleDetail", "关键词: ${currentArticle.keyWords}")
                android.util.Log.d("ArticleDetail", "浏览次数: ${currentArticle.viewCount}")
                android.util.Log.d("ArticleDetail", "是否收藏: ${currentArticle.isFavorite}")
                android.util.Log.d("ArticleDetail", "英文标题: ${currentArticle.englishTitle}")
                android.util.Log.d("ArticleDetail", "标题翻译: ${currentArticle.titleTranslation}")
                android.util.Log.d("ArticleDetail", "英文内容长度: ${currentArticle.englishContent.length} 字符")
                android.util.Log.d("ArticleDetail", "英文内容: ${currentArticle.englishContent}")
                android.util.Log.d("ArticleDetail", "中文内容长度: ${currentArticle.chineseContent.length} 字符")
                android.util.Log.d("ArticleDetail", "中文内容: ${currentArticle.chineseContent}")
                android.util.Log.d("ArticleDetail", "API原始结果长度: ${currentArticle.apiResult.length} 字符")
                android.util.Log.d("ArticleDetail", "API原始结果: ${currentArticle.apiResult}")
                android.util.Log.d("ArticleDetail", "=== 文章详情日志结束 ===")
                
                // 增加浏览次数
                incrementViewCount(currentArticle.id)
                
            } catch (e: Exception) {
                android.util.Log.e("ArticleDetail", "选择文章时发生错误: ${e.message}", e)
                // 发生错误时仍然显示原文章
                _selectedArticle.value = article
                _showDetailScreen.value = true
                incrementViewCount(article.id)
            }
        }
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
        android.util.Log.d("ArticleTts", "开始朗读文章: ${article.englishTitle}")
        articleTtsManager_7ree.readArticle(
            englishContent = article.englishContent,
            englishTitle = article.englishTitle
        )
    }
    
    /**
     * 停止朗读
     */
    fun stopReading() {
        android.util.Log.d("ArticleTts", "停止朗读")
        articleTtsManager_7ree.stopReading()
    }
    
    /**
     * 切换朗读状态（朗读/停止）
     */
    fun toggleReading() {
        _selectedArticle.value?.let { article ->
            android.util.Log.d("ArticleTts", "切换朗读状态，当前按钮状态: ${ttsButtonState.value}")
            articleTtsManager_7ree.toggleReading(
                englishContent = article.englishContent,
                englishTitle = article.englishTitle
            )
        }
    }
    
    /**
     * 清除TTS错误信息
     */
    fun clearTtsError() {
        articleTtsManager_7ree.clearError()
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
     * 释放资源
     */
    override fun onCleared() {
        super.onCleared()
        android.util.Log.d("ArticleViewModel", "释放ViewModel资源")
        articleTtsManager_7ree.release()
    }
}