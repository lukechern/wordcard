package com.x7ree.wordcard.test

import android.util.Log
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.config.ApiConfig_7ree
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withTimeoutOrNull

/**
 * 翻译大模型 API 测试器
 */
class TranslationApiTester_7ree {
    
    companion object {
        private const val TAG = "TranslationApiTester_7ree"
        private const val TEST_WORD = "hello"
        private const val TIMEOUT_MS = 30000L // 30秒超时
    }
    
    /**
     * 测试翻译 API 连接和功能
     * @param apiConfig API 配置
     * @param onResult 测试结果回调 (isSuccess: Boolean, message: String)
     */
    suspend fun testTranslationApi(
        apiConfig: ApiConfig_7ree,
        onResult: (Boolean, String) -> Unit
    ) {
        
        try {
            // 验证配置完整性
            val activeApi = apiConfig.getActiveTranslationApi()
            if (activeApi.apiKey.isBlank()) {
                onResult(false, "API Key 不能为空")
                return
            }
            
            if (activeApi.apiUrl.isBlank()) {
                onResult(false, "API URL 不能为空")
                return
            }
            
            if (activeApi.modelName.isBlank()) {
                onResult(false, "模型名称不能为空")
                return
            }
            
            // 创建临时 API 服务实例
            val apiService = OpenAiApiService_7ree()
            apiService.updateApiConfig_7ree(apiConfig)
            
            
            // 执行测试查询，带超时控制
            val result = withTimeoutOrNull(TIMEOUT_MS) {
                var testResult = ""
                var hasError = false
                var errorMessage = ""
                
                try {
                    apiService.queryWordStreamSimple_7ree(TEST_WORD)
                        .catch { exception ->
                            hasError = true
                            errorMessage = "API调用失败: ${exception.message}"
                        }
                        .onCompletion { exception ->
                            if (exception != null) {
                                hasError = true
                                errorMessage = "数据流处理失败: ${exception.message}"
                            }
                        }
                        .collect { chunk ->
                            testResult += chunk
                        }
                } catch (e: Exception) {
                    hasError = true
                    errorMessage = "测试异常: ${e.message}"
                }
                
                if (hasError) {
                    TestResult(false, errorMessage)
                } else if (testResult.isNotBlank()) {
                    TestResult(true, "API测试成功！收到响应数据 ${testResult.length} 字符")
                } else {
                    TestResult(false, "API响应为空，可能配置有误")
                }
            }
            
            if (result == null) {
                onResult(false, "API测试超时（${TIMEOUT_MS/1000}秒），请检查网络连接和API配置")
            } else {
                onResult(result.success, result.message)
            }
            
        } catch (e: Exception) {
            onResult(false, "测试失败: ${e.localizedMessage ?: e.message ?: "未知错误"}")
        }
    }
    
    /**
     * 验证 API 配置格式
     */
    fun validateApiConfig(apiConfig: ApiConfig_7ree): Pair<Boolean, String> {
        val activeApi = apiConfig.getActiveTranslationApi()
        return when {
            activeApi.apiKey.isBlank() -> false to "API Key 不能为空"
            activeApi.apiUrl.isBlank() -> false to "API URL 不能为空"
            activeApi.modelName.isBlank() -> false to "模型名称不能为空"
            !activeApi.apiUrl.startsWith("http") -> false to "API URL 格式不正确，应以 http:// 或 https:// 开头"
            else -> true to "配置格式正确"
        }
    }
    
    /**
     * 测试结果数据类
     */
    private data class TestResult(
        val success: Boolean,
        val message: String
    )
}
