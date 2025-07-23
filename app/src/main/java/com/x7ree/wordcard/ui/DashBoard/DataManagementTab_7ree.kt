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
    val operationResult_7ree by wordQueryViewModel_7ree.operationResult_7ree.collectAsState()
    
    // 使用状态管理器
    val dataManagementState = rememberDataManagementState_7ree(context, wordQueryViewModel_7ree)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "查询历史单词数据导出与导入",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 从电脑操作区域
        ServerControlSection_7ree(
            context = context,
            wordQueryViewModel_7ree = wordQueryViewModel_7ree,
            isServerEnabled = dataManagementState.isServerEnabled,
            onServerToggle = dataManagementState.onServerToggle,
            serverUrl = dataManagementState.serverUrl,
            httpServerManager = dataManagementState.httpServerManager
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 从手机操作区域
        PhoneOperationSection_7ree(
            wordQueryViewModel_7ree = wordQueryViewModel_7ree,
            onImportFile_7ree = onImportFile_7ree
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}