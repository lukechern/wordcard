package com.x7ree.wordcard.query.manager

import androidx.lifecycle.viewModelScope
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.config.ApiConfig_7ree
import com.x7ree.wordcard.config.AppConfigManager_7ree
import com.x7ree.wordcard.config.GeneralConfig_7ree
import com.x7ree.wordcard.config.PromptConfig_7ree
import com.x7ree.wordcard.query.state.ConfigState_7ree
import com.x7ree.wordcard.query.state.WordQueryState_7ree
import com.x7ree.wordcard.tts.TtsManager_7ree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 配置管理器
 */
class ConfigManager_7ree(
    private val appConfigManager_7ree: AppConfigManager_7ree,
    private val apiService_7ree: OpenAiApiService_7ree,
    private val ttsManager_7ree: TtsManager_7ree,
    private val configState_7ree: ConfigState_7ree,
    private val queryState_7ree: WordQueryState_7ree,
    private val coroutineScope: CoroutineScope
) {

    fun loadApiConfig_7ree() {
        val config = appConfigManager_7ree.loadApiConfig_7ree()
        configState_7ree.updateApiConfig_7ree(config)
        // 更新API服务的配置
        apiService_7ree.updateApiConfig_7ree(config)
    }
    
    fun loadPromptConfig_7ree() {
        val config = appConfigManager_7ree.loadPromptConfig_7ree()
        configState_7ree.updatePromptConfig_7ree(config)
        // 更新API服务的提示词配置
        apiService_7ree.updatePromptConfig_7ree(config)
    }
    
    fun loadGeneralConfig_7ree() {
        val config = appConfigManager_7ree.loadGeneralConfig_7ree()
        configState_7ree.updateGeneralConfig_7ree(config)
    }
    
    fun saveApiConfig_7ree(
        apiKey: String, 
        apiUrl: String, 
        modelName: String, 
        azureSpeechRegion: String = "", 
        azureSpeechApiKey: String = "", 
        azureSpeechEndpoint: String = "",
        azureSpeechVoice: String = "en-US-JennyNeural"
    ) {
        coroutineScope.launch {
            try {
                val currentConfig = configState_7ree.apiConfig_7ree.value
                val config = ApiConfig_7ree(
                    apiKey = apiKey,
                    apiUrl = apiUrl,
                    modelName = modelName,
                    azureRegion = currentConfig.azureRegion,
                    azureApiKey = currentConfig.azureApiKey,
                    azureSpeechRegion = azureSpeechRegion,
                    azureSpeechApiKey = azureSpeechApiKey,
                    azureSpeechEndpoint = azureSpeechEndpoint,
                    azureSpeechVoice = azureSpeechVoice
                )
                
                val success = appConfigManager_7ree.saveApiConfig_7ree(config)
                if (success) {
                    configState_7ree.updateApiConfig_7ree(config)
                    // 更新API服务的配置
                    apiService_7ree.updateApiConfig_7ree(config)
                    // 更新TTS管理器的API配置
                    ttsManager_7ree.updateApiConfig(config)
                    queryState_7ree.updateOperationResult_7ree("配置保存成功")
                    // println("DEBUG: API配置保存成功")
                } else {
                    queryState_7ree.updateOperationResult_7ree("配置保存失败")
                    // println("DEBUG: API配置保存失败")
                }
            } catch (e: Exception) {
                queryState_7ree.updateOperationResult_7ree("配置保存失败: ${e.message}")
                // println("DEBUG: API配置保存异常: ${e.message}")
            }
        }
    }
    
    fun savePromptConfig_7ree(queryPrompt: String, outputTemplate: String) {
        coroutineScope.launch {
            try {
                val config = PromptConfig_7ree(
                    queryPrompt_7ree = queryPrompt,
                    outputTemplate_7ree = outputTemplate
                )
                
                val success = appConfigManager_7ree.savePromptConfig_7ree(config)
                if (success) {
                    configState_7ree.updatePromptConfig_7ree(config)
                    // 更新API服务的提示词配置
                    apiService_7ree.updatePromptConfig_7ree(config)
                    queryState_7ree.updateOperationResult_7ree("提示词配置保存成功")
                    // println("DEBUG: 提示词配置保存成功")
                } else {
                    queryState_7ree.updateOperationResult_7ree("提示词配置保存失败")
                    // println("DEBUG: 提示词配置保存失败")
                }
            } catch (e: Exception) {
                queryState_7ree.updateOperationResult_7ree("提示词配置保存失败: ${e.message}")
                // println("DEBUG: 提示词配置保存异常: ${e.message}")
            }
        }
    }
    
    fun saveGeneralConfig_7ree(
        keyboardType: String, 
        autoReadAfterQuery: Boolean, 
        autoReadOnSpellingCard: Boolean, 
        ttsEngine: String
    ) {
        coroutineScope.launch {
            try {
                val config = GeneralConfig_7ree(
                    keyboardType = keyboardType,
                    autoReadAfterQuery = autoReadAfterQuery,
                    autoReadOnSpellingCard = autoReadOnSpellingCard,
                    ttsEngine = ttsEngine
                )
                
                val success = appConfigManager_7ree.saveGeneralConfig_7ree(config)
                if (success) {
                    configState_7ree.updateGeneralConfig_7ree(config)
                    // 更新TTS管理器的配置
                    ttsManager_7ree.updateGeneralConfig(config)
                    queryState_7ree.updateOperationResult_7ree("通用配置保存成功")
                    // println("DEBUG: 通用配置保存成功")
                } else {
                    queryState_7ree.updateOperationResult_7ree("通用配置保存失败")
                    // println("DEBUG: 通用配置保存失败")
                }
            } catch (e: Exception) {
                queryState_7ree.updateOperationResult_7ree("通用配置保存失败: ${e.message}")
                // println("DEBUG: 通用配置保存异常: ${e.message}")
            }
        }
    }
}