package com.x7ree.wordcard.query

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.config.ApiConfig_7ree
import com.x7ree.wordcard.config.AppConfigManager_7ree
import com.x7ree.wordcard.config.PromptConfig_7ree
import com.x7ree.wordcard.data.DataExportImportManager_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.data.WordEntity_7ree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
语言包定义

    'pl_querying_7r' => '查询中...',
    'pl_query_failed_7r' => '查询失败：',
    'pl_enter_word_hint_7r' => '请输入英文单词',
**/

class WordQueryViewModel_7ree(
    private val apiService_7ree: OpenAiApiService_7ree = OpenAiApiService_7ree(),
    private val wordRepository_7ree: WordRepository_7ree,
    private val context: Context
) : ViewModel() {
    
    private val configManager_7ree = AppConfigManager_7ree(context)
    private val dataManager_7ree = DataExportImportManager_7ree(context, wordRepository_7ree)
    
    // 配置状态
    private val _apiConfig_7ree = MutableStateFlow(ApiConfig_7ree())
    val apiConfig_7ree: StateFlow<ApiConfig_7ree> = _apiConfig_7ree
    
    // 提示词配置状态
    private val _promptConfig_7ree = MutableStateFlow(PromptConfig_7ree())
    val promptConfig_7ree: StateFlow<PromptConfig_7ree> = _promptConfig_7ree
    
    // 操作结果状态
    private val _operationResult_7ree = MutableStateFlow<String?>(null)
    val operationResult_7ree: StateFlow<String?> = _operationResult_7ree
    
    var wordInput_7ree by mutableStateOf("")
        private set

    var queryResult_7ree by mutableStateOf("")
        private set

    var isLoading_7ree by mutableStateOf(false)
        private set

    var isWordConfirmed_7ree by mutableStateOf(false)
        private set

    var isTtsReady_7ree by mutableStateOf(false)
    var isSpeaking_7ree by mutableStateOf(false)
        private set
    
    // 新增：区分不同类型的朗读状态
    var isSpeakingWord_7ree by mutableStateOf(false)
        private set
    var isSpeakingExamples_7ree by mutableStateOf(false)
        private set
    
    var isFromCache_7ree by mutableStateOf(false)
        private set
    
    // 当前单词的详细信息
    var currentWordInfo_7ree: WordEntity_7ree? by mutableStateOf(null)
        private set

    // 收集单词总数
    private val _wordCount_7ree = MutableStateFlow(0)
    val wordCount_7ree: StateFlow<Int> = _wordCount_7ree
    
    // 总查阅次数
    private val _totalViews_7ree = MutableStateFlow(0)
    val totalViews_7ree: StateFlow<Int> = _totalViews_7ree
    
    // 导出路径
    private val _exportPath_7ree = MutableStateFlow("")
    val exportPath_7ree: StateFlow<String> = _exportPath_7ree

    // 单词列表导航相关
    private val _allWords_7ree = MutableStateFlow<List<WordEntity_7ree>>(emptyList())
    val allWords_7ree: StateFlow<List<WordEntity_7ree>> = _allWords_7ree
    private var currentWordIndex_7ree = -1

    init {
        // 只初始化导出路径，其他数据改为按需加载
        _exportPath_7ree.value = dataManager_7ree.getDefaultExportDirectory_7ree()
    }
    
    // 按需加载单词计数
    fun loadWordCount_7ree() {
        val startTime = System.currentTimeMillis()
        viewModelScope.launch(Dispatchers.IO) {
            wordRepository_7ree.wordCount_7ree.collect { count ->
                withContext(Dispatchers.Main) {
                    _wordCount_7ree.value = count
                    val endTime = System.currentTimeMillis()
                    val duration = endTime - startTime
                    println("DEBUG: 加载单词计数完成，耗时: ${duration}ms")
                }
            }
        }
    }
    
    // 按需加载总查阅次数
    fun loadTotalViews_7ree() {
        val startTime = System.currentTimeMillis()
        viewModelScope.launch(Dispatchers.IO) {
            wordRepository_7ree.getTotalViews_7ree.collect { totalViews ->
                withContext(Dispatchers.Main) {
                    _totalViews_7ree.value = totalViews
                    val endTime = System.currentTimeMillis()
                    val duration = endTime - startTime
                    println("DEBUG: 加载总查阅次数完成，耗时: ${duration}ms")
                }
            }
        }
    }

    // 延迟加载所有单词列表 - 只在需要导航功能时才加载
    private fun loadAllWords_7ree() {
        // 如果已经加载过，就不再重复加载
        if (_allWords_7ree.value.isNotEmpty()) {
            return
        }
        
        val startTime = System.currentTimeMillis()
        println("DEBUG: 开始加载所有单词列表")
        
        viewModelScope.launch {
            wordRepository_7ree.getAllWords_7ree().collect { words ->
                _allWords_7ree.value = words
                val endTime = System.currentTimeMillis()
                val duration = endTime - startTime
                println("DEBUG: 加载所有单词列表完成，共${words.size}个单词，耗时: ${duration}ms")
            }
        }
    }
    
    // 公开方法，供外部调用时触发加载
    fun ensureWordsLoaded_7ree() {
        loadAllWords_7ree()
    }

    // 获取当前单词在列表中的索引
    private fun getCurrentWordIndex_7ree(): Int {
        return _allWords_7ree.value.indexOfFirst { it.word == wordInput_7ree }
    }

    // 切换到上一个单词
    fun navigateToPreviousWord_7ree() {
        println("DEBUG: navigateToPreviousWord_7ree - 开始切换到上一个单词")
        
        // 确保单词列表已加载
        ensureWordsLoaded_7ree()
        
        val currentIndex = getCurrentWordIndex_7ree()
        println("DEBUG: 当前单词索引: $currentIndex")
        if (currentIndex == -1) {
            println("DEBUG: 当前单词不在列表中，无法导航")
            return
        }
        
        val allWords = _allWords_7ree.value
        if (allWords.isEmpty()) {
            println("DEBUG: 单词列表为空，无法导航")
            return
        }
        
        val previousIndex = if (currentIndex == 0) allWords.size - 1 else currentIndex - 1
        val previousWord = allWords[previousIndex]
        println("DEBUG: 切换到上一个单词: ${previousWord.word}")
        
        loadWordFromHistory_7ree(previousWord.word)
    }

    // 切换到下一个单词
    fun navigateToNextWord_7ree() {
        println("DEBUG: navigateToNextWord_7ree - 开始切换到下一个单词")
        
        // 确保单词列表已加载
        ensureWordsLoaded_7ree()
        
        val currentIndex = getCurrentWordIndex_7ree()
        println("DEBUG: 当前单词索引: $currentIndex")
        if (currentIndex == -1) {
            println("DEBUG: 当前单词不在列表中，无法导航")
            return
        }
        
        val allWords = _allWords_7ree.value
        if (allWords.isEmpty()) {
            println("DEBUG: 单词列表为空，无法导航")
            return
        }
        
        val nextIndex = if (currentIndex == allWords.size - 1) 0 else currentIndex + 1
        val nextWord = allWords[nextIndex]
        println("DEBUG: 切换到下一个单词: ${nextWord.word}")
        
        loadWordFromHistory_7ree(nextWord.word)
    }

    // 检查是否可以导航（当前有单词且列表不为空）
    fun canNavigate_7ree(): Boolean {
        // 确保单词列表已加载
        ensureWordsLoaded_7ree()
        
        val canNavigate = wordInput_7ree.isNotBlank() && _allWords_7ree.value.isNotEmpty()
        println("DEBUG: canNavigate_7ree - wordInput='${wordInput_7ree}', wordsCount=${_allWords_7ree.value.size}, canNavigate=$canNavigate")
        return canNavigate
    }

    fun setIsSpeaking_7ree(speaking: Boolean) {
        isSpeaking_7ree = speaking
    }

    // 新增：设置单词朗读状态
    fun setIsSpeakingWord_7ree(speaking: Boolean) {
        isSpeakingWord_7ree = speaking
        isSpeaking_7ree = speaking
    }

    // 新增：设置例句朗读状态
    fun setIsSpeakingExamples_7ree(speaking: Boolean) {
        isSpeakingExamples_7ree = speaking
        isSpeaking_7ree = speaking
    }

    // 新增：获取单词朗读文本
    fun getWordSpeechText_7ree(): String {
        return wordInput_7ree
    }

    // 新增：获取例句朗读文本
    fun getExamplesSpeechText_7ree(): String {
        val rawText_7ree = queryResult_7ree
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

    fun onWordInputChanged_7ree(newInput: String) {
        wordInput_7ree = newInput
    }

    fun queryWord_7ree() {
        if (wordInput_7ree.isBlank()) {
            queryResult_7ree = "请输入英文单词"
            return
        }

        isLoading_7ree = true
        isWordConfirmed_7ree = true
        queryResult_7ree = ""
        isFromCache_7ree = false
        
        // 记录查询开始时间
        val queryStartTime = System.currentTimeMillis()
        println("DEBUG: 开始查询单词: ${wordInput_7ree}, 时间: ${queryStartTime}ms")
        
        viewModelScope.launch {
            try {
                // 1. 先查询本地数据库
                val dbStartTime = System.currentTimeMillis()
                val cachedWord_7ree = wordRepository_7ree.getWord_7ree(wordInput_7ree)
                val dbEndTime = System.currentTimeMillis()
                val dbQueryTime = dbEndTime - dbStartTime
                println("DEBUG: 数据库查询耗时: ${dbQueryTime}ms")
                
                if (cachedWord_7ree != null) {
                    // 本地有缓存数据
                    println("DEBUG: 从缓存获取到数据")
                    queryResult_7ree = cachedWord_7ree.apiResult
                    isFromCache_7ree = true
                    
                    // 增加浏览次数
                    val viewCountStartTime = System.currentTimeMillis()
                    wordRepository_7ree.incrementViewCount_7ree(wordInput_7ree)
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
                    
                    apiService_7ree.queryWordStreamSimple_7ree(wordInput_7ree).collect { chunk_7ree ->
                        val chunkTime = System.currentTimeMillis()
                        if (isFirstChunk_7ree) {
                            firstChunkTime = chunkTime
                            println("DEBUG: 收到第一个内容块，耗时: ${firstChunkTime - apiStartTime}ms")
                        }
                        
                        println("DEBUG: 收到流式内容块: $chunk_7ree")
                        queryResult_7ree += chunk_7ree
                        
                        // 收到第一个内容块时关闭加载动画
                        if (isFirstChunk_7ree) {
                            isLoading_7ree = false
                            isFirstChunk_7ree = false
                        }
                    }
                    
                    val apiEndTime = System.currentTimeMillis()
                    println("DEBUG: API请求总耗时: ${apiEndTime - apiStartTime}ms")
                    
                    // API请求成功后，保存到本地数据库
                    if (queryResult_7ree.isNotBlank() && !queryResult_7ree.startsWith("错误:")) {
                        println("DEBUG: 保存查询结果到数据库")
                        val saveStartTime = System.currentTimeMillis()
                        wordRepository_7ree.saveWord_7ree(wordInput_7ree, queryResult_7ree)
                        val saveEndTime = System.currentTimeMillis()
                        println("DEBUG: 保存到数据库耗时: ${saveEndTime - saveStartTime}ms")
                        
                        // 更新当前单词信息
                        val updateInfoStartTime = System.currentTimeMillis()
                        updateCurrentWordInfo_7ree()
                        val updateInfoEndTime = System.currentTimeMillis()
                        println("DEBUG: 更新单词信息耗时: ${updateInfoEndTime - updateInfoStartTime}ms")
                        
                        // 重新加载单词列表以确保导航功能正常
                        loadAllWords_7ree()
                    }
                }
                
                val queryEndTime = System.currentTimeMillis()
                val totalQueryTime = queryEndTime - queryStartTime
                println("DEBUG: 查询完成，总耗时: ${totalQueryTime}ms")
            } catch (e: Exception) {
                println("DEBUG: 查询异常: ${e.message}")
                queryResult_7ree = "查询失败: ${e.localizedMessage}"
                
                val queryEndTime = System.currentTimeMillis()
                val totalQueryTime = queryEndTime - queryStartTime
                println("DEBUG: 查询失败，总耗时: ${totalQueryTime}ms")
            } finally {
                isLoading_7ree = false
            }
        }
    }
    
    // 切换当前单词的收藏状态
    fun toggleFavorite_7ree() {
        if (wordInput_7ree.isNotBlank()) {
            viewModelScope.launch {
                wordRepository_7ree.toggleFavorite_7ree(wordInput_7ree)
                // 更新当前单词信息
                updateCurrentWordInfo_7ree()
            }
        }
    }
    
    // 更新当前单词信息
    private suspend fun updateCurrentWordInfo_7ree() {
        if (wordInput_7ree.isNotBlank()) {
            currentWordInfo_7ree = wordRepository_7ree.getWord_7ree(wordInput_7ree)
        }
    }
    
    // 设置当前单词的收藏状态
    fun setFavorite_7ree(isFavorite: Boolean) {
        if (wordInput_7ree.isNotBlank()) {
            viewModelScope.launch {
                wordRepository_7ree.setFavorite_7ree(wordInput_7ree, isFavorite)
            }
        }
    }
    
    // 设置指定单词的收藏状态
    fun setFavorite_7ree(word: String, isFavorite: Boolean) {
        viewModelScope.launch {
            wordRepository_7ree.setFavorite_7ree(word, isFavorite)
        }
    }
    
    // 删除指定单词
    fun deleteWord_7ree(word: String) {
        viewModelScope.launch {
            try {
                wordRepository_7ree.deleteWord_7ree(word)
                _operationResult_7ree.value = "删除成功"
                println("DEBUG: 单词删除成功: $word")
            } catch (e: Exception) {
                _operationResult_7ree.value = "删除失败: ${e.message}"
                println("DEBUG: 单词删除失败: ${e.message}")
            }
        }
    }
    
    // 获取历史单词列表
    fun getHistoryWords_7ree() = wordRepository_7ree.getAllWords_7ree()
    
    // 从历史记录中加载单词详情
    fun loadWordFromHistory_7ree(word: String) {
        wordInput_7ree = word
        isWordConfirmed_7ree = true
        isFromCache_7ree = false
        
        viewModelScope.launch {
            try {
                val cachedWord_7ree = wordRepository_7ree.getWord_7ree(word)
                if (cachedWord_7ree != null) {
                    queryResult_7ree = cachedWord_7ree.apiResult
                    isFromCache_7ree = true
                    // 增加浏览次数
                    wordRepository_7ree.incrementViewCount_7ree(word)
                    // 更新当前单词信息
                    updateCurrentWordInfo_7ree()
                    // 重新加载单词列表以确保导航功能正常
                    loadAllWords_7ree()
                } else {
                    queryResult_7ree = "未找到该单词的记录"
                    currentWordInfo_7ree = null
                }
            } catch (e: Exception) {
                queryResult_7ree = "加载失败: ${e.localizedMessage}"
                currentWordInfo_7ree = null
            }
        }
    }
    
    // 初始化时加载配置
    init {
        loadApiConfig_7ree()
        loadPromptConfig_7ree()
    }
    
    // 加载API配置
    private fun loadApiConfig_7ree() {
        val config = configManager_7ree.loadApiConfig_7ree()
        _apiConfig_7ree.value = config
        // 更新API服务的配置
        apiService_7ree.updateApiConfig_7ree(config)
    }
    
    // 加载提示词配置
    private fun loadPromptConfig_7ree() {
        val config = configManager_7ree.loadPromptConfig_7ree()
        _promptConfig_7ree.value = config
        // 更新API服务的提示词配置
        apiService_7ree.updatePromptConfig_7ree(config)
    }
    
    // 保存API配置
    fun saveApiConfig_7ree(apiKey: String, apiUrl: String, modelName: String) {
        viewModelScope.launch {
            try {
                val config = ApiConfig_7ree(
                    apiKey = apiKey,
                    apiUrl = apiUrl,
                    modelName = modelName
                )
                
                val success = configManager_7ree.saveApiConfig_7ree(config)
                if (success) {
                    _apiConfig_7ree.value = config
                    // 更新API服务的配置
                    apiService_7ree.updateApiConfig_7ree(config)
                    _operationResult_7ree.value = "配置保存成功"
                    println("DEBUG: API配置保存成功")
                } else {
                    _operationResult_7ree.value = "配置保存失败"
                    println("DEBUG: API配置保存失败")
                }
            } catch (e: Exception) {
                _operationResult_7ree.value = "配置保存失败: ${e.message}"
                println("DEBUG: API配置保存异常: ${e.message}")
            }
        }
    }
    
    // 保存提示词配置
    fun savePromptConfig_7ree(queryPrompt: String, outputTemplate: String) {
        viewModelScope.launch {
            try {
                val config = PromptConfig_7ree(
                    queryPrompt_7ree = queryPrompt,
                    outputTemplate_7ree = outputTemplate
                )
                
                val success = configManager_7ree.savePromptConfig_7ree(config)
                if (success) {
                    _promptConfig_7ree.value = config
                    // 更新API服务的提示词配置
                    apiService_7ree.updatePromptConfig_7ree(config)
                    _operationResult_7ree.value = "提示词配置保存成功"
                    println("DEBUG: 提示词配置保存成功")
                } else {
                    _operationResult_7ree.value = "提示词配置保存失败"
                    println("DEBUG: 提示词配置保存失败")
                }
            } catch (e: Exception) {
                _operationResult_7ree.value = "提示词配置保存失败: ${e.message}"
                println("DEBUG: 提示词配置保存异常: ${e.message}")
            }
        }
    }
    
    // 导出历史数据
    fun exportHistoryData_7ree() {
        viewModelScope.launch {
            try {
                val result = dataManager_7ree.exportData_7ree()
                result.fold(
                    onSuccess = { filePath ->
                        val fileName = filePath.substringAfterLast("/")
                        _operationResult_7ree.value = "数据导出成功！文件: $fileName\n位置: Android/data/com.x7ree.wordcard/files/Downloads/"
                        println("DEBUG: 数据导出成功: $filePath")
                    },
                    onFailure = { exception ->
                        _operationResult_7ree.value = "数据导出失败: ${exception.message}"
                        println("DEBUG: 数据导出失败: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _operationResult_7ree.value = "数据导出失败: ${e.message}"
                println("DEBUG: 数据导出异常: ${e.message}")
            }
        }
    }
    
    // 导入历史数据
    fun importHistoryData_7ree(uri: Uri) {
        viewModelScope.launch {
            try {
                val result = dataManager_7ree.importData_7ree(uri)
                result.fold(
                    onSuccess = { count ->
                        _operationResult_7ree.value = "数据导入成功，共导入 $count 条记录"
                        println("DEBUG: 数据导入成功，共导入 $count 条记录")
                    },
                    onFailure = { exception ->
                        _operationResult_7ree.value = "数据导入失败: ${exception.message}"
                        println("DEBUG: 数据导入失败: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _operationResult_7ree.value = "数据导入失败: ${e.message}"
                println("DEBUG: 数据导入异常: ${e.message}")
            }
        }
    }
    
    // 清除操作结果
    fun clearOperationResult_7ree() {
        _operationResult_7ree.value = null
    }
    
    // 重置查询状态
    fun resetQueryState_7ree() {
        wordInput_7ree = ""
        queryResult_7ree = ""
        isWordConfirmed_7ree = false
        isFromCache_7ree = false
        currentWordInfo_7ree = null
        clearOperationResult_7ree()
    }
}