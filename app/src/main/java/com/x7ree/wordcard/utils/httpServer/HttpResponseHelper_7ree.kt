package com.x7ree.wordcard.utils.httpServer

import java.io.PrintWriter
import java.nio.charset.StandardCharsets

/**
 * HTTP响应帮助类
 */
class HttpResponseHelper_7ree {
    
    /**
     * 发送HTML响应
     */
    fun sendHtmlResponse(output: PrintWriter, html: String) {
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
    fun sendJsonResponse(output: PrintWriter, jsonContent: String) {
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
    fun sendErrorResponse(output: PrintWriter, message: String) {
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
    fun send404Response(output: PrintWriter) {
        val html = "<html><body><h1>404 Not Found</h1></body></html>"
        output.println("HTTP/1.1 404 Not Found")
        output.println("Content-Type: text/html; charset=UTF-8")
        output.println("Content-Length: ${html.length}")
        output.println()
        output.print(html)
        output.flush()
    }
}