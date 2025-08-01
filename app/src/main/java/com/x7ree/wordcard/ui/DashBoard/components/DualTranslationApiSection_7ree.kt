package com.x7ree.wordcard.ui.DashBoard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
    onApi1Change: (String, String, String, String, Boolean) -> Unit, // 添加apiName参数
    onApi2Change: (String, String, String, String, Boolean) -> Unit, // 添加apiName参数
    onTestResult: (Boolean, String) -> Unit
) {
    // 使用remember和mutableStateOf来管理选中状态，只在初始化时设置，避免编辑时自动切换
    var selectedApi by remember {
        mutableStateOf(
            when {
                apiConfig.translationApi1.isEnabled && !apiConfig.translationApi2.isEnabled -> 1
                !apiConfig.translationApi1.isEnabled && apiConfig.translationApi2.isEnabled -> 2
                apiConfig.translationApi1.isEnabled && apiConfig.translationApi2.isEnabled -> 1 // 两个都启用时优先选择API1
                else -> 1 // 两个都禁用时默认选择API1
            }
        )
    }
    
    // 当配置的启用状态发生变化时，同步更新selectedApi
    LaunchedEffect(apiConfig.translationApi1.isEnabled, apiConfig.translationApi2.isEnabled) {
        val newSelectedApi = when {
            apiConfig.translationApi1.isEnabled && !apiConfig.translationApi2.isEnabled -> 1
            !apiConfig.translationApi1.isEnabled && apiConfig.translationApi2.isEnabled -> 2
            apiConfig.translationApi1.isEnabled && apiConfig.translationApi2.isEnabled -> 1 // 两个都启用时优先选择API1
            else -> 1 // 两个都禁用时默认选择API1
        }
        selectedApi = newSelectedApi
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
                        selectedApi = 1
                        // 更新启用状态：启用API1，禁用API2
                        onApi1Change(apiConfig.translationApi1.apiName, apiConfig.translationApi1.apiKey, apiConfig.translationApi1.apiUrl, apiConfig.translationApi1.modelName, true)
                        onApi2Change(apiConfig.translationApi2.apiName, apiConfig.translationApi2.apiKey, apiConfig.translationApi2.apiUrl, apiConfig.translationApi2.modelName, false)
                    }
                )
                    Text(
                        text = if (apiConfig.translationApi1.apiName.isNotBlank()) apiConfig.translationApi1.apiName else "翻译API(一)",
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
                        selectedApi = 2
                        // 更新启用状态：禁用API1，启用API2
                        onApi1Change(apiConfig.translationApi1.apiName, apiConfig.translationApi1.apiKey, apiConfig.translationApi1.apiUrl, apiConfig.translationApi1.modelName, false)
                        onApi2Change(apiConfig.translationApi2.apiName, apiConfig.translationApi2.apiKey, apiConfig.translationApi2.apiUrl, apiConfig.translationApi2.modelName, true)
                    }
                )
                    Text(
                        text = if (apiConfig.translationApi2.apiName.isNotBlank()) apiConfig.translationApi2.apiName else "翻译API(二)",
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
                    onConfigChange = { apiName, apiKey, apiUrl, modelName ->
                        // 保持当前API1的启用状态不变
                        onApi1Change(apiName, apiKey, apiUrl, modelName, apiConfig.translationApi1.isEnabled)
                    },
                    onTestResult = onTestResult,
                    apiLabel = "(一)"
                )
            } else {
                TranslationApiConfigSection_7ree(
                    config = apiConfig.translationApi2,
                    onConfigChange = { apiName, apiKey, apiUrl, modelName ->
                        // 保持当前API2的启用状态不变
                        onApi2Change(apiName, apiKey, apiUrl, modelName, apiConfig.translationApi2.isEnabled)
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
    onConfigChange: (String, String, String, String) -> Unit, // 添加apiName参数
    onTestResult: (Boolean, String) -> Unit,
    apiLabel: String
) {
    // 直接使用传入的config，不再维护本地状态
    val apiName = config.apiName
    val apiKey = config.apiKey
    val apiUrl = config.apiUrl
    val modelName = config.modelName
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // API名称输入框 - 放在最上面
        OutlinedTextField(
            value = apiName,
            onValueChange = { 
                onConfigChange(it, apiKey, apiUrl, modelName)
            },
            label = { Text("API名称") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true,
            placeholder = { Text("例如：OpenAI GPT-4、Claude-3、通义千问等") }
        )
        
        // API配置输入框
        OutlinedTextField(
            value = apiUrl,
            onValueChange = { 
                onConfigChange(apiName, apiKey, it, modelName)
            },
            label = { Text("API URL") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true
        )
        
        // API Key输入框（带显示/隐藏功能）
        var isApiKeyVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = apiKey,
            onValueChange = { 
                onConfigChange(apiName, it, apiUrl, modelName)
            },
            label = { Text("API Key") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            visualTransformation = if (isApiKeyVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 0.sp),
            trailingIcon = {
                IconButton(onClick = { isApiKeyVisible = !isApiKeyVisible }) {
                    Icon(
                        imageVector = if (isApiKeyVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (isApiKeyVisible) "隐藏API Key" else "显示API Key"
                    )
                }
            }
        )
        
        OutlinedTextField(
            value = modelName,
            onValueChange = { 
                onConfigChange(apiName, apiKey, apiUrl, it)
            },
            label = { Text("模型名称") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            singleLine = true
        )
        
        // 测试按钮
        TranslationApiTestButton_7ree(
            apiConfig = ApiConfig_7ree(
                translationApi1 = TranslationApiConfig_7ree(
                    apiName = apiName,
                    apiKey = apiKey,
                    apiUrl = apiUrl,
                    modelName = modelName,
                    isEnabled = true
                )
            ),
            onResult = onTestResult,
            buttonText = if (apiName.isNotBlank()) "测试${apiName}" else "测试翻译API$apiLabel"
        )
    }
}
