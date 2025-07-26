package com.x7ree.wordcard.utils

import android.content.Context
import com.x7ree.wordcard.utils.securestorage.CryptoManager_7ree
import com.x7ree.wordcard.utils.securestorage.TranslationApiStorage_7ree
import com.x7ree.wordcard.utils.securestorage.AzureServiceStorage_7ree
import com.x7ree.wordcard.utils.securestorage.BatchOperationManager_7ree
import com.x7ree.wordcard.utils.securestorage.StorageValidationManager_7ree

/**
 * API Key 安全存储管理类（重构版本）
 * 使用模块化设计，将功能拆分到不同的子模块中
 * 提供与原版本兼容的接口
 */
class ApiKeySecureStorage_7ree(private val context: Context) {
    
    private val cryptoManager = CryptoManager_7ree()
    private val translationApiStorage = TranslationApiStorage_7ree(context, cryptoManager)
    private val azureServiceStorage = AzureServiceStorage_7ree(context, cryptoManager)
    private val batchOperationManager = BatchOperationManager_7ree(context, translationApiStorage, azureServiceStorage)
    private val storageValidationManager = StorageValidationManager_7ree(context, translationApiStorage, azureServiceStorage)
    
    init {
        cryptoManager.generateOrGetSecretKey()
    }
    
    // ========== 向后兼容的方法 ==========
    
    fun storeApiKey_7ree(apiKey: String): Boolean = translationApiStorage.storeApiKey(apiKey)
    fun getApiKey_7ree(): String = translationApiStorage.getApiKey()
    fun storeApiUrl_7ree(apiUrl: String): Boolean = translationApiStorage.storeApiUrl(apiUrl)
    fun getApiUrl_7ree(): String = translationApiStorage.getApiUrl()
    fun storeModelName_7ree(modelName: String): Boolean = translationApiStorage.storeModelName(modelName)
    fun getModelName_7ree(): String = translationApiStorage.getModelName()
    
    // Azure服务方法
    fun storeAzureApiKey_7ree(azureApiKey: String): Boolean = azureServiceStorage.storeAzureApiKey(azureApiKey)
    fun getAzureApiKey_7ree(): String = azureServiceStorage.getAzureApiKey()
    fun storeAzureRegion_7ree(azureRegion: String): Boolean = azureServiceStorage.storeAzureRegion(azureRegion)
    fun getAzureRegion_7ree(): String = azureServiceStorage.getAzureRegion()
    
    fun storeAzureSpeechApiKey_7ree(azureSpeechApiKey: String): Boolean = azureServiceStorage.storeAzureSpeechApiKey(azureSpeechApiKey)
    fun getAzureSpeechApiKey_7ree(): String = azureServiceStorage.getAzureSpeechApiKey()
    fun storeAzureSpeechRegion_7ree(azureSpeechRegion: String): Boolean = azureServiceStorage.storeAzureSpeechRegion(azureSpeechRegion)
    fun getAzureSpeechRegion_7ree(): String = azureServiceStorage.getAzureSpeechRegion()
    fun storeAzureSpeechEndpoint_7ree(azureSpeechEndpoint: String): Boolean = azureServiceStorage.storeAzureSpeechEndpoint(azureSpeechEndpoint)
    fun getAzureSpeechEndpoint_7ree(): String = azureServiceStorage.getAzureSpeechEndpoint()
    fun storeAzureSpeechVoice_7ree(azureSpeechVoice: String): Boolean = azureServiceStorage.storeAzureSpeechVoice(azureSpeechVoice)
    fun getAzureSpeechVoice_7ree(): String = azureServiceStorage.getAzureSpeechVoice()
    
    // 翻译API配置方法
    fun storeTranslationApi1Config_7ree(apiName: String, apiKey: String, apiUrl: String, modelName: String, isEnabled: Boolean): Boolean =
        translationApiStorage.storeTranslationApi1Config(apiName, apiKey, apiUrl, modelName, isEnabled)
    
    fun storeTranslationApi2Config_7ree(apiName: String, apiKey: String, apiUrl: String, modelName: String, isEnabled: Boolean): Boolean =
        translationApiStorage.storeTranslationApi2Config(apiName, apiKey, apiUrl, modelName, isEnabled)
    
    fun getTranslationApi1Config_7ree(): Pair<Pair<Pair<String, String>, String>, Boolean> =
        translationApiStorage.getTranslationApi1Config()
    
    fun getTranslationApi2Config_7ree(): Pair<Pair<Pair<String, String>, String>, Boolean> =
        translationApiStorage.getTranslationApi2Config()
    
    fun getTranslationApi1Name_7ree(): String = translationApiStorage.getTranslationApi1Name()
    fun getTranslationApi2Name_7ree(): String = translationApiStorage.getTranslationApi2Name()
    
    // 批量操作方法
    fun storeApiConfig_7ree(apiKey: String, apiUrl: String, modelName: String): Boolean =
        batchOperationManager.storeApiConfig(apiKey, apiUrl, modelName)
    
    fun storeFullApiConfig_7ree(apiKey: String, apiUrl: String, modelName: String, azureRegion: String, azureApiKey: String): Boolean =
        batchOperationManager.storeFullApiConfig(apiKey, apiUrl, modelName, azureRegion, azureApiKey)
    
    fun storeFullApiConfigWithSpeech_7ree(
        apiKey: String, apiUrl: String, modelName: String, azureRegion: String, azureApiKey: String,
        azureSpeechRegion: String, azureSpeechApiKey: String, azureSpeechEndpoint: String, azureSpeechVoice: String
    ): Boolean = batchOperationManager.storeFullApiConfigWithSpeech(
        apiKey, apiUrl, modelName, azureRegion, azureApiKey,
        azureSpeechRegion, azureSpeechApiKey, azureSpeechEndpoint, azureSpeechVoice
    )
    
    fun storeNewApiConfig_7ree(
        api1Name: String, api1Key: String, api1Url: String, api1Model: String, api1Enabled: Boolean,
        api2Name: String, api2Key: String, api2Url: String, api2Model: String, api2Enabled: Boolean,
        azureRegion: String, azureApiKey: String,
        azureSpeechRegion: String, azureSpeechApiKey: String, azureSpeechEndpoint: String, azureSpeechVoice: String
    ): Boolean = batchOperationManager.storeNewApiConfig(
        api1Name, api1Key, api1Url, api1Model, api1Enabled,
        api2Name, api2Key, api2Url, api2Model, api2Enabled,
        azureRegion, azureApiKey, azureSpeechRegion, azureSpeechApiKey, azureSpeechEndpoint, azureSpeechVoice
    )
    
    // 清除方法
    fun clearApiKey_7ree() = translationApiStorage.clearTranslationApi1Key()
    fun clearApiUrl_7ree() = translationApiStorage.clearApiUrl()
    fun clearModelName_7ree() = translationApiStorage.clearModelName()
    fun clearAzureApiKey_7ree() = azureServiceStorage.clearAzureApiKey()
    fun clearAzureRegion_7ree() = azureServiceStorage.clearAzureRegion()
    fun clearAzureSpeechApiKey_7ree() = azureServiceStorage.clearAzureSpeechApiKey()
    fun clearAzureSpeechRegion_7ree() = azureServiceStorage.clearAzureSpeechRegion()
    fun clearAzureSpeechEndpoint_7ree() = azureServiceStorage.clearAzureSpeechEndpoint()
    fun clearAzureSpeechVoice_7ree() = azureServiceStorage.clearAzureSpeechVoice()
    fun clearTranslationApi1Key_7ree() = translationApiStorage.clearTranslationApi1Key()
    fun clearTranslationApi2Key_7ree() = translationApiStorage.clearTranslationApi2Key()
    fun clearAllTranslationApiConfig_7ree() = translationApiStorage.clearAllTranslationApiConfig()
    fun clearAllApiConfig_7ree() = batchOperationManager.clearAllApiConfig()
    
    // 验证和检查方法
    fun hasApiConfig_7ree(): Boolean = batchOperationManager.hasApiConfig()
    fun hasNewTranslationApiConfig_7ree(): Boolean = batchOperationManager.hasNewTranslationApiConfig()
    fun validateStorage_7ree(): Boolean = batchOperationManager.validateStorage()
    
    // 新的存储验证方法
    fun validateStorageIntegrity_7ree() = storageValidationManager.validateStorageIntegrity()
    fun needsStorageRecovery_7ree(): Boolean = storageValidationManager.needsStorageRecovery()
    fun attemptStorageRecovery_7ree() = storageValidationManager.attemptStorageRecovery()
}
