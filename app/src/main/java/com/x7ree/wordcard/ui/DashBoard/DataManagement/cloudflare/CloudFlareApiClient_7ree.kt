package com.x7ree.wordcard.ui.DashBoard.DataManagement.cloudflare

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

/**
 * CloudFlare D1 API客户端
 * 负责与CloudFlare D1数据库的通信
 */
class CloudFlareApiClient_7ree(
    private val accountId: String,
    private val databaseId: String,
    private val apiToken: String
) {
    private val baseUrl = "https://api.cloudflare.com/client/v4"
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * 测试CloudFlare D1连接
     */
    suspend fun testConnection(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/accounts/$accountId/d1/database/$databaseId")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer $apiToken")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            
            val responseCode = connection.responseCode
            val responseBody = if (responseCode == 200) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
            }
            
            if (responseCode == 200) {
                val response = json.decodeFromString<CloudFlareResponse<DatabaseInfo>>(responseBody)
                if (response.success) {
                    Result.success("连接成功！数据库: ${response.result?.name ?: "Unknown"}")
                } else {
                    Result.failure(Exception("API返回错误: ${response.errors?.firstOrNull()?.message ?: "Unknown error"}"))
                }
            } else {
                Result.failure(Exception("HTTP $responseCode: $responseBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 执行SQL查询
     */
    suspend fun executeQuery(sql: String): Result<QueryResult> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/accounts/$accountId/d1/database/$databaseId/query")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "POST"
            connection.setRequestProperty("Authorization", "Bearer $apiToken")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 30000
            connection.readTimeout = 30000
            
            val requestBody = json.encodeToString(QueryRequest.serializer(), QueryRequest(sql))
            connection.outputStream.use { it.write(requestBody.toByteArray()) }
            
            val responseCode = connection.responseCode
            val responseBody = if (responseCode == 200) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
            }
            
            if (responseCode == 200) {
                val response = json.decodeFromString<CloudFlareResponse<List<QueryResult>>>(responseBody)
                if (response.success && response.result?.isNotEmpty() == true) {
                    Result.success(response.result.first())
                } else {
                    Result.failure(Exception("查询失败: ${response.errors?.firstOrNull()?.message ?: "No results"}"))
                }
            } else {
                Result.failure(Exception("HTTP $responseCode: $responseBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 批量执行SQL语句
     */
    suspend fun executeBatch(sqlStatements: List<String>): Result<List<QueryResult>> = withContext(Dispatchers.IO) {
        try {
            val results = mutableListOf<QueryResult>()
            
            for (sql in sqlStatements) {
                val result = executeQuery(sql)
                if (result.isSuccess) {
                    results.add(result.getOrThrow())
                } else {
                    return@withContext Result.failure(result.exceptionOrNull() ?: Exception("Batch execution failed"))
                }
            }
            
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// 数据类定义
@Serializable
data class CloudFlareResponse<T>(
    val success: Boolean,
    val result: T? = null,
    val errors: List<CloudFlareError>? = null,
    val messages: List<String>? = null
)

@Serializable
data class CloudFlareError(
    val code: Int,
    val message: String
)

@Serializable
data class DatabaseInfo(
    val uuid: String,
    val name: String,
    val version: String? = null
)

@Serializable
data class QueryRequest(
    val sql: String
)

@Serializable
data class QueryResult(
    val success: Boolean,
    val meta: QueryMeta? = null,
    val results: List<Map<String, kotlinx.serialization.json.JsonElement>>? = null
)

@Serializable
data class QueryMeta(
    val changed_db: Boolean? = null,
    val changes: Int? = null,
    val duration: Double? = null,
    val last_row_id: Int? = null,
    val rows_read: Int? = null,
    val rows_written: Int? = null,
    val size_after: Int? = null
)