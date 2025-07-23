package com.x7ree.wordcard.ui.DashBoard.DataManagement

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.utils.HttpServerManager_7ree

/**
 * 服务器控制区域组件
 */
@Composable
fun ServerControlSection_7ree(
    context: Context,
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    isServerEnabled: Boolean,
    onServerToggle: (Boolean) -> Unit,
    serverUrl: String?,
    httpServerManager: HttpServerManager_7ree?
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
                onCheckedChange = onServerToggle
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
            
            // 第一行：显示URL地址
            SelectionContainer(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = serverUrl,
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 第二行：复制按钮
            val clipboardManager = LocalClipboardManager.current
            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(serverUrl))
                    wordQueryViewModel_7ree.setOperationResult_7ree("地址已复制到剪贴板")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = "复制地址",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("复制地址到剪贴板")
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
}