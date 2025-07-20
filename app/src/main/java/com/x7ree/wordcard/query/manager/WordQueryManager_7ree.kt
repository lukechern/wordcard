package com.x7ree.wordcard.query.manager

import androidx.lifecycle.viewModelScope
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.query.state.WordQueryState_7ree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
        val queryStartTime = System.currentTimeMillis()
        println("DEBUG: 开始查询单词: ${wordInput}, 时间: ${queryStartTime}ms")
        
        coroutineScope.launch {
            try {
                // 1. 先查询本地数据库
                val dbStartTime = System.currentTimeMillis()
                val cachedWord_7ree = wordRepository_7ree.getWord_7ree(wordInput)
                val dbEndTime = System.currentTimeMillis()
                val dbQueryTime = dbEndTime - dbStartTime
                println("DEBUG: 数据库查询耗时: ${dbQueryTime}ms")
                
                if (cachedWord_7ree != null) {
                    // 本地有缓存数据
                    println("DEBUG: 从缓存获取到数据")
                    queryState_7ree.updateQueryResult_7ree(cachedWord_7ree.apiResult)
                    queryState_7ree.updateFromCache_7ree(true)
                    
                    // 增加浏览次数
                    val viewCountStartTime = System.currentTimeMillis()
                    wordRepository_7ree.incrementViewCount_7ree(wordInput)
                    val viewCountEndTime = System.currentTimeMillis()
                    println("DEBUG: 增加浏览次数耗时: ${viewCountEndTime - viewCountStartTime}ms")
                    
                    // 更新当前单词信息
                    val updateInfoStartTime = System.currentTimeMillis()
                    updateCurrentWordInfo_7ree()
                    val updateInfoEndTime = System.currentTimeMillis()
                    println("DEBUG: 更新单词信息耗时: ${updateInfoEndTime - updateInfoStartTime}ms")
                } else {
                    // 本地没有数据，发起API请求
                    println("DEBUG: 本地无缓存，发起API请求")
                    val apiStartTime = System.currentTimeMillis()
                    var isFirstChunk_7ree = true
                    var firstChunkTime: Long = 0
                    
                    apiService_7ree.queryWordStreamSimple_7ree(wordInput).collect { chunk_7ree ->
                        val chunkTime = System.currentTimeMillis()
                        if (isFirstChunk_7ree) {
                            firstChunkTime = chunkTime
                            println("DEBUG: 收到第一个内容块，耗时: ${firstChunkTime - apiStartTime}ms")
                        }
                        
                        println("DEBUG: 收到流式内容块: $chunk_7ree")
                        val currentResult = queryState_7ree.queryResult_7ree
                        queryState_7ree.updateQueryResult_7ree(currentResult + chunk_7ree)
                        
                        // 收到第一个内容块时关闭加载动画
                        if (isFirstChunk_7ree) {
                            queryState_7ree.updateLoadingState_7ree(false)
                            isFirstChunk_7ree = false
                        }
                    }
                    
                    val apiEndTime = System.currentTimeMillis()
                    println("DEBUG: API请求总耗时: ${apiEndTime - apiStartTime}ms")
                    
                    // API请求成功后，保存到本地数据库
                    val queryResult = queryState_7ree.queryResult_7ree
                    if (queryResult.isNotBlank() && !queryResult.startsWith("错误:")) {
                        println("DEBUG: 保存查询结果到数据库")
                        val saveStartTime = System.currentTimeMillis()
                        wordRepository_7ree.saveWord_7ree(wordInput, queryResult)
                        val saveEndTime = System.currentTimeMillis()
                        println("DEBUG: 保存到数据库耗时: ${saveEndTime - saveStartTime}ms")
                        
                        // 更新当前单词信息
                        val updateInfoStartTime = System.currentTimeMillis()
                        updateCurrentWordInfo_7ree()
                        val updateInfoEndTime = System.currentTimeMillis()
                        println("DEBUG: 更新单词信息耗时: ${updateInfoEndTime - updateInfoStartTime}ms")
                    }
                }
                
                val queryEndTime = System.currentTimeMillis()
                val totalQueryTime = queryEndTime - queryStartTime
                println("DEBUG: 查询完成，总耗时: ${totalQueryTime}ms")
            } catch (e: Exception) {
                println("DEBUG: 查询异常: ${e.message}")
                queryState_7ree.updateQueryResult_7ree("查询失败: ${e.localizedMessage}")
                
                val queryEndTime = System.currentTimeMillis()
                val totalQueryTime = queryEndTime - queryStartTime
                println("DEBUG: 查询失败，总耗时: ${totalQueryTime}ms")
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

        println("DEBUG: getExamplesSpeechText_7ree - 开始处理例句")
        println("DEBUG: 原始文本行数: ${lines_7ree.size}")

        var inEnglishExamplesSection_7ree = false
        var exampleCount_7ree = 0
        for ((index, line_7ree) in lines_7ree.withIndex()) {
            val trimmedLine_7ree = line_7ree.trim()

            if (trimmedLine_7ree.matches(Regex("^#+\\s*英文例句.*$"))) {
                inEnglishExamplesSection_7ree = true
                println("DEBUG: 找到英文例句标题，行号: $index, 内容: '$trimmedLine_7ree'")
                continue // Skip the header itself
            } else if (trimmedLine_7ree.startsWith("#") && inEnglishExamplesSection_7ree) {
                println("DEBUG: 遇到新标题，停止处理例句，行号: $index, 内容: '$trimmedLine_7ree'")
                // Stop if we hit another header after the English examples section
                break
            }

            if (inEnglishExamplesSection_7ree && trimmedLine_7ree.isNotBlank()) {
                // Filter out lines that are not example sentences or contain Chinese
                val containsChinese_7ree = trimmedLine_7ree.contains(Regex("\\p{IsHan}"))
                val isExampleLine_7ree = trimmedLine_7ree.matches(Regex("^\\[\\d+\\]\\.\\s*.*$")) || // Matches [N]. Example
                    trimmedLine_7ree.matches(Regex("^[\\d]+\\.\\s*.*$")) // Matches N. Example

                println("DEBUG: 处理行 $index: '$trimmedLine_7ree'")
                println("DEBUG: 包含中文: $containsChinese_7ree, 是例句行: $isExampleLine_7ree")

                if (isExampleLine_7ree && !containsChinese_7ree) {
                    val cleanedLine_7ree = trimmedLine_7ree
                        .replace(Regex("[*_`~#+->]"), "") // Remove common markdown symbols
                        .replace(Regex("^\\[\\d+\\]\\.\\s*"), "") // Remove [N]. prefix
                        .replace(Regex("^[\\d]+\\.\\s*"), "") // Remove N. prefix
                        .replace(Regex("""\[.*?\]"""), "") // Remove [text](link) markdown
                        .replace(Regex("""\(.*?\)"""), "") // Remove (link) after [text] or standalone
                        .trim()
                    
                    println("DEBUG: 清理后的例句: '$cleanedLine_7ree'")
                    
                    if (cleanedLine_7ree.isNotBlank()) {
                        // 在例句之间添加停顿
                        if (exampleCount_7ree > 0) {
                            spokenContent_7ree.append("...... ") // 例句之间的停顿
                        }
                        spokenContent_7ree.append(cleanedLine_7ree).append(" ")
                        exampleCount_7ree++
                        println("DEBUG: 添加例句 $exampleCount_7ree: '$cleanedLine_7ree'")
                    }
                }
            }
        }

        val result_7ree = spokenContent_7ree.toString().trim()
        println("DEBUG: getExamplesSpeechText_7ree - 最终结果: '$result_7ree'")
        println("DEBUG: 找到的例句数量: $exampleCount_7ree")

        return result_7ree
    }
}