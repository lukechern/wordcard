package com.x7ree.wordcard.utils.httpServer

import android.content.Context
import android.net.Uri
import android.util.Log
import com.x7ree.wordcard.data.DataExportImportManager_7ree
import kotlinx.serialization.json.Json
import java.io.*

/**
 * HTTP导入处理器 - 公用组件
 * 提供统一的数据导入处理逻辑，支持单词和文章数据导入
 */
class HttpImportHandler_7ree(
    private val context: Context,
    private val dataExportImportManager: DataExportImportManager_7ree
) {
    private val json = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true 
    }
    
    /**
     * 导入类型枚举
     */
    enum class ImportType {
        WORD,    // 单词导入
        ARTICLE  // 文章导入
    }
    
    /**
     * 导入结果数据类
     */
    data class ImportResult(
        val success: Boolean,
        val message: String,
        val count: Int = 0
    )
    
    /**
     * 通用导入处理方法
     * @param input 输入流
     * @param headers HTTP请求头
     * @param importType 导入类型
     * @return 导入结果
     */
    suspend fun handleImport(
        input: BufferedReader, 
        headers: Map<String, String>,
        importType: ImportType
    ): ImportResult {
        return try {
            // 1. 读取JSON数据
            val jsonString = readJsonData(input, headers, importType)
            
            // 2. 验证JSON格式
            validateJsonFormat(jsonString, importType)
            
            // 3. 创建临时文件
            val tempFile = createTempFile(jsonString, importType)
            
            // 4. 执行导入操作
            val result = performImport(tempFile, importType)
            
            // 5. 清理临时文件
            tempFile.delete()
            
            result
        } catch (e: Exception) {
            Log.e("HttpImportHandler", "导入处理异常: ${e.message}", e)
            val dataType = when (importType) {
                ImportType.WORD -> "单词"
                ImportType.ARTICLE -> "文章"
            }
            ImportResult(
                success = false,
                message = "${dataType}导入异常: ${e.message}"
            )
        }
    }
    
    /**
     * 读取JSON数据 - 使用与单词导入相同的健壮算法
     */
    private fun readJsonData(input: BufferedReader, headers: Map<String, String>, importType: ImportType): String {
        val contentLength = headers["content-length"]?.toIntOrNull() ?: 0
        
        // 使用与单词导入相同的动态读取策略
        val jsonBuilder = StringBuilder()
        val buffer = CharArray(8192)
        var totalRead = 0
        var consecutiveZeroReads = 0
        var readAttempts = 0
        val maxReadAttempts = 100
        
        while (readAttempts < maxReadAttempts) {
            readAttempts++
            
            val read = input.read(buffer)
            
            if (read == -1) {
                // 遇到EOF，结束读取
                break
            }
            
            if (read > 0) {
                jsonBuilder.append(buffer, 0, read)
                totalRead += read
                consecutiveZeroReads = 0
                
                // 智能完成检测 - 统一使用JSON验证方式
                if (totalRead >= contentLength * 0.75 || totalRead >= 8192) {
                    // 尝试验证当前数据是否是完整的JSON
                    val currentData = jsonBuilder.toString().trim()
                    try {
                        val jsonElement = json.parseToJsonElement(currentData)
                        // 检查是否包含预期的数据数组
                        if (jsonElement is kotlinx.serialization.json.JsonObject) {
                            val dataArrayKey = when (importType) {
                                ImportType.WORD -> "words"
                                ImportType.ARTICLE -> "articles"
                            }
                            
                            if (jsonElement.containsKey(dataArrayKey)) {
                                val dataArray = jsonElement[dataArrayKey]
                                if (dataArray is kotlinx.serialization.json.JsonArray) {
                                    // JSON完整，提前结束读取
                                    break
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // 当前数据不是完整的JSON，继续读取
                    }
                    
                    // 检查输入流状态
                    if (!input.ready()) {
                        // 输入流没有更多数据，结束读取
                        break
                    }
                }
            } else {
                consecutiveZeroReads++
                
                // 如果连续多次读取为0，检查是否还有数据
                if (consecutiveZeroReads >= 5) {
                    if (!input.ready()) {
                        // 输入流没有更多数据，结束读取
                        break
                    }
                }
                
                // 短暂等待
                Thread.sleep(50)
            }
            
            // 安全检查：如果读取的数据远超预期，可能有问题
            if (totalRead > contentLength * 2) {
                break
            }
        }
        
        return jsonBuilder.toString().trim()
    }
    
    /**
     * 验证JSON格式
     */
    private fun validateJsonFormat(jsonString: String, importType: ImportType) {
        if (jsonString.isEmpty()) {
            throw Exception("没有接收到数据")
        }
        
        try {
            val jsonElement = json.parseToJsonElement(jsonString)
            
            // 检查JSON结构
            if (jsonElement is kotlinx.serialization.json.JsonObject) {
                when (importType) {
                    ImportType.WORD -> {
                        if (jsonElement.containsKey("words")) {
                            val wordsArray = jsonElement["words"]
                            if (wordsArray is kotlinx.serialization.json.JsonArray) {
                                if (wordsArray.size == 0) {
                                    throw Exception("JSON文件中的words数组为空，没有可导入的单词数据")
                                }
                            } else {
                                throw Exception("JSON文件格式错误：words字段必须是数组类型")
                            }
                        } else {
                            throw Exception("这不是有效的单词导入文件，JSON中缺少words数组。请确认您选择了正确的导入类型。")
                        }
                    }
                    ImportType.ARTICLE -> {
                        if (jsonElement.containsKey("articles")) {
                            val articlesArray = jsonElement["articles"]
                            if (articlesArray is kotlinx.serialization.json.JsonArray) {
                                if (articlesArray.size == 0) {
                                    throw Exception("JSON文件中的articles数组为空，没有可导入的文章数据")
                                }
                            } else {
                                throw Exception("JSON文件格式错误：articles字段必须是数组类型")
                            }
                        } else {
                            throw Exception("这不是有效的文章导入文件，JSON中缺少articles数组。请确认您选择了正确的导入类型。")
                        }
                    }
                }
            }
            
        } catch (e: Exception) {
            throw Exception("JSON格式验证失败: ${e.message}")
        }
    }
    
    /**
     * 创建临时文件
     */
    private fun createTempFile(jsonString: String, importType: ImportType): File {
        val prefix = when (importType) {
            ImportType.WORD -> "import_word_"
            ImportType.ARTICLE -> "import_article_"
        }
        
        val tempFile = File.createTempFile(prefix, ".json", context.cacheDir)
        tempFile.writeText(jsonString, Charsets.UTF_8)
        return tempFile
    }
    
    /**
     * 执行导入操作
     */
    private suspend fun performImport(tempFile: File, importType: ImportType): ImportResult {
        val uri = Uri.fromFile(tempFile)
        
        val result = when (importType) {
            ImportType.WORD -> dataExportImportManager.importData_7ree(uri)
            ImportType.ARTICLE -> dataExportImportManager.importArticleData_7ree(uri)
        }
        
        return if (result.isSuccess) {
            val count = result.getOrNull() ?: 0
            val dataType = when (importType) {
                ImportType.WORD -> "单词"
                ImportType.ARTICLE -> "文章"
            }
            ImportResult(
                success = true,
                message = "成功导入 $count 条${dataType}记录",
                count = count
            )
        } else {
            val errorMsg = result.exceptionOrNull()?.message ?: "未知错误"
            val dataType = when (importType) {
                ImportType.WORD -> "单词"
                ImportType.ARTICLE -> "文章"
            }
            ImportResult(
                success = false,
                message = "${dataType}导入失败: $errorMsg"
            )
        }
    }
}