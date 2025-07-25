package com.x7ree.wordcard.query.manager

import androidx.lifecycle.viewModelScope
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.query.state.WordQueryState_7ree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import android.util.Log

/**
 * 单词查询业务逻辑管理器
 */
class WordQueryManager_7ree(
    private val apiService_7ree: OpenAiApiService_7ree,
    private val wordRepository_7ree: WordRepository_7ree,
    private val queryState_7ree: WordQueryState_7ree,
    private val coroutineScope: CoroutineScope
) {

    fun queryWord_7ree() {
        val wordInput = queryState_7ree.wordInput_7ree
        if (wordInput.isBlank()) {
            queryState_7ree.updateQueryResult_7ree("请输入英文单词")
            return
        }

        queryState_7ree.updateLoadingState_7ree(true)
        queryState_7ree.updateWordConfirmed_7ree(true)
        queryState_7ree.updateQueryResult_7ree("")
        queryState_7ree.updateFromCache_7ree(false)
        
        // 记录查询开始时间
        // val queryStartTime = System.currentTimeMillis()
        Log.d("WordQueryManager_7ree", "DEBUG: 开始查询单词: ${wordInput}")
        
        coroutineScope.launch {
            try {
                // 1. 先查询本地数据库
                val cachedWord_7ree = wordRepository_7ree.getWord_7ree(wordInput)
                
                if (cachedWord_7ree != null) {
                    // 本地有缓存数据
                    Log.d("WordQueryManager_7ree", "DEBUG: 从缓存获取到数据")
                    queryState_7ree.updateQueryResult_7ree(cachedWord_7ree.apiResult)
                    queryState_7ree.updateFromCache_7ree(true)
                    
                    // 检查并自动补充缺失的字段信息
                    checkAndUpdateMissingFields_7ree(cachedWord_7ree)
                    
                    // 增加浏览次数
                    wordRepository_7ree.incrementViewCount_7ree(wordInput)
                    
                    // 更新当前单词信息
                    updateCurrentWordInfo_7ree()
                } else {
                    // 本地没有数据，发起API请求
                    Log.d("WordQueryManager_7ree", "DEBUG: 本地无缓存，发起API请求")
                    var isFirstChunk_7ree = true
                    
                    apiService_7ree.queryWordStreamSimple_7ree(wordInput).collect { chunk_7ree ->
                        
                        Log.d("WordQueryManager_7ree", "DEBUG: 收到流式内容块: $chunk_7ree")
                        val currentResult = queryState_7ree.queryResult_7ree
                        queryState_7ree.updateQueryResult_7ree(currentResult + chunk_7ree)
                        
                        // 收到第一个内容块时关闭加载动画
                        if (isFirstChunk_7ree) {
                            queryState_7ree.updateLoadingState_7ree(false)
                            isFirstChunk_7ree = false
                        }
                    }

                    
                    // API请求成功后，保存到本地数据库
                    val queryResult = queryState_7ree.queryResult_7ree
                    if (queryResult.isNotBlank() && !queryResult.startsWith("错误:")) {
                        Log.d("WordQueryManager_7ree", "DEBUG: 保存查询结果到数据库")
                        Log.d("WordQueryManager_7ree", "DEBUG: 查询结果长度: ${queryResult.length}")
                        Log.d("WordQueryManager_7ree", "DEBUG: 查询结果前200字符: ${queryResult.take(200)}")
                        wordRepository_7ree.saveWord_7ree(wordInput, queryResult)
                        
                        // 更新当前单词信息
                        updateCurrentWordInfo_7ree()
                    } else {
                        Log.e("WordQueryManager_7ree", "DEBUG: 查询结果无效或包含错误")
                        Log.e("WordQueryManager_7ree", "DEBUG: 查询结果: $queryResult")
                    }
                }

            } catch (e: Exception) {
                Log.e("WordQueryManager_7ree", "DEBUG: 查询异常: ${e.message}", e)
                queryState_7ree.updateQueryResult_7ree("查询失败: ${e.localizedMessage}")

            } finally {
                queryState_7ree.updateLoadingState_7ree(false)
            }
        }
    }

    fun loadWordFromHistory_7ree(word: String) {
        queryState_7ree.updateWordInput_7ree(word)
        queryState_7ree.updateWordConfirmed_7ree(true)
        queryState_7ree.updateFromCache_7ree(false)
        
        coroutineScope.launch {
            try {
                val cachedWord_7ree = wordRepository_7ree.getWord_7ree(word)
                if (cachedWord_7ree != null) {
                    queryState_7ree.updateQueryResult_7ree(cachedWord_7ree.apiResult)
                    queryState_7ree.updateFromCache_7ree(true)
                    
                    // 检查并自动补充缺失的字段信息
                    checkAndUpdateMissingFields_7ree(cachedWord_7ree)
                    
                    // 增加浏览次数
                    wordRepository_7ree.incrementViewCount_7ree(word)
                    // 更新当前单词信息
                    updateCurrentWordInfo_7ree()
                } else {
                    queryState_7ree.updateQueryResult_7ree("未找到该单词的记录")
                    queryState_7ree.updateCurrentWordInfo_7ree(null)
                }
            } catch (e: Exception) {
                queryState_7ree.updateQueryResult_7ree("加载失败: ${e.localizedMessage}")
                queryState_7ree.updateCurrentWordInfo_7ree(null)
            }
        }
    }

    private suspend fun updateCurrentWordInfo_7ree() {
        val wordInput = queryState_7ree.wordInput_7ree
        if (wordInput.isNotBlank()) {
            val wordInfo = wordRepository_7ree.getWord_7ree(wordInput)
            queryState_7ree.updateCurrentWordInfo_7ree(wordInfo)
        }
    }

    fun getExamplesSpeechText_7ree(): String {
        val rawText_7ree = queryState_7ree.queryResult_7ree
        val lines_7ree = rawText_7ree.split("\n")
        val spokenContent_7ree = StringBuilder()

        // println("DEBUG: getExamplesSpeechText_7ree - 开始处理例句")
        // println("DEBUG: 原始文本行数: ${lines_7ree.size}")

        var inEnglishExamplesSection_7ree = false
        var exampleCount_7ree = 0
        for (line_7ree in lines_7ree) {
            val trimmedLine_7ree = line_7ree.trim()

            if (trimmedLine_7ree.matches(Regex("^#+\\s*英文例句.*$"))) {
                inEnglishExamplesSection_7ree = true
                // println("DEBUG: 找到英文例句标题，行号: $index, 内容: '$trimmedLine_7ree'")
                continue // Skip the header itself
            } else if (trimmedLine_7ree.startsWith("#") && inEnglishExamplesSection_7ree) {
                // println("DEBUG: 遇到新标题，停止处理例句，行号: $index, 内容: '$trimmedLine_7ree'")
                // Stop if we hit another header after the English examples section
                break
            }

            if (inEnglishExamplesSection_7ree && trimmedLine_7ree.isNotBlank()) {
                // Filter out lines that are not example sentences or contain Chinese
                val containsChinese_7ree = trimmedLine_7ree.contains(Regex("\\p{IsHan}"))
                val isExampleLine_7ree = trimmedLine_7ree.matches(Regex("^\\[\\d+\\]\\.\\s*.*$")) || // Matches [N]. Example
                    trimmedLine_7ree.matches(Regex("^[\\d]+\\.\\s*.*$")) // Matches N. Example

                // println("DEBUG: 处理行 $index: '$trimmedLine_7ree'")
                // println("DEBUG: 包含中文: $containsChinese_7ree, 是例句行: $isExampleLine_7ree")

                if (isExampleLine_7ree && !containsChinese_7ree) {
                    val cleanedLine_7ree = trimmedLine_7ree
                        .replace(Regex("[*_`~#+->]"), "") // Remove common markdown symbols
                        .replace(Regex("^\\[\\d+\\]\\.\\s*"), "") // Remove [N]. prefix
                        .replace(Regex("^[\\d]+\\.\\s*"), "") // Remove N. prefix
                        .replace(Regex("""\[.*?\]"""), "") // Remove [text](link) markdown
                        .replace(Regex("""\(.*?\)"""), "") // Remove (link) after [text] or standalone
                        .trim()
                    
                    // println("DEBUG: 清理后的例句: '$cleanedLine_7ree'")
                    
                    if (cleanedLine_7ree.isNotBlank()) {
                        // 在例句之间添加停顿
                        if (exampleCount_7ree > 0) {
                            spokenContent_7ree.append("...... ") // 例句之间的停顿
                        }
                        spokenContent_7ree.append(cleanedLine_7ree).append(" ")
                        exampleCount_7ree++
                        // println("DEBUG: 添加例句 $exampleCount_7ree: '$cleanedLine_7ree'")
                    }
                }
            }
        }

        val result_7ree = spokenContent_7ree.toString().trim()
        // println("DEBUG: getExamplesSpeechText_7ree - 最终结果: '$result_7ree'")
        // println("DEBUG: 找到的例句数量: $exampleCount_7ree")

        return result_7ree
    }
    
    /**
     * 检查并自动补充缺失的字段信息
     * 如果中文释义、音标、词性字段为空，则从API结果中解析并更新到数据库
     */
    private suspend fun checkAndUpdateMissingFields_7ree(wordEntity: com.x7ree.wordcard.data.WordEntity_7ree) {
        // 检查是否需要更新（任一字段为空）
        val needsUpdate = wordEntity.chineseDefinition.isEmpty() || 
                         wordEntity.phonetic.isEmpty() || 
                         wordEntity.partOfSpeech.isEmpty()
        
        if (needsUpdate) {
            // println("DEBUG: 检测到缺失字段，开始自动补充")
            // println("DEBUG: 当前字段状态 - 中文释义: '${wordEntity.chineseDefinition}', 音标: '${wordEntity.phonetic}', 词性: '${wordEntity.partOfSpeech}'")
            
            try {
                // 从API结果中解析信息
                val wordInfo = com.x7ree.wordcard.utils.MarkdownParser_7ree.parseWordInfo(wordEntity.apiResult)
                
                // 只有解析出有效信息才更新
                if (wordInfo.chineseDefinition.isNotEmpty() || 
                    wordInfo.phonetic.isNotEmpty() || 
                    wordInfo.partOfSpeech.isNotEmpty()) {
                    
                    val updatedWord = wordEntity.copy(
                        chineseDefinition = if (wordEntity.chineseDefinition.isEmpty()) wordInfo.chineseDefinition else wordEntity.chineseDefinition,
                        phonetic = if (wordEntity.phonetic.isEmpty()) wordInfo.phonetic else wordEntity.phonetic,
                        partOfSpeech = if (wordEntity.partOfSpeech.isEmpty()) wordInfo.partOfSpeech else wordEntity.partOfSpeech
                    )
                    
                    // 更新到数据库
                    wordRepository_7ree.updateWord_7ree(updatedWord)
                    
                    // println("DEBUG: 字段补充完成")
                    // println("DEBUG: 更新后字段 - 中文释义: '${updatedWord.chineseDefinition}', 音标: '${updatedWord.phonetic}', 词性: '${updatedWord.partOfSpeech}'")
                } else {
                    // println("DEBUG: 未能从API结果中解析出有效的字段信息")
                }
            } catch (e: Exception) {
                // println("DEBUG: 补充字段信息时发生错误: ${e.message}")
                e.printStackTrace()
            }
        } else {
            // println("DEBUG: 字段信息完整，无需补充")
        }
    }
}
