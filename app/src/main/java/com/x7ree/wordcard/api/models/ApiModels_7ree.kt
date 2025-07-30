package com.x7ree.wordcard.api.models

import kotlinx.serialization.Serializable

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
    val top_p: Double? = null,  // 核采样参数，控制随机性
    val frequency_penalty: Double? = null,  // 频率惩罚，减少重复
    val presence_penalty: Double? = null,   // 存在惩罚，鼓励新话题
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
