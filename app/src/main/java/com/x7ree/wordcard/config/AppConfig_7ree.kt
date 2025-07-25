package com.x7ree.wordcard.config

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.x7ree.wordcard.utils.ApiKeySecureStorage_7ree
import android.util.Log


@Serializable
data class TranslationApiConfig_7ree(
    val apiName: String = "",
    val apiKey: String = "",
    val apiUrl: String = "https://api.openai.com/v1/chat/completions",
    val modelName: String = "gpt-3.5-turbo",
    val isEnabled: Boolean = true
)

@Serializable
data class ApiConfig_7ree(
    val translationApi1: TranslationApiConfig_7ree = TranslationApiConfig_7ree(
        apiName = "OpenAI GPT-3.5"
    ),
    val translationApi2: TranslationApiConfig_7ree = TranslationApiConfig_7ree(
        apiName = "OpenAI GPT-4",
        apiUrl = "https://api.openai.com/v1/chat/completions",
        modelName = "gpt-4",
        isEnabled = false
    ),
    val azureRegion: String = "",
    val azureApiKey: String = "",
    val azureSpeechRegion: String = "",
    val azureSpeechApiKey: String = "",
    val azureSpeechEndpoint: String = "",
    val azureSpeechVoice: String = "en-US-JennyNeural", // 默认音色：女性-美式
    
    // 向后兼容的属性
    @Deprecated("使用 translationApi1.apiKey")
    val apiKey: String = "",
    @Deprecated("使用 translationApi1.apiUrl")
    val apiUrl: String = "https://api.openai.com/v1/chat/completions",
    @Deprecated("使用 translationApi1.modelName")
    val modelName: String = "gpt-3.5-turbo"
) {
    // 获取当前启用的翻译API配置
    fun getActiveTranslationApi(): TranslationApiConfig_7ree {
        return when {
            translationApi1.isEnabled -> translationApi1
            translationApi2.isEnabled -> translationApi2
            else -> translationApi1 // 默认使用第一个
        }
    }
}

@Serializable
data class GeneralConfig_7ree(
    val keyboardType: String = "system", // "system" 或 "custom"
    val autoReadAfterQuery: Boolean = false, // 单词查询完成是否自动朗读
    val autoReadOnSpellingCard: Boolean = false, // 拼写单词卡片打开是否自动朗读
    val ttsEngine: String = "google" // "google" 或 "azure"
)

class AppConfigManager_7ree(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "wordcard_config_7ree",
        Context.MODE_PRIVATE
    )
    
    private val secureStorage_7ree = ApiKeySecureStorage_7ree(context)
    
    private val json = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true 
    }
    
    companion object {
        private const val KEY_API_CONFIG = "api_config_7ree"
        private const val KEY_PROMPT_CONFIG = "prompt_config_7ree"
        private const val KEY_GENERAL_CONFIG = "general_config_7ree"
    }
    
    // 保存API配置（使用安全存储）
    fun saveApiConfig_7ree(config: ApiConfig_7ree): Boolean {
        return try {
            Log.d("AppConfig_7ree", "DEBUG: 保存API配置")
            Log.d("AppConfig_7ree", "DEBUG: API1配置 - 名称: ${config.translationApi1.apiName}, URL: ${config.translationApi1.apiUrl}, 模型: ${config.translationApi1.modelName}, 启用: ${config.translationApi1.isEnabled}")
            Log.d("AppConfig_7ree", "DEBUG: API2配置 - 名称: ${config.translationApi2.apiName}, URL: ${config.translationApi2.apiUrl}, 模型: ${config.translationApi2.modelName}, 启用: ${config.translationApi2.isEnabled}")
            
            // 使用新的API配置结构保存，包括API名称
            val secureResult = secureStorage_7ree.storeNewApiConfig_7ree(
                config.translationApi1.apiName,
                config.translationApi1.apiKey,
                config.translationApi1.apiUrl,
                config.translationApi1.modelName,
                config.translationApi1.isEnabled,
                config.translationApi2.apiName,
                config.translationApi2.apiKey,
                config.translationApi2.apiUrl,
                config.translationApi2.modelName,
                config.translationApi2.isEnabled,
                config.azureRegion,
                config.azureApiKey,
                config.azureSpeechRegion,
                config.azureSpeechApiKey,
                config.azureSpeechEndpoint,
                config.azureSpeechVoice
            )
            
            if (secureResult) {
                Log.d("AppConfig_7ree", "DEBUG: API配置安全存储保存成功")
                // 标记已有配置（不存储实际内容）
                sharedPreferences.edit()
                    .putBoolean(KEY_API_CONFIG, true)
                    .apply()
                true
            } else {
                Log.e("AppConfig_7ree", "DEBUG: API配置安全存储保存失败")
                false
            }
        } catch (e: Exception) {
            Log.e("AppConfig_7ree", "DEBUG: 保存API配置失败: ${e.message}", e)
            false
        }
    }
    
    // 读取API配置（使用安全存储）
    fun loadApiConfig_7ree(): ApiConfig_7ree {
        return try {
            Log.d("AppConfig_7ree", "DEBUG: 读取API配置")
            if (hasApiConfig_7ree()) {
                Log.d("AppConfig_7ree", "DEBUG: 存在API配置")
                // 检查是否有新的翻译API配置结构
                if (secureStorage_7ree.hasNewTranslationApiConfig_7ree()) {
                    Log.d("AppConfig_7ree", "DEBUG: 使用新的翻译API配置结构")
                    // 使用新的配置结构
                    val api1Config = secureStorage_7ree.getTranslationApi1Config_7ree()
                    val api2Config = secureStorage_7ree.getTranslationApi2Config_7ree()
                    
                    // 读取API名称
                    val api1Name = secureStorage_7ree.getTranslationApi1Name_7ree()
                    val api2Name = secureStorage_7ree.getTranslationApi2Name_7ree()
                    
                    val config = ApiConfig_7ree(
                        translationApi1 = TranslationApiConfig_7ree(
                            apiName = api1Name,
                            apiKey = api1Config.first.first.first,
                            apiUrl = api1Config.first.first.second,
                            modelName = api1Config.first.second,
                            isEnabled = api1Config.second
                        ),
                        translationApi2 = TranslationApiConfig_7ree(
                            apiName = api2Name,
                            apiKey = api2Config.first.first.first,
                            apiUrl = api2Config.first.first.second,
                            modelName = api2Config.first.second,
                            isEnabled = api2Config.second
                        ),
                        azureRegion = secureStorage_7ree.getAzureRegion_7ree(),
                        azureApiKey = secureStorage_7ree.getAzureApiKey_7ree(),
                        azureSpeechRegion = secureStorage_7ree.getAzureSpeechRegion_7ree(),
                        azureSpeechApiKey = secureStorage_7ree.getAzureSpeechApiKey_7ree(),
                        azureSpeechEndpoint = secureStorage_7ree.getAzureSpeechEndpoint_7ree(),
                        azureSpeechVoice = secureStorage_7ree.getAzureSpeechVoice_7ree()
                    )
                    
                    Log.d("AppConfig_7ree", "DEBUG: 加载的API配置 - API1: ${config.translationApi1.apiName}, URL: ${config.translationApi1.apiUrl}, 模型: ${config.translationApi1.modelName}, 启用: ${config.translationApi1.isEnabled}, API Key长度: ${config.translationApi1.apiKey.length}")
                    Log.d("AppConfig_7ree", "DEBUG: 加载的API配置 - API2: ${config.translationApi2.apiName}, URL: ${config.translationApi2.apiUrl}, 模型: ${config.translationApi2.modelName}, 启用: ${config.translationApi2.isEnabled}, API Key长度: ${config.translationApi2.apiKey.length}")
                    
                    config
                } else {
                    Log.d("AppConfig_7ree", "DEBUG: 使用旧的API配置结构")
                    // 使用旧的配置结构，迁移到新结构
                    val oldApiKey = secureStorage_7ree.getApiKey_7ree()
                    val oldApiUrl = secureStorage_7ree.getApiUrl_7ree()
                    val oldModelName = secureStorage_7ree.getModelName_7ree()
                    
                    val config = ApiConfig_7ree(
                        translationApi1 = TranslationApiConfig_7ree(
                            apiName = "OpenAI GPT-3.5",
                            apiKey = oldApiKey,
                            apiUrl = oldApiUrl,
                            modelName = oldModelName,
                            isEnabled = true
                        ),
                        translationApi2 = TranslationApiConfig_7ree(
                            apiName = "OpenAI GPT-4",
                            apiKey = "",
                            apiUrl = "https://api.openai.com/v1/chat/completions",
                            modelName = "gpt-4",
                            isEnabled = false
                        ),
                        azureRegion = secureStorage_7ree.getAzureRegion_7ree(),
                        azureApiKey = secureStorage_7ree.getAzureApiKey_7ree(),
                        azureSpeechRegion = secureStorage_7ree.getAzureSpeechRegion_7ree(),
                        azureSpeechApiKey = secureStorage_7ree.getAzureSpeechApiKey_7ree(),
                        azureSpeechEndpoint = secureStorage_7ree.getAzureSpeechEndpoint_7ree(),
                        azureSpeechVoice = secureStorage_7ree.getAzureSpeechVoice_7ree()
                    )
                    
                    Log.d("AppConfig_7ree", "DEBUG: 加载的旧API配置 - API1: ${config.translationApi1.apiName}, URL: ${config.translationApi1.apiUrl}, 模型: ${config.translationApi1.modelName}, 启用: ${config.translationApi1.isEnabled}")
                    Log.d("AppConfig_7ree", "DEBUG: 加载的旧API配置 - API2: ${config.translationApi2.apiName}, URL: ${config.translationApi2.apiUrl}, 模型: ${config.translationApi2.modelName}, 启用: ${config.translationApi2.isEnabled}")
                    
                    config
                }
            } else {
                Log.d("AppConfig_7ree", "DEBUG: 不存在API配置，尝试从旧的明文存储迁移")
                // 尝试从旧的明文存储迁移
                migrateFromLegacyStorage_7ree()
            }
        } catch (e: Exception) {
            Log.e("AppConfig_7ree", "DEBUG: 读取API配置失败: ${e.message}", e)
            ApiConfig_7ree()
        }
    }
    
    // 检查是否有保存的配置
    fun hasApiConfig_7ree(): Boolean {
        return secureStorage_7ree.hasApiConfig_7ree() || 
               sharedPreferences.contains(KEY_API_CONFIG)
    }
    
    // 保存提示词配置
    fun savePromptConfig_7ree(config: PromptConfig_7ree): Boolean {
        return try {
            val configJson = json.encodeToString(config)
            sharedPreferences.edit()
                .putString(KEY_PROMPT_CONFIG, configJson)
                .apply()
            true
        } catch (e: Exception) {
            // println("DEBUG: 保存提示词配置失败: ${e.message}")
            false
        }
    }
    
    // 读取提示词配置
    fun loadPromptConfig_7ree(): PromptConfig_7ree {
        return try {
            val configJson = sharedPreferences.getString(KEY_PROMPT_CONFIG, null)
            if (configJson != null) {
                json.decodeFromString<PromptConfig_7ree>(configJson)
            } else {
                PromptConfig_7ree()
            }
        } catch (e: Exception) {
            // println("DEBUG: 读取提示词配置失败: ${e.message}")
            PromptConfig_7ree()
        }
    }
    
    // 检查是否有保存的提示词配置
    fun hasPromptConfig_7ree(): Boolean {
        return sharedPreferences.contains(KEY_PROMPT_CONFIG)
    }
    
    // 保存通用配置
    fun saveGeneralConfig_7ree(config: GeneralConfig_7ree): Boolean {
        return try {
            val configJson = json.encodeToString(config)
            sharedPreferences.edit()
                .putString(KEY_GENERAL_CONFIG, configJson)
                .apply()
            true
        } catch (e: Exception) {
            // println("DEBUG: 保存通用配置失败: ${e.message}")
            false
        }
    }
    
    // 读取通用配置
    fun loadGeneralConfig_7ree(): GeneralConfig_7ree {
        return try {
            val configJson = sharedPreferences.getString(KEY_GENERAL_CONFIG, null)
            if (configJson != null) {
                json.decodeFromString<GeneralConfig_7ree>(configJson)
            } else {
                GeneralConfig_7ree()
            }
        } catch (e: Exception) {
            // println("DEBUG: 读取通用配置失败: ${e.message}")
            GeneralConfig_7ree()
        }
    }
    
    // 检查是否有保存的通用配置
    fun hasGeneralConfig_7ree(): Boolean {
        return sharedPreferences.contains(KEY_GENERAL_CONFIG)
    }
    
    /**
     * 从旧的明文存储迁移到安全存储
     */
    private fun migrateFromLegacyStorage_7ree(): ApiConfig_7ree {
        return try {
            val configJson = sharedPreferences.getString(KEY_API_CONFIG, null)
            if (configJson != null) {
                // 解析旧配置
                val oldConfig = json.decodeFromString<ApiConfig_7ree>(configJson)
                
                // 迁移到安全存储
                val migrationSuccess = secureStorage_7ree.storeApiConfig_7ree(
                    oldConfig.apiKey,
                    oldConfig.apiUrl,
                    oldConfig.modelName
                )
                
                if (migrationSuccess) {
                    // 清除旧的明文存储
                    sharedPreferences.edit()
                        .remove(KEY_API_CONFIG)
                        .putBoolean(KEY_API_CONFIG, true) // 标记已迁移
                        .apply()
                    
                    // println("DEBUG: API配置已成功迁移到安全存储")
                    oldConfig
                } else {
                    // println("DEBUG: API配置迁移失败，返回默认配置")
                    ApiConfig_7ree()
                }
            } else {
                ApiConfig_7ree()
            }
        } catch (e: Exception) {
            // println("DEBUG: API配置迁移异常: ${e.message}")
            ApiConfig_7ree()
        }
    }
    
    /**
     * 清除所有API配置（包括安全存储）
     */
    fun clearApiConfig_7ree(): Boolean {
        return try {
            // 清除安全存储
            secureStorage_7ree.clearAllApiConfig_7ree()
            
            // 清除标记
            sharedPreferences.edit()
                .remove(KEY_API_CONFIG)
                .apply()
            
            true
        } catch (e: Exception) {
            // println("DEBUG: 清除API配置失败: ${e.message}")
            false
        }
    }
    
    /**
     * 验证安全存储的完整性
     */
    fun validateSecureStorage_7ree(): Boolean {
        return secureStorage_7ree.validateStorage_7ree()
    }
}
