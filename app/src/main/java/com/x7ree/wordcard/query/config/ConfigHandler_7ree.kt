package com.x7ree.wordcard.query.config

import com.x7ree.wordcard.query.manager.ConfigManager_7ree

/**
 * 配置管理功能模块
 */
class ConfigHandler_7ree(
    private val configManager_7ree: ConfigManager_7ree
) {
    
    /**
     * 保存API配置
     */
    fun saveApiConfig_7ree(
        apiKey: String, 
        apiUrl: String, 
        modelName: String, 
        azureSpeechRegion: String = "", 
        azureSpeechApiKey: String = "", 
        azureSpeechEndpoint: String = "",
        azureSpeechVoice: String = "en-US-JennyNeural"
    ) {
        configManager_7ree.saveApiConfig_7ree(
            apiKey, apiUrl, modelName, azureSpeechRegion, azureSpeechApiKey, azureSpeechEndpoint, azureSpeechVoice
        )
    }
    
    /**
     * 保存翻译API配置
     */
    fun saveTranslationApiConfig_7ree(
        api1Name: String, api1Key: String, api1Url: String, api1Model: String, api1Enabled: Boolean,
        api2Name: String, api2Key: String, api2Url: String, api2Model: String, api2Enabled: Boolean
    ) {
        configManager_7ree.saveTranslationApiConfig_7ree(
            api1Name, api1Key, api1Url, api1Model, api1Enabled,
            api2Name, api2Key, api2Url, api2Model, api2Enabled
        )
    }
    
    /**
     * 保存提示词配置
     */
    fun savePromptConfig_7ree(queryPrompt: String, outputTemplate: String, articleGenerationPrompt: String, articleOutputTemplate: String) {
        configManager_7ree.savePromptConfig_7ree(queryPrompt, outputTemplate, articleGenerationPrompt, articleOutputTemplate)
    }
    
    /**
     * 保存通用配置
     */
    fun saveGeneralConfig_7ree(
        keyboardType: String, 
        autoReadAfterQuery: Boolean, 
        autoReadOnSpellingCard: Boolean, 
        ttsEngine: String
    ) {
        configManager_7ree.saveGeneralConfig_7ree(
            keyboardType, autoReadAfterQuery, autoReadOnSpellingCard, ttsEngine
        )
    }
    
    /**
     * 保存Azure Speech配置
     */
    fun saveAzureSpeechConfig_7ree(
        azureSpeechRegion: String,
        azureSpeechApiKey: String,
        azureSpeechEndpoint: String,
        azureSpeechVoice: String
    ) {
        configManager_7ree.saveAzureSpeechConfig_7ree(
            azureSpeechRegion, azureSpeechApiKey, azureSpeechEndpoint, azureSpeechVoice
        )
    }
    
    /**
     * 保存当前通用配置（从UI状态）
     */
    fun saveCurrentGeneralConfigFromUI_7ree() {
        // 这个方法现在由UI组件直接调用保存方法替代
        println("DEBUG: saveCurrentGeneralConfigFromUI_7ree被调用，但应该由UI组件直接调用保存方法")
    }
    
    /**
     * 保存当前API配置（从UI状态）
     */
    fun saveCurrentApiConfigFromUI_7ree(apiConfig: com.x7ree.wordcard.config.ApiConfig_7ree) {
        configManager_7ree.saveTranslationApiConfig_7ree(
            apiConfig.translationApi1.apiName,
            apiConfig.translationApi1.apiKey,
            apiConfig.translationApi1.apiUrl,
            apiConfig.translationApi1.modelName,
            apiConfig.translationApi1.isEnabled,
            apiConfig.translationApi2.apiName,
            apiConfig.translationApi2.apiKey,
            apiConfig.translationApi2.apiUrl,
            apiConfig.translationApi2.modelName,
            apiConfig.translationApi2.isEnabled
        )
    }
    
    /**
     * 保存当前提示词配置（从UI状态）
     */
    fun saveCurrentPromptConfigFromUI_7ree(promptConfig: com.x7ree.wordcard.config.PromptConfig_7ree) {
        configManager_7ree.savePromptConfig_7ree(
            promptConfig.queryPrompt_7ree,
            promptConfig.outputTemplate_7ree,
            promptConfig.articleGenerationPrompt_7ree,
            promptConfig.articleOutputTemplate_7ree
        )
    }
}
