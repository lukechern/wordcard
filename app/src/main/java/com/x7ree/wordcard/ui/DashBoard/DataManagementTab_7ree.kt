package com.x7ree.wordcard.ui.DashBoard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.utils.HttpServerManager_7ree

@Composable
fun DataManagementTab_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onImportFile_7ree: () -> Unit = {}
) {
    val context = LocalContext.current
    val operationResult_7ree by wordQueryViewModel_7ree.operationResult_7ree.collectAsState()
    
    // HTTP服务器相关状态
    var isServerEnabled by remember { mutableStateOf(false) }
    var serverUrl by remember { mutableStateOf<String?>(null) }
    var httpServerManager by remember { mutableStateOf<HttpServerManager_7ree?>(null) }
    
    // 初始化HTTP服务器管理器
    LaunchedEffect(Unit) {
        val dataExportImportManager = wordQueryViewModel_7ree.getDataExportImportManager()
        httpServerManager = HttpServerManager_7ree(context, dataExportImportManager)
    }
    
    // 处理服务器开关状态变化
    LaunchedEffect(isServerEnabled) {
        httpServerManager?.let { manager ->
            if (isServerEnabled) {
                if (manager.startServer()) {
                    serverUrl = manager.getServerUrl()
                } else {
                    isServerEnabled = false
                    serverUrl = null
                }
            } else {
                manager.stopServer()
                serverUrl = null
            }
        }
    }
    
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Computer,
                        contentDescription = "电脑操作",
                        modifier = Modifier.padding(end = 8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "从电脑操作",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Switch(
                    checked = isServerEnabled,
                    onCheckedChange = { isServerEnabled = it }
                )
            }
            
            if (isServerEnabled && serverUrl != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "局域网访问地址：",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SelectionContainer(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = serverUrl!!,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(12.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    val clipboardManager = LocalClipboardManager.current
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(serverUrl!!))
                            wordQueryViewModel_7ree.setOperationResult_7ree("地址已复制到剪贴板")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "复制地址",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "在同一局域网的电脑浏览器中访问上述地址，可以导出或导入app数据",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (isServerEnabled && serverUrl == null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "正在启动服务器...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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