package com.x7ree.wordcard.ui.DashBoard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.config.ApiConfig_7ree

@Composable
fun TranslationApiSection_7ree(
    apiKey: String,
    apiUrl: String,
    modelName: String,
    onApiKeyChange: (String) -> Unit,
    onApiUrlChange: (String) -> Unit,
    onModelNameChange: (String) -> Unit,
    apiConfig: ApiConfig_7ree,
    onTestResult: (Boolean, String) -> Unit
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
        Text(
            text = "AI大模型翻译API参数",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        OutlinedTextField(
            value = apiUrl,
            onValueChange = onApiUrlChange,
            label = { Text("API URL") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true
        )
        
        OutlinedTextField(
            value = apiKey,
            onValueChange = onApiKeyChange,
            label = { Text("API Key") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 0.sp)
        )
        
        OutlinedTextField(
            value = modelName,
            onValueChange = onModelNameChange,
            label = { Text("模型名称") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            singleLine = true
        )
        
        // 翻译API测试按钮
        TranslationApiTestButton_7ree(
            apiConfig = ApiConfig_7ree(
                apiKey = apiKey,
                apiUrl = apiUrl,
                modelName = modelName,
                azureRegion = apiConfig.azureRegion,
                azureApiKey = apiConfig.azureApiKey,
                azureSpeechRegion = apiConfig.azureSpeechRegion,
                azureSpeechApiKey = apiConfig.azureSpeechApiKey,
                azureSpeechEndpoint = apiConfig.azureSpeechEndpoint
            ),
            onResult = onTestResult
        )
    }
}