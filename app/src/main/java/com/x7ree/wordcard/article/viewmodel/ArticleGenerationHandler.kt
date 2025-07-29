package com.x7ree.wordcard.article.viewmodel

import androidx.lifecycle.viewModelScope
import com.x7ree.wordcard.article.utils.ArticleGenerationHelper2_7ree
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ArticleGenerationHandler(private val state: ArticleState, private val articleGenerationHelper: ArticleGenerationHelper2_7ree) {

    fun generateArticle(keyWords: String, scope: kotlinx.coroutines.CoroutineScope, handleNewArticleGenerated: () -> Unit) {
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
                            delay(100)
                            val latestArticle = if (state.usePaginationMode.value) {
                                // This part needs to be adapted based on how pagination is handled
                                null
                            } else {
                                state.articles.value
                                    .sortedByDescending { it.generationTimestamp }
                                    .firstOrNull()
                            }
                            val title = latestArticle?.englishTitle ?: "无标题"

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