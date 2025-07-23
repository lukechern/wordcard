package com.x7ree.wordcard.utils

import android.content.Context
import com.x7ree.wordcard.data.DataExportImportManager_7ree
import com.x7ree.wordcard.utils.httpServer.HttpServer_7ree

/**
 * HTTP服务器管理器，用于提供数据导入导出的Web界面
 * 重构后的简化版本，将具体实现委托给HttpServer_7ree
 */
class HttpServerManager_7ree(
    private val context: Context,
    private val dataExportImportManager: DataExportImportManager_7ree
) {
    private val httpServer = HttpServer_7ree(context, dataExportImportManager)
    
    /**
     * 启动HTTP服务器
     */
    fun startServer(): Boolean {
        return httpServer.startServer()
    }
    
    /**
     * 停止HTTP服务器
     */
    fun stopServer() {
        httpServer.stopServer()
    }
    
    /**
     * 获取服务器URL
     */
    fun getServerUrl(): String? {
        return httpServer.getServerUrl()
    }
    
    /**
     * 检查服务器是否运行中
     */
    fun isServerRunning(): Boolean {
        return httpServer.isServerRunning()
    }
}