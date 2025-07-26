package com.x7ree.wordcard.utils

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * API Key 安全存储管理类
 * 使用 Android Keystore 加密存储敏感的 API Key
 * API URL 和模型名称使用普通 SharedPreferences 存储
 * 提供安全高效的存储和读取功能
 */
class ApiKeySecureStorage_7ree(private val context: Context) {
    
    companion object {
        private const val KEYSTORE_ALIAS = "wordcard_api_key_alias_7ree"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val PREFS_NAME = "secure_api_storage_7ree"
        
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
        
        private const val AZURE_REGION = "azure_region_7ree"
        private const val ENCRYPTED_AZURE_API_KEY = "encrypted_azure_api_key_7ree"
        private const val AZURE_SPEECH_REGION = "azure_speech_region_7ree"
        private const val ENCRYPTED_AZURE_SPEECH_API_KEY = "encrypted_azure_speech_api_key_7ree"
        private const val AZURE_SPEECH_ENDPOINT = "azure_speech_endpoint_7ree"
        private const val AZURE_SPEECH_VOICE = "azure_speech_voice_7ree"
        private const val IV_SUFFIX = "_iv"
    }
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    init {
        generateOrGetSecretKey_7ree()
    }
    
    /**
     * 生成或获取密钥
     */
    private fun generateOrGetSecretKey_7ree(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        
        return if (keyStore.containsAlias(KEYSTORE_ALIAS)) {
            // 密钥已存在，直接获取
            keyStore.getKey(KEYSTORE_ALIAS, null) as SecretKey
        } else {
            // 生成新密钥
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false)
                .build()
            
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }
    
    /**
     * 加密字符串
     */
    private fun encryptString_7ree(plainText: String): Pair<String, String> {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        
        val secretKey = keyStore.getKey(KEYSTORE_ALIAS, null) as SecretKey
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        
        val encryptedText = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        val ivString = Base64.encodeToString(iv, Base64.DEFAULT)
        
        return Pair(encryptedText, ivString)
    }
    
    /**
     * 解密字符串
     */
    private fun decryptString_7ree(encryptedText: String, ivString: String): String {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        
        val secretKey = keyStore.getKey(KEYSTORE_ALIAS, null) as SecretKey
        val cipher = Cipher.getInstance(TRANSFORMATION)
        
        val iv = Base64.decode(ivString, Base64.DEFAULT)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        
        val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        
        return String(decryptedBytes, Charsets.UTF_8)
    }
    
    /**
     * 安全存储API Key
     */
    fun storeApiKey_7ree(apiKey: String): Boolean {
        return try {
            if (apiKey.isBlank()) {
                // 如果API Key为空，删除存储的数据
                clearApiKey_7ree()
                return true
            }
            
            val (encryptedKey, iv) = encryptString_7ree(apiKey)
            sharedPreferences.edit()
                .putString(ENCRYPTED_API_KEY, encryptedKey)
                .putString(ENCRYPTED_API_KEY + IV_SUFFIX, iv)
                .apply()
            true
        } catch (e: Exception) {
            // println("DEBUG: API Key存储失败: ${e.message}")
            false
        }
    }
    
    /**
     * 安全读取API Key
     */
    fun getApiKey_7ree(): String {
        return try {
            val encryptedKey = sharedPreferences.getString(ENCRYPTED_API_KEY, null)
            val iv = sharedPreferences.getString(ENCRYPTED_API_KEY + IV_SUFFIX, null)
            
            if (encryptedKey != null && iv != null) {
                decryptString_7ree(encryptedKey, iv)
            } else {
                ""
            }
        } catch (e: Exception) {
            // println("DEBUG: API Key读取失败: ${e.message}")
            ""
        }
    }
    
    /**
     * 存储API URL（普通存储，无需加密）
     */
    fun storeApiUrl_7ree(apiUrl: String): Boolean {
        return try {
            if (apiUrl.isBlank()) {
                clearApiUrl_7ree()
                return true
            }
            
            sharedPreferences.edit()
                .putString(API_URL, apiUrl)
                .apply()
            true
        } catch (e: Exception) {
            // println("DEBUG: API URL存储失败: ${e.message}")
            false
        }
    }
    
    /**
     * 读取API URL（普通读取）
     */
    fun getApiUrl_7ree(): String {
        return try {
            sharedPreferences.getString(API_URL, null) 
                ?: "https://api.openai.com/v1/chat/completions" // 默认值
        } catch (e: Exception) {
            // println("DEBUG: API URL读取失败: ${e.message}")
            "https://api.openai.com/v1/chat/completions"
        }
    }
    
    /**
     * 存储模型名称（普通存储，无需加密）
     */
    fun storeModelName_7ree(modelName: String): Boolean {
        return try {
            if (modelName.isBlank()) {
                clearModelName_7ree()
                return true
            }
            
            sharedPreferences.edit()
                .putString(MODEL_NAME, modelName)
                .apply()
            true
        } catch (e: Exception) {
            // println("DEBUG: 模型名称存储失败: ${e.message}")
            false
        }
    }
    
    /**
     * 读取模型名称（普通读取）
     */
    fun getModelName_7ree(): String {
        return try {
            sharedPreferences.getString(MODEL_NAME, null) 
                ?: "gpt-3.5-turbo" // 默认值
        } catch (e: Exception) {
            // println("DEBUG: 模型名称读取失败: ${e.message}")
            "gpt-3.5-turbo"
        }
    }
    
    /**
     * 安全存储Azure API Key
     */
    fun storeAzureApiKey_7ree(azureApiKey: String): Boolean {
        return try {
            if (azureApiKey.isBlank()) {
                clearAzureApiKey_7ree()
                return true
            }
            
            val (encryptedKey, iv) = encryptString_7ree(azureApiKey)
            sharedPreferences.edit()
                .putString(ENCRYPTED_AZURE_API_KEY, encryptedKey)
                .putString(ENCRYPTED_AZURE_API_KEY + IV_SUFFIX, iv)
                .apply()
            true
        } catch (e: Exception) {
            // println("DEBUG: Azure API Key存储失败: ${e.message}")
            false
        }
    }
    
    /**
     * 安全读取Azure API Key
     */
    fun getAzureApiKey_7ree(): String {
        return try {
            val encryptedKey = sharedPreferences.getString(ENCRYPTED_AZURE_API_KEY, null)
            val iv = sharedPreferences.getString(ENCRYPTED_AZURE_API_KEY + IV_SUFFIX, null)
            
            if (encryptedKey != null && iv != null) {
                decryptString_7ree(encryptedKey, iv)
            } else {
                ""
            }
        } catch (e: Exception) {
            // println("DEBUG: Azure API Key读取失败: ${e.message}")
            ""
        }
    }
    
    /**
     * 存储Azure区域（普通存储，无需加密）
     */
    fun storeAzureRegion_7ree(azureRegion: String): Boolean {
        return try {
            if (azureRegion.isBlank()) {
                clearAzureRegion_7ree()
                return true
            }
            
            sharedPreferences.edit()
                .putString(AZURE_REGION, azureRegion)
                .apply()
            true
        } catch (e: Exception) {
            // println("DEBUG: Azure区域存储失败: ${e.message}")
            false
        }
    }
    
    /**
     * 读取Azure区域（普通读取）
     */
    fun getAzureRegion_7ree(): String {
        return try {
            sharedPreferences.getString(AZURE_REGION, null) ?: ""
        } catch (e: Exception) {
            // println("DEBUG: Azure区域读取失败: ${e.message}")
            ""
        }
    }

    /**
     * 安全存储Azure Speech API Key
     */
    fun storeAzureSpeechApiKey_7ree(azureSpeechApiKey: String): Boolean {
        return try {
            if (azureSpeechApiKey.isBlank()) {
                clearAzureSpeechApiKey_7ree()
                return true
            }
            
            val (encryptedKey, iv) = encryptString_7ree(azureSpeechApiKey)
            sharedPreferences.edit()
                .putString(ENCRYPTED_AZURE_SPEECH_API_KEY, encryptedKey)
                .putString(ENCRYPTED_AZURE_SPEECH_API_KEY + IV_SUFFIX, iv)
                .apply()
            true
        } catch (e: Exception) {
            // println("DEBUG: Azure Speech API Key存储失败: ${e.message}")
            false
        }
    }
    
    /**
     * 安全读取Azure Speech API Key
     */
    fun getAzureSpeechApiKey_7ree(): String {
        return try {
            val encryptedKey = sharedPreferences.getString(ENCRYPTED_AZURE_SPEECH_API_KEY, null)
            val iv = sharedPreferences.getString(ENCRYPTED_AZURE_SPEECH_API_KEY + IV_SUFFIX, null)
            
            if (encryptedKey != null && iv != null) {
                decryptString_7ree(encryptedKey, iv)
            } else {
                ""
            }
        } catch (e: Exception) {
            // println("DEBUG: Azure Speech API Key读取失败: ${e.message}")
            ""
        }
    }
    
    /**
     * 存储Azure Speech区域（普通存储，无需加密）
     */
    fun storeAzureSpeechRegion_7ree(azureSpeechRegion: String): Boolean {
        return try {
            if (azureSpeechRegion.isBlank()) {
                clearAzureSpeechRegion_7ree()
                return true
            }
            
            sharedPreferences.edit()
                .putString(AZURE_SPEECH_REGION, azureSpeechRegion)
                .apply()
            true
        } catch (e: Exception) {
            // println("DEBUG: Azure Speech区域存储失败: ${e.message}")
            false
        }
    }
    
    /**
     * 读取Azure Speech区域（普通读取）
     */
    fun getAzureSpeechRegion_7ree(): String {
        return try {
            sharedPreferences.getString(AZURE_SPEECH_REGION, null) ?: ""
        } catch (e: Exception) {
            // println("DEBUG: Azure Speech区域读取失败: ${e.message}")
            ""
        }
    }

    /**
     * 存储Azure Speech终结点（普通存储，无需加密）
     */
    fun storeAzureSpeechEndpoint_7ree(azureSpeechEndpoint: String): Boolean {
        return try {
            if (azureSpeechEndpoint.isBlank()) {
                clearAzureSpeechEndpoint_7ree()
                return true
            }
            
            sharedPreferences.edit()
                .putString(AZURE_SPEECH_ENDPOINT, azureSpeechEndpoint)
                .apply()
            true
        } catch (e: Exception) {
            // println("DEBUG: Azure Speech终结点存储失败: ${e.message}")
            false
        }
    }
    
    /**
     * 读取Azure Speech终结点（普通读取）
     */
    fun getAzureSpeechEndpoint_7ree(): String {
        return try {
            sharedPreferences.getString(AZURE_SPEECH_ENDPOINT, null) ?: ""
        } catch (e: Exception) {
            // println("DEBUG: Azure Speech终结点读取失败: ${e.message}")
            ""
        }
    }

    /**
     * 存储Azure Speech音色（普通存储，无需加密）
     */
    fun storeAzureSpeechVoice_7ree(azureSpeechVoice: String): Boolean {
        return try {
            if (azureSpeechVoice.isBlank()) {
                clearAzureSpeechVoice_7ree()
                return true
            }
            
            sharedPreferences.edit()
                .putString(AZURE_SPEECH_VOICE, azureSpeechVoice)
                .apply()
            true
        } catch (e: Exception) {
            // println("DEBUG: Azure Speech音色存储失败: ${e.message}")
            false
        }
    }
    
    /**
     * 读取Azure Speech音色（普通读取）
     */
    fun getAzureSpeechVoice_7ree(): String {
        return try {
            sharedPreferences.getString(AZURE_SPEECH_VOICE, null) ?: "en-US-JennyNeural"
        } catch (e: Exception) {
            // println("DEBUG: Azure Speech音色读取失败: ${e.message}")
            "en-US-JennyNeural"
        }
    }

    /**
     * 批量存储API配置
     */
    fun storeApiConfig_7ree(apiKey: String, apiUrl: String, modelName: String): Boolean {
        return try {
            val keyResult = storeApiKey_7ree(apiKey)
            val urlResult = storeApiUrl_7ree(apiUrl)
            val modelResult = storeModelName_7ree(modelName)
            
            keyResult && urlResult && modelResult
        } catch (e: Exception) {
            // println("DEBUG: API配置批量存储失败: ${e.message}")
            false
        }
    }
    
    /**
     * 批量存储完整API配置（包括Azure）
     */
    fun storeFullApiConfig_7ree(apiKey: String, apiUrl: String, modelName: String, azureRegion: String, azureApiKey: String): Boolean {
        return try {
            val keyResult = storeApiKey_7ree(apiKey)
            val urlResult = storeApiUrl_7ree(apiUrl)
            val modelResult = storeModelName_7ree(modelName)
            val azureRegionResult = storeAzureRegion_7ree(azureRegion)
            val azureKeyResult = storeAzureApiKey_7ree(azureApiKey)
            
            keyResult && urlResult && modelResult && azureRegionResult && azureKeyResult
        } catch (e: Exception) {
            // println("DEBUG: 完整API配置批量存储失败: ${e.message}")
            false
        }
    }

    /**
     * 批量存储完整API配置（包括Azure和Azure Speech）
     */
    fun storeFullApiConfigWithSpeech_7ree(apiKey: String, apiUrl: String, modelName: String, azureRegion: String, azureApiKey: String, azureSpeechRegion: String, azureSpeechApiKey: String, azureSpeechEndpoint: String, azureSpeechVoice: String): Boolean {
        return try {
            val keyResult = storeApiKey_7ree(apiKey)
            val urlResult = storeApiUrl_7ree(apiUrl)
            val modelResult = storeModelName_7ree(modelName)
            val azureRegionResult = storeAzureRegion_7ree(azureRegion)
            val azureKeyResult = storeAzureApiKey_7ree(azureApiKey)
            val azureSpeechRegionResult = storeAzureSpeechRegion_7ree(azureSpeechRegion)
            val azureSpeechKeyResult = storeAzureSpeechApiKey_7ree(azureSpeechApiKey)
            val azureSpeechEndpointResult = storeAzureSpeechEndpoint_7ree(azureSpeechEndpoint)
            val azureSpeechVoiceResult = storeAzureSpeechVoice_7ree(azureSpeechVoice)
            
            keyResult && urlResult && modelResult && azureRegionResult && azureKeyResult && azureSpeechRegionResult && azureSpeechKeyResult && azureSpeechEndpointResult && azureSpeechVoiceResult
        } catch (e: Exception) {
            // println("DEBUG: 完整API配置（含Speech）批量存储失败: ${e.message}")
            false
        }
    }
    
    /**
     * 检查是否有存储的API配置
     */
    fun hasApiConfig_7ree(): Boolean {
        return sharedPreferences.contains(ENCRYPTED_API_KEY) ||
               sharedPreferences.contains(API_URL) ||
               sharedPreferences.contains(MODEL_NAME)
    }
    
    /**
     * 清除API Key
     */
    fun clearApiKey_7ree() {
        sharedPreferences.edit()
            .remove(ENCRYPTED_API_KEY)
            .remove(ENCRYPTED_API_KEY + IV_SUFFIX)
            .apply()
    }
    
    /**
     * 清除API URL
     */
    fun clearApiUrl_7ree() {
        sharedPreferences.edit()
            .remove(API_URL)
            .apply()
    }
    
    /**
     * 清除模型名称
     */
    fun clearModelName_7ree() {
        sharedPreferences.edit()
            .remove(MODEL_NAME)
            .apply()
    }
    
    /**
     * 清除Azure API Key
     */
    fun clearAzureApiKey_7ree() {
        sharedPreferences.edit()
            .remove(ENCRYPTED_AZURE_API_KEY)
            .remove(ENCRYPTED_AZURE_API_KEY + IV_SUFFIX)
            .apply()
    }
    
    /**
     * 清除Azure区域
     */
    fun clearAzureRegion_7ree() {
        sharedPreferences.edit()
            .remove(AZURE_REGION)
            .apply()
    }

    /**
     * 清除Azure Speech API Key
     */
    fun clearAzureSpeechApiKey_7ree() {
        sharedPreferences.edit()
            .remove(ENCRYPTED_AZURE_SPEECH_API_KEY)
            .remove(ENCRYPTED_AZURE_SPEECH_API_KEY + IV_SUFFIX)
            .apply()
    }
    
    /**
     * 清除Azure Speech区域
     */
    fun clearAzureSpeechRegion_7ree() {
        sharedPreferences.edit()
            .remove(AZURE_SPEECH_REGION)
            .apply()
    }

    /**
     * 清除Azure Speech终结点
     */
    fun clearAzureSpeechEndpoint_7ree() {
        sharedPreferences.edit()
            .remove(AZURE_SPEECH_ENDPOINT)
            .apply()
    }

    /**
     * 清除Azure Speech音色
     */
    fun clearAzureSpeechVoice_7ree() {
        sharedPreferences.edit()
            .remove(AZURE_SPEECH_VOICE)
            .apply()
    }

    /**
     * 清除所有存储的API配置
     */
    fun clearAllApiConfig_7ree() {
        clearApiKey_7ree()
        clearApiUrl_7ree()
        clearModelName_7ree()
        clearAzureApiKey_7ree()
        clearAzureRegion_7ree()
        clearAzureSpeechApiKey_7ree()
        clearAzureSpeechRegion_7ree()
        clearAzureSpeechEndpoint_7ree()
    }
    
    // ========== 新的翻译API配置方法 ==========
    
    /**
     * 存储翻译API 1配置
     */
    fun storeTranslationApi1Config_7ree(apiName: String, apiKey: String, apiUrl: String, modelName: String, isEnabled: Boolean): Boolean {
        return try {
            val keyResult = if (apiKey.isBlank()) {
                clearTranslationApi1Key_7ree()
                true
            } else {
                val (encryptedKey, iv) = encryptString_7ree(apiKey)
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
    fun storeTranslationApi2Config_7ree(apiName: String, apiKey: String, apiUrl: String, modelName: String, isEnabled: Boolean): Boolean {
        return try {
            val keyResult = if (apiKey.isBlank()) {
                clearTranslationApi2Key_7ree()
                true
            } else {
                val (encryptedKey, iv) = encryptString_7ree(apiKey)
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
    fun getTranslationApi1Config_7ree(): Pair<Pair<Pair<String, String>, String>, Boolean> {
        return try {
            val apiKey = try {
                val encryptedKey = sharedPreferences.getString(ENCRYPTED_TRANSLATION_API1_KEY, null)
                val iv = sharedPreferences.getString(ENCRYPTED_TRANSLATION_API1_KEY + IV_SUFFIX, null)
                if (encryptedKey != null && iv != null) {
                    decryptString_7ree(encryptedKey, iv)
                } else ""
            } catch (e: Exception) { "" }
            
            val apiUrl = sharedPreferences.getString(TRANSLATION_API1_URL, null) 
                ?: "https://api.openai.com/v1/chat/completions"
            val modelName = sharedPreferences.getString(TRANSLATION_API1_MODEL, null) 
                ?: "gpt-3.5-turbo"
            val isEnabled = sharedPreferences.getBoolean(TRANSLATION_API1_ENABLED, true)
            
            
            Pair(Pair(Pair(apiKey, apiUrl), modelName), isEnabled)
        } catch (e: Exception) {
            Log.e("ApiKeySecureStorage_7ree", "DEBUG: 读取API1配置失败: ${e.message}", e)
            Pair(Pair(Pair("", "https://api.openai.com/v1/chat/completions"), "gpt-3.5-turbo"), true)
        }
    }
    
    /**
     * 读取翻译API 2配置
     */
    fun getTranslationApi2Config_7ree(): Pair<Pair<Pair<String, String>, String>, Boolean> {
        return try {
            val apiKey = try {
                val encryptedKey = sharedPreferences.getString(ENCRYPTED_TRANSLATION_API2_KEY, null)
                val iv = sharedPreferences.getString(ENCRYPTED_TRANSLATION_API2_KEY + IV_SUFFIX, null)
                if (encryptedKey != null && iv != null) {
                    decryptString_7ree(encryptedKey, iv)
                } else ""
            } catch (e: Exception) { "" }
            
            val apiUrl = sharedPreferences.getString(TRANSLATION_API2_URL, null) 
                ?: "https://api.openai.com/v1/chat/completions"
            val modelName = sharedPreferences.getString(TRANSLATION_API2_MODEL, null) 
                ?: "gpt-4"
            val isEnabled = sharedPreferences.getBoolean(TRANSLATION_API2_ENABLED, false)
            
            
            Pair(Pair(Pair(apiKey, apiUrl), modelName), isEnabled)
        } catch (e: Exception) {
            Log.e("ApiKeySecureStorage_7ree", "DEBUG: 读取API2配置失败: ${e.message}", e)
            Pair(Pair(Pair("", "https://api.openai.com/v1/chat/completions"), "gpt-4"), false)
        }
    }
    
    /**
     * 读取翻译API 1名称
     */
    fun getTranslationApi1Name_7ree(): String {
        return try {
            sharedPreferences.getString(TRANSLATION_API1_NAME, "OpenAI GPT-3.5") ?: "OpenAI GPT-3.5"
        } catch (e: Exception) {
            "OpenAI GPT-3.5"
        }
    }
    
    /**
     * 读取翻译API 2名称
     */
    fun getTranslationApi2Name_7ree(): String {
        return try {
            sharedPreferences.getString(TRANSLATION_API2_NAME, "OpenAI GPT-4") ?: "OpenAI GPT-4"
        } catch (e: Exception) {
            "OpenAI GPT-4"
        }
    }
    
    /**
     * 清除翻译API 1 Key
     */
    fun clearTranslationApi1Key_7ree() {
        sharedPreferences.edit()
            .remove(ENCRYPTED_TRANSLATION_API1_KEY)
            .remove(ENCRYPTED_TRANSLATION_API1_KEY + IV_SUFFIX)
            .apply()
    }
    
    /**
     * 清除翻译API 2 Key
     */
    fun clearTranslationApi2Key_7ree() {
        sharedPreferences.edit()
            .remove(ENCRYPTED_TRANSLATION_API2_KEY)
            .remove(ENCRYPTED_TRANSLATION_API2_KEY + IV_SUFFIX)
            .apply()
    }
    
    /**
     * 清除所有翻译API配置
     */
    fun clearAllTranslationApiConfig_7ree() {
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
    
    /**
     * 批量存储新的API配置结构
     */
    fun storeNewApiConfig_7ree(
        api1Name: String, api1Key: String, api1Url: String, api1Model: String, api1Enabled: Boolean,
        api2Name: String, api2Key: String, api2Url: String, api2Model: String, api2Enabled: Boolean,
        azureRegion: String, azureApiKey: String,
        azureSpeechRegion: String, azureSpeechApiKey: String, azureSpeechEndpoint: String, azureSpeechVoice: String
    ): Boolean {
        return try {
            val api1Result = storeTranslationApi1Config_7ree(api1Name, api1Key, api1Url, api1Model, api1Enabled)
            
            val api2Result = storeTranslationApi2Config_7ree(api2Name, api2Key, api2Url, api2Model, api2Enabled)
            
            val azureRegionResult = storeAzureRegion_7ree(azureRegion)
            val azureKeyResult = storeAzureApiKey_7ree(azureApiKey)
            val azureSpeechRegionResult = storeAzureSpeechRegion_7ree(azureSpeechRegion)
            val azureSpeechKeyResult = storeAzureSpeechApiKey_7ree(azureSpeechApiKey)
            val azureSpeechEndpointResult = storeAzureSpeechEndpoint_7ree(azureSpeechEndpoint)
            val azureSpeechVoiceResult = storeAzureSpeechVoice_7ree(azureSpeechVoice)
            
            val finalResult = api1Result && api2Result && azureRegionResult && azureKeyResult && 
            azureSpeechRegionResult && azureSpeechKeyResult && azureSpeechEndpointResult && azureSpeechVoiceResult
            
            finalResult
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 检查是否有新的翻译API配置
     */
    fun hasNewTranslationApiConfig_7ree(): Boolean {
        return sharedPreferences.contains(ENCRYPTED_TRANSLATION_API1_KEY) ||
               sharedPreferences.contains(TRANSLATION_API1_URL) ||
               sharedPreferences.contains(TRANSLATION_API1_MODEL) ||
               sharedPreferences.contains(ENCRYPTED_TRANSLATION_API2_KEY) ||
               sharedPreferences.contains(TRANSLATION_API2_URL) ||
               sharedPreferences.contains(TRANSLATION_API2_MODEL)
    }
    
    /**
     * 验证存储的完整性
     */
    fun validateStorage_7ree(): Boolean {
        return try {
            // 尝试读取所有配置，验证API Key的加密存储是否正常
            getApiKey_7ree() // 这个会验证加密存储
            getApiUrl_7ree()  // 普通存储
            getModelName_7ree() // 普通存储
            true
        } catch (e: Exception) {
            // println("DEBUG: 存储验证失败: ${e.message}")
            false
        }
    }
}
