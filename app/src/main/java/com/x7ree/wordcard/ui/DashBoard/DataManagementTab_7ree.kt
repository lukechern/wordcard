package com.x7ree.wordcard.ui.DashBoard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree

@Composable
fun DataManagementTab_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onImportFile_7ree: () -> Unit = {}
) {
    val operationResult_7ree by wordQueryViewModel_7ree.operationResult_7ree.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "查询历史单词数据导出与导入",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "数据导出",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "将查询历史导出为JSON文件，包含单词、查询结果、时间戳等信息",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
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
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "数据导入",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "从JSON文件导入查询历史数据，支持批量恢复历史记录",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Button(
                onClick = {
                    onImportFile_7ree()
                },
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

        // 显示数据导出默认路径
        val exportPath_7ree by wordQueryViewModel_7ree.exportPath_7ree.collectAsState()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "数据将默认导出到: \n$exportPath_7ree",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Start
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}