package com.x7ree.wordcard.utils.securestorage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * 翻译API存储模块
 * 负责翻译API配置的存储和读取
 */
class TranslationApiStorage_7ree(
    private val context: Context,
    private val cryptoManager: CryptoManager_7ree
) {
    
    companion object {
        private const val PREFS_NAME = "secure_api_storage_7ree"
        private const val IV_SUFFIX = "_iv"
        
        // 翻译API 1
        private const val ENCRYPTED_TRANSLATION_API1_KEY = "encrypted_translation_api1_key_7ree"
        private const val TRANSLATION_API1_NAME = "translation_api1_name_7ree"
        private const val TRANSLATION_API1_URL = "translation_api1_url_7ree"
        private const val TRANSLATION_API1_MODEL = "translation_api1_model_7ree"
        private const val TRANSLATION_API1_ENABLED = "translation_api1_enabled_7ree"
        
        // 翻译API 2
        private const val ENCRYPTED_TRANSLATION_API2_KEY = "encrypted_translation_api2_key_7ree"
        private const val TRANSLATION_API2_NAME = "translation_api2_name_7ree"
        private const val TRANSLATION_API2_URL = "translation_api2_url_7ree"
        private const val TRANSLATION_API2_MODEL = "translation_api2_model_7ree"
        private const val TRANSLATION_API2_ENABLED = "translation_api2_enabled_7ree"
        
        // 向后兼容的旧字段
        private const val ENCRYPTED_API_KEY = "encrypted_api_key_7ree"
        private const val API_URL = "api_url_7ree"
        private const val MODEL_NAME = "model_name_7ree"
    }
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    /**
     * 存储翻译API 1配置
     */
    fun storeTranslationApi1Config(apiName: String, apiKey: String, apiUrl: String, modelName: String, isEnabled: Boolean): Boolean {
        return try {
            val keyResult = if (apiKey.isBlank()) {
                clearTranslationApi1Key()
                true
            } else {
                val (encryptedKey, iv) = cryptoManager.encryptString(apiKey)
                sharedPreferences.edit()
                    .putString(ENCRYPTED_TRANSLATION_API1_KEY, encryptedKey)
                    .putString(ENCRYPTED_TRANSLATION_API1_KEY + IV_SUFFIX, iv)
                    .apply()
                true
            }
            
            val nameResult = sharedPreferences.edit()
                .putString(TRANSLATION_API1_NAME, apiName)
                .apply().let { true }
            
            val urlResult = sharedPreferences.edit()
                .putString(TRANSLATION_API1_URL, apiUrl.ifBlank { "https://api.openai.com/v1/chat/completions" })
                .apply().let { true }
                
            val modelResult = sharedPreferences.edit()
                .putString(TRANSLATION_API1_MODEL, modelName.ifBlank { "gpt-3.5-turbo" })
                .apply().let { true }
                
            val enabledResult = sharedPreferences.edit()
                .putBoolean(TRANSLATION_API1_ENABLED, isEnabled)
                .apply().let { true }
            
            keyResult && nameResult && urlResult && modelResult && enabledResult
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 存储翻译API 2配置
     */
    fun storeTranslationApi2Config(apiName: String, apiKey: String, apiUrl: String, modelName: String, isEnabled: Boolean): Boolean {
        return try {
            val keyResult = if (apiKey.isBlank()) {
                clearTranslationApi2Key()
                true
            } else {
                val (encryptedKey, iv) = cryptoManager.encryptString(apiKey)
                sharedPreferences.edit()
                    .putString(ENCRYPTED_TRANSLATION_API2_KEY, encryptedKey)
                    .putString(ENCRYPTED_TRANSLATION_API2_KEY + IV_SUFFIX, iv)
                    .apply()
                true
            }
            
            val nameResult = sharedPreferences.edit()
                .putString(TRANSLATION_API2_NAME, apiName)
                .apply().let { true }
            
            val urlResult = sharedPreferences.edit()
                .putString(TRANSLATION_API2_URL, apiUrl.ifBlank { "https://api.openai.com/v1/chat/completions" })
                .apply().let { true }
                
            val modelResult = sharedPreferences.edit()
                .putString(TRANSLATION_API2_MODEL, modelName.ifBlank { "gpt-4" })
                .apply().let { true }
                
            val enabledResult = sharedPreferences.edit()
                .putBoolean(TRANSLATION_API2_ENABLED, isEnabled)
                .apply().let { true }
            
            keyResult && nameResult && urlResult && modelResult && enabledResult
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 读取翻译API 1配置
     */
    fun getTranslationApi1Config(): Pair<Pair<Pair<String, String>, String>, Boolean> {
        return try {
            val apiKey = try {
                val encryptedKey = sharedPreferences.getString(ENCRYPTED_TRANSLATION_API1_KEY, null)
                val iv = sharedPreferences.getString(ENCRYPTED_TRANSLATION_API1_KEY + IV_SUFFIX, null)
                if (encryptedKey != null && iv != null) {
                    cryptoManager.decryptString(encryptedKey, iv)
                } else ""
            } catch (e: Exception) { 
                Log.e("TranslationApiStorage_7ree", "解密API1密钥失败: ${e.message}", e)
                "" 
            }
            
            val apiUrl = sharedPreferences.getString(TRANSLATION_API1_URL, null) 
                ?: "https://api.openai.com/v1/chat/completions"
            val modelName = sharedPreferences.getString(TRANSLATION_API1_MODEL, null) 
                ?: "gpt-3.5-turbo"
            val isEnabled = sharedPreferences.getBoolean(TRANSLATION_API1_ENABLED, true)
            
            Pair(Pair(Pair(apiKey, apiUrl), modelName), isEnabled)
        } catch (e: Exception) {
            Log.e("TranslationApiStorage_7ree", "读取API1配置失败: ${e.message}", e)
            Pair(Pair(Pair("", "https://api.openai.com/v1/chat/completions"), "gpt-3.5-turbo"), true)
        }
    }
    
    /**
     * 读取翻译API 2配置
     */
    fun getTranslationApi2Config(): Pair<Pair<Pair<String, String>, String>, Boolean> {
        return try {
            val apiKey = try {
                val encryptedKey = sharedPreferences.getString(ENCRYPTED_TRANSLATION_API2_KEY, null)
                val iv = sharedPreferences.getString(ENCRYPTED_TRANSLATION_API2_KEY + IV_SUFFIX, null)
                if (encryptedKey != null && iv != null) {
                    cryptoManager.decryptString(encryptedKey, iv)
                } else ""
            } catch (e: Exception) { 
                Log.e("TranslationApiStorage_7ree", "解密API2密钥失败: ${e.message}", e)
                "" 
            }
            
            val apiUrl = sharedPreferences.getString(TRANSLATION_API2_URL, null) 
                ?: "https://api.openai.com/v1/chat/completions"
            val modelName = sharedPreferences.getString(TRANSLATION_API2_MODEL, null) 
                ?: "gpt-4"
            val isEnabled = sharedPreferences.getBoolean(TRANSLATION_API2_ENABLED, false)
            
            Pair(Pair(Pair(apiKey, apiUrl), modelName), isEnabled)
        } catch (e: Exception) {
            Log.e("TranslationApiStorage_7ree", "读取API2配置失败: ${e.message}", e)
            Pair(Pair(Pair("", "https://api.openai.com/v1/chat/completions"), "gpt-4"), false)
        }
    }
    
    /**
     * 读取翻译API 1名称
     */
    fun getTranslationApi1Name(): String {
        return try {
            sharedPreferences.getString(TRANSLATION_API1_NAME, "OpenAI GPT-3.5") ?: "OpenAI GPT-3.5"
        } catch (e: Exception) {
            "OpenAI GPT-3.5"
        }
    }
    
    /**
     * 读取翻译API 2名称
     */
    fun getTranslationApi2Name(): String {
        return try {
            sharedPreferences.getString(TRANSLATION_API2_NAME, "OpenAI GPT-4") ?: "OpenAI GPT-4"
        } catch (e: Exception) {
            "OpenAI GPT-4"
        }
    }
    
    // 向后兼容的方法
    fun storeApiKey(apiKey: String): Boolean {
        return try {
            if (apiKey.isBlank()) {
                clearApiKey()
                return true
            }
            
            val (encryptedKey, iv) = cryptoManager.encryptString(apiKey)
            sharedPreferences.edit()
                .putString(ENCRYPTED_API_KEY, encryptedKey)
                .putString(ENCRYPTED_API_KEY + IV_SUFFIX, iv)
                .apply()
            true
        } catch (e: Exception) {
            android.util.Log.e("TranslationApiStorage_7ree", "存储API密钥失败: ${e.message}", e)
            false
        }
    }
    
    fun getApiKey(): String {
        return try {
            val encryptedKey = sharedPreferences.getString(ENCRYPTED_API_KEY, null)
            val iv = sharedPreferences.getString(ENCRYPTED_API_KEY + IV_SUFFIX, null)
            
            if (encryptedKey != null && iv != null) {
                cryptoManager.decryptString(encryptedKey, iv)
            } else {
                ""
            }
        } catch (e: Exception) {
            android.util.Log.e("TranslationApiStorage_7ree", "解密API密钥失败: ${e.message}", e)
            ""
        }
    }
    
    fun storeApiUrl(apiUrl: String): Boolean {
        return try {
            if (apiUrl.isBlank()) {
                clearApiUrl()
                return true
            }
            
            sharedPreferences.edit()
                .putString(API_URL, apiUrl)
                .apply()
            true
        } catch (e: Exception) {
            android.util.Log.e("TranslationApiStorage_7ree", "存储API URL失败: ${e.message}", e)
            false
        }
    }
    
    fun getApiUrl(): String {
        return try {
            sharedPreferences.getString(API_URL, null) 
                ?: "https://api.openai.com/v1/chat/completions"
        } catch (e: Exception) {
            android.util.Log.e("TranslationApiStorage_7ree", "读取API URL失败: ${e.message}", e)
            "https://api.openai.com/v1/chat/completions"
        }
    }
    
    fun storeModelName(modelName: String): Boolean {
        return try {
            if (modelName.isBlank()) {
                clearModelName()
                return true
            }
            
            sharedPreferences.edit()
                .putString(MODEL_NAME, modelName)
                .apply()
            true
        } catch (e: Exception) {
            android.util.Log.e("TranslationApiStorage_7ree", "存储模型名称失败: ${e.message}", e)
            false
        }
    }
    
    fun getModelName(): String {
        return try {
            sharedPreferences.getString(MODEL_NAME, null) 
                ?: "gpt-3.5-turbo"
        } catch (e: Exception) {
            android.util.Log.e("TranslationApiStorage_7ree", "读取模型名称失败: ${e.message}", e)
            "gpt-3.5-turbo"
        }
    }
    
    // 清除方法
    fun clearTranslationApi1Key() {
        sharedPreferences.edit()
            .remove(ENCRYPTED_TRANSLATION_API1_KEY)
            .remove(ENCRYPTED_TRANSLATION_API1_KEY + IV_SUFFIX)
            .apply()
    }
    
    fun clearTranslationApi2Key() {
        sharedPreferences.edit()
            .remove(ENCRYPTED_TRANSLATION_API2_KEY)
            .remove(ENCRYPTED_TRANSLATION_API2_KEY + IV_SUFFIX)
            .apply()
    }
    
    fun clearAllTranslationApiConfig() {
        sharedPreferences.edit()
            .remove(ENCRYPTED_TRANSLATION_API1_KEY)
            .remove(ENCRYPTED_TRANSLATION_API1_KEY + IV_SUFFIX)
            .remove(TRANSLATION_API1_URL)
            .remove(TRANSLATION_API1_MODEL)
            .remove(TRANSLATION_API1_ENABLED)
            .remove(ENCRYPTED_TRANSLATION_API2_KEY)
            .remove(ENCRYPTED_TRANSLATION_API2_KEY + IV_SUFFIX)
            .remove(TRANSLATION_API2_URL)
            .remove(TRANSLATION_API2_MODEL)
            .remove(TRANSLATION_API2_ENABLED)
            .apply()
    }
    
    fun clearApiKey() {
        sharedPreferences.edit()
            .remove(ENCRYPTED_API_KEY)
            .remove(ENCRYPTED_API_KEY + IV_SUFFIX)
            .apply()
    }
    
    fun clearApiUrl() {
        sharedPreferences.edit()
            .remove(API_URL)
            .apply()
    }
    
    fun clearModelName() {
        sharedPreferences.edit()
            .remove(MODEL_NAME)
            .apply()
    }
    
    fun hasNewTranslationApiConfig(): Boolean {
        return sharedPreferences.contains(ENCRYPTED_TRANSLATION_API1_KEY) ||
               sharedPreferences.contains(TRANSLATION_API1_URL) ||
               sharedPreferences.contains(TRANSLATION_API1_MODEL) ||
               sharedPreferences.contains(ENCRYPTED_TRANSLATION_API2_KEY) ||
               sharedPreferences.contains(TRANSLATION_API2_URL) ||
               sharedPreferences.contains(TRANSLATION_API2_MODEL)
    }
    
    fun hasApiConfig(): Boolean {
        return sharedPreferences.contains(ENCRYPTED_API_KEY) ||
               sharedPreferences.contains(API_URL) ||
               sharedPreferences.contains(MODEL_NAME)
    }
}
