package com.x7ree.wordcard.api

import com.x7ree.wordcard.config.ApiConfig_7ree
import com.x7ree.wordcard.config.PromptConfig_7ree
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.core.readBytes
import kotlinx.serialization.Contextual


@Serializable
data class Message_7ree(
    val role: String,
    val content: String
)

@Serializable
data class ChatCompletionRequest_7ree(
    val model: String,
    val messages: List<Message_7ree>,
    val temperature: Double = 0.7,
    val stream: Boolean = false,  // 添加stream参数
    val enable_thinking: Boolean = false  // 通义千问API特殊参数
)

@Serializable
data class Choice_7ree(
    val index: Int,
    val message: Message_7ree,
    val finish_reason: String
)

@Serializable
data class Usage_7ree(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

@Serializable
data class ChatCompletionResponse_7ree(
    val id: String? = null,
    val `object`: String? = null, // Make object field nullable
    val created: Long? = null,
    val model: String? = null,
    val system_fingerprint: String? = null, // 通义千问API返回的额外字段
    val choices: List<Choice_7ree>? = null,
    val usage: Usage_7ree? = null
)

@Serializable
data class ErrorDetail_7ree(
    val code: String? = null,
    val message: String? = null,
    val param: String? = null,
    val type: String? = null
)

@Serializable
data class ErrorResponse_7ree(
    val error: ErrorDetail_7ree? = null,
    val request_id: String? = null
)

@Serializable
data class ChatCompletionStreamRequest_7ree(
    val model: String,
    val messages: List<Message_7ree>,
    val temperature: Double = 0.7,
    val stream: Boolean = true
)

@Serializable
data class ChatCompletionStreamChoice_7ree(
    val index: Int,
    val delta: Message_7ree,
    val finish_reason: String? = null
)

@Serializable
data class ChatCompletionStreamResponse_7ree(
    val id: String? = null,
    val `object`: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val system_fingerprint: String? = null, // 通义千问API返回的额外字段
    val choices: List<ChatCompletionStreamChoice_7ree>? = null
)

class OpenAiApiService_7ree(
    private var apiConfig_7ree: ApiConfig_7ree = ApiConfig_7ree(),
    private var promptConfig_7ree: PromptConfig_7ree = PromptConfig_7ree()
) {
    private val json_7ree = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    private val client_7ree = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json_7ree)
        }
    }
    
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
    private fun getQueryPrompt_7ree(): String {
        return promptConfig_7ree.queryPrompt_7ree
    }
    
    // 获取输出模板
    private fun getOutputTemplate_7ree(): String {
        return promptConfig_7ree.outputTemplate_7ree
    }

    // 验证和修正API URL
    private fun validateAndFixApiUrl_7ree(apiUrl: String): String {
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

    suspend fun queryWord_7ree(word: String): Result<String> {
        return try {
            val prompt_7ree = """${getQueryPrompt_7ree()}

请严格按照以下模板格式输出：

${getOutputTemplate_7ree()}

待解释的英文单词：$word"""
            val activeApi = apiConfig_7ree.getActiveTranslationApi()
            val fixedApiUrl = validateAndFixApiUrl_7ree(activeApi.apiUrl)
            // 根据API类型创建不同的请求
            val request_7ree = if (activeApi.apiName.contains("通义千问", ignoreCase = true) || 
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

            val response_7ree: HttpResponse = client_7ree.post(fixedApiUrl) {
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
                    val errorResponse: ErrorResponse_7ree = json_7ree.decodeFromString<ErrorResponse_7ree>(responseText)
                    val errorMessage = errorResponse.error?.message ?: "未知错误"
                    return Result.failure(Exception("API 请求失败: $errorMessage"))
                } catch (e: Exception) {
                    return Result.failure(Exception("API 请求失败: ${responseText}"))
                }
            }

            // 尝试解析正常响应
            val parsedResponse: ChatCompletionResponse_7ree = try {
                json_7ree.decodeFromString<ChatCompletionResponse_7ree>(responseText)
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

    suspend fun queryWordStream_7ree(word: String): Flow<String> = flow {
        try {
            val prompt_7ree = """${getQueryPrompt_7ree()}

请严格按照以下模板格式输出：

${getOutputTemplate_7ree()}

待解释的英文单词：$word"""
            val activeApi = apiConfig_7ree.getActiveTranslationApi()
            val fixedApiUrl = validateAndFixApiUrl_7ree(activeApi.apiUrl)
            // 根据API类型创建不同的请求
            val request_7ree = if (activeApi.apiName.contains("通义千问", ignoreCase = true) || 
                activeApi.apiUrl.contains("modelscope.cn", ignoreCase = true)) {
                // 通义千问API需要特殊参数
                ChatCompletionRequest_7ree(
                    model = activeApi.modelName,
                    messages = listOf(Message_7ree(role = "user", content = prompt_7ree)),
                    stream = true,
                    enable_thinking = false
                )
            } else {
                // 其他API使用默认请求
                ChatCompletionStreamRequest_7ree(
                    model = activeApi.modelName,
                    messages = listOf(Message_7ree(role = "user", content = prompt_7ree)),
                    stream = true
                )
            }

            val response_7ree: HttpResponse = client_7ree.post(fixedApiUrl) {
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
                                val streamResponse_7ree = json_7ree.decodeFromString<ChatCompletionStreamResponse_7ree>(data_7ree)
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

    suspend fun queryWordStreamTest_7ree(word: String): Flow<String> = flow {
        try {
            // 先尝试非流式查询确保API正常工作
            val nonStreamResult_7ree = queryWord_7ree(word)
            nonStreamResult_7ree.onSuccess { content_7ree ->
                // 将内容分块模拟流式输出
                val chunks_7ree = content_7ree.chunked(10)
                for (chunk_7ree in chunks_7ree) {
                    emit(chunk_7ree)
                    kotlinx.coroutines.delay(100) // 模拟网络延迟
                }
            }.onFailure { error_7ree ->
                emit("错误: ${error_7ree.localizedMessage}")
            }
        } catch (e: Exception) {
            emit("错误: ${e.localizedMessage}")
        }
    }

    suspend fun queryWordStreamSimple_7ree(word: String): Flow<String> = flow {
        try {
            // 使用非流式查询获取完整内容
            val result_7ree = queryWord_7ree(word)
            result_7ree.onSuccess { content_7ree ->
                // 将内容按字符分块，模拟真正的流式输出
                val chunks_7ree = content_7ree.chunked(5) // 每次发送5个字符
                for (chunk_7ree in chunks_7ree) {
                    emit(chunk_7ree)
                    kotlinx.coroutines.delay(50) // 50ms延迟，模拟网络传输
                }
            }.onFailure { error_7ree ->
                emit("错误: ${error_7ree.localizedMessage}")
            }
        } catch (e: Exception) {
            emit("错误: ${e.localizedMessage}")
        }
    }

    suspend fun testApiConnection_7ree(): Result<String> {
        return try {
            val prompt_7ree = "Hello"
            val activeApi = apiConfig_7ree.getActiveTranslationApi()
            val fixedApiUrl = validateAndFixApiUrl_7ree(activeApi.apiUrl)
            // 根据API类型创建不同的请求
            val request_7ree = if (activeApi.apiName.contains("通义千问", ignoreCase = true) || 
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

            val response_7ree: HttpResponse = client_7ree.post(fixedApiUrl) {
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
                    val errorResponse: ErrorResponse_7ree = json_7ree.decodeFromString<ErrorResponse_7ree>(responseText)
                    val errorMessage = errorResponse.error?.message ?: "未知错误"
                    return Result.failure(Exception("API 请求失败: $errorMessage"))
                } catch (e: Exception) {
                    return Result.failure(Exception("API 请求失败: ${responseText}"))
                }
            }

            // 尝试解析正常响应
            val parsedResponse: ChatCompletionResponse_7ree = try {
                json_7ree.decodeFromString<ChatCompletionResponse_7ree>(responseText)
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
