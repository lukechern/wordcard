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


@Serializable
data class Message_7ree(
    val role: String,
    val content: String
)

@Serializable
data class ChatCompletionRequest_7ree(
    val model: String,
    val messages: List<Message_7ree>,
    val temperature: Double = 0.7
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
    val id: String,
    val `object`: String? = null, // Make object field nullable
    val created: Long,
    val model: String,
    val choices: List<Choice_7ree>,
    val usage: Usage_7ree
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
    val id: String,
    val `object`: String? = null,
    val created: Long,
    val model: String,
    val choices: List<ChatCompletionStreamChoice_7ree>
)

class OpenAiApiService_7ree(
    private var apiConfig_7ree: ApiConfig_7ree = ApiConfig_7ree(),
    private var promptConfig_7ree: PromptConfig_7ree = PromptConfig_7ree()
) {
    private val client_7ree = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
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
    
    // 获取查询提示词
    private fun getQueryPrompt_7ree(): String {
        return promptConfig_7ree.queryPrompt_7ree
    }
    
    // 获取输出模板
    private fun getOutputTemplate_7ree(): String {
        return promptConfig_7ree.outputTemplate_7ree
    }

    suspend fun queryWord_7ree(word: String): Result<String> {
        return try {
            val prompt_7ree = """${getQueryPrompt_7ree()}

请严格按照以下模板格式输出：

${getOutputTemplate_7ree()}

待解释的英文单词：$word"""
            val request_7ree = ChatCompletionRequest_7ree(
                model = apiConfig_7ree.modelName,
                messages = listOf(Message_7ree(role = "user", content = prompt_7ree))
            )

            // println("DEBUG: 发送请求到: ${apiConfig_7ree.apiUrl}")
            // println("DEBUG: 请求内容: $request_7ree")

            val response_7ree: ChatCompletionResponse_7ree = client_7ree.post(apiConfig_7ree.apiUrl) {
                header(HttpHeaders.Authorization, "Bearer ${apiConfig_7ree.apiKey}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(request_7ree)
            }.body()

            // println("DEBUG: 收到响应: $response_7ree")

            val result_7ree = response_7ree.choices.firstOrNull()?.message?.content
            if (result_7ree != null) {
                // println("DEBUG: 解析成功，内容长度: ${result_7ree.length}")
                Result.success(result_7ree)
            } else {
                // println("DEBUG: 响应中没有内容")
                Result.failure(Exception("API 请求失败: No content in response."))
            }
        } catch (e: Exception) {
            // println("DEBUG: API调用异常: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun queryWordStream_7ree(word: String): Flow<String> = flow {
        try {
            val prompt_7ree = """${getQueryPrompt_7ree()}

请严格按照以下模板格式输出：

${getOutputTemplate_7ree()}

待解释的英文单词：$word"""
            val request_7ree = ChatCompletionStreamRequest_7ree(
                model = apiConfig_7ree.modelName,
                messages = listOf(Message_7ree(role = "user", content = prompt_7ree)),
                stream = true
            )

            val response_7ree: HttpResponse = client_7ree.post(apiConfig_7ree.apiUrl) {
                header(HttpHeaders.Authorization, "Bearer ${apiConfig_7ree.apiKey}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(request_7ree)
            }

            // 先尝试获取完整的响应文本进行调试
            val responseText_7ree = response_7ree.bodyAsText()
            // println("DEBUG: 完整响应: $responseText_7ree")
            
            val lines_7ree = responseText_7ree.split("\n")
            var hasContent_7ree = false
            
            for (line_7ree in lines_7ree) {
                // println("DEBUG: 处理行: $line_7ree")
                if (line_7ree.startsWith("data: ")) {
                    val data_7ree = line_7ree.substring(6)
                    if (data_7ree == "[DONE]") {
                        // println("DEBUG: 收到结束标记")
                        break
                    }
                    
                    try {
                        val streamResponse_7ree = Json.decodeFromString<ChatCompletionStreamResponse_7ree>(data_7ree)
                        val content_7ree = streamResponse_7ree.choices.firstOrNull()?.delta?.content
                        if (content_7ree != null) {
                            // println("DEBUG: 发送内容: $content_7ree")
                            emit(content_7ree)
                            hasContent_7ree = true
                        }
                    } catch (e: Exception) {
                        // println("DEBUG: 解析错误: ${e.message}")
                        // 忽略解析错误，继续处理下一行
                        continue
                    }
                }
            }
            
            if (!hasContent_7ree) {
                // println("DEBUG: 没有收到任何内容，尝试非流式解析")
                // 如果没有收到流式内容，尝试解析完整响应
                try {
                    val fullResponse_7ree = Json.decodeFromString<ChatCompletionResponse_7ree>(responseText_7ree)
                    val content_7ree = fullResponse_7ree.choices.firstOrNull()?.message?.content
                    if (content_7ree != null) {
                        emit(content_7ree)
                    } else {
                        emit("错误: 响应中没有内容")
                    }
                } catch (e: Exception) {
                    emit("错误: 无法解析响应 - ${e.localizedMessage}")
                }
            }
        } catch (e: Exception) {
            // println("DEBUG: 流式查询异常: ${e.message}")
            emit("错误: ${e.localizedMessage}")
        }
    }

    suspend fun queryWordStreamTest_7ree(word: String): Flow<String> = flow {
        try {
            // println("DEBUG: 开始测试流式查询")
            
            // 先尝试非流式查询确保API正常工作
            val nonStreamResult_7ree = queryWord_7ree(word)
            nonStreamResult_7ree.onSuccess { content_7ree ->
                // println("DEBUG: 非流式查询成功，内容长度: ${content_7ree.length}")
                // 将内容分块模拟流式输出
                val chunks_7ree = content_7ree.chunked(10)
                for (chunk_7ree in chunks_7ree) {
                    emit(chunk_7ree)
                    kotlinx.coroutines.delay(100) // 模拟网络延迟
                }
            }.onFailure { error_7ree ->
                // println("DEBUG: 非流式查询失败: ${error_7ree.message}")
                emit("错误: ${error_7ree.localizedMessage}")
            }
        } catch (e: Exception) {
            // println("DEBUG: 测试流式查询异常: ${e.message}")
            emit("错误: ${e.localizedMessage}")
        }
    }

    suspend fun queryWordStreamSimple_7ree(word: String): Flow<String> = flow {
        try {
            // println("DEBUG: 开始简单流式查询")
            
            // 使用非流式查询获取完整内容
            val result_7ree = queryWord_7ree(word)
            result_7ree.onSuccess { content_7ree ->
                // println("DEBUG: 获取到完整内容，长度: ${content_7ree.length}")
                
                // 将内容按字符分块，模拟真正的流式输出
                val chunks_7ree = content_7ree.chunked(5) // 每次发送5个字符
                for (chunk_7ree in chunks_7ree) {
                    emit(chunk_7ree)
                    kotlinx.coroutines.delay(50) // 50ms延迟，模拟网络传输
                }
            }.onFailure { error_7ree ->
                // println("DEBUG: 查询失败: ${error_7ree.message}")
                emit("错误: ${error_7ree.localizedMessage}")
            }
        } catch (e: Exception) {
            // println("DEBUG: 简单流式查询异常: ${e.message}")
            emit("错误: ${e.localizedMessage}")
        }
    }

    suspend fun testApiConnection_7ree(): Result<String> {
        return try {
            // println("DEBUG: 测试API连接")
            val testRequest_7ree = ChatCompletionRequest_7ree(
                model = apiConfig_7ree.modelName,
                messages = listOf(Message_7ree(role = "user", content = "Hello"))
            )

            client_7ree.post(apiConfig_7ree.apiUrl) {
                header(HttpHeaders.Authorization, "Bearer ${apiConfig_7ree.apiKey}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(testRequest_7ree)
            }.body<ChatCompletionResponse_7ree>()

            // println("DEBUG: 测试响应成功")
            Result.success("API连接成功")
        } catch (e: Exception) {
            // println("DEBUG: API连接测试失败: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
