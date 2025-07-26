package com.x7ree.wordcard.test.speech

import com.x7ree.wordcard.config.ApiConfig_7ree

/**
 * Speech API 配置验证器
 */
class SpeechConfigValidator {
    
    companion object {
        private const val TAG = "SpeechConfigValidator"
    }
    
    /**
     * 验证 Speech API 配置格式
     * 现在终结点是可选的，系统会根据区域自动生成
     */
    fun validateSpeechConfig(apiConfig: ApiConfig_7ree): Pair<Boolean, String> {
        return when {
            apiConfig.azureSpeechApiKey.isBlank() -> false to "Azure Speech API 密钥不能为空"
            apiConfig.azureSpeechRegion.isBlank() -> false to "Azure Speech 区域不能为空"
            // 终结点不再是必填项，如果提供了则验证格式
            apiConfig.azureSpeechEndpoint.isNotBlank() && !apiConfig.azureSpeechEndpoint.startsWith("https://") -> 
                false to "Azure Speech 终结点必须以 https:// 开头（或留空使用自动生成）"
            else -> true to "Speech API 配置格式正确"
        }
    }
}
