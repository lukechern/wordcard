package com.x7ree.wordcard.article.viewmodel

import androidx.lifecycle.viewModelScope
import com.x7ree.wordcard.article.utils.ArticleDeleteHelper_7ree
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ArticleManagementHandler(private val state: ArticleState, private val articleDeleteHelper: ArticleDeleteHelper_7ree) {

    fun enterManagementMode() {
        state._isManagementMode.value = true
        state._selectedArticleIds.value = emptySet()
    }

    fun exitManagementMode() {
        state._isManagementMode.value = false
        state._selectedArticleIds.value = emptySet()
    }

    fun toggleArticleSelection(articleId: Long) {
        val currentSelected = state.selectedArticleIds.value
        state._selectedArticleIds.value = if (currentSelected.contains(articleId)) {
            currentSelected - articleId
        } else {
            currentSelected + articleId
        }
    }

    fun toggleSelectAll() {
        val currentSelected = state.selectedArticleIds.value
        val allArticleIds = state.articles.value.map { it.id }.toSet()

        state._selectedArticleIds.value = if (currentSelected.size == allArticleIds.size) {
            emptySet()
        } else {
            allArticleIds
        }
    }

    fun deleteSelectedArticles(scope: kotlinx.coroutines.CoroutineScope) {
        val selectedIds = state.selectedArticleIds.value
        if (selectedIds.isEmpty()) {
            state._operationResult.value = "请先选择要删除的文章"
            return
        }

        scope.launch {
            try {
                var successCount = 0
                var failCount = 0

                // 使用同步方式删除文章，确保每个删除操作都完成
                for (articleId in selectedIds) {
                    try {
                        // 直接调用repository的删除方法，而不是通过helper的异步回调
                        articleDeleteHelper.deleteArticleSync(articleId)
                        successCount++
                    } catch (e: Exception) {
                        failCount++
                    }
                }

                val resultMessage = when {
                    failCount == 0 -> "成功删除 ${successCount} 篇文章"
                    successCount == 0 -> "删除失败，共 ${failCount} 篇文章删除失败"
                    else -> "删除完成，成功 ${successCount} 篇，失败 ${failCount} 篇"
                }

                state._operationResult.value = resultMessage

                state._selectedArticleIds.value = emptySet()
                state._isManagementMode.value = false

            } catch (e: Exception) {
                state._operationResult.value = "批量删除失败: ${e.message}"
            }
        }
    }
}