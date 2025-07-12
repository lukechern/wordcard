package com.x7ree.wordcard.config

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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

class AppConfigManager_7ree(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "wordcard_config_7ree",
        Context.MODE_PRIVATE
    )
    
    private val json = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true 
    }
    
    companion object {
        private const val KEY_API_CONFIG = "api_config_7ree"
        private const val KEY_PROMPT_CONFIG = "prompt_config_7ree"
    }
    
    // 保存API配置
    fun saveApiConfig_7ree(config: ApiConfig_7ree): Boolean {
        return try {
            val configJson = json.encodeToString(config)
            sharedPreferences.edit()
                .putString(KEY_API_CONFIG, configJson)
                .apply()
            true
        } catch (e: Exception) {
            println("DEBUG: 保存API配置失败: ${e.message}")
            false
        }
    }
    
    // 读取API配置
    fun loadApiConfig_7ree(): ApiConfig_7ree {
        return try {
            val configJson = sharedPreferences.getString(KEY_API_CONFIG, null)
            if (configJson != null) {
                json.decodeFromString<ApiConfig_7ree>(configJson)
            } else {
                ApiConfig_7ree()
            }
        } catch (e: Exception) {
            println("DEBUG: 读取API配置失败: ${e.message}")
            ApiConfig_7ree()
        }
    }
    
    // 检查是否有保存的配置
    fun hasApiConfig_7ree(): Boolean {
        return sharedPreferences.contains(KEY_API_CONFIG)
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
} 