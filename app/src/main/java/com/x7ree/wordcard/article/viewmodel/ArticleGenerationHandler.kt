package com.x7ree.wordcard.article.viewmodel

import androidx.lifecycle.viewModelScope
import com.x7ree.wordcard.article.utils.ArticleGenerationHelper2_7ree
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ArticleGenerationHandler(
    private val state: ArticleState, 
    private val articleGenerationHelper: ArticleGenerationHelper2_7ree,
    private val articleRepository: com.x7ree.wordcard.data.ArticleRepository_7ree? = null
) {
    // 记录生成开始时间，用于确保获取的是新生成的文章
    private var generationStartTime: Long = 0

    fun generateArticle(keyWords: String, scope: kotlinx.coroutines.CoroutineScope, handleNewArticleGenerated: () -> Unit) {
        // 记录生成开始时间
        generationStartTime = System.currentTimeMillis()
        
        articleGenerationHelper.generateArticle(
            keyWords,
            scope,
            { generating -> state._isGenerating.value = generating },
            { result ->
                state._operationResult.value = result

                if (result == "文章生成成功！") {
                    handleNewArticleGenerated()
                }

                if (state.showSmartGenerationCard.value) {
                    if (result == "文章生成成功！") {
                        scope.launch {
                            // 尝试多次获取最新文章，确保数据已更新
                            var latestArticle: com.x7ree.wordcard.data.ArticleEntity_7ree? = null
                            var attempts = 0
                            val maxAttempts = 8 // 增加尝试次数
                            
                            while (latestArticle == null && attempts < maxAttempts) {
                                delay(200L + attempts * 150L) // 调整延时：200ms, 350ms, 500ms, 650ms...
                                
                                latestArticle = try {
                                    // 方法1：直接从数据库获取最新文章（最可靠）
                                    if (articleRepository != null) {
                                        val recentArticles = articleRepository.getAllArticlesSortedByTimeDesc(3, 0) // 获取最新3篇文章
                                        recentArticles.firstOrNull { article ->
                                            // 确保是在生成开始时间之后创建的文章
                                            article.generationTimestamp > generationStartTime &&
                                            // 并且是最近10分钟内生成的（增加时间窗口）
                                            System.currentTimeMillis() - article.generationTimestamp < 10 * 60 * 1000
                                        }
                                    } else {
                                        // 方法2：从状态获取（备用方案）
                                        val articlesFromState = if (state.usePaginationMode.value) {
                                            // 尝试从多个状态源获取文章
                                            state.articles.value.ifEmpty { 
                                                // 如果articles为空，可能数据还在pagedArticles中
                                                emptyList()
                                            }
                                        } else {
                                            state.articles.value
                                        }
                                        
                                        // 按生成时间排序，获取最新的文章
                                        articlesFromState
                                            .sortedByDescending { it.generationTimestamp }
                                            .firstOrNull { article ->
                                                // 确保是在生成开始时间之后创建的文章
                                                article.generationTimestamp > generationStartTime &&
                                                // 并且是最近10分钟内生成的
                                                System.currentTimeMillis() - article.generationTimestamp < 10 * 60 * 1000
                                            }
                                    }
                                } catch (e: Exception) {
                                    null
                                }
                                
                                attempts++
                            }
                            
                            val title = latestArticle?.englishTitle?.takeIf { it.isNotBlank() } ?: "无标题"
                            state._smartGenerationStatus.value = "文章已生成，标题如下：\n$title"
                        }
                    } else if (result.startsWith("文章生成失败")) {
                        state._smartGenerationStatus.value = result
                    }
                }
            }
        )
    }
}