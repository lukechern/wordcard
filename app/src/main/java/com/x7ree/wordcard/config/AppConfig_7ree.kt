package com.x7ree.wordcard.config

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.x7ree.wordcard.utils.ApiKeySecureStorage_7ree

/**
语言包定义

    'pl_config_saved_7r' => '配置已保存',
    'pl_config_load_failed_7r' => '配置加载失败',
    'pl_config_save_failed_7r' => '配置保存失败',
**/

@Serializable
data class ApiConfig_7ree(
    val apiKey: String = "",
    val apiUrl: String = "https://api.openai.com/v1/chat/completions",
    val modelName: String = "gpt-3.5-turbo"
)

@Serializable
data class GeneralConfig_7ree(
    val keyboardType: String = "system", // "system" 或 "custom"
    val autoReadAfterQuery: Boolean = false, // 单词查询完成是否自动朗读
    val autoReadOnSpellingCard: Boolean = false // 拼写单词卡片打开是否自动朗读
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
            // 使用安全存储保存敏感信息
            val secureResult = secureStorage_7ree.storeApiConfig_7ree(
                config.apiKey,
                config.apiUrl,
                config.modelName
            )
            
            if (secureResult) {
                // 标记已有配置（不存储实际内容）
                sharedPreferences.edit()
                    .putBoolean(KEY_API_CONFIG, true)
                    .apply()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("DEBUG: 保存API配置失败: ${e.message}")
            false
        }
    }
    
    // 读取API配置（使用安全存储）
    fun loadApiConfig_7ree(): ApiConfig_7ree {
        return try {
            if (hasApiConfig_7ree()) {
                // 从安全存储读取
                ApiConfig_7ree(
                    apiKey = secureStorage_7ree.getApiKey_7ree(),
                    apiUrl = secureStorage_7ree.getApiUrl_7ree(),
                    modelName = secureStorage_7ree.getModelName_7ree()
                )
            } else {
                // 尝试从旧的明文存储迁移
                migrateFromLegacyStorage_7ree()
            }
        } catch (e: Exception) {
            println("DEBUG: 读取API配置失败: ${e.message}")
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
            println("DEBUG: 保存提示词配置失败: ${e.message}")
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
            println("DEBUG: 读取提示词配置失败: ${e.message}")
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
            println("DEBUG: 保存通用配置失败: ${e.message}")
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
            println("DEBUG: 读取通用配置失败: ${e.message}")
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
                    
                    println("DEBUG: API配置已成功迁移到安全存储")
                    oldConfig
                } else {
                    println("DEBUG: API配置迁移失败，返回默认配置")
                    ApiConfig_7ree()
                }
            } else {
                ApiConfig_7ree()
            }
        } catch (e: Exception) {
            println("DEBUG: API配置迁移异常: ${e.message}")
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
            println("DEBUG: 清除API配置失败: ${e.message}")
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