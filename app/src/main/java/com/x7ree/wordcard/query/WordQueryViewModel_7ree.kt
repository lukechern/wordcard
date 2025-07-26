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
import com.x7ree.wordcard.query.input.InputHandler_7ree
import com.x7ree.wordcard.query.navigation.NavigationHandler_7ree
import com.x7ree.wordcard.query.tts.TtsHandler_7ree
import com.x7ree.wordcard.query.favorite.FavoriteHandler_7ree
import com.x7ree.wordcard.query.config.ConfigHandler_7ree
import com.x7ree.wordcard.query.data.DataHandler_7ree
import com.x7ree.wordcard.query.pagination.PaginationHandler_7ree
import com.x7ree.wordcard.query.spelling.SpellingHandler_7ree
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
    
    // 功能处理模块
    private val inputHandler_7ree: InputHandler_7ree
    private val navigationHandler_7ree: NavigationHandler_7ree
    private val ttsHandler_7ree: TtsHandler_7ree
    private val favoriteHandler_7ree: FavoriteHandler_7ree
    private val configHandler_7ree: ConfigHandler_7ree
    private val dataHandler_7ree: DataHandler_7ree
    private val paginationHandler_7ree: PaginationHandler_7ree
    private val spellingHandler_7ree: SpellingHandler_7ree
    
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
        
        // 初始化功能处理模块
        inputHandler_7ree = InputHandler_7ree(wordQueryManager_7ree)
        navigationHandler_7ree = NavigationHandler_7ree(
            wordRepository_7ree,
            wordQueryManager_7ree,
            navigationState_7ree,
            viewModelScope
        )
        ttsHandler_7ree = TtsHandler_7ree(wordQueryManager_7ree, ttsManagerService_7ree)
        favoriteHandler_7ree = FavoriteHandler_7ree(dataManagerService_7ree, wordRepository_7ree, queryState_7ree)
        configHandler_7ree = ConfigHandler_7ree(configManagerService_7ree)
        dataHandler_7ree = DataHandler_7ree(dataManagerService_7ree, dataExportImportManager_7ree)
        paginationHandler_7ree = PaginationHandler_7ree(dataManagerService_7ree, paginationState_7ree)
        spellingHandler_7ree = SpellingHandler_7ree(dataManagerService_7ree, queryState_7ree)
        
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
        inputHandler_7ree.onWordInputChanged_7ree(newInput) { updatedInput ->
            queryState_7ree.updateWordInput_7ree(updatedInput)
        }
    }
    
    fun queryWord_7ree() {
        inputHandler_7ree.queryWord_7ree()
    }
    
    // 单词导航
    fun ensureWordsLoaded_7ree() {
        navigationHandler_7ree.ensureWordsLoaded_7ree()
    }
    
    fun navigateToPreviousWord_7ree() {
        navigationHandler_7ree.navigateToPreviousWord_7ree(wordInput_7ree)
    }
    
    fun navigateToNextWord_7ree() {
        navigationHandler_7ree.navigateToNextWord_7ree(wordInput_7ree)
    }
    
    fun canNavigate_7ree(): Boolean {
        return navigationHandler_7ree.canNavigate_7ree(wordInput_7ree)
    }
    
    fun setCurrentScreen_7ree(screen: String) {
        navigationHandler_7ree.setCurrentScreen_7ree(screen)
    }
    
    fun returnToWordBook_7ree() {
        navigationHandler_7ree.returnToWordBook_7ree()
    }
    
    // TTS相关
    fun setIsSpeaking_7ree(speaking: Boolean) {
        ttsHandler_7ree.setIsSpeaking_7ree(speaking) { updatedState ->
            queryState_7ree.updateSpeakingState_7ree(updatedState)
        }
    }
    
    fun setIsSpeakingWord_7ree(speaking: Boolean) {
        ttsHandler_7ree.setIsSpeakingWord_7ree(speaking) { updatedState ->
            queryState_7ree.updateSpeakingWordState_7ree(updatedState)
        }
    }
    
    fun setIsSpeakingExamples_7ree(speaking: Boolean) {
        ttsHandler_7ree.setIsSpeakingExamples_7ree(speaking) { updatedState ->
            queryState_7ree.updateSpeakingExamplesState_7ree(updatedState)
        }
    }
    
    fun getWordSpeechText_7ree(): String {
        return ttsHandler_7ree.getWordSpeechText_7ree(wordInput_7ree)
    }
    
    fun getExamplesSpeechText_7ree(): String {
        return ttsHandler_7ree.getExamplesSpeechText_7ree()
    }
    
    fun speakWord_7ree(word: String) {
        ttsHandler_7ree.speakWord_7ree(word)
    }

    fun speakExamples_7ree() {
        ttsHandler_7ree.speakExamples_7ree()
    }

    fun stopSpeaking_7ree() {
        ttsHandler_7ree.stopSpeaking_7ree()
    }
    
    fun getTtsEngineStatus_7ree(): String {
        return ttsHandler_7ree.getTtsEngineStatus_7ree()
    }
    
    // 收藏相关
    fun toggleFavorite_7ree() {
        favoriteHandler_7ree.toggleFavorite_7ree(wordInput_7ree)
    }
    
    fun setFavorite_7ree(isFavorite: Boolean) {
        favoriteHandler_7ree.setFavoriteForCurrentWord_7ree(wordInput_7ree, isFavorite)
    }
    
    fun setFavorite_7ree(word: String, isFavorite: Boolean) {
        favoriteHandler_7ree.setFavoriteForWord_7ree(word, isFavorite)
    }
    
    // 单词管理
    fun deleteWord_7ree(word: String) {
        favoriteHandler_7ree.deleteWord_7ree(word)
    }
    
    fun getHistoryWords_7ree() = favoriteHandler_7ree.getHistoryWords_7ree()
    
    fun loadWordFromHistory_7ree(word: String) {
        wordQueryManager_7ree.loadWordFromHistory_7ree(word)
        // 标记为从单词本进入
        navigationState_7ree.updateFromWordBook_7ree(true)
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
        configHandler_7ree.saveApiConfig_7ree(
            apiKey, apiUrl, modelName, azureSpeechRegion, azureSpeechApiKey, azureSpeechEndpoint, azureSpeechVoice
        )
    }
    
    fun saveTranslationApiConfig_7ree(
        api1Name: String, api1Key: String, api1Url: String, api1Model: String, api1Enabled: Boolean,
        api2Name: String, api2Key: String, api2Url: String, api2Model: String, api2Enabled: Boolean
    ) {
        configHandler_7ree.saveTranslationApiConfig_7ree(
            api1Name, api1Key, api1Url, api1Model, api1Enabled,
            api2Name, api2Key, api2Url, api2Model, api2Enabled
        )
    }
    
    fun savePromptConfig_7ree(queryPrompt: String, outputTemplate: String) {
        configHandler_7ree.savePromptConfig_7ree(queryPrompt, outputTemplate)
    }
    
    fun saveGeneralConfig_7ree(
        keyboardType: String, 
        autoReadAfterQuery: Boolean, 
        autoReadOnSpellingCard: Boolean, 
        ttsEngine: String
    ) {
        configHandler_7ree.saveGeneralConfig_7ree(
            keyboardType, autoReadAfterQuery, autoReadOnSpellingCard, ttsEngine
        )
    }
    
    fun saveAzureSpeechConfig_7ree(
        azureSpeechRegion: String,
        azureSpeechApiKey: String,
        azureSpeechEndpoint: String,
        azureSpeechVoice: String
    ) {
        configHandler_7ree.saveAzureSpeechConfig_7ree(
            azureSpeechRegion, azureSpeechApiKey, azureSpeechEndpoint, azureSpeechVoice
        )
    }
    
    fun saveCurrentGeneralConfigFromUI_7ree() {
        configHandler_7ree.saveCurrentGeneralConfigFromUI_7ree()
    }
    
    fun saveCurrentApiConfigFromUI_7ree() {
        configHandler_7ree.saveCurrentApiConfigFromUI_7ree(apiConfig_7ree.value)
    }
    
    fun saveCurrentPromptConfigFromUI_7ree() {
        configHandler_7ree.saveCurrentPromptConfigFromUI_7ree(promptConfig_7ree.value)
    }
    
    // 数据导入导出
    fun exportHistoryData_7ree() {
        dataHandler_7ree.exportHistoryData_7ree()
    }
    
    fun importHistoryData_7ree(uri: Uri) {
        dataHandler_7ree.importHistoryData_7ree(uri)
    }
    
    fun getDataExportImportManager(): DataExportImportManager_7ree {
        return dataHandler_7ree.getDataExportImportManager()
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
        paginationHandler_7ree.loadWordCount_7ree()
    }
    
    fun loadTotalViews_7ree() {
        paginationHandler_7ree.loadTotalViews_7ree()
    }
    
    fun loadInitialWords_7ree() {
        paginationHandler_7ree.loadInitialWords_7ree()
    }
    
    fun loadMoreWords_7ree() {
        paginationHandler_7ree.loadMoreWords_7ree()
    }
    
    fun resetPagination_7ree() {
        paginationHandler_7ree.resetPagination_7ree()
    }
    
    // 状态重置
    fun resetQueryState_7ree() {
        queryState_7ree.resetQueryState_7ree()
    }
    
    // 收藏过滤
    fun toggleFavoriteFilter_7ree() {
        paginationHandler_7ree.toggleFavoriteFilter_7ree()
    }
    
    // 搜索功能
    fun updateSearchQuery_7ree(query: String) {
        paginationHandler_7ree.updateSearchQuery_7ree(query)
    }
    
    fun toggleSearchMode_7ree() {
        paginationHandler_7ree.toggleSearchMode_7ree()
    }
    
    fun setSearchMode_7ree(isSearchMode: Boolean) {
        paginationHandler_7ree.setSearchMode_7ree(isSearchMode)
    }
    
    fun clearSearch_7ree() {
        paginationHandler_7ree.clearSearch_7ree()
    }
    
    // 排序功能
    fun setSortType_7ree(sortType: String?) {
        paginationHandler_7ree.setSortType_7ree(sortType)
    }
    
    fun clearSort_7ree() {
        paginationHandler_7ree.clearSort_7ree()
    }
    
    // 拼写练习
    fun onSpellingSuccess_7ree() {
        spellingHandler_7ree.onSpellingSuccess_7ree(wordInput_7ree)
    }
    
    fun getCurrentSpellingCount_7ree(): Int {
        return spellingHandler_7ree.getCurrentSpellingCount_7ree()
    }
    
    // 资源释放
    override fun onCleared() {
        super.onCleared()
        ttsManagerService_7ree.release_7ree()
        // println("DEBUG: WordQueryViewModel已清理，TTS资源已释放")
    }
}
