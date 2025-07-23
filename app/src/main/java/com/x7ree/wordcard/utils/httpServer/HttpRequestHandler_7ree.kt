package com.x7ree.wordcard.utils.httpServer

import android.content.Context
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
        when {
            method == "GET" && path == "/" -> {
                responseHelper.sendHtmlResponse(output, htmlProvider.getMainPageHtml())
            }
            method == "GET" && path == "/export" -> {
                handleExport(output)
            }
            method == "POST" && path == "/import" -> {
                handleImport(input, output, headers)
            }
            else -> {
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
        try {
            val contentLength = headers["content-length"]?.toIntOrNull() ?: 0
            if (contentLength > 0) {
                // 直接读取指定长度的字符
                val buffer = CharArray(contentLength)
                var totalRead = 0
                while (totalRead < contentLength) {
                    val read = input.read(buffer, totalRead, contentLength - totalRead)
                    if (read == -1) break
                    totalRead += read
                }
                
                val jsonString = String(buffer, 0, totalRead).trim()
                
                if (jsonString.isNotEmpty()) {
                    // 验证JSON格式
                    try {
                        // 使用kotlinx.serialization验证JSON
                        json.parseToJsonElement(jsonString)
                    } catch (e: Exception) {
                        responseHelper.sendErrorResponse(output, "JSON格式验证失败: ${e.message}")
                        return
                    }
                    
                    // 创建临时文件，使用UTF-8编码
                    val tempFile = File.createTempFile("import_", ".json", context.cacheDir)
                    tempFile.writeText(jsonString, Charsets.UTF_8)
                    
                    val uri = android.net.Uri.fromFile(tempFile)
                    val result = dataExportImportManager.importData_7ree(uri)
                    
                    tempFile.delete()
                    
                    if (result.isSuccess) {
                        val count = result.getOrNull() ?: 0
                        responseHelper.sendJsonResponse(output, """{"success": true, "message": "成功导入 $count 条记录"}""")
                    } else {
                        val errorMsg = result.exceptionOrNull()?.message ?: "未知错误"
                        responseHelper.sendErrorResponse(output, "导入失败: $errorMsg")
                    }
                } else {
                    responseHelper.sendErrorResponse(output, "没有接收到数据")
                }
            } else {
                responseHelper.sendErrorResponse(output, "请求中没有数据")
            }
        } catch (e: Exception) {
            responseHelper.sendErrorResponse(output, "导入异常: ${e.message}")
        }
    }
}