package com.x7ree.wordcard.utils.securestorage

import android.content.Context
import android.content.SharedPreferences

/**
 * Azure服务存储模块
 * 负责Azure翻译和语音服务配置的存储和读取
 */
class AzureServiceStorage_7ree(
    private val context: Context,
    private val cryptoManager: CryptoManager_7ree
) {
    
    companion object {
        private const val PREFS_NAME = "secure_api_storage_7ree"
        private const val IV_SUFFIX = "_iv"
        
        // Azure翻译服务
        private const val AZURE_REGION = "azure_region_7ree"
        private const val ENCRYPTED_AZURE_API_KEY = "encrypted_azure_api_key_7ree"
        
        // Azure语音服务
        private const val AZURE_SPEECH_REGION = "azure_speech_region_7ree"
        private const val ENCRYPTED_AZURE_SPEECH_API_KEY = "encrypted_azure_speech_api_key_7ree"
        private const val AZURE_SPEECH_ENDPOINT = "azure_speech_endpoint_7ree"
        private const val AZURE_SPEECH_VOICE = "azure_speech_voice_7ree"
    }
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    // Azure翻译服务方法
    fun storeAzureApiKey(azureApiKey: String): Boolean {
        return try {
            if (azureApiKey.isBlank()) {
                clearAzureApiKey()
                return true
            }
            
            val (encryptedKey, iv) = cryptoManager.encryptString(azureApiKey)
            sharedPreferences.edit()
                .putString(ENCRYPTED_AZURE_API_KEY, encryptedKey)
                .putString(ENCRYPTED_AZURE_API_KEY + IV_SUFFIX, iv)
                .apply()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun getAzureApiKey(): String {
        return try {
            val encryptedKey = sharedPreferences.getString(ENCRYPTED_AZURE_API_KEY, null)
            val iv = sharedPreferences.getString(ENCRYPTED_AZURE_API_KEY + IV_SUFFIX, null)
            
            if (encryptedKey != null && iv != null) {
                cryptoManager.decryptString(encryptedKey, iv)
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }
    
    fun storeAzureRegion(azureRegion: String): Boolean {
        return try {
            if (azureRegion.isBlank()) {
                clearAzureRegion()
                return true
            }
            
            sharedPreferences.edit()
                .putString(AZURE_REGION, azureRegion)
                .apply()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun getAzureRegion(): String {
        return try {
            sharedPreferences.getString(AZURE_REGION, null) ?: ""
        } catch (e: Exception) {
            ""
        }
    }
    
    // Azure语音服务方法
    fun storeAzureSpeechApiKey(azureSpeechApiKey: String): Boolean {
        return try {
            if (azureSpeechApiKey.isBlank()) {
                clearAzureSpeechApiKey()
                return true
            }
            
            val (encryptedKey, iv) = cryptoManager.encryptString(azureSpeechApiKey)
            sharedPreferences.edit()
                .putString(ENCRYPTED_AZURE_SPEECH_API_KEY, encryptedKey)
                .putString(ENCRYPTED_AZURE_SPEECH_API_KEY + IV_SUFFIX, iv)
                .apply()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun getAzureSpeechApiKey(): String {
        return try {
            val encryptedKey = sharedPreferences.getString(ENCRYPTED_AZURE_SPEECH_API_KEY, null)
            val iv = sharedPreferences.getString(ENCRYPTED_AZURE_SPEECH_API_KEY + IV_SUFFIX, null)
            
            if (encryptedKey != null && iv != null) {
                cryptoManager.decryptString(encryptedKey, iv)
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }
    
    fun storeAzureSpeechRegion(azureSpeechRegion: String): Boolean {
        return try {
            if (azureSpeechRegion.isBlank()) {
                clearAzureSpeechRegion()
                return true
            }
            
            sharedPreferences.edit()
                .putString(AZURE_SPEECH_REGION, azureSpeechRegion)
                .apply()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun getAzureSpeechRegion(): String {
        return try {
            sharedPreferences.getString(AZURE_SPEECH_REGION, null) ?: ""
        } catch (e: Exception) {
            ""
        }
    }
    
    fun storeAzureSpeechEndpoint(azureSpeechEndpoint: String): Boolean {
        return try {
            if (azureSpeechEndpoint.isBlank()) {
                clearAzureSpeechEndpoint()
                return true
            }
            
            sharedPreferences.edit()
                .putString(AZURE_SPEECH_ENDPOINT, azureSpeechEndpoint)
                .apply()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun getAzureSpeechEndpoint(): String {
        return try {
            sharedPreferences.getString(AZURE_SPEECH_ENDPOINT, null) ?: ""
        } catch (e: Exception) {
            ""
        }
    }
    
    fun storeAzureSpeechVoice(azureSpeechVoice: String): Boolean {
        return try {
            if (azureSpeechVoice.isBlank()) {
                clearAzureSpeechVoice()
                return true
            }
            
            sharedPreferences.edit()
                .putString(AZURE_SPEECH_VOICE, azureSpeechVoice)
                .apply()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun getAzureSpeechVoice(): String {
        return try {
            sharedPreferences.getString(AZURE_SPEECH_VOICE, null) ?: "en-US-JennyNeural"
        } catch (e: Exception) {
            "en-US-JennyNeural"
        }
    }
    
    // 清除方法
    fun clearAzureApiKey() {
        sharedPreferences.edit()
            .remove(ENCRYPTED_AZURE_API_KEY)
            .remove(ENCRYPTED_AZURE_API_KEY + IV_SUFFIX)
            .apply()
    }
    
    fun clearAzureRegion() {
        sharedPreferences.edit()
            .remove(AZURE_REGION)
            .apply()
    }
    
    fun clearAzureSpeechApiKey() {
        sharedPreferences.edit()
            .remove(ENCRYPTED_AZURE_SPEECH_API_KEY)
            .remove(ENCRYPTED_AZURE_SPEECH_API_KEY + IV_SUFFIX)
            .apply()
    }
    
    fun clearAzureSpeechRegion() {
        sharedPreferences.edit()
            .remove(AZURE_SPEECH_REGION)
            .apply()
    }
    
    fun clearAzureSpeechEndpoint() {
        sharedPreferences.edit()
            .remove(AZURE_SPEECH_ENDPOINT)
            .apply()
    }
    
    fun clearAzureSpeechVoice() {
        sharedPreferences.edit()
            .remove(AZURE_SPEECH_VOICE)
            .apply()
    }
    
    fun clearAllAzureConfig() {
        clearAzureApiKey()
        clearAzureRegion()
        clearAzureSpeechApiKey()
        clearAzureSpeechRegion()
        clearAzureSpeechEndpoint()
        clearAzureSpeechVoice()
    }
}