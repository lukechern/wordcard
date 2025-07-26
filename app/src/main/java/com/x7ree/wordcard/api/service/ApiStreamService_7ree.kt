package com.x7ree.wordcard.api.service

import com.x7ree.wordcard.api.client.ApiClient_7ree
import com.x7ree.wordcard.api.config.ApiConfigManager_7ree
import com.x7ree.wordcard.api.models.*
import com.x7ree.wordcard.config.ApiConfig_7ree
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

/**
 * API流式查询服务类
 */
class ApiStreamService_7ree(
    private val apiConfigManager_7ree: ApiConfigManager_7ree,
    private val apiConfig_7ree: ApiConfig_7ree,
    private val apiQueryService_7ree: ApiQueryService_7ree
) {
    
    /**
     * 流式查询单词
     */
    suspend fun queryWordStream_7ree(word: String): Flow<String> = flow {
        try {
            val prompt_7ree = """${apiConfigManager_7ree.getQueryPrompt_7ree()}

请严格按照以下模板格式输出：

${apiConfigManager_7ree.getOutputTemplate_7ree()}

待解释的英文单词：$word"""
            val activeApi = apiConfig_7ree.getActiveTranslationApi()
            val fixedApiUrl = apiConfigManager_7ree.validateAndFixApiUrl_7ree(activeApi.apiUrl)
            // 根据API类型创建不同的请求
            val request_7ree: ChatCompletionRequest_7ree = if (activeApi.apiName.contains("通义千问", ignoreCase = true) || 
                activeApi.apiUrl.contains("modelscope.cn", ignoreCase = true)) {
                // 通义千问API需要特殊参数
                ChatCompletionRequest_7ree(
                    model = activeApi.modelName,
                    messages = listOf(Message_7ree(role = "user", content = prompt_7ree)),
                    stream = true,
                    enable_thinking = false
                )
            } else {
                // 其他API使用默认请求，但需要转换为ChatCompletionRequest_7ree类型以匹配变量类型
                ChatCompletionRequest_7ree(
                    model = activeApi.modelName,
                    messages = listOf(Message_7ree(role = "user", content = prompt_7ree)),
                    stream = true
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

            // 处理流式响应
            val channel_7ree = response_7ree.bodyAsChannel()
            var hasContent_7ree = false
            var shouldContinue = true
            
            while (!channel_7ree.isClosedForRead && shouldContinue) {
                val line_7ree = channel_7ree.readUTF8Line()
                line_7ree?.let {
                    if (it.startsWith("data: ")) {
                        val data_7ree = it.substring(6)
                        if (data_7ree == "[DONE]") {
                            shouldContinue = false
                        } else {
                            try {
                                val streamResponse_7ree = ApiClient_7ree.json_7ree.decodeFromString<ChatCompletionStreamResponse_7ree>(data_7ree)
                                val content_7ree = streamResponse_7ree.choices?.firstOrNull()?.delta?.content
                                if (content_7ree != null) {
                                    emit(content_7ree)
                                    hasContent_7ree = true
                                }
                            } catch (e: Exception) {
                                // 忽略解析错误，继续处理下一行
                            }
                        }
                    }
                }
            }
            
            if (!hasContent_7ree) {
                emit("错误: 流式查询未收到任何内容")
            }
        } catch (e: Exception) {
            emit("错误: ${e.localizedMessage}")
        }
    }
    
    /**
     * 测试流式查询单词
     */
    suspend fun queryWordStreamTest_7ree(word: String): Flow<String> = flow {
        try {
            // 先尝试非流式查询确保API正常工作
            val nonStreamResult_7ree = apiQueryService_7ree.queryWord_7ree(word)
            nonStreamResult_7ree.onSuccess { content_7ree ->
                // 将内容分块模拟流式输出
                val chunks_7ree = content_7ree.chunked(10)
                for (chunk_7ree in chunks_7ree) {
                    emit(chunk_7ree)
                    delay(100) // 模拟网络延迟
                }
            }.onFailure { error_7ree ->
                emit("错误: ${error_7ree.localizedMessage}")
            }
        } catch (e: Exception) {
            emit("错误: ${e.localizedMessage}")
        }
    }
    
    /**
     * 简单流式查询单词
     */
    suspend fun queryWordStreamSimple_7ree(word: String): Flow<String> = flow {
        try {
            // 使用非流式查询获取完整内容
            val result_7ree = apiQueryService_7ree.queryWord_7ree(word)
            result_7ree.onSuccess { content_7ree ->
                // 将内容按字符分块，模拟真正的流式输出
                val chunks_7ree = content_7ree.chunked(5) // 每次发送5个字符
                for (chunk_7ree in chunks_7ree) {
                    emit(chunk_7ree)
                    delay(20) // 20ms延迟，提高响应速度
                }
            }.onFailure { error_7ree ->
                emit("错误: ${error_7ree.localizedMessage}")
            }
        } catch (e: Exception) {
            emit("错误: ${e.localizedMessage}")
        }
    }
}
