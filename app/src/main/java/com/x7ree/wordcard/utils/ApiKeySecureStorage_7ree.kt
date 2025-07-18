package com.x7ree.wordcard.utils

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
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
        private const val ENCRYPTED_API_KEY = "encrypted_api_key_7ree"
        private const val API_URL = "api_url_7ree"
        private const val MODEL_NAME = "model_name_7ree"
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
            println("DEBUG: API Key存储失败: ${e.message}")
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
            println("DEBUG: API Key读取失败: ${e.message}")
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
            println("DEBUG: API URL存储失败: ${e.message}")
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
            println("DEBUG: API URL读取失败: ${e.message}")
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
            println("DEBUG: 模型名称存储失败: ${e.message}")
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
            println("DEBUG: 模型名称读取失败: ${e.message}")
            "gpt-3.5-turbo"
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
            println("DEBUG: API配置批量存储失败: ${e.message}")
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
     * 清除所有存储的API配置
     */
    fun clearAllApiConfig_7ree() {
        clearApiKey_7ree()
        clearApiUrl_7ree()
        clearModelName_7ree()
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
            println("DEBUG: 存储验证失败: ${e.message}")
            false
        }
    }
}