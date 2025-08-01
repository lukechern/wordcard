package com.x7ree.wordcard.ui.DashBoard.DataManagement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.DashBoard.DataManagement.cloudflare.CloudFlareConnectionTester_7ree
import com.x7ree.wordcard.ui.DashBoard.DataManagement.cloudflare.CloudFlareDataUploader_7ree
import com.x7ree.wordcard.ui.DashBoard.DataManagement.cloudflare.CloudFlareDataDownloader_7ree
import kotlinx.coroutines.*

/**
 * CloudFlare操作区域组件
 */
@Composable
fun CloudFlareOperationSection_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    isCloudFlareEnabled: Boolean,
    onCloudFlareToggle: (Boolean) -> Unit,
    databaseId: String,
    onDatabaseIdChange: (String) -> Unit,
    apiToken: String,
    onApiTokenChange: (String) -> Unit,
    accountId: String,
    onAccountIdChange: (String) -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var isTesting by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var testSuccess by remember { mutableStateOf<Boolean?>(null) }
    
    // 获取协程作用域和上下文
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // 创建业务模块实例
    val connectionTester = remember { CloudFlareConnectionTester_7ree() }
    val dataUploader = remember { CloudFlareDataUploader_7ree(context, wordQueryViewModel_7ree) }
    val dataDownloader = remember { CloudFlareDataDownloader_7ree(context, wordQueryViewModel_7ree) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFE3F2FD).copy(alpha = 0.3f), // 浅蓝色背景
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
                    imageVector = Icons.Filled.Cloud,
                    contentDescription = "CloudFlare操作",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color(0xFF1976D2) // 蓝色图标
                )
                Text(
                    text = "从CloudFlare操作",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Switch(
                checked = isCloudFlareEnabled,
                onCheckedChange = onCloudFlareToggle
            )
        }
        
        // 只有当CloudFlare操作开关开启时才显示内容
        if (isCloudFlareEnabled) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "CloudFlare D1 数据库配置",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1976D2),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // 账户ID输入框
            OutlinedTextField(
                value = accountId,
                onValueChange = onAccountIdChange,
                label = { Text("账户ID") },
                placeholder = { Text("请输入CloudFlare账户ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true
            )
            
            // 数据库ID输入框
            OutlinedTextField(
                value = databaseId,
                onValueChange = onDatabaseIdChange,
                label = { Text("数据库ID") },
                placeholder = { Text("请输入CloudFlare D1数据库ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true
            )
            
            // API Token输入框
            OutlinedTextField(
                value = apiToken,
                onValueChange = onApiTokenChange,
                label = { Text("API Token") },
                placeholder = { Text("请输入CloudFlare API Token") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (isPasswordVisible) "隐藏密码" else "显示密码"
                        )
                    }
                }
            )
            
            // API连接测试按钮
            OutlinedButton(
                onClick = {
                    if (databaseId.isNotBlank() && apiToken.isNotBlank() && accountId.isNotBlank()) {
                        isTesting = true
                        testResult = null
                        testSuccess = null
                        
                        // 使用实际的连接测试器
                        coroutineScope.launch {
                            try {
                                val result = connectionTester.testConnection(
                                    accountId = accountId,
                                    databaseId = databaseId,
                                    apiToken = apiToken,
                                    onProgress = { progress ->
                                        wordQueryViewModel_7ree.setOperationResult_7ree(progress)
                                    }
                                )
                                
                                isTesting = false
                                if (result.isSuccess) {
                                    testSuccess = true
                                    testResult = result.getOrNull() ?: "连接成功"
                                    wordQueryViewModel_7ree.setOperationResult_7ree("CloudFlare D1连接测试成功")
                                } else {
                                    testSuccess = false
                                    testResult = result.exceptionOrNull()?.message ?: "连接失败"
                                    wordQueryViewModel_7ree.setOperationResult_7ree("CloudFlare D1连接测试失败")
                                }
                            } catch (e: Exception) {
                                isTesting = false
                                testSuccess = false
                                testResult = "连接异常：${e.message}"
                                wordQueryViewModel_7ree.setOperationResult_7ree("CloudFlare D1连接测试异常：${e.message}")
                            }
                        }
                    } else {
                        testResult = "请先填写完整的配置参数"
                        testSuccess = false
                        wordQueryViewModel_7ree.setOperationResult_7ree("请填写完整的CloudFlare配置信息")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isTesting && !isUploading && !isDownloading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("测试中...")
                    } else {
                        Icon(
                            imageVector = Icons.Filled.NetworkCheck,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("测试CloudFlare D1连接")
                    }
                }
            }
            
            // 显示测试结果
            testResult?.let { result ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (testSuccess == true) 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else 
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = if (testSuccess == true) "✅ $result" else "❌ $result",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (testSuccess == true) 
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else 
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 操作按钮区域
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 上传到CloudFlare按钮
                Button(
                    onClick = {
                        if (databaseId.isNotBlank() && apiToken.isNotBlank() && accountId.isNotBlank()) {
                            isUploading = true
                            wordQueryViewModel_7ree.setOperationResult_7ree("开始上传数据到CloudFlare...")
                            
                            coroutineScope.launch {
                                try {
                                    val result = dataUploader.uploadData(
                                        accountId = accountId,
                                        databaseId = databaseId,
                                        apiToken = apiToken,
                                        onProgress = { progress ->
                                            wordQueryViewModel_7ree.setOperationResult_7ree(progress)
                                        }
                                    )
                                    
                                    isUploading = false
                                    if (result.isSuccess) {
                                        wordQueryViewModel_7ree.setOperationResult_7ree("✅ ${result.getOrNull()}")
                                    } else {
                                        wordQueryViewModel_7ree.setOperationResult_7ree("❌ 上传失败：${result.exceptionOrNull()?.message}")
                                    }
                                    
                                } catch (e: Exception) {
                                    isUploading = false
                                    wordQueryViewModel_7ree.setOperationResult_7ree("❌ 上传异常：${e.message}")
                                }
                            }
                        } else {
                            wordQueryViewModel_7ree.setOperationResult_7ree("请填写完整的CloudFlare配置信息")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isUploading && !isDownloading && 
                             databaseId.isNotBlank() && apiToken.isNotBlank() && accountId.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2) // 蓝色背景
                    )
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("上传中...")
                    } else {
                        Icon(
                            imageVector = Icons.Filled.CloudUpload,
                            contentDescription = "上传",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("上传到云端")
                    }
                }
                
                // 从CloudFlare下载按钮
                Button(
                    onClick = {
                        if (databaseId.isNotBlank() && apiToken.isNotBlank() && accountId.isNotBlank()) {
                            isDownloading = true
                            wordQueryViewModel_7ree.setOperationResult_7ree("开始从CloudFlare下载数据...")
                            
                            coroutineScope.launch {
                                try {
                                    val result = dataDownloader.downloadData(
                                        accountId = accountId,
                                        databaseId = databaseId,
                                        apiToken = apiToken,
                                        onProgress = { progress ->
                                            wordQueryViewModel_7ree.setOperationResult_7ree(progress)
                                        }
                                    )
                                    
                                    isDownloading = false
                                    if (result.isSuccess) {
                                        wordQueryViewModel_7ree.setOperationResult_7ree("✅ ${result.getOrNull()}")
                                    } else {
                                        wordQueryViewModel_7ree.setOperationResult_7ree("❌ 下载失败：${result.exceptionOrNull()?.message}")
                                    }
                                    
                                } catch (e: Exception) {
                                    isDownloading = false
                                    wordQueryViewModel_7ree.setOperationResult_7ree("❌ 下载异常：${e.message}")
                                }
                            }
                        } else {
                            wordQueryViewModel_7ree.setOperationResult_7ree("请填写完整的CloudFlare配置信息")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isUploading && !isDownloading && 
                             databaseId.isNotBlank() && apiToken.isNotBlank() && accountId.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF388E3C) // 绿色背景
                    )
                ) {
                    if (isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("下载中...")
                    } else {
                        Icon(
                            imageVector = Icons.Filled.CloudDownload,
                            contentDescription = "下载",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("从云端下载")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 功能说明
            Text(
                text = "� 参功能说明：\n" +
                      "• 上传：将本地单词和文章数据同步到CloudFlare D1数据库\n" +
                      "• 下载：从CloudFlare D1数据库恢复数据到本地",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(8.dp)
            )
        }
    }
}