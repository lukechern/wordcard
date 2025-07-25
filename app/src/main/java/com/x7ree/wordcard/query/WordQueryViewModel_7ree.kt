package com.x7ree.wordcard.query

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.config.ApiConfig_7ree
import com.x7ree.wordcard.config.AppConfigManager_7ree
import com.x7ree.wordcard.config.GeneralConfig_7ree
import com.x7ree.wordcard.config.PromptConfig_7ree
import com.x7ree.wordcard.data.DataExportImportManager_7ree
import com.x7ree.wordcard.data.WordEntity_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.query.manager.ConfigManager_7ree
import com.x7ree.wordcard.query.manager.DataManager_7ree
import com.x7ree.wordcard.query.manager.TtsManager_7ree
import com.x7ree.wordcard.query.manager.WordQueryManager_7ree
import com.x7ree.wordcard.query.state.ConfigState_7ree
import com.x7ree.wordcard.query.state.NavigationState_7ree
import com.x7ree.wordcard.query.state.PaginationState_7ree
import com.x7ree.wordcard.query.state.ScrollPosition_7ree
import com.x7ree.wordcard.query.state.WordQueryState_7ree
import com.x7ree.wordcard.tts.TtsManager_7ree as CoreTtsManager
import kotlinx.coroutines.flow.StateFlow

/**
 * 单词查询 ViewModel - 重构版
 * 
 * 该类将复杂的业务逻辑拆分到多个专门的管理器中，使代码更易于维护和扩展
 */
class WordQueryViewModel_7ree(
    private val apiService_7ree: OpenAiApiService_7ree = OpenAiApiService_7ree(),
    private val wordRepository_7ree: WordRepository_7ree,
    private val context: Context
) : ViewModel() {
    
    // 状态管理
    private val queryState_7ree = WordQueryState_7ree()
    private val configState_7ree = ConfigState_7ree()
    private val navigationState_7ree = NavigationState_7ree()
    private val paginationState_7ree = PaginationState_7ree()
    
    // 核心服务
    private val configManager_7ree = AppConfigManager_7ree(context)
    private val dataExportImportManager_7ree = DataExportImportManager_7ree(context, wordRepository_7ree)
    private val coreTtsManager_7ree = CoreTtsManager(context)
    
    // 业务管理器
    private val wordQueryManager_7ree: WordQueryManager_7ree
    private val configManagerService_7ree: ConfigManager_7ree
    private val dataManagerService_7ree: DataManager_7ree
    private val ttsManagerService_7ree: TtsManager_7ree
    
    init {
        // 初始化业务管理器
        wordQueryManager_7ree = WordQueryManager_7ree(
            apiService_7ree,
            wordRepository_7ree,
            queryState_7ree,
            viewModelScope
        )
        
        configManagerService_7ree = ConfigManager_7ree(
            configManager_7ree,
            apiService_7ree,
            coreTtsManager_7ree,
            configState_7ree,
            queryState_7ree,
            viewModelScope
        )
        
        dataManagerService_7ree = DataManager_7ree(
            dataExportImportManager_7ree,
            wordRepository_7ree,
            paginationState_7ree,
            queryState_7ree,
            viewModelScope
        )
        
        ttsManagerService_7ree = TtsManager_7ree(
            coreTtsManager_7ree,
            queryState_7ree,
            viewModelScope
        )
        
        // 初始化配置
        configManagerService_7ree.loadApiConfig_7ree()
        configManagerService_7ree.loadPromptConfig_7ree()
        configManagerService_7ree.loadGeneralConfig_7ree()
        
        // 初始化TTS - 确保在配置加载后初始化
        ttsManagerService_7ree.initializeTts_7ree()
        
        // 确保TTS管理器使用最新的配置
        coreTtsManager_7ree.updateGeneralConfig(configState_7ree.generalConfig_7ree.value)
        coreTtsManager_7ree.updateApiConfig(configState_7ree.apiConfig_7ree.value)
        
        // 设置导出路径
        paginationState_7ree.updateExportPath_7ree(dataExportImportManager_7ree.getDefaultExportDirectory_7ree())
    }
    
    // 暴露状态
    val wordInput_7ree get() = queryState_7ree.wordInput_7ree
    val queryResult_7ree get() = queryState_7ree.queryResult_7ree
    val isLoading_7ree get() = queryState_7ree.isLoading_7ree
    val isWordConfirmed_7ree get() = queryState_7ree.isWordConfirmed_7ree
    val isFromCache_7ree get() = queryState_7ree.isFromCache_7ree
    val currentWordInfo_7ree get() = queryState_7ree.currentWordInfo_7ree
    
    // TTS状态
    var isTtsReady_7ree: Boolean
        get() = queryState_7ree.isTtsReady_7ree
        set(value) { queryState_7ree.updateTtsReadyState_7ree(value) }
    val isSpeaking_7ree get() = queryState_7ree.isSpeaking_7ree
    val isSpeakingWord_7ree get() = queryState_7ree.isSpeakingWord_7ree
    val isSpeakingExamples_7ree get() = queryState_7ree.isSpeakingExamples_7ree
    
    val operationResult_7ree: StateFlow<String?> get() = queryState_7ree.operationResult_7ree
    val hasExportedData_7ree: StateFlow<Boolean> get() = queryState_7ree.hasExportedData_7ree
    val currentScreen_7ree: StateFlow<String> get() = navigationState_7ree.currentScreen_7ree
    val isFromWordBook_7ree: StateFlow<Boolean> get() = navigationState_7ree.isFromWordBook_7ree
    
    val apiConfig_7ree: StateFlow<ApiConfig_7ree> get() = configState_7ree.apiConfig_7ree
    val promptConfig_7ree: StateFlow<PromptConfig_7ree> get() = configState_7ree.promptConfig_7ree
    val generalConfig_7ree: StateFlow<GeneralConfig_7ree> get() = configState_7ree.generalConfig_7ree
    
    val wordCount_7ree: StateFlow<Int> get() = paginationState_7ree.wordCount_7ree
    val totalViews_7ree: StateFlow<Int> get() = paginationState_7ree.totalViews_7ree
    val exportPath_7ree: StateFlow<String> get() = paginationState_7ree.exportPath_7ree
    val allWords_7ree: StateFlow<List<WordEntity_7ree>> get() = navigationState_7ree.allWords_7ree
    val pagedWords_7ree: StateFlow<List<WordEntity_7ree>> get() = paginationState_7ree.pagedWords_7ree
    val isLoadingMore_7ree: StateFlow<Boolean> get() = paginationState_7ree.isLoadingMore_7ree
    val hasMoreData_7ree: StateFlow<Boolean> get() = paginationState_7ree.hasMoreData_7ree
    val showFavoritesOnly_7ree: StateFlow<Boolean> get() = paginationState_7ree.showFavoritesOnly_7ree
    val searchQuery_7ree: StateFlow<String> get() = paginationState_7ree.searchQuery_7ree
    val isSearchMode_7ree: StateFlow<Boolean> get() = paginationState_7ree.isSearchMode_7ree
    val sortType_7ree: StateFlow<String?> get() = paginationState_7ree.sortType_7ree
    
    // 单词本状态保存
    var savedWordBookScrollPosition_7ree: ScrollPosition_7ree
        get() = navigationState_7ree.savedWordBookScrollPosition_7ree
        set(value) { navigationState_7ree.savedWordBookScrollPosition_7ree = value }
    
    var savedWordBookFilterState_7ree: Boolean
        get() = navigationState_7ree.savedWordBookFilterState_7ree
        set(value) { navigationState_7ree.savedWordBookFilterState_7ree = value }
    
    // 单词输入和查询
    fun onWordInputChanged_7ree(newInput: String) {
        queryState_7ree.updateWordInput_7ree(newInput)
    }
    
    fun queryWord_7ree() {
        wordQueryManager_7ree.queryWord_7ree()
    }
    
    // 单词导航
    fun ensureWordsLoaded_7ree() {
        loadAllWords_7ree()
    }
    
    private fun loadAllWords_7ree() {
        if (navigationState_7ree.allWords_7ree.value.isNotEmpty()) {
            return
        }
        
        // println("DEBUG: 开始加载所有单词列表")
        
        viewModelScope.launch {
            try {
                wordRepository_7ree.getAllWords_7ree().collect { words ->
                    navigationState_7ree.updateAllWords_7ree(words)
                    // println("DEBUG: 加载所有单词列表完成，共${words.size}个单词")
                }
            } catch (e: Exception) {
                // println("DEBUG: 加载单词列表失败: ${e.message}")
            }
        }
    }
    
    fun navigateToPreviousWord_7ree() {
        // println("DEBUG: navigateToPreviousWord_7ree - 开始切换到上一个单词")
        
        // 确保单词列表已加载
        ensureWordsLoaded_7ree()
        
        val previousWord = navigationState_7ree.getPreviousWord_7ree(wordInput_7ree)
        if (previousWord != null) {
            // println("DEBUG: 切换到上一个单词: ${previousWord.word}")
            loadWordFromHistory_7ree(previousWord.word)
        } else {
            // println("DEBUG: 无法导航到上一个单词")
        }
    }
    
    fun navigateToNextWord_7ree() {
        // println("DEBUG: navigateToNextWord_7ree - 开始切换到下一个单词")
        
        // 确保单词列表已加载
        ensureWordsLoaded_7ree()
        
        val nextWord = navigationState_7ree.getNextWord_7ree(wordInput_7ree)
        if (nextWord != null) {
            // println("DEBUG: 切换到下一个单词: ${nextWord.word}")
            loadWordFromHistory_7ree(nextWord.word)
        } else {
            // println("DEBUG: 无法导航到下一个单词")
        }
    }
    
    fun canNavigate_7ree(): Boolean {
        // 确保单词列表已加载
        ensureWordsLoaded_7ree()
        
        val canNavigate = navigationState_7ree.canNavigate_7ree(wordInput_7ree)
        // println("DEBUG: canNavigate_7ree - wordInput='${wordInput_7ree}', canNavigate=$canNavigate")
        return canNavigate
    }
    
    // TTS相关
    fun setIsSpeaking_7ree(speaking: Boolean) {
        queryState_7ree.updateSpeakingState_7ree(speaking)
    }
    
    fun setIsSpeakingWord_7ree(speaking: Boolean) {
        queryState_7ree.updateSpeakingWordState_7ree(speaking)
    }
    
    fun setIsSpeakingExamples_7ree(speaking: Boolean) {
        queryState_7ree.updateSpeakingExamplesState_7ree(speaking)
    }
    
    fun getWordSpeechText_7ree(): String {
        return wordInput_7ree
    }
    
    fun getExamplesSpeechText_7ree(): String {
        return wordQueryManager_7ree.getExamplesSpeechText_7ree()
    }
    
    // 收藏相关
    fun toggleFavorite_7ree() {
        if (wordInput_7ree.isNotBlank()) {
            dataManagerService_7ree.toggleFavorite_7ree(wordInput_7ree)
        }
    }
    
    fun setFavorite_7ree(isFavorite: Boolean) {
        if (wordInput_7ree.isNotBlank()) {
            dataManagerService_7ree.setFavorite_7ree(wordInput_7ree, isFavorite)
        }
    }
    
    fun setFavorite_7ree(word: String, isFavorite: Boolean) {
        dataManagerService_7ree.setFavorite_7ree(word, isFavorite)
    }
    
    // 单词管理
    fun deleteWord_7ree(word: String) {
        dataManagerService_7ree.deleteWord_7ree(word)
    }
    
    fun getHistoryWords_7ree() = wordRepository_7ree.getAllWords_7ree()
    
    fun loadWordFromHistory_7ree(word: String) {
        // 标记为从单词本进入
        navigationState_7ree.updateFromWordBook_7ree(true)
        
        // 保存当前单词本状态
        savedWordBookFilterState_7ree = showFavoritesOnly_7ree.value
        
        wordQueryManager_7ree.loadWordFromHistory_7ree(word)
    }
    
    // 配置管理
    fun saveApiConfig_7ree(
        apiKey: String, 
        apiUrl: String, 
        modelName: String, 
        azureSpeechRegion: String = "", 
        azureSpeechApiKey: String = "", 
        azureSpeechEndpoint: String = "",
        azureSpeechVoice: String = "en-US-JennyNeural"
    ) {
        configManagerService_7ree.saveApiConfig_7ree(
            apiKey, apiUrl, modelName, azureSpeechRegion, azureSpeechApiKey, azureSpeechEndpoint, azureSpeechVoice
        )
    }
    
    // 新增：保存翻译API配置的方法
    fun saveTranslationApiConfig_7ree(
        api1Name: String, api1Key: String, api1Url: String, api1Model: String, api1Enabled: Boolean,
        api2Name: String, api2Key: String, api2Url: String, api2Model: String, api2Enabled: Boolean
    ) {
        configManagerService_7ree.saveTranslationApiConfig_7ree(
            api1Name, api1Key, api1Url, api1Model, api1Enabled,
            api2Name, api2Key, api2Url, api2Model, api2Enabled
        )
    }
    
    fun savePromptConfig_7ree(queryPrompt: String, outputTemplate: String) {
        configManagerService_7ree.savePromptConfig_7ree(queryPrompt, outputTemplate)
    }
    
    fun saveGeneralConfig_7ree(
        keyboardType: String, 
        autoReadAfterQuery: Boolean, 
        autoReadOnSpellingCard: Boolean, 
        ttsEngine: String
    ) {
        configManagerService_7ree.saveGeneralConfig_7ree(
            keyboardType, autoReadAfterQuery, autoReadOnSpellingCard, ttsEngine
        )
    }
    
    // 新增：保存当前通用配置的方法
    fun saveCurrentGeneralConfig_7ree() {
        val currentConfig = generalConfig_7ree.value
        configManagerService_7ree.saveGeneralConfig_7ree(
            currentConfig.keyboardType,
            currentConfig.autoReadAfterQuery,
            currentConfig.autoReadOnSpellingCard,
            currentConfig.ttsEngine
        )
    }
    
    // 新增：保存当前API配置的方法
    fun saveCurrentApiConfig_7ree() {
        val currentConfig = apiConfig_7ree.value
        configManagerService_7ree.saveTranslationApiConfig_7ree(
            currentConfig.translationApi1.apiName,
            currentConfig.translationApi1.apiKey,
            currentConfig.translationApi1.apiUrl,
            currentConfig.translationApi1.modelName,
            currentConfig.translationApi1.isEnabled,
            currentConfig.translationApi2.apiName,
            currentConfig.translationApi2.apiKey,
            currentConfig.translationApi2.apiUrl,
            currentConfig.translationApi2.modelName,
            currentConfig.translationApi2.isEnabled
        )
    }
    
    // 新增：保存当前提示词配置的方法
    fun saveCurrentPromptConfig_7ree() {
        val currentConfig = promptConfig_7ree.value
        configManagerService_7ree.savePromptConfig_7ree(
            currentConfig.queryPrompt_7ree,
            currentConfig.outputTemplate_7ree
        )
    }
    
    // 数据导入导出
    fun exportHistoryData_7ree() {
        dataManagerService_7ree.exportHistoryData_7ree()
    }
    
    fun importHistoryData_7ree(uri: Uri) {
        dataManagerService_7ree.importHistoryData_7ree(uri)
    }
    
    fun getDataExportImportManager(): DataExportImportManager_7ree {
        return dataExportImportManager_7ree
    }
    
    // 操作结果管理
    fun clearOperationResult_7ree() {
        queryState_7ree.clearOperationResult_7ree()
    }
    
    fun setOperationResult_7ree(message: String) {
        queryState_7ree.updateOperationResult_7ree(message)
    }
    
    // 分页加载
    fun loadWordCount_7ree() {
        dataManagerService_7ree.loadWordCount_7ree()
    }
    
    fun loadTotalViews_7ree() {
        dataManagerService_7ree.loadTotalViews_7ree()
    }
    
    fun loadInitialWords_7ree() {
        dataManagerService_7ree.loadInitialWords_7ree()
    }
    
    fun loadMoreWords_7ree() {
        dataManagerService_7ree.loadMoreWords_7ree()
    }
    
    fun resetPagination_7ree() {
        paginationState_7ree.resetPagination_7ree()
    }
    
    // 状态重置
    fun resetQueryState_7ree() {
        queryState_7ree.resetQueryState_7ree()
    }
    
    // 屏幕导航
    fun setCurrentScreen_7ree(screen: String) {
        navigationState_7ree.updateCurrentScreen_7ree(screen)
    }
    
    fun returnToWordBook_7ree() {
        navigationState_7ree.returnToWordBook_7ree()
    }
    
    // 收藏过滤
    fun toggleFavoriteFilter_7ree() {
        paginationState_7ree.toggleFavoriteFilter_7ree()
        resetPagination_7ree()
        loadInitialWords_7ree()
        // println("DEBUG: 切换单词过滤")
    }
    
    // 搜索功能
    fun updateSearchQuery_7ree(query: String) {
        paginationState_7ree.updateSearchQuery_7ree(query)
        dataManagerService_7ree.searchWords_7ree(query)
    }
    
    fun toggleSearchMode_7ree() {
        paginationState_7ree.toggleSearchMode_7ree()
        if (!isSearchMode_7ree.value) {
            // 退出搜索模式时清空搜索查询并重新加载初始数据
            paginationState_7ree.updateSearchQuery_7ree("")
            resetPagination_7ree()
            loadInitialWords_7ree()
        }
    }
    
    fun setSearchMode_7ree(isSearchMode: Boolean) {
        paginationState_7ree.updateSearchMode_7ree(isSearchMode)
        if (!isSearchMode) {
            // 退出搜索模式时清空搜索查询并重新加载初始数据
            paginationState_7ree.updateSearchQuery_7ree("")
            resetPagination_7ree()
            loadInitialWords_7ree()
        }
    }
    
    fun clearSearch_7ree() {
        paginationState_7ree.clearSearch_7ree()
        resetPagination_7ree()
        loadInitialWords_7ree()
    }
    
    // 排序功能
    fun setSortType_7ree(sortType: String?) {
        paginationState_7ree.updateSortType_7ree(sortType)
        resetPagination_7ree()
        loadInitialWords_7ree()
    }
    
    fun clearSort_7ree() {
        paginationState_7ree.clearSort_7ree()
        resetPagination_7ree()
        loadInitialWords_7ree()
    }
    
    // 拼写练习
    fun onSpellingSuccess_7ree() {
        if (wordInput_7ree.isNotBlank()) {
            dataManagerService_7ree.onSpellingSuccess_7ree(wordInput_7ree)
        }
    }
    
    fun getCurrentSpellingCount_7ree(): Int {
        return currentWordInfo_7ree?.spellingCount ?: 0
    }
    
    // TTS相关
    fun speakWord_7ree(word: String) {
        ttsManagerService_7ree.speakWord_7ree(word)
    }

    fun speakExamples_7ree() {
        val examplesText = getExamplesSpeechText_7ree()
        ttsManagerService_7ree.speakExamples_7ree(examplesText)
    }

    fun stopSpeaking_7ree() {
        ttsManagerService_7ree.stopSpeaking_7ree()
    }
    
    fun getTtsEngineStatus_7ree(): String {
        return ttsManagerService_7ree.getTtsEngineStatus_7ree()
    }
    
    // 资源释放
    override fun onCleared() {
        super.onCleared()
        ttsManagerService_7ree.release_7ree()
        // println("DEBUG: WordQueryViewModel已清理，TTS资源已释放")
    }
}
