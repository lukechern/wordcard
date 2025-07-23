package com.x7ree.wordcard.utils

import android.content.Context
import com.x7ree.wordcard.data.DataExportImportManager_7ree
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * HTTP服务器管理器，用于提供数据导入导出的Web界面
 */
class HttpServerManager_7ree(
    private val context: Context,
    private val dataExportImportManager: DataExportImportManager_7ree
) {
    private var serverSocket: ServerSocket? = null
    private var serverJob: Job? = null
    private var isRunning = false
    private val port = 8080
    
    private val json = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true 
    }
    
    /**
     * 启动HTTP服务器
     */
    fun startServer(): Boolean {
        if (isRunning) return true
        
        return try {
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
            false
        }
    }
    
    /**
     * 停止HTTP服务器
     */
    fun stopServer() {
        isRunning = false
        serverJob?.cancel()
        serverSocket?.close()
        serverSocket = null
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
                    
                    when {
                        method == "GET" && path == "/" -> {
                            sendHtmlResponse(output, getMainPageHtml())
                        }
                        method == "GET" && path == "/export" -> {
                            handleExport(output)
                        }
                        method == "POST" && path == "/import" -> {
                            handleImport(input, output, headers)
                        }
                        else -> {
                            send404Response(output)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            socket.close()
        }
    }
    
    /**
     * 处理数据导出请求
     */
    private suspend fun handleExport(output: PrintWriter) {
        try {
            val result = dataExportImportManager.exportData_7ree()
            if (result.isSuccess) {
                val filePath = result.getOrNull()
                if (filePath != null) {
                    val file = File(filePath)
                    if (file.exists()) {
                        val content = file.readText()
                        sendJsonResponse(output, content)
                        return
                    }
                }
            }
            sendErrorResponse(output, "导出失败")
        } catch (e: Exception) {
            sendErrorResponse(output, "导出异常: ${e.message}")
        }
    }
    
    /**
     * 处理数据导入请求
     */
    private suspend fun handleImport(input: BufferedReader, output: PrintWriter, headers: Map<String, String>) {
        try {
            val contentLength = headers["content-length"]?.toIntOrNull() ?: 0
            if (contentLength > 0) {
                val buffer = CharArray(contentLength)
                input.read(buffer, 0, contentLength)
                val body = String(buffer)
                
                // 直接使用body作为JSON数据，因为前端发送的就是纯JSON
                val jsonData = body.trim()
                
                if (jsonData.isNotEmpty()) {
                    // 验证JSON格式
                    try {
                        json.parseToJsonElement(jsonData)
                    } catch (e: Exception) {
                        sendErrorResponse(output, "无效的JSON格式: ${e.message}")
                        return
                    }
                    
                    // 创建临时文件
                    val tempFile = File.createTempFile("import_", ".json", context.cacheDir)
                    tempFile.writeText(jsonData)
                    
                    val uri = android.net.Uri.fromFile(tempFile)
                    val result = dataExportImportManager.importData_7ree(uri)
                    
                    tempFile.delete()
                    
                    if (result.isSuccess) {
                        val count = result.getOrNull() ?: 0
                        sendJsonResponse(output, """{"success": true, "message": "成功导入 $count 条记录"}""")
                    } else {
                        sendErrorResponse(output, "导入失败: ${result.exceptionOrNull()?.message}")
                    }
                } else {
                    sendErrorResponse(output, "没有数据")
                }
            } else {
                sendErrorResponse(output, "没有数据")
            }
        } catch (e: Exception) {
            sendErrorResponse(output, "导入异常: ${e.message}")
        }
    }
    
    /**
     * 从表单数据中提取JSON
     */
    private fun extractJsonFromFormData(body: String): String {
        return try {
            // 查找JSON数据部分
            val lines = body.split("\n")
            val jsonStart = lines.indexOfFirst { it.trim().startsWith("{") }
            val jsonEnd = lines.indexOfLast { it.trim().endsWith("}") }
            
            if (jsonStart >= 0 && jsonEnd >= jsonStart) {
                lines.subList(jsonStart, jsonEnd + 1).joinToString("\n").trim()
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * 发送HTML响应
     */
    private fun sendHtmlResponse(output: PrintWriter, html: String) {
        output.println("HTTP/1.1 200 OK")
        output.println("Content-Type: text/html; charset=UTF-8")
        output.println("Content-Length: ${html.toByteArray(StandardCharsets.UTF_8).size}")
        output.println("Access-Control-Allow-Origin: *")
        output.println()
        output.print(html)
        output.flush()
    }
    
    /**
     * 发送JSON响应
     */
    private fun sendJsonResponse(output: PrintWriter, jsonContent: String) {
        output.println("HTTP/1.1 200 OK")
        output.println("Content-Type: application/json; charset=UTF-8")
        output.println("Content-Length: ${jsonContent.toByteArray(StandardCharsets.UTF_8).size}")
        output.println("Access-Control-Allow-Origin: *")
        output.println()
        output.print(jsonContent)
        output.flush()
    }
    
    /**
     * 发送错误响应
     */
    private fun sendErrorResponse(output: PrintWriter, message: String) {
        val errorJson = """{"success": false, "message": "$message"}"""
        output.println("HTTP/1.1 500 Internal Server Error")
        output.println("Content-Type: application/json; charset=UTF-8")
        output.println("Content-Length: ${errorJson.toByteArray(StandardCharsets.UTF_8).size}")
        output.println("Access-Control-Allow-Origin: *")
        output.println()
        output.print(errorJson)
        output.flush()
    }
    
    /**
     * 发送404响应
     */
    private fun send404Response(output: PrintWriter) {
        val html = "<html><body><h1>404 Not Found</h1></body></html>"
        output.println("HTTP/1.1 404 Not Found")
        output.println("Content-Type: text/html; charset=UTF-8")
        output.println("Content-Length: ${html.length}")
        output.println()
        output.print(html)
        output.flush()
    }    

    /**
     * 获取主页面HTML
     */
    private fun getMainPageHtml(): String {
        return """
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WordCard 数据管理</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background: white;
            border-radius: 12px;
            padding: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 30px;
        }
        
        /* Tab导航样式 */
        .tab-nav {
            display: flex;
            border-bottom: 2px solid #e0e0e0;
            margin-bottom: 30px;
        }
        .tab-button {
            background: none;
            border: none;
            padding: 12px 24px;
            cursor: pointer;
            font-size: 16px;
            color: #666;
            border-bottom: 3px solid transparent;
            transition: all 0.3s ease;
        }
        .tab-button:hover {
            color: #007AFF;
            background-color: #f8f9fa;
        }
        .tab-button.active {
            color: #007AFF;
            border-bottom-color: #007AFF;
            font-weight: 600;
        }
        
        /* Tab内容样式 */
        .tab-content {
            display: none;
        }
        .tab-content.active {
            display: block;
        }
        
        .section {
            padding: 20px;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            background-color: #fafafa;
        }
        .section h2 {
            color: #555;
            margin-top: 0;
            margin-bottom: 16px;
        }
        button:not(.tab-button) {
            background-color: #007AFF;
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 6px;
            cursor: pointer;
            font-size: 16px;
            margin: 5px;
        }
        button:not(.tab-button):hover {
            background-color: #0056CC;
        }
        button:not(.tab-button):disabled {
            background-color: #ccc;
            cursor: not-allowed;
        }
        .file-input {
            margin: 10px 0;
        }
        .message {
            padding: 10px;
            border-radius: 4px;
            margin: 10px 0;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .loading {
            display: none;
            color: #007AFF;
        }
        textarea {
            width: 100%;
            height: 200px;
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 10px;
            font-family: monospace;
            font-size: 12px;
            box-sizing: border-box;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>WordCard 数据管理</h1>
        
        <!-- Tab导航 -->
        <div class="tab-nav">
            <button class="tab-button active" onclick="switchTab('export')">📤 数据导出</button>
            <button class="tab-button" onclick="switchTab('import')">📥 文件导入</button>
            <button class="tab-button" onclick="switchTab('manual')">📝 手动导入</button>
        </div>
        
        <!-- 数据导出Tab -->
        <div id="export-tab" class="tab-content active">
            <div class="section">
                <h2>数据导出</h2>
                <p>导出您的单词查询历史数据为JSON格式文件</p>
                <button onclick="exportData()">导出数据</button>
                <div class="loading" id="exportLoading">导出中...</div>
                <div id="exportMessage"></div>
            </div>
        </div>
        
        <!-- 文件导入Tab -->
        <div id="import-tab" class="tab-content">
            <div class="section">
                <h2>文件导入</h2>
                <p>从JSON文件导入单词查询历史数据</p>
                <div class="file-input">
                    <input type="file" id="importFile" accept=".json" />
                </div>
                <button onclick="importData()">导入数据</button>
                <div class="loading" id="importLoading">导入中...</div>
                <div id="importMessage"></div>
            </div>
        </div>
        
        <!-- 手动导入Tab -->
        <div id="manual-tab" class="tab-content">
            <div class="section">
                <h2>手动导入</h2>
                <p>直接粘贴JSON数据进行导入</p>
                <textarea id="jsonInput" placeholder="请粘贴JSON数据..."></textarea>
                <br>
                <button onclick="importFromText()">从文本导入</button>
                <div class="loading" id="textImportLoading">导入中...</div>
                <div id="textImportMessage"></div>
            </div>
        </div>
    </div>

    <script>
        // Tab切换功能
        function switchTab(tabName) {
            // 隐藏所有tab内容
            const tabContents = document.querySelectorAll('.tab-content');
            tabContents.forEach(content => content.classList.remove('active'));
            
            // 移除所有tab按钮的active状态
            const tabButtons = document.querySelectorAll('.tab-button');
            tabButtons.forEach(button => button.classList.remove('active'));
            
            // 显示选中的tab内容
            document.getElementById(tabName + '-tab').classList.add('active');
            
            // 激活对应的tab按钮
            event.target.classList.add('active');
        }
        
        function showMessage(elementId, message, isError = false) {
            const element = document.getElementById(elementId);
            element.innerHTML = '<div class="message ' + (isError ? 'error' : 'success') + '">' + message + '</div>';
        }
        
        function showLoading(elementId, show = true) {
            document.getElementById(elementId).style.display = show ? 'block' : 'none';
        }
        
        async function exportData() {
            showLoading('exportLoading', true);
            document.getElementById('exportMessage').innerHTML = '';
            
            try {
                const response = await fetch('/export');
                if (response.ok) {
                    const data = await response.text();
                    
                    // 创建下载链接
                    const blob = new Blob([data], { type: 'application/json' });
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = 'wordcard_export_' + new Date().toISOString().slice(0,19).replace(/:/g, '-') + '.json';
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                    window.URL.revokeObjectURL(url);
                    
                    showMessage('exportMessage', '数据导出成功！文件已开始下载。');
                } else {
                    showMessage('exportMessage', '导出失败：' + response.statusText, true);
                }
            } catch (error) {
                showMessage('exportMessage', '导出失败：' + error.message, true);
            } finally {
                showLoading('exportLoading', false);
            }
        }
        
        async function importData() {
            const fileInput = document.getElementById('importFile');
            const file = fileInput.files[0];
            
            if (!file) {
                showMessage('importMessage', '请选择要导入的文件', true);
                return;
            }
            
            showLoading('importLoading', true);
            document.getElementById('importMessage').innerHTML = '';
            
            try {
                const text = await file.text();
                await performImport(text, 'importMessage', 'importLoading');
            } catch (error) {
                showMessage('importMessage', '读取文件失败：' + error.message, true);
                showLoading('importLoading', false);
            }
        }
        
        async function importFromText() {
            const jsonInput = document.getElementById('jsonInput');
            const jsonText = jsonInput.value.trim();
            
            if (!jsonText) {
                showMessage('textImportMessage', '请输入JSON数据', true);
                return;
            }
            
            showLoading('textImportLoading', true);
            document.getElementById('textImportMessage').innerHTML = '';
            
            await performImport(jsonText, 'textImportMessage', 'textImportLoading');
        }
        
        async function performImport(jsonText, messageElementId, loadingElementId) {
            try {
                // 验证JSON格式
                JSON.parse(jsonText);
                
                const response = await fetch('/import', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: jsonText
                });
                
                const result = await response.json();
                
                if (result.success) {
                    showMessage(messageElementId, result.message);
                } else {
                    showMessage(messageElementId, result.message, true);
                }
            } catch (error) {
                if (error instanceof SyntaxError) {
                    showMessage(messageElementId, '无效的JSON格式', true);
                } else {
                    showMessage(messageElementId, '导入失败：' + error.message, true);
                }
            } finally {
                showLoading(loadingElementId, false);
            }
        }
    </script>
</body>
</html>
        """.trimIndent()
    }
}