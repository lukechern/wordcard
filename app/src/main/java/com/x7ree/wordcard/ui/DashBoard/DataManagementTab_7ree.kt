package com.x7ree.wordcard.ui.DashBoard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.DashBoard.DataManagement.ServerControlSection_7ree
import com.x7ree.wordcard.ui.DashBoard.DataManagement.PhoneOperationSection_7ree
import com.x7ree.wordcard.ui.DashBoard.DataManagement.rememberDataManagementState_7ree

/**
 * 数据管理标签页 - 重构后的简化版本
 */
@Composable
fun DataManagementTab_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onImportFile_7ree: () -> Unit = {}
) {
    val context = LocalContext.current
    
    // 获取单词记录数量 - 使用状态来存储
    var wordCount by remember { mutableStateOf(0) }
    
    // 在组件初始化时获取单词数量
    LaunchedEffect(Unit) {
        try {
            val dataExportImportManager = wordQueryViewModel_7ree.getDataExportImportManager()
            val result = dataExportImportManager.exportData_7ree()
            if (result.isSuccess) {
                val filePath = result.getOrNull()
                if (filePath != null) {
                    val file = java.io.File(filePath)
                    if (file.exists()) {
                        val content = file.readText()
                        try {
                            val jsonElement = kotlinx.serialization.json.Json.parseToJsonElement(content)
                            if (jsonElement is kotlinx.serialization.json.JsonObject) {
                                val wordsArray = jsonElement["words"]
                                if (wordsArray is kotlinx.serialization.json.JsonArray) {
                                    wordCount = wordsArray.size
                                }
                            }
                        } catch (e: Exception) {
                            // 解析失败，保持默认值0
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // 获取失败，保持默认值0
        }
    }
    
    // 使用状态管理器
    val dataManagementState = rememberDataManagementState_7ree(context, wordQueryViewModel_7ree)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(end = 8.dp) // 为滚动条留出空间
    ) {
        Text(
            text = "WordCard 数据管理",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        Text(
            text = "共存储了${wordCount}条单词记录",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 从电脑操作区域
        ServerControlSection_7ree(
            wordQueryViewModel_7ree = wordQueryViewModel_7ree,
            isServerEnabled = dataManagementState.isServerEnabled,
            onServerToggle = dataManagementState.onServerToggle,
            serverUrl = dataManagementState.serverUrl
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 从手机操作区域
        PhoneOperationSection_7ree(
            wordQueryViewModel_7ree = wordQueryViewModel_7ree,
            onImportFile_7ree = onImportFile_7ree,
            isPhoneOperationEnabled = dataManagementState.isPhoneOperationEnabled,
            onPhoneOperationToggle = dataManagementState.onPhoneOperationToggle
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
