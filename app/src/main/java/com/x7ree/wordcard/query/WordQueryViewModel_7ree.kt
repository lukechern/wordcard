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
import com.x7ree.wordcard.config.GeneralConfig_7ree
import com.x7ree.wordcard.config.PromptConfig_7ree
import com.x7ree.wordcard.data.DataExportImportManager_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.data.WordEntity_7ree
import com.x7ree.wordcard.tts.TtsManager_7ree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

// 滚动位置数据类
data class ScrollPosition_7ree(
    val firstVisibleItemIndex: Int = 0,
    val firstVisibleItemScrollOffset: Int = 0
)

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
    private val ttsManager_7ree = TtsManager_7ree(context)
    
    // 配置状态
    private val _apiConfig_7ree = MutableStateFlow(ApiConfig_7ree())
    val apiConfig_7ree: StateFlow<ApiConfig_7ree> = _apiConfig_7ree
    
    // 提示词配置状态
    private val _promptConfig_7ree = MutableStateFlow(PromptConfig_7ree())
    val promptConfig_7ree: StateFlow<PromptConfig_7ree> = _promptConfig_7ree
    
    // 通用配置状态
    private val _generalConfig_7ree = MutableStateFlow(GeneralConfig_7ree())
    val generalConfig_7ree: StateFlow<GeneralConfig_7ree> = _generalConfig_7ree
    
    // 操作结果状态
    private val _operationResult_7ree = MutableStateFlow<String?>(null)
    val operationResult_7ree: StateFlow<String?> = _operationResult_7ree
    
    // 当前屏幕状态
    private val _currentScreen_7ree = MutableStateFlow("SEARCH")
    val currentScreen_7ree: StateFlow<String> = _currentScreen_7ree
    
    // 是否从单词本进入单词详情页面
    private val _isFromWordBook_7ree = MutableStateFlow(false)
    val isFromWordBook_7ree: StateFlow<Boolean> = _isFromWordBook_7ree
    
    // 单词本状态保存（用于返回时恢复状态）
    var savedWordBookScrollPosition_7ree = ScrollPosition_7ree()
    var savedWordBookFilterState_7ree = false
    
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
    
    // 分页加载相关状态
    private val _pagedWords_7ree = MutableStateFlow<List<WordEntity_7ree>>(emptyList())
    val pagedWords_7ree: StateFlow<List<WordEntity_7ree>> = _pagedWords_7ree
    
    private val _isLoadingMore_7ree = MutableStateFlow(false)
    val isLoadingMore_7ree: StateFlow<Boolean> = _isLoadingMore_7ree
    
    private val _hasMoreData_7ree = MutableStateFlow(true)
    val hasMoreData_7ree: StateFlow<Boolean> = _hasMoreData_7ree
    
    // 收藏过滤状态
    private val _showFavoritesOnly_7ree = MutableStateFlow(false)
    val showFavoritesOnly_7ree: StateFlow<Boolean> = _showFavoritesOnly_7ree
    
    private var currentPage_7ree = 0
    private val pageSize_7ree = 10 // 每页10个项目

    init {
        // 只初始化导出路径，其他数据改为按需加载
        _exportPath_7ree.value = dataManager_7ree.getDefaultExportDirectory_7ree()
        // 加载通用配置
        loadGeneralConfig_7ree()
        // 初始化TTS
        initializeTts_7ree()
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
                        
                        // 🔧 新增：刷新单词本分页数据
                        if (_currentScreen_7ree.value != "HISTORY") {
                            // 只有在不在单词本页面时才重置分页，避免影响用户当前浏览
                            resetPagination_7ree()
                            loadInitialWords_7ree()
                        } else {
                            // 如果当前在单词本页面，只在列表顶部插入新单词
                            refreshWordBookWithNewWord_7ree(wordInput_7ree)
                        }
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
                try {
                    // 获取当前收藏状态
                    val currentWord = wordRepository_7ree.getWord_7ree(wordInput_7ree)
                    val wasAlreadyFavorite = currentWord?.isFavorite ?: false
                    
                    // 切换收藏状态
                    wordRepository_7ree.toggleFavorite_7ree(wordInput_7ree)
                    
                    // 更新当前单词信息
                    updateCurrentWordInfo_7ree()
                    
                    // 设置操作结果提示
                    _operationResult_7ree.value = if (wasAlreadyFavorite) "已取消收藏" else "已添加收藏"
                } catch (e: Exception) {
                    _operationResult_7ree.value = "收藏操作失败: ${e.message}"
                    println("DEBUG: 收藏操作失败: ${e.message}")
                }
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
                
                // 从分页数据中移除已删除的单词
                val currentWords = _pagedWords_7ree.value.toMutableList()
                currentWords.removeAll { it.word == word }
                _pagedWords_7ree.value = currentWords
                
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
        
        // 标记为从单词本进入
        _isFromWordBook_7ree.value = true
        
        // 保存当前单词本状态
        savedWordBookFilterState_7ree = _showFavoritesOnly_7ree.value
        
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
    
    // 加载通用配置
    private fun loadGeneralConfig_7ree() {
        val config = configManager_7ree.loadGeneralConfig_7ree()
        _generalConfig_7ree.value = config
    }
    
    // 保存API配置
    fun saveApiConfig_7ree(apiKey: String, apiUrl: String, modelName: String, azureSpeechRegion: String = "", azureSpeechApiKey: String = "", azureSpeechEndpoint: String = "") {
        viewModelScope.launch {
            try {
                val config = ApiConfig_7ree(
                    apiKey = apiKey,
                    apiUrl = apiUrl,
                    modelName = modelName,
                    azureRegion = _apiConfig_7ree.value.azureRegion,
                    azureApiKey = _apiConfig_7ree.value.azureApiKey,
                    azureSpeechRegion = azureSpeechRegion,
                    azureSpeechApiKey = azureSpeechApiKey,
                    azureSpeechEndpoint = azureSpeechEndpoint
                )
                
                val success = configManager_7ree.saveApiConfig_7ree(config)
                if (success) {
                    _apiConfig_7ree.value = config
                    // 更新API服务的配置
                    apiService_7ree.updateApiConfig_7ree(config)
                    // 更新TTS管理器的API配置
                    ttsManager_7ree.updateApiConfig(config)
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
    
    // 保存通用配置
    fun saveGeneralConfig_7ree(keyboardType: String, autoReadAfterQuery: Boolean, autoReadOnSpellingCard: Boolean, ttsEngine: String) {
        viewModelScope.launch {
            try {
                val config = GeneralConfig_7ree(
                    keyboardType = keyboardType,
                    autoReadAfterQuery = autoReadAfterQuery,
                    autoReadOnSpellingCard = autoReadOnSpellingCard,
                    ttsEngine = ttsEngine
                )
                
                val success = configManager_7ree.saveGeneralConfig_7ree(config)
                if (success) {
                    _generalConfig_7ree.value = config
                    // 更新TTS管理器的配置
                    ttsManager_7ree.updateGeneralConfig(config)
                    _operationResult_7ree.value = "通用配置保存成功"
                    println("DEBUG: 通用配置保存成功")
                } else {
                    _operationResult_7ree.value = "通用配置保存失败"
                    println("DEBUG: 通用配置保存失败")
                }
            } catch (e: Exception) {
                _operationResult_7ree.value = "通用配置保存失败: ${e.message}"
                println("DEBUG: 通用配置保存异常: ${e.message}")
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
    
    // 设置操作结果
    fun setOperationResult_7ree(message: String) {
        _operationResult_7ree.value = message
    }
    
    // 分页加载单词列表
    fun loadInitialWords_7ree() {
        viewModelScope.launch {
            try {
                currentPage_7ree = 0
                _hasMoreData_7ree.value = true
                val words = if (_showFavoritesOnly_7ree.value) {
                    wordRepository_7ree.getFavoriteWordsPaged_7ree(pageSize_7ree, 0)
                } else {
                    wordRepository_7ree.getWordsPaged_7ree(pageSize_7ree, 0)
                }
                _pagedWords_7ree.value = words
                
                // 如果返回的数据少于页面大小，说明没有更多数据了
                if (words.size < pageSize_7ree) {
                    _hasMoreData_7ree.value = false
                }
                
                val filterType = if (_showFavoritesOnly_7ree.value) "收藏" else "全部"
                println("DEBUG: 初始加载完成，共${words.size}个${filterType}单词")
            } catch (e: Exception) {
                println("DEBUG: 初始加载失败: ${e.message}")
            }
        }
    }
    
    // 加载更多单词
    fun loadMoreWords_7ree() {
        if (_isLoadingMore_7ree.value || !_hasMoreData_7ree.value) {
            return
        }
        
        viewModelScope.launch {
            try {
                _isLoadingMore_7ree.value = true
                currentPage_7ree++
                val offset = currentPage_7ree * pageSize_7ree
                val newWords = if (_showFavoritesOnly_7ree.value) {
                    wordRepository_7ree.getFavoriteWordsPaged_7ree(pageSize_7ree, offset)
                } else {
                    wordRepository_7ree.getWordsPaged_7ree(pageSize_7ree, offset)
                }
                
                if (newWords.isNotEmpty()) {
                    val currentWords = _pagedWords_7ree.value.toMutableList()
                    currentWords.addAll(newWords)
                    _pagedWords_7ree.value = currentWords
                    
                    // 如果返回的数据少于页面大小，说明没有更多数据了
                    if (newWords.size < pageSize_7ree) {
                        _hasMoreData_7ree.value = false
                    }
                    
                    val filterType = if (_showFavoritesOnly_7ree.value) "收藏" else "全部"
                    println("DEBUG: 加载更多完成，新增${newWords.size}个${filterType}单词，总计${currentWords.size}个")
                } else {
                    _hasMoreData_7ree.value = false
                    println("DEBUG: 没有更多数据")
                }
            } catch (e: Exception) {
                println("DEBUG: 加载更多失败: ${e.message}")
            } finally {
                _isLoadingMore_7ree.value = false
            }
        }
    }
    
    // 重置分页状态
    fun resetPagination_7ree() {
        currentPage_7ree = 0
        _pagedWords_7ree.value = emptyList()
        _hasMoreData_7ree.value = true
        _isLoadingMore_7ree.value = false
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
    
    fun setCurrentScreen_7ree(screen: String) {
        _currentScreen_7ree.value = screen
        
        // 如果切换到非搜索页面，清除从单词本进入的标记
        if (screen != "SEARCH") {
            _isFromWordBook_7ree.value = false
        }
    }
    
    // 返回单词本并恢复状态
    fun returnToWordBook_7ree() {
        // 恢复单词本的过滤状态
        _showFavoritesOnly_7ree.value = savedWordBookFilterState_7ree
        
        // 切换到单词本页面
        _currentScreen_7ree.value = "HISTORY"
        
        // 清除从单词本进入的标记
        _isFromWordBook_7ree.value = false
        
        // 注意：不重新加载数据，保持原有的分页状态和滚动位置
        // 滚动位置会通过savedWordBookScrollPosition_7ree在UI层恢复
    }
    
    // 切换收藏过滤状态
    fun toggleFavoriteFilter_7ree() {
        _showFavoritesOnly_7ree.value = !_showFavoritesOnly_7ree.value
        // 重新加载数据
        resetPagination_7ree()
        loadInitialWords_7ree()
        val filterType = if (_showFavoritesOnly_7ree.value) "收藏" else "全部"
        println("DEBUG: 切换到${filterType}单词过滤")
    }
    
    // 拼写练习成功，增加拼写次数
    fun onSpellingSuccess_7ree() {
        viewModelScope.launch {
            try {
                if (wordInput_7ree.isNotBlank()) {
                    wordRepository_7ree.incrementSpellingCount_7ree(wordInput_7ree)
                    // 更新当前单词信息
                    updateCurrentWordInfo_7ree()
                    println("DEBUG: 拼写练习成功，单词: $wordInput_7ree")
                }
            } catch (e: Exception) {
                println("DEBUG: 更新拼写次数失败: ${e.message}")
            }
        }
    }
    
    // 获取当前单词的拼写次数
    fun getCurrentSpellingCount_7ree(): Int {
        return currentWordInfo_7ree?.spellingCount ?: 0
    }
    
    // 新增方法：在单词本中刷新新单词
    private fun refreshWordBookWithNewWord_7ree(newWord: String) {
        viewModelScope.launch {
            try {
                // 获取新单词的完整信息
                val newWordEntity = wordRepository_7ree.getWord_7ree(newWord)
                if (newWordEntity != null) {
                    val currentWords = _pagedWords_7ree.value.toMutableList()
                    // 检查是否已存在（避免重复）
                    val existingIndex = currentWords.indexOfFirst { it.word == newWord }
                    if (existingIndex >= 0) {
                        // 更新现有单词（可能是浏览次数等信息变化）
                        currentWords[existingIndex] = newWordEntity
                    } else {
                        // 在列表顶部插入新单词
                        currentWords.add(0, newWordEntity)
                    }
                    _pagedWords_7ree.value = currentWords
                    println("DEBUG: 单词本已刷新，新增/更新单词: $newWord")
                }
            } catch (e: Exception) {
                println("DEBUG: 刷新单词本失败: ${e.message}")
            }
        }
    }
    
    // TTS相关方法
    
    /**
     * 朗读单词
     */
    fun speakWord_7ree(word: String) {
        viewModelScope.launch {
            try {
                // 更新TTS管理器的配置
                ttsManager_7ree.updateGeneralConfig(generalConfig_7ree.value)
                ttsManager_7ree.updateApiConfig(apiConfig_7ree.value)
                
                // 开始朗读
                ttsManager_7ree.speak(
                    text = word,
                    onStart = {
                        println("DEBUG: 开始朗读单词: $word")
                    },
                    onComplete = {
                        println("DEBUG: 朗读完成: $word")
                    },
                    onError = { error ->
                        println("DEBUG: 朗读失败: $error")
                        _operationResult_7ree.value = "朗读失败: $error"
                    }
                )
            } catch (e: Exception) {
                println("DEBUG: 朗读异常: ${e.message}")
                _operationResult_7ree.value = "朗读异常: ${e.message}"
            }
        }
    }
    
    /**
     * 停止朗读
     */
    fun stopSpeaking_7ree() {
        ttsManager_7ree.stopSpeaking()
    }
    
    /**
     * 检查是否正在朗读

    fun isSpeaking_7ree(): Boolean {
        return ttsManager_7ree.isSpeaking()
    }
    */


    /**
     * 获取TTS引擎状态
     */
    fun getTtsEngineStatus_7ree(): String {
        val status = ttsManager_7ree.getEngineStatus()
        return when {
            status.currentEngine == "google" && status.googleReady -> "Google TTS 已就绪"
            status.currentEngine == "azure" && status.azureReady -> "Azure Speech 已就绪"
            status.currentEngine == "google" && !status.googleReady -> "Google TTS 未就绪"
            status.currentEngine == "azure" && !status.azureReady -> "Azure Speech 配置无效"
            else -> "TTS 引擎未知状态"
        }
    }
    
    /**
     * 初始化TTS配置
     */
    private fun initializeTts_7ree() {
        // 设置TTS状态变化回调
        ttsManager_7ree.onTtsStateChanged = { isReady, engine ->
            println("DEBUG: TTS引擎状态变化 - $engine: ${if (isReady) "就绪" else "未就绪"}")
        }
        
        ttsManager_7ree.onSpeakingStateChanged = { isSpeaking, engine ->
            println("DEBUG: TTS朗读状态变化 - $engine: ${if (isSpeaking) "朗读中" else "停止"}")
        }
        
        // 更新配置
        ttsManager_7ree.updateGeneralConfig(generalConfig_7ree.value)
        ttsManager_7ree.updateApiConfig(apiConfig_7ree.value)
    }
    
    /**
     * 释放TTS资源
     */
    override fun onCleared() {
        super.onCleared()
        ttsManager_7ree.release()
        println("DEBUG: WordQueryViewModel已清理，TTS资源已释放")
    }
}