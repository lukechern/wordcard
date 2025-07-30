package com.x7ree.wordcard.api.service

import com.x7ree.wordcard.api.client.ApiClient_7ree
import com.x7ree.wordcard.api.config.ApiConfigManager_7ree
import com.x7ree.wordcard.api.models.*
import com.x7ree.wordcard.config.ApiConfig_7ree
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

/**
 * API查询服务类
 */
class ApiQueryService_7ree(
    private val apiConfigManager_7ree: ApiConfigManager_7ree,
    private val apiConfig_7ree: ApiConfig_7ree
) {
    
    /**
     * 查询单词
     */
    suspend fun queryWord_7ree(word: String): Result<String> {
        return try {
            val prompt_7ree = """${apiConfigManager_7ree.getQueryPrompt_7ree()}

请严格按照以下模板格式输出：

${apiConfigManager_7ree.getOutputTemplate_7ree()}

待解释的英文单词：$word"""
            val activeApi = apiConfig_7ree.getActiveTranslationApi()
            val fixedApiUrl = apiConfigManager_7ree.validateAndFixApiUrl_7ree(activeApi.apiUrl)
            
            // 判断是否为文章生成请求（通过检查提示词内容）
            val isArticleGeneration = prompt_7ree.contains("文章", ignoreCase = true) || 
                                    prompt_7ree.contains("article", ignoreCase = true) ||
                                    prompt_7ree.contains("故事", ignoreCase = true) ||
                                    prompt_7ree.contains("story", ignoreCase = true) ||
                                    prompt_7ree.contains("写作", ignoreCase = true) ||
                                    prompt_7ree.contains("创作", ignoreCase = true)
            
            // 根据请求类型设置不同的创造性参数
            val temperature: Double
            val topP: Double?
            val frequencyPenalty: Double?
            val presencePenalty: Double?
            
            if (isArticleGeneration) {
                // 文章生成使用更高的创造性参数
                temperature = 0.9   // 高温度，增加随机性和创造性
                topP = 0.95         // 高top_p，保持多样性的同时确保连贯性
                frequencyPenalty = 0.3  // 适度的频率惩罚，减少重复词汇
                presencePenalty = 0.6   // 较高的存在惩罚，鼓励引入新话题和概念
            } else {
                // 单词查询使用保守参数，确保准确性
                temperature = 0.7
                topP = null
                frequencyPenalty = null
                presencePenalty = null
            }
            
            // 根据API类型创建不同的请求
            val request_7ree: ChatCompletionRequest_7ree = if (activeApi.apiName.contains("通义千问", ignoreCase = true) || 
                activeApi.apiUrl.contains("modelscope.cn", ignoreCase = true)) {
                // 通义千问API需要特殊参数
                ChatCompletionRequest_7ree(
                    model = activeApi.modelName,
                    messages = listOf(Message_7ree(role = "user", content = prompt_7ree)),
                    temperature = temperature,
                    top_p = topP,
                    frequency_penalty = frequencyPenalty,
                    presence_penalty = presencePenalty,
                    stream = false,
                    enable_thinking = false
                )
            } else {
                // 其他API使用默认请求
                ChatCompletionRequest_7ree(
                    model = activeApi.modelName,
                    messages = listOf(Message_7ree(role = "user", content = prompt_7ree)),
                    temperature = temperature,
                    top_p = topP,
                    frequency_penalty = frequencyPenalty,
                    presence_penalty = presencePenalty
                )
            }

            val response_7ree: HttpResponse = ApiClient_7ree.client_7ree.post(fixedApiUrl) {
                // 根据API类型设置不同的认证头
                if (activeApi.apiName.contains("通义千问", ignoreCase = true) || 
                    activeApi.apiUrl.contains("modelscope.cn", ignoreCase = true)) {
                    // 通义千问使用Authorization头
                    header(HttpHeaders.Authorization, "Bearer ${activeApi.apiKey}")
                    // 可能需要额外的请求头
                    header("X-DashScope-SSE", "enable")
                } else if (activeApi.apiName.contains("智谱", ignoreCase = true) || 
                          activeApi.apiUrl.contains("bigmodel.cn", ignoreCase = true)) {
                    // 智谱清言可能需要不同的认证方式
                    header(HttpHeaders.Authorization, "Bearer ${activeApi.apiKey}")
                } else {
                    // 默认使用Bearer认证
                    header(HttpHeaders.Authorization, "Bearer ${activeApi.apiKey}")
                }
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(request_7ree)
            }

            // 获取响应文本
            val responseText = response_7ree.bodyAsText()

            // 检查是否有错误信息
            if (responseText.contains("\"error\"", ignoreCase = true)) {
                try {
                    val errorResponse: ErrorResponse_7ree = ApiClient_7ree.json_7ree.decodeFromString<ErrorResponse_7ree>(responseText)
                    val errorMessage = errorResponse.error?.message ?: "未知错误"
                    return Result.failure(Exception("API 请求失败: $errorMessage"))
                } catch (e: Exception) {
                    return Result.failure(Exception("API 请求失败: ${responseText}"))
                }
            }

            // 尝试解析正常响应
            val parsedResponse: ChatCompletionResponse_7ree = try {
                ApiClient_7ree.json_7ree.decodeFromString<ChatCompletionResponse_7ree>(responseText)
            } catch (e: Exception) {
                return Result.failure(Exception("API 请求失败: 无法解析响应内容 - ${e.message}"))
            }

            val result_7ree = parsedResponse.choices?.firstOrNull()?.message?.content
            if (result_7ree != null) {
                Result.success(result_7ree)
            } else {
                Result.failure(Exception("API 请求失败: No content in response."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 测试API连接
     */
    suspend fun testApiConnection_7ree(): Result<String> {
        return try {
            val prompt_7ree = "Hello"
            val activeApi = apiConfig_7ree.getActiveTranslationApi()
            val fixedApiUrl = apiConfigManager_7ree.validateAndFixApiUrl_7ree(activeApi.apiUrl)
            // 根据API类型创建不同的请求
            val request_7ree: ChatCompletionRequest_7ree = if (activeApi.apiName.contains("通义千问", ignoreCase = true) || 
                activeApi.apiUrl.contains("modelscope.cn", ignoreCase = true)) {
                // 通义千问API需要特殊参数
                ChatCompletionRequest_7ree(
                    model = activeApi.modelName,
                    messages = listOf(Message_7ree(role = "user", content = prompt_7ree)),
                    stream = false,
                    enable_thinking = false
                )
            } else {
                // 其他API使用默认请求
                ChatCompletionRequest_7ree(
                    model = activeApi.modelName,
                    messages = listOf(Message_7ree(role = "user", content = prompt_7ree))
                )
            }

            val response_7ree: HttpResponse = ApiClient_7ree.client_7ree.post(fixedApiUrl) {
                // 根据API类型设置不同的认证头
                if (activeApi.apiName.contains("通义千问", ignoreCase = true) || 
                    activeApi.apiUrl.contains("modelscope.cn", ignoreCase = true)) {
                    // 通义千问使用Authorization头
                    header(HttpHeaders.Authorization, "Bearer ${activeApi.apiKey}")
                    // 可能需要额外的请求头
                    header("X-DashScope-SSE", "enable")
                } else if (activeApi.apiName.contains("智谱", ignoreCase = true) || 
                          activeApi.apiUrl.contains("bigmodel.cn", ignoreCase = true)) {
                    // 智谱清言可能需要不同的认证方式
                    header(HttpHeaders.Authorization, "Bearer ${activeApi.apiKey}")
                } else {
                    // 默认使用Bearer认证
                    header(HttpHeaders.Authorization, "Bearer ${activeApi.apiKey}")
                }
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(request_7ree)
            }

            // 获取响应文本
            val responseText = response_7ree.bodyAsText()

            // 检查是否有错误信息
            if (responseText.contains("\"error\"", ignoreCase = true)) {
                try {
                    val errorResponse: ErrorResponse_7ree = ApiClient_7ree.json_7ree.decodeFromString<ErrorResponse_7ree>(responseText)
                    val errorMessage = errorResponse.error?.message ?: "未知错误"
                    return Result.failure(Exception("API 请求失败: $errorMessage"))
                } catch (e: Exception) {
                    return Result.failure(Exception("API 请求失败: ${responseText}"))
                }
            }

            // 尝试解析正常响应
            val parsedResponse: ChatCompletionResponse_7ree = try {
                ApiClient_7ree.json_7ree.decodeFromString<ChatCompletionResponse_7ree>(responseText)
            } catch (e: Exception) {
                return Result.failure(Exception("API 请求失败: 无法解析响应内容 - ${e.message}"))
            }

            val result_7ree = parsedResponse.choices?.firstOrNull()?.message?.content
            if (result_7ree != null) {
                Result.success("API连接成功")
            } else {
                Result.failure(Exception("API 请求失败: No content in response."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
