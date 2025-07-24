package com.x7ree.wordcard.ui.DashBoard.DataManagement

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.utils.HttpServerManager_7ree

// 全局服务器实例，确保整个应用只有一个
private var globalHttpServerManager: HttpServerManager_7ree? = null

/**
 * 数据管理状态管理类
 */
@Composable
fun rememberDataManagementState_7ree(
    context: Context,
    wordQueryViewModel_7ree: WordQueryViewModel_7ree
): DataManagementState_7ree {
    // SharedPreferences for persistent state
    val sharedPrefs = remember { 
        context.getSharedPreferences("wordcard_server_prefs", Context.MODE_PRIVATE) 
    }
    
    // HTTP服务器相关状态
    var isServerEnabled by remember { mutableStateOf(false) }
    var serverUrl by remember { mutableStateOf<String?>(null) }
    var httpServerManager by remember { mutableStateOf<HttpServerManager_7ree?>(null) }
    
    // 手机操作开关状态
    var isPhoneOperationEnabled by remember { 
        mutableStateOf(sharedPrefs.getBoolean("phone_operation_enabled", true)) // 默认开启
    }
    
    // 初始化HTTP服务器管理器（使用全局实例）
    LaunchedEffect(Unit) {
        // Log.d("DataManagement", "=== 进入页面，开始初始化 ===")
        
        // 使用全局实例，确保整个应用只有一个服务器
        if (globalHttpServerManager == null) {
            val dataExportImportManager = wordQueryViewModel_7ree.getDataExportImportManager()
            globalHttpServerManager = HttpServerManager_7ree(context, dataExportImportManager)
            // Log.d("DataManagement", "全局 HttpServerManager 创建完成")
        } else {
            // Log.d("DataManagement", "全局 HttpServerManager 已存在，复用实例")
        }
        
        httpServerManager = globalHttpServerManager
        
        // 从SharedPreferences读取保存的状态
        val savedState = sharedPrefs.getBoolean("server_enabled", false)
        // Log.d("DataManagement", "从SharedPreferences读取的状态: $savedState")
        
        // 检查服务器实际运行状态
        val actualServerRunning = httpServerManager?.isServerRunning() ?: false
        // Log.d("DataManagement", "服务器实际运行状态: $actualServerRunning")
        
        // 同步UI状态与实际服务器状态
        if (savedState) {
            if (actualServerRunning) {
                // 状态一致：保存状态=开启，服务器正在运行
                // Log.d("DataManagement", "状态一致：保存状态=开启，服务器正在运行")
                isServerEnabled = true
                serverUrl = httpServerManager?.getServerUrl()
                // Log.d("DataManagement", "同步后的URL: $serverUrl")
            } else {
                // 状态不一致：保存状态=开启，但服务器未运行，尝试启动
                // Log.d("DataManagement", "状态不一致：保存状态=开启，服务器未运行，尝试启动")
                httpServerManager?.let { manager ->
                    val startResult = manager.startServer()
                    // Log.d("DataManagement", "启动服务器结果: $startResult")
                    if (startResult) {
                        isServerEnabled = true
                        serverUrl = manager.getServerUrl()
                        // Log.d("DataManagement", "服务器启动成功，URL: $serverUrl")
                    } else {
                        // 启动失败，更新保存的状态
                        // Log.d("DataManagement", "服务器启动失败，重置保存状态")
                        isServerEnabled = false
                        serverUrl = null
                        sharedPrefs.edit().putBoolean("server_enabled", false).apply()
                    }
                }
            }
        } else {
            // 保存状态=关闭
            if (actualServerRunning) {
                // 状态不一致：保存状态=关闭，但服务器正在运行，停止服务器
                // Log.d("DataManagement", "状态不一致：保存状态=关闭，服务器正在运行，停止服务器")
                httpServerManager?.stopServer()
            } else {
                // Log.d("DataManagement", "状态一致：保存状态=关闭，服务器未运行")
            }
            isServerEnabled = false
            serverUrl = null
        }
        
        // Log.d("DataManagement", "初始化完成 - isServerEnabled: $isServerEnabled, serverUrl: $serverUrl")
    }
    
    // 处理服务器开关状态变化
    LaunchedEffect(isServerEnabled, httpServerManager) {
        // Log.d("DataManagement", "=== LaunchedEffect 触发 ===")
        // Log.d("DataManagement", "当前 isServerEnabled: $isServerEnabled")
        // Log.d("DataManagement", "httpServerManager 是否为空: ${httpServerManager == null}")
        
        httpServerManager?.let { manager ->
            val currentlyRunning = manager.isServerRunning()
            // Log.d("DataManagement", "服务器当前运行状态: $currentlyRunning")
            
            if (isServerEnabled) {
                // Log.d("DataManagement", "开关状态=开启")
                if (!currentlyRunning) {
                    // Log.d("DataManagement", "服务器未运行，尝试启动")
                    val startResult = manager.startServer()
                    // Log.d("DataManagement", "启动结果: $startResult")
                    if (startResult) {
                        serverUrl = manager.getServerUrl()
                        // Log.d("DataManagement", "启动成功，URL: $serverUrl")
                    } else {
                        // Log.d("DataManagement", "启动失败，重置状态")
                        isServerEnabled = false
                        serverUrl = null
                        sharedPrefs.edit().putBoolean("server_enabled", false).apply()
                    }
                } else {
                    // Log.d("DataManagement", "服务器已运行，获取URL")
                    serverUrl = manager.getServerUrl()
                    // Log.d("DataManagement", "获取到的URL: $serverUrl")
                }
            } else {
                // Log.d("DataManagement", "开关状态=关闭")
                if (currentlyRunning) {
                    // Log.d("DataManagement", "服务器正在运行，停止服务器")
                    manager.stopServer()
                }
                serverUrl = null
                // Log.d("DataManagement", "服务器已停止，URL清空")
            }
        } ?: run {
            // Log.d("DataManagement", "httpServerManager 为空，无法处理状态变化")
        }
    }
    
    return DataManagementState_7ree(
        isServerEnabled = isServerEnabled,
        serverUrl = serverUrl,
        httpServerManager = httpServerManager,
        onServerToggle = { newState ->
            // Log.d("DataManagement", "=== 用户点击电脑操作开关 ===")
            // Log.d("DataManagement", "开关从 $isServerEnabled 切换到 $newState")
            
            // 如果开启电脑操作，先关闭手机操作
            if (newState && isPhoneOperationEnabled) {
                isPhoneOperationEnabled = false
                sharedPrefs.edit().putBoolean("phone_operation_enabled", false).apply()
                // Log.d("DataManagement", "电脑操作开启，自动关闭手机操作")
            }
            
            // 然后设置电脑操作状态
            isServerEnabled = newState
            
            // 保存状态到SharedPreferences
            val saveResult = sharedPrefs.edit().putBoolean("server_enabled", newState).commit()
            // Log.d("DataManagement", "保存到SharedPreferences结果: $saveResult")
            // Log.d("DataManagement", "验证保存结果: ${sharedPrefs.getBoolean("server_enabled", false)}")
        },
        isPhoneOperationEnabled = isPhoneOperationEnabled,
        onPhoneOperationToggle = { newState ->
            // Log.d("DataManagement", "=== 用户点击手机操作开关 ===")
            // Log.d("DataManagement", "手机操作开关从 $isPhoneOperationEnabled 切换到 $newState")
            
            // 如果开启手机操作，先关闭电脑操作
            if (newState && isServerEnabled) {
                isServerEnabled = false
                sharedPrefs.edit().putBoolean("server_enabled", false).apply()
                // Log.d("DataManagement", "手机操作开启，自动关闭电脑操作")
            }
            
            // 然后设置手机操作状态
            isPhoneOperationEnabled = newState
            
            // 保存手机操作状态到SharedPreferences
            val saveResult = sharedPrefs.edit().putBoolean("phone_operation_enabled", newState).commit()
            // Log.d("DataManagement", "保存手机操作状态结果: $saveResult")
        }
    )
}

/**
 * 数据管理状态数据类
 */
data class DataManagementState_7ree(
    val isServerEnabled: Boolean,
    val serverUrl: String?,
    val httpServerManager: HttpServerManager_7ree?,
    val onServerToggle: (Boolean) -> Unit,
    val isPhoneOperationEnabled: Boolean,
    val onPhoneOperationToggle: (Boolean) -> Unit
)