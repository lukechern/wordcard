package com.x7ree.wordcard.ui.DashBoard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree

@Composable
fun ApiConfigTab_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree
) {
    val apiConfig_7ree by wordQueryViewModel_7ree.apiConfig_7ree.collectAsState()
    val operationResult_7ree by wordQueryViewModel_7ree.operationResult_7ree.collectAsState()
    
    var apiKey_7ree by remember { mutableStateOf(apiConfig_7ree.apiKey) }
    var apiUrl_7ree by remember { mutableStateOf(apiConfig_7ree.apiUrl) }
    var modelName_7ree by remember { mutableStateOf(apiConfig_7ree.modelName) }
    var azureSpeechRegion_7ree by remember { mutableStateOf(apiConfig_7ree.azureSpeechRegion) }
    var azureSpeechApiKey_7ree by remember { mutableStateOf(apiConfig_7ree.azureSpeechApiKey) }
    var azureSpeechEndpoint_7ree by remember { mutableStateOf(apiConfig_7ree.azureSpeechEndpoint) }
    
    // 当配置更新时，同步到输入框
    LaunchedEffect(apiConfig_7ree) {
        apiKey_7ree = apiConfig_7ree.apiKey
        apiUrl_7ree = apiConfig_7ree.apiUrl
        modelName_7ree = apiConfig_7ree.modelName
        azureSpeechRegion_7ree = apiConfig_7ree.azureSpeechRegion
        azureSpeechApiKey_7ree = apiConfig_7ree.azureSpeechApiKey
        azureSpeechEndpoint_7ree = apiConfig_7ree.azureSpeechEndpoint
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "API配置",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // AI大模型翻译API参数配置区域
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
                value = apiUrl_7ree,
                onValueChange = { apiUrl_7ree = it },
                label = { Text("API URL") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true
            )
            
            OutlinedTextField(
                value = apiKey_7ree,
                onValueChange = { apiKey_7ree = it },
                label = { Text("API Key") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 0.sp)
            )
            
            OutlinedTextField(
                value = modelName_7ree,
                onValueChange = { modelName_7ree = it },
                label = { Text("模型名称") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 微软Azure Speech API配置区域
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
                text = "微软Azure Speech API",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            OutlinedTextField(
                value = azureSpeechEndpoint_7ree,
                onValueChange = { azureSpeechEndpoint_7ree = it },
                label = { Text("终结点") },
                placeholder = { Text("例如: https://eastus.tts.speech.microsoft.com/") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true
            )
            
            OutlinedTextField(
                value = azureSpeechRegion_7ree,
                onValueChange = { azureSpeechRegion_7ree = it },
                label = { Text("位置/区域") },
                placeholder = { Text("例如: eastus, westus2") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true
            )
            
            OutlinedTextField(
                value = azureSpeechApiKey_7ree,
                onValueChange = { azureSpeechApiKey_7ree = it },
                label = { Text("密钥") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 0.sp)
            )
        }
        
        Button(
            onClick = {
                wordQueryViewModel_7ree.saveApiConfig_7ree(
                    apiKey_7ree, 
                    apiUrl_7ree, 
                    modelName_7ree,
                    azureSpeechRegion_7ree,
                    azureSpeechApiKey_7ree,
                    azureSpeechEndpoint_7ree
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("保存配置")
        }
    }
}