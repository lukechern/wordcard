package com.x7ree.wordcard.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
语言包定义

    'pl_export_success_7r' => '导出成功',
    'pl_export_failed_7r' => '导出失败',
    'pl_import_success_7r' => '导入成功',
    'pl_import_failed_7r' => '导入失败',
    'pl_file_not_found_7r' => '文件未找到',
    'pl_invalid_file_format_7r' => '文件格式无效',
**/

@Serializable
data class ExportData_7ree(
    val exportTime: Long = System.currentTimeMillis(),
    val version: String = "1.0",
    val words: List<WordEntity_7ree> = emptyList()
)

class DataExportImportManager_7ree(
    private val context: Context,
    private val wordRepository_7ree: WordRepository_7ree
) {
    private val json = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true 
    }
    
    // 导出数据到文件
    suspend fun exportData_7ree(): Result<String> = withContext(Dispatchers.IO) {
        try {
            // 获取所有单词数据
            val words_7ree = wordRepository_7ree.getAllWords_7ree().first()
            
            // 创建导出数据对象
            val exportData_7ree = ExportData_7ree(words = words_7ree)
            
            // 序列化为JSON
            val jsonString = json.encodeToString(exportData_7ree)
            
            // 生成文件名
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val timestamp = dateFormat.format(Date())
            val fileName = "wordcard_export_${timestamp}.json"
            
            // 保存到外部存储的Downloads目录
            val downloadsDir = File(context.getExternalFilesDir(null), "Downloads")
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            
            val file = File(downloadsDir, fileName)
            file.writeText(jsonString)
            
            println("DEBUG: 数据导出成功，文件: ${file.absolutePath}")
            println("DEBUG: 文件大小: ${file.length()} bytes")
            println("DEBUG: 文件是否存在: ${file.exists()}")
            Result.success(file.absolutePath)
        } catch (e: Exception) {
            println("DEBUG: 数据导出失败: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    // 从文件导入数据
    suspend fun importData_7ree(uri: Uri): Result<Int> = withContext(Dispatchers.IO) {
        try {
            // 读取文件内容
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("无法打开文件"))
            
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()
            
            // 反序列化数据
            val exportData_7ree = json.decodeFromString<ExportData_7ree>(jsonString)
            
            // 验证数据格式
            if (exportData_7ree.words.isEmpty()) {
                return@withContext Result.failure(Exception("文件不包含有效的单词数据"))
            }
            
            // 导入数据到数据库
            var importedCount = 0
            for (wordEntity_7ree in exportData_7ree.words) {
                try {
                    // 检查是否已存在
                    val existingWord = wordRepository_7ree.getWord_7ree(wordEntity_7ree.word)
                    if (existingWord == null) {
                        // 插入新记录
                        wordRepository_7ree.insertWord_7ree(wordEntity_7ree)
                        importedCount++
                    } else {
                        // 更新现有记录（保留原有的浏览次数和收藏状态）
                        val updatedWord = existingWord.copy(
                            apiResult = wordEntity_7ree.apiResult,
                            queryTimestamp = wordEntity_7ree.queryTimestamp
                        )
                        wordRepository_7ree.updateWord_7ree(updatedWord)
                        importedCount++
                    }
                } catch (e: Exception) {
                    println("DEBUG: 导入单词 ${wordEntity_7ree.word} 失败: ${e.message}")
                }
            }
            
            println("DEBUG: 数据导入成功，共导入 $importedCount 条记录")
            Result.success(importedCount)
        } catch (e: Exception) {
            println("DEBUG: 数据导入失败: ${e.message}")
            Result.failure(e)
        }
    }
    
    // 获取导出文件列表
    fun getExportFiles_7ree(): List<File> {
        return try {
            context.filesDir.listFiles { file ->
                file.name.startsWith("wordcard_export_") && file.name.endsWith(".json")
            }?.sortedByDescending { it.lastModified() } ?: emptyList()
        } catch (e: Exception) {
            println("DEBUG: 获取导出文件列表失败: ${e.message}")
            emptyList()
        }
    }
    
    // 删除导出文件
    fun deleteExportFile_7ree(fileName: String): Boolean {
        return try {
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                file.delete()
            }
            true
        } catch (e: Exception) {
            println("DEBUG: 删除文件失败: ${e.message}")
            false
        }
    }
    
    // 获取数据导出的默认目录路径
    fun getDefaultExportDirectory_7ree(): String {
        val downloadsDir = File(context.getExternalFilesDir(null), "Downloads")
        return downloadsDir.absolutePath
    }
} 