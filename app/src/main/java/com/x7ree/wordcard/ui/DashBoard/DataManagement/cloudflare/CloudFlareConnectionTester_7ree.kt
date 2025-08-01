package com.x7ree.wordcard.ui.DashBoard.DataManagement.cloudflare

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * CloudFlare D1连接测试器
 * 负责测试CloudFlare D1数据库连接
 */
class CloudFlareConnectionTester_7ree {
    
    /**
     * 测试CloudFlare D1连接
     */
    suspend fun testConnection(
        accountId: String,
        databaseId: String,
        apiToken: String,
        onProgress: (String) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            onProgress("正在验证API Token...")
            
            // 创建API客户端
            val apiClient = CloudFlareApiClient_7ree(accountId, databaseId, apiToken)
            
            onProgress("正在连接CloudFlare D1数据库...")
            
            // 测试基本连接
            val connectionResult = apiClient.testConnection()
            if (connectionResult.isFailure) {
                return@withContext Result.failure(
                    connectionResult.exceptionOrNull() ?: Exception("连接测试失败")
                )
            }
            
            onProgress("正在验证数据库权限...")
            
            // 测试基本查询权限
            val testQueryResult = apiClient.executeQuery("SELECT 1 as test")
            if (testQueryResult.isFailure) {
                return@withContext Result.failure(
                    Exception("数据库权限验证失败: ${testQueryResult.exceptionOrNull()?.message}")
                )
            }
            
            onProgress("连接测试完成")
            
            Result.success("连接成功！CloudFlare D1数据库配置正确，具有完整的读写权限")
            
        } catch (e: Exception) {
            Result.failure(Exception("连接测试异常: ${e.message}"))
        }
    }
    
    /**
     * 快速连接测试（仅验证基本连接）
     */
    suspend fun quickTest(
        accountId: String,
        databaseId: String,
        apiToken: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiClient = CloudFlareApiClient_7ree(accountId, databaseId, apiToken)
            val result = apiClient.testConnection()
            
            if (result.isSuccess) {
                Result.success("快速连接测试成功")
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("快速连接测试失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}