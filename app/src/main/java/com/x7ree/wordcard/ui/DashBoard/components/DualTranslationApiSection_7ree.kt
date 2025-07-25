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
    // 使用remember和mutableStateOf来管理选中状态，确保UI能正确响应状态变化
    var selectedApi by remember(apiConfig.translationApi1.isEnabled, apiConfig.translationApi2.isEnabled) {
        mutableStateOf(
            when {
                apiConfig.translationApi1.isEnabled && !apiConfig.translationApi2.isEnabled -> 1
                !apiConfig.translationApi1.isEnabled && apiConfig.translationApi2.isEnabled -> 2
                apiConfig.translationApi1.isEnabled && apiConfig.translationApi2.isEnabled -> 1 // 两个都启用时优先选择API1
                else -> 1 // 两个都禁用时默认选择API1
            }
        )
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
            Text(
                text = "AI大模型翻译API配置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Radio选择区域
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 翻译API(一)选项
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedApi == 1,
                        onClick = {
                            // 立即更新本地状态
                            selectedApi = 1
                            // 执行切换到API1的操作
                            onApi1Change(
                                apiConfig.translationApi1.apiKey,
                                apiConfig.translationApi1.apiUrl,
                                apiConfig.translationApi1.modelName,
                                true
                            )
                            onApi2Change(
                                apiConfig.translationApi2.apiKey,
                                apiConfig.translationApi2.apiUrl,
                                apiConfig.translationApi2.modelName,
                                false
                            )
                        }
                    )
                    Text(
                        text = "翻译API(一)",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                
                // 翻译API(二)选项
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedApi == 2,
                        onClick = {
                            // 立即更新本地状态
                            selectedApi = 2
                            // 执行切换到API2的操作
                            onApi1Change(
                                apiConfig.translationApi1.apiKey,
                                apiConfig.translationApi1.apiUrl,
                                apiConfig.translationApi1.modelName,
                                false
                            )
                            onApi2Change(
                                apiConfig.translationApi2.apiKey,
                                apiConfig.translationApi2.apiUrl,
                                apiConfig.translationApi2.modelName,
                                true
                            )
                        }
                    )
                    Text(
                        text = "翻译API(二)",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 根据选择显示对应的API配置
            if (selectedApi == 1) {
                TranslationApiConfigSection_7ree(
                    config = apiConfig.translationApi1,
                    onConfigChange = { key, url, model ->
                        onApi1Change(key, url, model, true)
                    },
                    onTestResult = onTestResult,
                    apiLabel = "(一)"
                )
            } else {
                TranslationApiConfigSection_7ree(
                    config = apiConfig.translationApi2,
                    onConfigChange = { key, url, model ->
                        onApi2Change(key, url, model, true)
                    },
                    onTestResult = onTestResult,
                    apiLabel = "(二)"
                )
            }
        }
    }
}

@Composable
private fun TranslationApiConfigSection_7ree(
    config: TranslationApiConfig_7ree,
    onConfigChange: (String, String, String) -> Unit,
    onTestResult: (Boolean, String) -> Unit,
    apiLabel: String
) {
    var apiKey by remember { mutableStateOf(config.apiKey) }
    var apiUrl by remember { mutableStateOf(config.apiUrl) }
    var modelName by remember { mutableStateOf(config.modelName) }
    
    // 当配置更新时，同步到输入框
    LaunchedEffect(config.apiKey, config.apiUrl, config.modelName) {
        apiKey = config.apiKey
        apiUrl = config.apiUrl
        modelName = config.modelName
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // API配置输入框
        OutlinedTextField(
            value = apiUrl,
            onValueChange = { 
                apiUrl = it
                onConfigChange(apiKey, it, modelName)
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
                onConfigChange(it, apiUrl, modelName)
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
                onConfigChange(apiKey, apiUrl, it)
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
            onResult = onTestResult,
            buttonText = "测试翻译API$apiLabel"
        )
    }
}