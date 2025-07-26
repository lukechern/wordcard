package com.x7ree.wordcard.api

import com.x7ree.wordcard.api.config.ApiConfigManager_7ree
import com.x7ree.wordcard.api.service.ApiQueryService_7ree
import com.x7ree.wordcard.api.service.ApiStreamService_7ree
import com.x7ree.wordcard.config.ApiConfig_7ree
import com.x7ree.wordcard.config.PromptConfig_7ree
import kotlinx.coroutines.flow.Flow

class OpenAiApiService_7ree(
    private var apiConfig_7ree: ApiConfig_7ree = ApiConfig_7ree(),
    private var promptConfig_7ree: PromptConfig_7ree = PromptConfig_7ree()
) {
    private val apiConfigManager_7ree = ApiConfigManager_7ree(apiConfig_7ree, promptConfig_7ree)
    
    // 服务实例
    private var apiQueryService_7ree = ApiQueryService_7ree(apiConfigManager_7ree, apiConfig_7ree)
    private var apiStreamService_7ree = ApiStreamService_7ree(apiConfigManager_7ree, apiConfig_7ree, apiQueryService_7ree)
    
    // 更新API配置
    fun updateApiConfig_7ree(config: ApiConfig_7ree) {
        apiConfigManager_7ree.updateApiConfig_7ree(config)
        apiConfig_7ree = config
        // 重新创建服务实例以使用新的配置
        recreateServices_7ree()
    }
    
    // 更新提示词配置
    fun updatePromptConfig_7ree(config: PromptConfig_7ree) {
        apiConfigManager_7ree.updatePromptConfig_7ree(config)
        promptConfig_7ree = config
        // 重新创建服务实例以使用新的配置
        recreateServices_7ree()
    }
    
    // 重新创建服务实例
    private fun recreateServices_7ree() {
        apiQueryService_7ree = ApiQueryService_7ree(apiConfigManager_7ree, apiConfig_7ree)
        apiStreamService_7ree = ApiStreamService_7ree(apiConfigManager_7ree, apiConfig_7ree, apiQueryService_7ree)
    }
    
    // 获取当前API配置
    fun getActiveApiConfig_7ree(): ApiConfig_7ree {
        return apiConfigManager_7ree.getActiveApiConfig_7ree()
    }
    
    // 获取查询提示词
    private fun getQueryPrompt_7ree(): String {
        return apiConfigManager_7ree.getQueryPrompt_7ree()
    }
    
    // 获取输出模板
    private fun getOutputTemplate_7ree(): String {
        return apiConfigManager_7ree.getOutputTemplate_7ree()
    }

    // 验证和修正API URL
    private fun validateAndFixApiUrl_7ree(apiUrl: String): String {
        return apiConfigManager_7ree.validateAndFixApiUrl_7ree(apiUrl)
    }

    suspend fun queryWord_7ree(word: String): Result<String> {
        return apiQueryService_7ree.queryWord_7ree(word)
    }

    suspend fun queryWordStream_7ree(word: String): Flow<String> {
        return apiStreamService_7ree.queryWordStream_7ree(word)
    }

    suspend fun queryWordStreamTest_7ree(word: String): Flow<String> {
        return apiStreamService_7ree.queryWordStreamTest_7ree(word)
    }

    suspend fun queryWordStreamSimple_7ree(word: String): Flow<String> {
        return apiStreamService_7ree.queryWordStreamSimple_7ree(word)
    }

    suspend fun testApiConnection_7ree(): Result<String> {
        return apiQueryService_7ree.testApiConnection_7ree()
    }
}
