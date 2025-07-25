package com.x7ree.wordcard.ui.DashBoard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.config.ApiConfig_7ree
import com.x7ree.wordcard.config.TranslationApiConfig_7ree

@Composable
fun DualTranslationApiSection_7ree(
    apiConfig: ApiConfig_7ree,
    onApi1Change: (String, String, String, Boolean) -> Unit,
    onApi2Change: (String, String, String, Boolean) -> Unit,
    onTestResult: (Boolean, String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "AI大模型翻译API配置",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 根据启用状态动态排序卡片
        val api1Config = apiConfig.translationApi1
        val api2Config = apiConfig.translationApi2
        
        // 确保至少有一个API是启用的
        val actualApi1Enabled = api1Config.isEnabled || !api2Config.isEnabled
        val actualApi2Enabled = api2Config.isEnabled && !actualApi1Enabled
        
        // 创建卡片数据列表，启用的排在前面
        val cardData = listOf(
            Triple("AI大模型翻译API(一)", api1Config.copy(isEnabled = actualApi1Enabled), true),
            Triple("AI大模型翻译API(二)", api2Config.copy(isEnabled = actualApi2Enabled), false)
        ).sortedByDescending { it.second.isEnabled }
        
        // 渲染排序后的卡片
        cardData.forEachIndexed { index, (title, config, isApi1) ->
            TranslationApiCard_7ree(
                title = title,
                config = config,
                onConfigChange = { key, url, model, enabled ->
                    if (isApi1) {
                        // 如果是API1的变更
                        if (enabled) {
                            // 启用API1，自动禁用API2
                            onApi1Change(key, url, model, true)
                            onApi2Change(api2Config.apiKey, api2Config.apiUrl, api2Config.modelName, false)
                        } else {
                            // 禁用API1，自动启用API2
                            onApi1Change(key, url, model, false)
                            onApi2Change(api2Config.apiKey, api2Config.apiUrl, api2Config.modelName, true)
                        }
                    } else {
                        // 如果是API2的变更
                        if (enabled) {
                            // 启用API2，自动禁用API1
                            onApi2Change(key, url, model, true)
                            onApi1Change(api1Config.apiKey, api1Config.apiUrl, api1Config.modelName, false)
                        } else {
                            // 禁用API2，自动启用API1
                            onApi2Change(key, url, model, false)
                            onApi1Change(api1Config.apiKey, api1Config.apiUrl, api1Config.modelName, true)
                        }
                    }
                },
                onTestResult = onTestResult
            )
            
            // 在卡片之间添加间距，但不在最后一个卡片后添加
            if (index < cardData.size - 1) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TranslationApiCard_7ree(
    title: String,
    config: TranslationApiConfig_7ree,
    onConfigChange: (String, String, String, Boolean) -> Unit,
    onTestResult: (Boolean, String) -> Unit
) {
    var apiKey by remember { mutableStateOf(config.apiKey) }
    var apiUrl by remember { mutableStateOf(config.apiUrl) }
    var modelName by remember { mutableStateOf(config.modelName) }
    var isEnabled by remember { mutableStateOf(config.isEnabled) }
    
    // 当配置更新时，同步到输入框
    LaunchedEffect(config) {
        apiKey = config.apiKey
        apiUrl = config.apiUrl
        modelName = config.modelName
        isEnabled = config.isEnabled
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和启用开关
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "启用",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { enabled ->
                            isEnabled = enabled
                            onConfigChange(apiKey, apiUrl, modelName, enabled)
                        }
                    )
                }
            }
            
            // 只有启用时才显示参数配置项目
            if (isEnabled) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // API配置输入框
                OutlinedTextField(
                    value = apiUrl,
                    onValueChange = { 
                        apiUrl = it
                        onConfigChange(apiKey, it, modelName, isEnabled)
                    },
                    label = { Text("API URL") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { 
                        apiKey = it
                        onConfigChange(it, apiUrl, modelName, isEnabled)
                    },
                    label = { Text("API Key") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 0.sp)
                )
                
                OutlinedTextField(
                    value = modelName,
                    onValueChange = { 
                        modelName = it
                        onConfigChange(apiKey, apiUrl, it, isEnabled)
                    },
                    label = { Text("模型名称") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    singleLine = true
                )
                
                // 测试按钮
                TranslationApiTestButton_7ree(
                    apiConfig = ApiConfig_7ree(
                        translationApi1 = TranslationApiConfig_7ree(
                            apiKey = apiKey,
                            apiUrl = apiUrl,
                            modelName = modelName,
                            isEnabled = true
                        )
                    ),
                    onResult = onTestResult
                )
            }
        }
    }
}