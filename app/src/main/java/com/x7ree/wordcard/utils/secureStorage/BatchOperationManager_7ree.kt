package com.x7ree.wordcard.utils.securestorage

import android.content.Context

/**
 * 批量操作和验证管理类
 * 负责批量存储、验证和清除操作
 */
class BatchOperationManager_7ree(
    private val context: Context,
    private val translationApiStorage: TranslationApiStorage_7ree,
    private val azureServiceStorage: AzureServiceStorage_7ree
) {
    
    /**
     * 批量存储API配置（向后兼容）
     */
    fun storeApiConfig(apiKey: String, apiUrl: String, modelName: String): Boolean {
        return try {
            val keyResult = translationApiStorage.storeApiKey(apiKey)
            val urlResult = translationApiStorage.storeApiUrl(apiUrl)
            val modelResult = translationApiStorage.storeModelName(modelName)
            
            keyResult && urlResult && modelResult
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 批量存储完整API配置（包括Azure）
     */
    fun storeFullApiConfig(
        apiKey: String, 
        apiUrl: String, 
        modelName: String, 
        azureRegion: String, 
        azureApiKey: String
    ): Boolean {
        return try {
            val keyResult = translationApiStorage.storeApiKey(apiKey)
            val urlResult = translationApiStorage.storeApiUrl(apiUrl)
            val modelResult = translationApiStorage.storeModelName(modelName)
            val azureRegionResult = azureServiceStorage.storeAzureRegion(azureRegion)
            val azureKeyResult = azureServiceStorage.storeAzureApiKey(azureApiKey)
            
            keyResult && urlResult && modelResult && azureRegionResult && azureKeyResult
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 批量存储完整API配置（包括Azure和Azure Speech）
     */
    fun storeFullApiConfigWithSpeech(
        apiKey: String, 
        apiUrl: String, 
        modelName: String, 
        azureRegion: String, 
        azureApiKey: String, 
        azureSpeechRegion: String, 
        azureSpeechApiKey: String, 
        azureSpeechEndpoint: String, 
        azureSpeechVoice: String
    ): Boolean {
        return try {
            val keyResult = translationApiStorage.storeApiKey(apiKey)
            val urlResult = translationApiStorage.storeApiUrl(apiUrl)
            val modelResult = translationApiStorage.storeModelName(modelName)
            val azureRegionResult = azureServiceStorage.storeAzureRegion(azureRegion)
            val azureKeyResult = azureServiceStorage.storeAzureApiKey(azureApiKey)
            val azureSpeechRegionResult = azureServiceStorage.storeAzureSpeechRegion(azureSpeechRegion)
            val azureSpeechKeyResult = azureServiceStorage.storeAzureSpeechApiKey(azureSpeechApiKey)
            val azureSpeechEndpointResult = azureServiceStorage.storeAzureSpeechEndpoint(azureSpeechEndpoint)
            val azureSpeechVoiceResult = azureServiceStorage.storeAzureSpeechVoice(azureSpeechVoice)
            
            keyResult && urlResult && modelResult && azureRegionResult && azureKeyResult && 
            azureSpeechRegionResult && azureSpeechKeyResult && azureSpeechEndpointResult && azureSpeechVoiceResult
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 批量存储新的API配置结构
     */
    fun storeNewApiConfig(
        api1Name: String, api1Key: String, api1Url: String, api1Model: String, api1Enabled: Boolean,
        api2Name: String, api2Key: String, api2Url: String, api2Model: String, api2Enabled: Boolean,
        azureRegion: String, azureApiKey: String,
        azureSpeechRegion: String, azureSpeechApiKey: String, azureSpeechEndpoint: String, azureSpeechVoice: String
    ): Boolean {
        return try {
            val api1Result = translationApiStorage.storeTranslationApi1Config(api1Name, api1Key, api1Url, api1Model, api1Enabled)
            val api2Result = translationApiStorage.storeTranslationApi2Config(api2Name, api2Key, api2Url, api2Model, api2Enabled)
            val azureRegionResult = azureServiceStorage.storeAzureRegion(azureRegion)
            val azureKeyResult = azureServiceStorage.storeAzureApiKey(azureApiKey)
            val azureSpeechRegionResult = azureServiceStorage.storeAzureSpeechRegion(azureSpeechRegion)
            val azureSpeechKeyResult = azureServiceStorage.storeAzureSpeechApiKey(azureSpeechApiKey)
            val azureSpeechEndpointResult = azureServiceStorage.storeAzureSpeechEndpoint(azureSpeechEndpoint)
            val azureSpeechVoiceResult = azureServiceStorage.storeAzureSpeechVoice(azureSpeechVoice)
            
            api1Result && api2Result && azureRegionResult && azureKeyResult && 
            azureSpeechRegionResult && azureSpeechKeyResult && azureSpeechEndpointResult && azureSpeechVoiceResult
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 清除所有存储的API配置
     */
    fun clearAllApiConfig() {
        translationApiStorage.clearAllTranslationApiConfig()
        azureServiceStorage.clearAllAzureConfig()
    }
    
    /**
     * 验证存储的完整性
     */
    fun validateStorage(): Boolean {
        return try {
            // 尝试读取所有配置，验证API Key的加密存储是否正常
            translationApiStorage.getApiKey() // 这个会验证加密存储
            translationApiStorage.getApiUrl()  // 普通存储
            translationApiStorage.getModelName() // 普通存储
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 检查是否有存储的API配置
     */
    fun hasApiConfig(): Boolean {
        return translationApiStorage.hasApiConfig()
    }
    
    /**
     * 检查是否有新的翻译API配置
     */
    fun hasNewTranslationApiConfig(): Boolean {
        return translationApiStorage.hasNewTranslationApiConfig()
    }
}