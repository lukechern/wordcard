package com.x7ree.wordcard.utils.httpServer

import android.content.Context
import com.x7ree.wordcard.data.DataExportImportManager_7ree
import com.x7ree.wordcard.utils.NetworkUtils_7ree
import kotlinx.coroutines.*
import java.io.*
import java.net.ServerSocket
import java.net.Socket

/**
 * HTTP服务器核心类
 */
class HttpServer_7ree(
    private val context: Context,
    private val dataExportImportManager: DataExportImportManager_7ree
) {
    private var serverSocket: ServerSocket? = null
    private var serverJob: Job? = null
    private var isRunning = false
    private val port = 8080
    
    private val requestHandler = HttpRequestHandler_7ree(context, dataExportImportManager)
    
    /**
     * 启动HTTP服务器
     */
    fun startServer(): Boolean {
        if (isRunning) return true
        
        // 如果之前有残留的服务器实例，先清理
        stopServer()
        
        return try {
            // 尝试创建服务器套接字
            serverSocket = ServerSocket(port)
            isRunning = true
            
            serverJob = CoroutineScope(Dispatchers.IO).launch {
                while (isRunning && !Thread.currentThread().isInterrupted) {
                    try {
                        val clientSocket = serverSocket?.accept()
                        clientSocket?.let { socket ->
                            launch { handleClient(socket) }
                        }
                    } catch (e: Exception) {
                        if (isRunning) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            // 启动失败时清理状态
            isRunning = false
            serverSocket = null
            serverJob = null
            false
        }
    }
    
    /**
     * 停止HTTP服务器
     */
    fun stopServer() {
        isRunning = false
        
        // 取消协程任务
        serverJob?.cancel()
        serverJob = null
        
        // 关闭服务器套接字
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            serverSocket = null
        }
        
        // 等待一小段时间确保端口释放
        Thread.sleep(100)
    }
    
    /**
     * 获取服务器URL
     */
    fun getServerUrl(): String? {
        val ip = NetworkUtils_7ree.getBestLocalIpAddress(context)
        return if (ip != null && isRunning) {
            "http://$ip:$port"
        } else {
            null
        }
    }
    
    /**
     * 检查服务器是否运行中
     */
    fun isServerRunning(): Boolean = isRunning
    
    /**
     * 处理客户端请求
     */
    private suspend fun handleClient(socket: Socket) {
        try {
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            val output = PrintWriter(socket.getOutputStream(), true)
            
            val requestLine = input.readLine()
            if (requestLine != null) {
                val parts = requestLine.split(" ")
                if (parts.size >= 2) {
                    val method = parts[0]
                    val path = parts[1]
                    
                    // 读取请求头
                    val headers = mutableMapOf<String, String>()
                    var line: String?
                    while (input.readLine().also { line = it } != null && line!!.isNotEmpty()) {
                        val headerParts = line!!.split(": ", limit = 2)
                        if (headerParts.size == 2) {
                            headers[headerParts[0].lowercase()] = headerParts[1]
                        }
                    }
                    
                    requestHandler.handleRequest(method, path, input, output, headers)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            socket.close()
        }
    }
}