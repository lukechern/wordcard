package com.x7ree.wordcard.ui.DashBoard.DataManagement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree

/**
 * 手机操作区域组件
 */
@Composable
fun PhoneOperationSection_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onImportFile_7ree: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.Gray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PhoneAndroid,
                contentDescription = "手机操作",
                modifier = Modifier.padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "从手机操作",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // 数据导出部分
        DataExportSection_7ree(wordQueryViewModel_7ree)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 数据导入部分
        DataImportSection_7ree(onImportFile_7ree)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 存储路径信息
        StoragePathSection_7ree(wordQueryViewModel_7ree)
    }
}

/**
 * 数据导出部分
 */
@Composable
private fun DataExportSection_7ree(wordQueryViewModel_7ree: WordQueryViewModel_7ree) {
    Text(
        text = "数据导出",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    
    Text(
        text = "将查询历史导出为JSON文件，包含单词、查询结果、时间戳等信息",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    
    Button(
        onClick = {
            wordQueryViewModel_7ree.exportHistoryData_7ree()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Filled.Download,
            contentDescription = "导出",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text("导出历史数据")
    }
}

/**
 * 数据导入部分
 */
@Composable
private fun DataImportSection_7ree(onImportFile_7ree: () -> Unit) {
    Text(
        text = "数据导入",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    
    Text(
        text = "从JSON文件导入查询历史数据，支持批量恢复历史记录",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    
    Button(
        onClick = onImportFile_7ree,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Filled.Upload,
            contentDescription = "导入",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text("导入历史数据")
    }
}

/**
 * 存储路径信息部分
 */
@Composable
private fun StoragePathSection_7ree(wordQueryViewModel_7ree: WordQueryViewModel_7ree) {
    val exportPath_7ree by wordQueryViewModel_7ree.exportPath_7ree.collectAsState()
    
    Text(
        text = "存储路径",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    
    Text(
        text = "数据将默认导出到：\n$exportPath_7ree",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(8.dp)
    )
}