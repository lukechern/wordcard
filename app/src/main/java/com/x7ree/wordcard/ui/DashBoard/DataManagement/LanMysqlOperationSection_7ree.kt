package com.x7ree.wordcard.ui.DashBoard.DataManagement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storage
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
import com.x7ree.wordcard.ui.DashBoard.DataManagement.LanMysql.LanMysqlConnectionTester_7ree
import com.x7ree.wordcard.ui.DashBoard.DataManagement.LanMysql.LanMysqlDataUploader_7ree
import com.x7ree.wordcard.ui.DashBoard.DataManagement.LanMysql.LanMysqlDataDownloader_7ree
import com.x7ree.wordcard.ui.DashBoard.DataManagement.LanMysql.LanMysqlProgressManager_7ree
import com.x7ree.wordcard.ui.DashBoard.DataManagement.LanMysql.LanMysqlProgressDisplay_7ree
import com.x7ree.wordcard.ui.DashBoard.DataManagement.LanMysql.LanMysqlOperationType_7ree
import kotlinx.coroutines.*

/**
 * 局域网PHP API操作区域组件
 */
@Composable
fun LanMysqlOperationSection_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    isLanMysqlEnabled: Boolean,
    onLanMysqlToggle: (Boolean) -> Unit,
    serverUrl: String,
    onServerUrlChange: (String) -> Unit,
    apiKey: String,
    onApiKeyChange: (String) -> Unit
) {
    var isApiKeyVisible by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var testSuccess by remember { mutableStateOf<Boolean?>(null) }
    
    // 获取协程作用域和上下文
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // 创建进度管理器
    val progressManager = remember { LanMysqlProgressManager_7ree() }
    val progressState by progressManager.progressState
    
    // 当局域网MySQL操作开关关闭时，清除进度显示
    LaunchedEffect(isLanMysqlEnabled) {
        if (!isLanMysqlEnabled) {
            progressManager.hideProgress()
        }
    }
    
    // 创建业务模块实例
    val connectionTester = remember { LanMysqlConnectionTester_7ree() }
    val dataUploader = remember { LanMysqlDataUploader_7ree(context, wordQueryViewModel_7ree) }
    val dataDownloader = remember { LanMysqlDataDownloader_7ree(context, wordQueryViewModel_7ree) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFE8F5E8).copy(alpha = 0.3f), // 浅绿色背景
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
                    imageVector = Icons.Filled.Storage,
                    contentDescription = "局域网MySQL操作",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color(0xFF388E3C) // 绿色图标
                )
                Text(
                    text = "从局域网操作",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Switch(
                checked = isLanMysqlEnabled,
                onCheckedChange = onLanMysqlToggle
            )
        }
        
        // 只有当局域网MySQL操作开关开启时才显示内容
        if (isLanMysqlEnabled) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "MySQL 服务端配置",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF388E3C),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // 服务端URL输入框
            OutlinedTextField(
                value = serverUrl,
                onValueChange = onServerUrlChange,
                label = { Text("服务端URL") },
                placeholder = { Text("请输入接收APP推送POST数据的PHP访问URL") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true
            )
            
            // 密钥输入框
            OutlinedTextField(
                value = apiKey,
                onValueChange = onApiKeyChange,
                label = { Text("密钥") },
                placeholder = { Text("请输入用于生成通讯密钥的KEY") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                visualTransformation = if (isApiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { isApiKeyVisible = !isApiKeyVisible }) {
                        Icon(
                            imageVector = if (isApiKeyVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (isApiKeyVisible) "隐藏密钥" else "显示密钥"
                        )
                    }
                }
            )
            
            // 进度显示组件 - 移动到测试按钮上方
            LanMysqlProgressDisplay_7ree(
                progressManager = progressManager,
                modifier = Modifier.fillMaxWidth()
            )
            
            // PHP API连接测试按钮
            OutlinedButton(
                onClick = {
                    if (serverUrl.isNotBlank() && apiKey.isNotBlank()) {
                        testResult = null
                        testSuccess = null
                        
                        // 开始测试进度
                        progressManager.startOperation(LanMysqlOperationType_7ree.TEST)
                        
                        coroutineScope.launch {
                            try {
                                val result = connectionTester.testConnection(
                                    serverUrl = serverUrl,
                                    apiKey = apiKey,
                                    onProgress = { progress ->
                                        progressManager.parseProgressFromText(progress)
                                    }
                                )
                                
                                if (result.isSuccess) {
                                    testSuccess = true
                                    testResult = result.getOrNull() ?: "连接成功"
                                    progressManager.completeOperation("✅ ${result.getOrNull()}")
                                } else {
                                    testSuccess = false
                                    testResult = result.exceptionOrNull()?.message ?: "连接失败"
                                    progressManager.failOperation("❌ ${result.exceptionOrNull()?.message}")
                                }
                            } catch (e: Exception) {
                                testSuccess = false
                                testResult = "连接异常：${e.message}"
                                progressManager.failOperation("❌ PHP API连接异常：${e.message}")
                            }
                        }
                    } else {
                        testResult = "请先填写完整的配置参数"
                        testSuccess = false
                        progressManager.failOperation("❌ 请填写完整的PHP API配置信息")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !progressState.isVisible || progressState.isCompleted
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (progressState.isVisible && !progressState.isCompleted && progressState.operationType == LanMysqlOperationType_7ree.TEST) {
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
                        Text("测试PHP API连接")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 操作按钮区域
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 上传到PHP API按钮
                Button(
                    onClick = {
                        if (serverUrl.isNotBlank() && apiKey.isNotBlank()) {
                            // 开始上传进度
                            progressManager.startOperation(LanMysqlOperationType_7ree.UPLOAD)
                            
                            coroutineScope.launch {
                                try {
                                    val result = dataUploader.uploadData(
                                        serverUrl = serverUrl,
                                        apiKey = apiKey,
                                        onProgress = { progress ->
                                            progressManager.parseProgressFromText(progress)
                                        }
                                    )
                                    
                                    if (result.isSuccess) {
                                        progressManager.completeOperation("✅ ${result.getOrNull()}")
                                    } else {
                                        progressManager.failOperation("❌ PHP API上传失败：${result.exceptionOrNull()?.message}")
                                    }
                                    
                                } catch (e: Exception) {
                                    progressManager.failOperation("❌ PHP API上传异常：${e.message}")
                                }
                            }
                        } else {
                            progressManager.failOperation("❌ 请填写完整的PHP API配置信息")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = (!progressState.isVisible || progressState.isCompleted) && 
                             serverUrl.isNotBlank() && apiKey.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF388E3C) // 绿色背景
                    )
                ) {
                    if (progressState.isVisible && !progressState.isCompleted && progressState.operationType == LanMysqlOperationType_7ree.UPLOAD) {
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
                        Text("上传到服务端")
                    }
                }
                
                // 从PHP API下载按钮
                Button(
                    onClick = {
                        if (serverUrl.isNotBlank() && apiKey.isNotBlank()) {
                            // 开始下载进度
                            progressManager.startOperation(LanMysqlOperationType_7ree.DOWNLOAD)
                            
                            coroutineScope.launch {
                                try {
                                    val result = dataDownloader.downloadData(
                                        serverUrl = serverUrl,
                                        apiKey = apiKey,
                                        onProgress = { progress ->
                                            progressManager.parseProgressFromText(progress)
                                        }
                                    )
                                    
                                    if (result.isSuccess) {
                                        progressManager.completeOperation("✅ ${result.getOrNull()}")
                                    } else {
                                        progressManager.failOperation("❌ PHP API下载失败：${result.exceptionOrNull()?.message}")
                                    }
                                    
                                } catch (e: Exception) {
                                    progressManager.failOperation("❌ PHP API下载异常：${e.message}")
                                }
                            }
                        } else {
                            progressManager.failOperation("❌ 请填写完整的PHP API配置信息")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = (!progressState.isVisible || progressState.isCompleted) && 
                             serverUrl.isNotBlank() && apiKey.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2) // 蓝色背景
                    )
                ) {
                    if (progressState.isVisible && !progressState.isCompleted && progressState.operationType == LanMysqlOperationType_7ree.DOWNLOAD) {
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
                        Text("从服务端下载")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 功能说明
            Text(
                text = "• 上传：将本地单词和文章数据通过PHP API同步到MySQL数据库\n" +
                      "• 下载：从PHP API获取MySQL数据库中的数据恢复到本地\n" +
                      "• 安全：使用密钥+日期生成动态token进行安全验证",
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