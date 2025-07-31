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
    private val importHandler = HttpImportHandler_7ree(context, dataExportImportManager)
    
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
            method == "GET" && path == "/export-article" -> {
                // Log.d("HttpServer", "处理文章导出请求")
                handleArticleExport(output)
            }
            method == "POST" && path == "/import" -> {
                // Log.d("HttpServer", "处理导入请求")
                handleImport(input, output, headers)
            }
            method == "POST" && path == "/import-article" -> {
                // Log.d("HttpServer", "处理文章导入请求")
                handleArticleImport(input, output, headers)
            }
            method == "GET" && path == "/wordcount" -> {
                // Log.d("HttpServer", "处理单词数量查询请求")
                handleWordCount(output)
            }
            method == "GET" && path == "/articlecount" -> {
                // Log.d("HttpServer", "处理文章数量查询请求")
                handleArticleCount(output)
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
     * 处理文章数据导出请求
     */
    private suspend fun handleArticleExport(output: PrintWriter) {
        try {
            val result = dataExportImportManager.exportArticleData_7ree()
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
            responseHelper.sendErrorResponse(output, "文章数据导出失败")
        } catch (e: Exception) {
            responseHelper.sendErrorResponse(output, "文章数据导出异常: ${e.message}")
        }
    }
    
    /**
     * 处理数据导入请求 - 使用公用导入组件
     */
    private suspend fun handleImport(input: BufferedReader, output: PrintWriter, headers: Map<String, String>) {
        val result = importHandler.handleImport(input, headers, HttpImportHandler_7ree.ImportType.WORD)
        
        if (result.success) {
            responseHelper.sendJsonResponse(output, """{"success": true, "message": "${result.message}"}""")
        } else {
            responseHelper.sendErrorResponse(output, result.message)
        }
    }
    
    /**
     * 处理文章数据导入请求 - 使用公用导入组件
     */
    private suspend fun handleArticleImport(input: BufferedReader, output: PrintWriter, headers: Map<String, String>) {
        val result = importHandler.handleImport(input, headers, HttpImportHandler_7ree.ImportType.ARTICLE)
        
        if (result.success) {
            responseHelper.sendJsonResponse(output, """{"success": true, "message": "${result.message}"}""")
        } else {
            responseHelper.sendErrorResponse(output, result.message)
        }
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
    
    /**
     * 处理文章数量查询请求
     */
    private suspend fun handleArticleCount(output: PrintWriter) {
        try {
            // 通过导出数据来获取文章数量
            val result = dataExportImportManager.exportArticleData_7ree()
            var articleCount = 0
            
            if (result.isSuccess) {
                val filePath = result.getOrNull()
                if (filePath != null) {
                    val file = java.io.File(filePath)
                    if (file.exists()) {
                        val content = file.readText()
                        try {
                            val jsonElement = json.parseToJsonElement(content)
                            if (jsonElement is kotlinx.serialization.json.JsonObject) {
                                val articlesArray = jsonElement["articles"]
                                if (articlesArray is kotlinx.serialization.json.JsonArray) {
                                    articleCount = articlesArray.size
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("HttpServer", "解析文章导出数据失败: ${e.message}")
                        }
                    }
                }
            }
            
            // Log.d("HttpServer", "查询到文章数量: $articleCount")
            responseHelper.sendJsonResponse(output, """{"count": $articleCount}""")
        } catch (e: Exception) {
            Log.e("HttpServer", "查询文章数量异常: ${e.message}")
            responseHelper.sendErrorResponse(output, "查询文章数量失败: ${e.message}")
        }
    }
}
