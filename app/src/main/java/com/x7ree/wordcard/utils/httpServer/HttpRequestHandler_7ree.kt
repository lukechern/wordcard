package com.x7ree.wordcard.utils.httpServer

import android.content.Context
import android.util.Log
import com.x7ree.wordcard.data.DataExportImportManager_7ree
import kotlinx.serialization.json.Json
import java.io.*
import java.io.File

/**
 * HTTP请求处理器
 */
class HttpRequestHandler_7ree(
    private val context: Context,
    private val dataExportImportManager: DataExportImportManager_7ree
) {
    private val json = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true 
    }
    
    private val responseHelper = HttpResponseHelper_7ree()
    private val htmlProvider = HtmlPageProvider_7ree()
    
    /**
     * 处理HTTP请求
     */
    suspend fun handleRequest(
        method: String,
        path: String,
        input: BufferedReader,
        output: PrintWriter,
        headers: Map<String, String>
    ) {
        // Log.d("HttpServer", "收到请求: $method $path")
        
        when {
            method == "GET" && path == "/" -> {
                // Log.d("HttpServer", "处理主页请求")
                responseHelper.sendHtmlResponse(output, htmlProvider.getMainPageHtml())
            }
            method == "GET" && path == "/export" -> {
                // Log.d("HttpServer", "处理导出请求")
                handleExport(output)
            }
            method == "POST" && path == "/import" -> {
                // Log.d("HttpServer", "处理导入请求")
                handleImport(input, output, headers)
            }
            method == "GET" && path == "/wordcount" -> {
                // Log.d("HttpServer", "处理单词数量查询请求")
                handleWordCount(output)
            }
            method == "GET" && path == "/favicon.ico" -> {
                // Log.d("HttpServer", "处理favicon请求")
                responseHelper.sendFaviconResponse(output)
            }
            method == "OPTIONS" -> {
                // Log.d("HttpServer", "处理OPTIONS预检请求")
                responseHelper.sendOptionsResponse(output)
            }
            else -> {
                // Log.d("HttpServer", "未知请求路径: $method $path")
                responseHelper.send404Response(output)
            }
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
                        responseHelper.sendJsonResponse(output, content)
                        return
                    }
                }
            }
            responseHelper.sendErrorResponse(output, "导出失败")
        } catch (e: Exception) {
            responseHelper.sendErrorResponse(output, "导出异常: ${e.message}")
        }
    }
    
    /**
     * 处理数据导入请求
     */
    private suspend fun handleImport(input: BufferedReader, output: PrintWriter, headers: Map<String, String>) {
        // Log.d("HttpServer", "=== 开始处理导入请求 ===")
        
        try {
            // 打印所有请求头
            // Log.d("HttpServer", "请求头信息:")
            headers.forEach { (_, _) ->
                // Log.d("HttpServer", "  $key: $value")
            }
            
            val contentLength = headers["content-length"]?.toIntOrNull() ?: 0
            // Log.d("HttpServer", "Content-Length: $contentLength")
            
            // Log.d("HttpServer", "开始读取请求体数据...")
            
            val finalJsonString: String
            
            try {
                // 不依赖Content-Length，直接读取所有可用数据
                val jsonBuilder = StringBuilder()
                val buffer = CharArray(8192)
                var totalRead = 0
                var consecutiveZeroReads = 0
                
                // Log.d("HttpServer", "使用动态读取模式，预期长度: $contentLength")
                
                var readAttempts = 0
                val maxReadAttempts = 100 // 最大读取尝试次数
                
                while (readAttempts < maxReadAttempts) {
                    readAttempts++
                    
                    val read = input.read(buffer)
                    // Log.d("HttpServer", "第 $readAttempts 次读取尝试，返回: $read")
                    
                    if (read == -1) {
                        // Log.d("HttpServer", "读取遇到EOF，总共读取: $totalRead 字符")
                        break
                    }
                    
                    if (read > 0) {
                        jsonBuilder.append(buffer, 0, read)
                        totalRead += read
                        consecutiveZeroReads = 0
                        // Log.d("HttpServer", "成功读取 $read 字符，总计: $totalRead")
                        
                        // 如果读取的数据接近预期长度，可能已经完成
                        if (totalRead >= contentLength * 0.75) { // 降低阈值到75%
                            // Log.d("HttpServer", "读取数据接近预期长度(${totalRead}/${contentLength})，检查是否完成")
                            
                            // 尝试验证当前数据是否是完整的JSON
                            val currentData = jsonBuilder.toString().trim()
                            try {
                                json.parseToJsonElement(currentData)
                                // Log.d("HttpServer", "当前数据已经是完整的JSON，提前结束读取")
                                break
                            } catch (e: Exception) {
                                // Log.d("HttpServer", "当前数据不是完整的JSON，继续读取: ${e.message}")
                            }
                            
                            // 检查输入流状态
                            val isReady = input.ready()
                            // Log.d("HttpServer", "输入流是否准备好: $isReady")
                            if (!isReady) {
                                // Log.d("HttpServer", "输入流没有更多数据，可能读取完成")
                                break
                            } else {
                                // Log.d("HttpServer", "输入流还有数据，继续读取")
                            }
                        }
                    } else {
                        consecutiveZeroReads++
                        // Log.d("HttpServer", "读取返回0，连续零读取次数: $consecutiveZeroReads")
                        
                        // 如果连续多次读取为0，检查是否还有数据
                        if (consecutiveZeroReads >= 5) {
                            val isReady = input.ready()
                            // Log.d("HttpServer", "输入流是否准备好: $isReady")
                            if (!isReady) {
                                // Log.d("HttpServer", "输入流没有更多数据，结束读取")
                                break
                            }
                        }
                        
                        // 短暂等待
                        Thread.sleep(50) // 增加等待时间
                    }
                    
                    // 安全检查：如果读取的数据远超预期，可能有问题
                    if (totalRead > contentLength * 2) {
                        Log.w("HttpServer", "读取数据量超出预期，可能有问题，停止读取")
                        break
                    }
                }
                
                if (readAttempts >= maxReadAttempts) {
                    Log.w("HttpServer", "达到最大读取尝试次数，停止读取")
                }
                
                finalJsonString = jsonBuilder.toString().trim()
                // Log.d("HttpServer", "动态读取完成")
                // Log.d("HttpServer", "实际读取长度: ${finalJsonString.length}, Content-Length: $contentLength")
                // Log.d("HttpServer", "数据前100字符: ${finalJsonString.take(100)}")
                // Log.d("HttpServer", "数据后100字符: ${finalJsonString.takeLast(100)}")
                
            } catch (e: Exception) {
                Log.e("HttpServer", "读取数据时发生异常: ${e.message}", e)
                responseHelper.sendErrorResponse(output, "读取数据异常: ${e.message}")
                return
            }
            
            if (finalJsonString.isNotEmpty()) {
                // Log.d("HttpServer", "开始验证JSON格式...")
                
                // 验证JSON格式
                try {
                    // 使用kotlinx.serialization验证JSON
                    json.parseToJsonElement(finalJsonString)
                    // Log.d("HttpServer", "JSON格式验证通过")
                } catch (e: Exception) {
                    Log.e("HttpServer", "JSON格式验证失败: ${e.message}")
                    responseHelper.sendErrorResponse(output, "JSON格式验证失败: ${e.message}")
                    return
                }
                
                // Log.d("HttpServer", "创建临时文件...")
                // 创建临时文件，使用UTF-8编码
                val tempFile = File.createTempFile("import_", ".json", context.cacheDir)
                tempFile.writeText(finalJsonString, Charsets.UTF_8)
                // Log.d("HttpServer", "临时文件创建成功: ${tempFile.absolutePath}")
                
                // Log.d("HttpServer", "开始调用数据导入管理器...")
                val uri = android.net.Uri.fromFile(tempFile)
                val result = dataExportImportManager.importData_7ree(uri)
                // Log.d("HttpServer", "数据导入管理器调用完成")
                
                tempFile.delete()
                // Log.d("HttpServer", "临时文件已删除")
                
                if (result.isSuccess) {
                    val count = result.getOrNull() ?: 0
                    // Log.d("HttpServer", "导入成功，记录数: $count")
                    responseHelper.sendJsonResponse(output, """{"success": true, "message": "成功导入 $count 条记录"}""")
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "未知错误"
                    Log.e("HttpServer", "导入失败: $errorMsg")
                    responseHelper.sendErrorResponse(output, "导入失败: $errorMsg")
                }
            } else {
                Log.e("HttpServer", "接收到的数据为空")
                responseHelper.sendErrorResponse(output, "没有接收到数据")
            }
        } catch (e: Exception) {
            Log.e("HttpServer", "导入处理异常: ${e.message}", e)
            responseHelper.sendErrorResponse(output, "导入异常: ${e.message}")
        }
        
        // Log.d("HttpServer", "=== 导入请求处理完成 ===")
    }
    
    /**
     * 处理单词数量查询请求
     */
    private suspend fun handleWordCount(output: PrintWriter) {
        try {
            // 通过导出数据来获取单词数量
            val result = dataExportImportManager.exportData_7ree()
            var wordCount = 0
            
            if (result.isSuccess) {
                val filePath = result.getOrNull()
                if (filePath != null) {
                    val file = java.io.File(filePath)
                    if (file.exists()) {
                        val content = file.readText()
                        try {
                            val jsonElement = json.parseToJsonElement(content)
                            if (jsonElement is kotlinx.serialization.json.JsonObject) {
                                val wordsArray = jsonElement["words"]
                                if (wordsArray is kotlinx.serialization.json.JsonArray) {
                                    wordCount = wordsArray.size
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("HttpServer", "解析导出数据失败: ${e.message}")
                        }
                    }
                }
            }
            
            // Log.d("HttpServer", "查询到单词数量: $wordCount")
            responseHelper.sendJsonResponse(output, """{"count": $wordCount}""")
        } catch (e: Exception) {
            Log.e("HttpServer", "查询单词数量异常: ${e.message}")
            responseHelper.sendErrorResponse(output, "查询单词数量失败: ${e.message}")
        }
    }
}
