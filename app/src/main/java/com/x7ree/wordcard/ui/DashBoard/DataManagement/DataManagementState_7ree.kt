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
    var httpServerUrl by remember { mutableStateOf<String?>(null) }
    var httpServerManager by remember { mutableStateOf<HttpServerManager_7ree?>(null) }
    
    // 手机操作开关状态
    var isPhoneOperationEnabled by remember { 
        mutableStateOf(sharedPrefs.getBoolean("phone_operation_enabled", true)) // 默认开启
    }
    
    // CloudFlare操作开关状态
    var isCloudFlareEnabled by remember { 
        mutableStateOf(sharedPrefs.getBoolean("cloudflare_enabled", false)) // 默认关闭
    }
    
    // CloudFlare配置参数
    var databaseId by remember { 
        mutableStateOf(sharedPrefs.getString("cloudflare_database_id", "") ?: "") 
    }
    var apiToken by remember { 
        mutableStateOf(sharedPrefs.getString("cloudflare_api_token", "") ?: "") 
    }
    var accountId by remember { 
        mutableStateOf(sharedPrefs.getString("cloudflare_account_id", "") ?: "") 
    }
    
    // 局域网MySQL操作开关状态
    var isLanMysqlEnabled by remember { 
        mutableStateOf(sharedPrefs.getBoolean("lan_mysql_enabled", false)) // 默认关闭
    }
    
    // 局域网PHP API配置参数
    var serverUrl by remember { 
        mutableStateOf(sharedPrefs.getString("lan_php_server_url", "") ?: "") 
    }
    var apiKey by remember { 
        mutableStateOf(sharedPrefs.getString("lan_php_api_key", "") ?: "") 
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
                httpServerUrl = httpServerManager?.getServerUrl()
                // Log.d("DataManagement", "同步后的URL: $httpServerUrl")
            } else {
                // 状态不一致：保存状态=开启，但服务器未运行，尝试启动
                // Log.d("DataManagement", "状态不一致：保存状态=开启，服务器未运行，尝试启动")
                httpServerManager?.let { manager ->
                    val startResult = manager.startServer()
                    // Log.d("DataManagement", "启动服务器结果: $startResult")
                    if (startResult) {
                        isServerEnabled = true
                        httpServerUrl = manager.getServerUrl()
                        // Log.d("DataManagement", "服务器启动成功，URL: $httpServerUrl")
                    } else {
                        // 启动失败，更新保存的状态
                        // Log.d("DataManagement", "服务器启动失败，重置保存状态")
                        isServerEnabled = false
                        httpServerUrl = null
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
            httpServerUrl = null
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
                        httpServerUrl = manager.getServerUrl()
                        // Log.d("DataManagement", "启动成功，URL: $httpServerUrl")
                    } else {
                        // Log.d("DataManagement", "启动失败，重置状态")
                        isServerEnabled = false
                        httpServerUrl = null
                        sharedPrefs.edit().putBoolean("server_enabled", false).apply()
                    }
                } else {
                    // Log.d("DataManagement", "服务器已运行，获取URL")
                    httpServerUrl = manager.getServerUrl()
                    // Log.d("DataManagement", "获取到的URL: $httpServerUrl")
                }
            } else {
                // Log.d("DataManagement", "开关状态=关闭")
                if (currentlyRunning) {
                    // Log.d("DataManagement", "服务器正在运行，停止服务器")
                    manager.stopServer()
                }
                httpServerUrl = null
                // Log.d("DataManagement", "服务器已停止，URL清空")
            }
        } ?: run {
            // Log.d("DataManagement", "httpServerManager 为空，无法处理状态变化")
        }
    }
    
    return DataManagementState_7ree(
        isServerEnabled = isServerEnabled,
        serverUrl = httpServerUrl,
        httpServerManager = httpServerManager,
        onServerToggle = { newState ->
            // Log.d("DataManagement", "=== 用户点击电脑操作开关 ===")
            // Log.d("DataManagement", "开关从 $isServerEnabled 切换到 $newState")
            
            // 如果开启电脑操作，先关闭手机操作、CloudFlare操作和局域网MySQL操作
            if (newState) {
                if (isPhoneOperationEnabled) {
                    isPhoneOperationEnabled = false
                    sharedPrefs.edit().putBoolean("phone_operation_enabled", false).apply()
                    // Log.d("DataManagement", "电脑操作开启，自动关闭手机操作")
                }
                if (isCloudFlareEnabled) {
                    isCloudFlareEnabled = false
                    sharedPrefs.edit().putBoolean("cloudflare_enabled", false).apply()
                    // Log.d("DataManagement", "电脑操作开启，自动关闭CloudFlare操作")
                }
                if (isLanMysqlEnabled) {
                    isLanMysqlEnabled = false
                    sharedPrefs.edit().putBoolean("lan_mysql_enabled", false).apply()
                    // Log.d("DataManagement", "电脑操作开启，自动关闭局域网MySQL操作")
                }
            }
            
            // 然后设置电脑操作状态
            isServerEnabled = newState
            
            // 保存状态到SharedPreferences
            sharedPrefs.edit().putBoolean("server_enabled", newState).commit()
            // Log.d("DataManagement", "保存到SharedPreferences结果: $saveResult")
            // Log.d("DataManagement", "验证保存结果: ${sharedPrefs.getBoolean("server_enabled", false)}")
        },
        isPhoneOperationEnabled = isPhoneOperationEnabled,
        onPhoneOperationToggle = { newState ->
            // Log.d("DataManagement", "=== 用户点击手机操作开关 ===")
            // Log.d("DataManagement", "手机操作开关从 $isPhoneOperationEnabled 切换到 $newState")
            
            // 如果开启手机操作，先关闭电脑操作、CloudFlare操作和局域网MySQL操作
            if (newState) {
                if (isServerEnabled) {
                    isServerEnabled = false
                    sharedPrefs.edit().putBoolean("server_enabled", false).apply()
                    // Log.d("DataManagement", "手机操作开启，自动关闭电脑操作")
                }
                if (isCloudFlareEnabled) {
                    isCloudFlareEnabled = false
                    sharedPrefs.edit().putBoolean("cloudflare_enabled", false).apply()
                    // Log.d("DataManagement", "手机操作开启，自动关闭CloudFlare操作")
                }
                if (isLanMysqlEnabled) {
                    isLanMysqlEnabled = false
                    sharedPrefs.edit().putBoolean("lan_mysql_enabled", false).apply()
                    // Log.d("DataManagement", "手机操作开启，自动关闭局域网MySQL操作")
                }
            }
            
            // 然后设置手机操作状态
            isPhoneOperationEnabled = newState
            
            // 保存手机操作状态到SharedPreferences
            sharedPrefs.edit().putBoolean("phone_operation_enabled", newState).commit()
            // Log.d("DataManagement", "保存手机操作状态结果: $saveResult")
        },
        isCloudFlareEnabled = isCloudFlareEnabled,
        onCloudFlareToggle = { newState ->
            // 如果开启CloudFlare操作，先关闭电脑操作、手机操作和局域网MySQL操作
            if (newState) {
                if (isServerEnabled) {
                    isServerEnabled = false
                    sharedPrefs.edit().putBoolean("server_enabled", false).apply()
                }
                if (isPhoneOperationEnabled) {
                    isPhoneOperationEnabled = false
                    sharedPrefs.edit().putBoolean("phone_operation_enabled", false).apply()
                }
                if (isLanMysqlEnabled) {
                    isLanMysqlEnabled = false
                    sharedPrefs.edit().putBoolean("lan_mysql_enabled", false).apply()
                }
            }
            
            // 设置CloudFlare操作状态
            isCloudFlareEnabled = newState
            
            // 保存CloudFlare操作状态到SharedPreferences
            sharedPrefs.edit().putBoolean("cloudflare_enabled", newState).commit()
        },
        databaseId = databaseId,
        onDatabaseIdChange = { newValue ->
            databaseId = newValue
            sharedPrefs.edit().putString("cloudflare_database_id", newValue).apply()
        },
        apiToken = apiToken,
        onApiTokenChange = { newValue ->
            apiToken = newValue
            sharedPrefs.edit().putString("cloudflare_api_token", newValue).apply()
        },
        accountId = accountId,
        onAccountIdChange = { newValue ->
            accountId = newValue
            sharedPrefs.edit().putString("cloudflare_account_id", newValue).apply()
        },
        isLanMysqlEnabled = isLanMysqlEnabled,
        onLanMysqlToggle = { newState ->
            // 如果开启局域网MySQL操作，先关闭电脑操作、手机操作和CloudFlare操作
            if (newState) {
                if (isServerEnabled) {
                    isServerEnabled = false
                    sharedPrefs.edit().putBoolean("server_enabled", false).apply()
                }
                if (isPhoneOperationEnabled) {
                    isPhoneOperationEnabled = false
                    sharedPrefs.edit().putBoolean("phone_operation_enabled", false).apply()
                }
                if (isCloudFlareEnabled) {
                    isCloudFlareEnabled = false
                    sharedPrefs.edit().putBoolean("cloudflare_enabled", false).apply()
                }
            }
            
            // 设置局域网MySQL操作状态
            isLanMysqlEnabled = newState
            
            // 保存局域网MySQL操作状态到SharedPreferences
            sharedPrefs.edit().putBoolean("lan_mysql_enabled", newState).commit()
        },
        phpServerUrl = serverUrl,
        onPhpServerUrlChange = { newValue ->
            serverUrl = newValue
            sharedPrefs.edit().putString("lan_php_server_url", newValue).apply()
        },
        phpApiKey = apiKey,
        onPhpApiKeyChange = { newValue ->
            apiKey = newValue
            sharedPrefs.edit().putString("lan_php_api_key", newValue).apply()
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
    val onPhoneOperationToggle: (Boolean) -> Unit,
    val isCloudFlareEnabled: Boolean,
    val onCloudFlareToggle: (Boolean) -> Unit,
    val databaseId: String,
    val onDatabaseIdChange: (String) -> Unit,
    val apiToken: String,
    val onApiTokenChange: (String) -> Unit,
    val accountId: String,
    val onAccountIdChange: (String) -> Unit,
    val isLanMysqlEnabled: Boolean,
    val onLanMysqlToggle: (Boolean) -> Unit,
    val phpServerUrl: String,
    val onPhpServerUrlChange: (String) -> Unit,
    val phpApiKey: String,
    val onPhpApiKeyChange: (String) -> Unit
)