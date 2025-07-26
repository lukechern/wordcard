package com.x7ree.wordcard.utils.securestorage

import android.content.Context
import android.util.Log

/**
 * 存储验证和恢复管理类
 * 负责验证存储的完整性并在必要时提供恢复机制
 */
class StorageValidationManager_7ree(
    private val context: Context,
    private val translationApiStorage: TranslationApiStorage_7ree,
    private val azureServiceStorage: AzureServiceStorage_7ree
) {
    
    companion object {
        private const val TAG = "StorageValidationManager_7ree"
    }
    
    /**
     * 验证存储的完整性
     * @return 验证结果，包含是否通过验证以及详细信息
     */
    fun validateStorageIntegrity(): StorageValidationResult {
        return try {
            // 检查API1配置
            val api1Result = validateTranslationApi1Config()
            
            // 检查API2配置
            val api2Result = validateTranslationApi2Config()
            
            // 检查Azure配置
            val azureResult = validateAzureConfig()
            
            // 检查Azure语音配置
            val azureSpeechResult = validateAzureSpeechConfig()
            
            // 检查向后兼容的配置
            val legacyResult = validateLegacyConfig()
            
            val allPassed = api1Result.isValid && api2Result.isValid && 
                           azureResult.isValid && azureSpeechResult.isValid && legacyResult.isValid
            
            StorageValidationResult(
                isValid = allPassed,
                api1Result = api1Result,
                api2Result = api2Result,
                azureResult = azureResult,
                azureSpeechResult = azureSpeechResult,
                legacyResult = legacyResult
            )
        } catch (e: Exception) {
            Log.e(TAG, "验证存储完整性时发生异常: ${e.message}", e)
            StorageValidationResult(
                isValid = false,
                error = "验证过程中发生异常: ${e.message}"
            )
        }
    }
    
    /**
     * 验证翻译API1配置
     */
    private fun validateTranslationApi1Config(): ValidationResult {
        return try {
            // 尝试读取API1配置
            val config = translationApiStorage.getTranslationApi1Config()
            val apiKey = config.first.first.first
            val apiUrl = config.first.first.second
            val modelName = config.first.second
            val isEnabled = config.second
            
            // 检查关键字段是否有效
            val hasValidApiKey = apiKey.isNotEmpty()
            val hasValidUrl = apiUrl.isNotEmpty()
            val hasValidModel = modelName.isNotEmpty()
            
            ValidationResult(
                isValid = true,
                hasValidApiKey = hasValidApiKey,
                hasValidUrl = hasValidUrl,
                hasValidModel = hasValidModel,
                isEnabled = isEnabled
            )
        } catch (e: Exception) {
            Log.w(TAG, "验证API1配置时发生异常: ${e.message}")
            ValidationResult(
                isValid = false,
                error = "验证API1配置时发生异常: ${e.message}"
            )
        }
    }
    
    /**
     * 验证翻译API2配置
     */
    private fun validateTranslationApi2Config(): ValidationResult {
        return try {
            // 尝试读取API2配置
            val config = translationApiStorage.getTranslationApi2Config()
            val apiKey = config.first.first.first
            val apiUrl = config.first.first.second
            val modelName = config.first.second
            val isEnabled = config.second
            
            // 检查关键字段是否有效
            val hasValidUrl = apiUrl.isNotEmpty()
            val hasValidModel = modelName.isNotEmpty()
            
            ValidationResult(
                isValid = true,
                hasValidApiKey = apiKey.isNotEmpty(),
                hasValidUrl = hasValidUrl,
                hasValidModel = hasValidModel,
                isEnabled = isEnabled
            )
        } catch (e: Exception) {
            Log.w(TAG, "验证API2配置时发生异常: ${e.message}")
            ValidationResult(
                isValid = false,
                error = "验证API2配置时发生异常: ${e.message}"
            )
        }
    }
    
    /**
     * 验证Azure配置
     */
    private fun validateAzureConfig(): ValidationResult {
        return try {
            // 尝试读取Azure配置
            val apiKey = azureServiceStorage.getAzureApiKey()
            val region = azureServiceStorage.getAzureRegion()
            
            // 检查关键字段是否有效
            val hasValidApiKey = apiKey.isNotEmpty()
            val hasValidRegion = region.isNotEmpty()
            
            ValidationResult(
                isValid = true,
                hasValidApiKey = hasValidApiKey,
                hasValidRegion = hasValidRegion
            )
        } catch (e: Exception) {
            Log.w(TAG, "验证Azure配置时发生异常: ${e.message}")
            ValidationResult(
                isValid = false,
                error = "验证Azure配置时发生异常: ${e.message}"
            )
        }
    }
    
    /**
     * 验证Azure语音配置
     */
    private fun validateAzureSpeechConfig(): ValidationResult {
        return try {
            // 尝试读取Azure语音配置
            val apiKey = azureServiceStorage.getAzureSpeechApiKey()
            val region = azureServiceStorage.getAzureSpeechRegion()
            val endpoint = azureServiceStorage.getAzureSpeechEndpoint()
            val voice = azureServiceStorage.getAzureSpeechVoice()
            
            // 检查关键字段是否有效
            val hasValidApiKey = apiKey.isNotEmpty()
            val hasValidRegion = region.isNotEmpty()
            val hasValidEndpoint = endpoint.isNotEmpty()
            val hasValidVoice = voice.isNotEmpty()
            
            ValidationResult(
                isValid = true,
                hasValidApiKey = hasValidApiKey,
                hasValidRegion = hasValidRegion,
                hasValidEndpoint = hasValidEndpoint,
                hasValidVoice = hasValidVoice
            )
        } catch (e: Exception) {
            Log.w(TAG, "验证Azure语音配置时发生异常: ${e.message}")
            ValidationResult(
                isValid = false,
                error = "验证Azure语音配置时发生异常: ${e.message}"
            )
        }
    }
    
    /**
     * 验证向后兼容的配置
     */
    private fun validateLegacyConfig(): ValidationResult {
        return try {
            // 尝试读取旧配置
            val apiKey = translationApiStorage.getApiKey()
            val apiUrl = translationApiStorage.getApiUrl()
            val modelName = translationApiStorage.getModelName()
            
            // 检查关键字段是否有效
            val hasValidApiKey = apiKey.isNotEmpty()
            val hasValidUrl = apiUrl.isNotEmpty()
            val hasValidModel = modelName.isNotEmpty()
            
            ValidationResult(
                isValid = true,
                hasValidApiKey = hasValidApiKey,
                hasValidUrl = hasValidUrl,
                hasValidModel = hasValidModel
            )
        } catch (e: Exception) {
            Log.w(TAG, "验证旧配置时发生异常: ${e.message}")
            ValidationResult(
                isValid = false,
                error = "验证旧配置时发生异常: ${e.message}"
            )
        }
    }
    
    /**
     * 检查是否需要恢复存储
     * @return 是否需要恢复存储
     */
    fun needsStorageRecovery(): Boolean {
        val validationResult = validateStorageIntegrity()
        return !validationResult.isValid
    }
    
    /**
     * 尝试恢复存储
     * @return 恢复结果
     */
    fun attemptStorageRecovery(): RecoveryResult {
        return try {
            // 这里可以实现具体的恢复逻辑
            // 例如：从备份恢复、提示用户重新输入等
            RecoveryResult(
                success = false,
                message = "存储恢复功能尚未实现，请手动重新配置API参数"
            )
        } catch (e: Exception) {
            Log.e(TAG, "尝试恢复存储时发生异常: ${e.message}", e)
            RecoveryResult(
                success = false,
                message = "存储恢复过程中发生异常: ${e.message}"
            )
        }
    }
}

/**
 * 存储验证结果数据类
 */
data class StorageValidationResult(
    val isValid: Boolean,
    val api1Result: ValidationResult = ValidationResult(isValid = false),
    val api2Result: ValidationResult = ValidationResult(isValid = false),
    val azureResult: ValidationResult = ValidationResult(isValid = false),
    val azureSpeechResult: ValidationResult = ValidationResult(isValid = false),
    val legacyResult: ValidationResult = ValidationResult(isValid = false),
    val error: String = ""
)

/**
 * 验证结果数据类
 */
data class ValidationResult(
    val isValid: Boolean,
    val hasValidApiKey: Boolean = false,
    val hasValidUrl: Boolean = false,
    val hasValidModel: Boolean = false,
    val hasValidRegion: Boolean = false,
    val hasValidEndpoint: Boolean = false,
    val hasValidVoice: Boolean = false,
    val isEnabled: Boolean = false,
    val error: String = ""
)

/**
 * 恢复结果数据类
 */
data class RecoveryResult(
    val success: Boolean,
    val message: String
)
