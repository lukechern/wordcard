package com.x7ree.wordcard.query.manager

import android.net.Uri
import com.x7ree.wordcard.data.DataExportImportManager_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.query.state.PaginationState_7ree
import com.x7ree.wordcard.query.state.WordQueryState_7ree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 数据管理器
 */
class DataManager_7ree(
    private val dataExportImportManager_7ree: DataExportImportManager_7ree,
    private val wordRepository_7ree: WordRepository_7ree,
    private val paginationState_7ree: PaginationState_7ree,
    private val queryState_7ree: WordQueryState_7ree,
    private val coroutineScope: CoroutineScope
) {

    fun loadWordCount_7ree() {
        coroutineScope.launch(Dispatchers.IO) {
            wordRepository_7ree.wordCount_7ree.collect { count ->
                withContext(Dispatchers.Main) {
                    paginationState_7ree.updateWordCount_7ree(count)
                }
            }
        }
    }
    
    fun loadTotalViews_7ree() {
        coroutineScope.launch(Dispatchers.IO) {
            wordRepository_7ree.getTotalViews_7ree.collect { totalViews ->
                withContext(Dispatchers.Main) {
                    paginationState_7ree.updateTotalViews_7ree(totalViews)
                }
            }
        }
    }

    fun loadInitialWords_7ree() {
        coroutineScope.launch {
            try {
                paginationState_7ree.resetPagination_7ree()
                val words = if (paginationState_7ree.showFavoritesOnly_7ree.value) {
                    wordRepository_7ree.getFavoriteWordsPaged_7ree(paginationState_7ree.pageSize_7ree, 0)
                } else {
                    wordRepository_7ree.getWordsPaged_7ree(paginationState_7ree.pageSize_7ree, 0)
                }
                
                // 检查并自动补充缺失的字段信息
                val updatedWords = checkAndUpdateMissingFieldsForWords_7ree(words)
                paginationState_7ree.updatePagedWords_7ree(updatedWords)
                
                // 如果返回的数据少于页面大小，说明没有更多数据了
                if (words.size < paginationState_7ree.pageSize_7ree) {
                    paginationState_7ree.updateHasMoreData_7ree(false)
                }
                
                // println("DEBUG: 初始加载完成，共${words.size}个单词")
            } catch (e: Exception) {
                // println("DEBUG: 初始加载失败: ${e.message}")
            }
        }
    }

    fun searchWords_7ree(query: String) {
        if (query.isBlank()) {
            // 如果搜索查询为空，加载初始单词
            loadInitialWords_7ree()
            return
        }
        
        coroutineScope.launch {
            try {
                wordRepository_7ree.searchWords_7ree(query).collect { words ->
                    withContext(Dispatchers.Main) {
                        // 检查并自动补充缺失的字段信息
                        val updatedWords = checkAndUpdateMissingFieldsForWords_7ree(words)
                        // 搜索结果不需要分页，直接显示所有结果
                        paginationState_7ree.updatePagedWords_7ree(updatedWords)
                        paginationState_7ree.updateHasMoreData_7ree(false) // 搜索结果不支持分页加载
                        // println("DEBUG: 搜索完成，找到${words.size}个匹配的单词")
                    }
                }
            } catch (e: Exception) {
                // println("DEBUG: 搜索失败: ${e.message}")
            }
        }
    }
    
    fun loadMoreWords_7ree() {
        if (paginationState_7ree.isLoadingMore_7ree.value || !paginationState_7ree.hasMoreData_7ree.value) {
            return
        }
        
        coroutineScope.launch {
            try {
                paginationState_7ree.updateLoadingMore_7ree(true)
                paginationState_7ree.incrementPage_7ree()
                val offset = paginationState_7ree.currentPage_7ree * paginationState_7ree.pageSize_7ree
                val newWords = if (paginationState_7ree.showFavoritesOnly_7ree.value) {
                    wordRepository_7ree.getFavoriteWordsPaged_7ree(paginationState_7ree.pageSize_7ree, offset)
                } else {
                    wordRepository_7ree.getWordsPaged_7ree(paginationState_7ree.pageSize_7ree, offset)
                }
                
                if (newWords.isNotEmpty()) {
                    // 检查并自动补充缺失的字段信息
                    val updatedNewWords = checkAndUpdateMissingFieldsForWords_7ree(newWords)
                    paginationState_7ree.addMoreWords_7ree(updatedNewWords)
                    
                    // 如果返回的数据少于页面大小，说明没有更多数据了
                    if (newWords.size < paginationState_7ree.pageSize_7ree) {
                        paginationState_7ree.updateHasMoreData_7ree(false)
                    }
                    
                    // println("DEBUG: 加载更多完成，新增${newWords.size}个单词")
                } else {
                    paginationState_7ree.updateHasMoreData_7ree(false)
                    // println("DEBUG: 没有更多数据")
                }
            } catch (e: Exception) {
                // println("DEBUG: 加载更多失败: ${e.message}")
            } finally {
                paginationState_7ree.updateLoadingMore_7ree(false)
            }
        }
    }

    fun toggleFavorite_7ree(word: String) {
        coroutineScope.launch {
            try {
                // 获取当前收藏状态
                val currentWord = wordRepository_7ree.getWord_7ree(word)
                val wasAlreadyFavorite = currentWord?.isFavorite ?: false
                
                // 切换收藏状态
                wordRepository_7ree.toggleFavorite_7ree(word)
                
                // 重新获取更新后的单词信息并更新UI状态
                val updatedWord = wordRepository_7ree.getWord_7ree(word)
                if (updatedWord != null) {
                    queryState_7ree.updateCurrentWordInfo_7ree(updatedWord)
                    // println("DEBUG: 收藏状态已更新，UI状态已刷新: ${updatedWord.isFavorite}")
                }
                
                // 设置操作结果提示
                queryState_7ree.updateOperationResult_7ree(
                    if (wasAlreadyFavorite) "已取消收藏" else "已添加收藏"
                )
                
                // println("DEBUG: 收藏操作成功: $word, 原状态: $wasAlreadyFavorite, 新状态: ${updatedWord?.isFavorite}")
            } catch (e: Exception) {
                queryState_7ree.updateOperationResult_7ree("收藏操作失败: ${e.message}")
                // println("DEBUG: 收藏操作失败: ${e.message}")
            }
        }
    }

    fun setFavorite_7ree(word: String, isFavorite: Boolean) {
        coroutineScope.launch {
            try {
                // 设置收藏状态
                wordRepository_7ree.setFavorite_7ree(word, isFavorite)
                
                // 重新获取更新后的单词信息并更新UI状态
                val updatedWord = wordRepository_7ree.getWord_7ree(word)
                if (updatedWord != null) {
                    queryState_7ree.updateCurrentWordInfo_7ree(updatedWord)
                    // println("DEBUG: 收藏状态已设置，UI状态已刷新: ${updatedWord.isFavorite}")
                }
                
                // println("DEBUG: 设置收藏状态成功: $word, 新状态: $isFavorite")
            } catch (e: Exception) {
                // println("DEBUG: 设置收藏状态失败: ${e.message}")
            }
        }
    }
    
    fun deleteWord_7ree(word: String) {
        coroutineScope.launch {
            try {
                wordRepository_7ree.deleteWord_7ree(word)
                
                // 从分页数据中移除已删除的单词
                paginationState_7ree.removeWord_7ree(word)
                
                queryState_7ree.updateOperationResult_7ree("删除成功")
                // println("DEBUG: 单词删除成功: $word")
            } catch (e: Exception) {
                queryState_7ree.updateOperationResult_7ree("删除失败: ${e.message}")
                // println("DEBUG: 单词删除失败: ${e.message}")
            }
        }
    }

    fun onSpellingSuccess_7ree(word: String) {
        coroutineScope.launch {
            try {
                if (word.isNotBlank()) {
                    wordRepository_7ree.incrementSpellingCount_7ree(word)
                    
                    // 重新获取更新后的单词信息并更新UI状态
                    val updatedWord = wordRepository_7ree.getWord_7ree(word)
                    if (updatedWord != null) {
                        queryState_7ree.updateCurrentWordInfo_7ree(updatedWord)
                        // println("DEBUG: 拼写练习成功，UI状态已刷新: ${updatedWord.spellingCount}")
                    }
                    
                    // println("DEBUG: 拼写练习成功，单词: $word")
                }
            } catch (e: Exception) {
                // println("DEBUG: 更新拼写次数失败: ${e.message}")
            }
        }
    }

    fun refreshWordBookWithNewWord_7ree(newWord: String) {
        coroutineScope.launch {
            try {
                // 获取新单词的完整信息
                val newWordEntity = wordRepository_7ree.getWord_7ree(newWord)
                if (newWordEntity != null) {
                    paginationState_7ree.addNewWordToTop_7ree(newWordEntity)
                    // println("DEBUG: 单词本已刷新，新增/更新单词: $newWord")
                }
            } catch (e: Exception) {
                // println("DEBUG: 刷新单词本失败: ${e.message}")
            }
        }
    }
    
    fun exportHistoryData_7ree() {
        coroutineScope.launch {
            try {
                val result = dataExportImportManager_7ree.exportData_7ree()
                result.fold(
                    onSuccess = { filePath ->
                        val fileName = filePath.substringAfterLast("/")
                        queryState_7ree.updateOperationResult_7ree(
                            "数据导出成功！文件: $fileName\n位置: Android/data/com.x7ree.wordcard/files/Downloads/"
                        )
                        // println("DEBUG: 数据导出成功: $filePath")
                    },
                    onFailure = { exception ->
                        queryState_7ree.updateOperationResult_7ree("数据导出失败: ${exception.message}")
                        // println("DEBUG: 数据导出失败: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                queryState_7ree.updateOperationResult_7ree("数据导出失败: ${e.message}")
                // println("DEBUG: 数据导出异常: ${e.message}")
            }
        }
    }
    
    fun importHistoryData_7ree(uri: Uri) {
        coroutineScope.launch {
            try {
                val result = dataExportImportManager_7ree.importData_7ree(uri)
                result.fold(
                    onSuccess = { count ->
                        queryState_7ree.updateOperationResult_7ree("数据导入成功，共导入 $count 条记录")
                        // println("DEBUG: 数据导入成功，共导入 $count 条记录")
                    },
                    onFailure = { exception ->
                        queryState_7ree.updateOperationResult_7ree("数据导入失败: ${exception.message}")
                        // println("DEBUG: 数据导入失败: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                queryState_7ree.updateOperationResult_7ree("数据导入失败: ${e.message}")
                // println("DEBUG: 数据导入异常: ${e.message}")
            }
        }
    }
    
    /**
     * 检查并自动补充单词列表中缺失的字段信息
     * 如果中文释义、音标、词性字段为空，则从API结果中解析并更新到数据库
     */
    private suspend fun checkAndUpdateMissingFieldsForWords_7ree(words: List<com.x7ree.wordcard.data.WordEntity_7ree>): List<com.x7ree.wordcard.data.WordEntity_7ree> {
        val updatedWords = mutableListOf<com.x7ree.wordcard.data.WordEntity_7ree>()
        
        for (word in words) {
            // 检查是否需要更新（任一字段为空）
            val needsUpdate = word.chineseDefinition.isEmpty() || 
                             word.phonetic.isEmpty() || 
                             word.partOfSpeech.isEmpty()
            
            if (needsUpdate) {
                try {
                    // println("DEBUG: 检测到单词 '${word.word}' 缺失字段，开始自动补充")
                    
                    // 从API结果中解析信息
                    val wordInfo = com.x7ree.wordcard.utils.MarkdownParser_7ree.parseWordInfo(word.apiResult)
                    
                    // 只有解析出有效信息才更新
                    if (wordInfo.chineseDefinition.isNotEmpty() || 
                        wordInfo.phonetic.isNotEmpty() || 
                        wordInfo.partOfSpeech.isNotEmpty()) {
                        
                        val updatedWord = word.copy(
                            chineseDefinition = if (word.chineseDefinition.isEmpty()) wordInfo.chineseDefinition else word.chineseDefinition,
                            phonetic = if (word.phonetic.isEmpty()) wordInfo.phonetic else word.phonetic,
                            partOfSpeech = if (word.partOfSpeech.isEmpty()) wordInfo.partOfSpeech else word.partOfSpeech
                        )
                        
                        // 更新到数据库
                        wordRepository_7ree.updateWord_7ree(updatedWord)
                        updatedWords.add(updatedWord)
                        
                        // println("DEBUG: 单词 '${word.word}' 字段补充完成")
                        // println("DEBUG: 中文释义: '${updatedWord.chineseDefinition}', 音标: '${updatedWord.phonetic}', 词性: '${updatedWord.partOfSpeech}'")
                    } else {
                        // println("DEBUG: 未能从API结果中解析出有效的字段信息")
                        updatedWords.add(word)
                    }
                } catch (e: Exception) {
                    // println("DEBUG: 补充单词 '${word.word}' 字段信息时发生错误: ${e.message}")
                    updatedWords.add(word)
                }
            } else {
                // 字段信息完整，无需补充
                updatedWords.add(word)
            }
        }
        
        return updatedWords
    }
}
