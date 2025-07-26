package com.x7ree.wordcard.api.config

import com.x7ree.wordcard.config.ApiConfig_7ree
import com.x7ree.wordcard.config.PromptConfig_7ree

class ApiConfigManager_7ree(
    private var apiConfig_7ree: ApiConfig_7ree = ApiConfig_7ree(),
    private var promptConfig_7ree: PromptConfig_7ree = PromptConfig_7ree()
) {
    // 更新API配置
    fun updateApiConfig_7ree(config: ApiConfig_7ree) {
        apiConfig_7ree = config
    }
    
    // 更新提示词配置
    fun updatePromptConfig_7ree(config: PromptConfig_7ree) {
        promptConfig_7ree = config
    }
    
    // 获取当前API配置
    fun getActiveApiConfig_7ree(): ApiConfig_7ree {
        return apiConfig_7ree
    }
    
    // 获取查询提示词
    fun getQueryPrompt_7ree(): String {
        return promptConfig_7ree.queryPrompt_7ree
    }
    
    // 获取输出模板
    fun getOutputTemplate_7ree(): String {
        return promptConfig_7ree.outputTemplate_7ree
    }

    // 验证和修正API URL
    fun validateAndFixApiUrl_7ree(apiUrl: String): String {
        var fixedUrl = apiUrl.trim()
        
        // 如果URL不以http开头，添加https://
        if (!fixedUrl.startsWith("http://") && !fixedUrl.startsWith("https://")) {
            fixedUrl = "https://$fixedUrl"
        }
        
        // 如果URL不包含chat/completions端点，自动添加
        if (!fixedUrl.contains("/chat/completions")) {
            // 移除末尾的斜杠
            fixedUrl = fixedUrl.trimEnd('/')
            // 添加正确的端点
            fixedUrl += "/chat/completions"
        }
        
        return fixedUrl
    }
}
