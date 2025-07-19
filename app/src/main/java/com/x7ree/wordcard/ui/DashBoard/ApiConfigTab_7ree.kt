package com.x7ree.wordcard.ui.DashBoard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    
    // 当配置更新时，同步到输入框
    LaunchedEffect(apiConfig_7ree) {
        apiKey_7ree = apiConfig_7ree.apiKey
        apiUrl_7ree = apiConfig_7ree.apiUrl
        modelName_7ree = apiConfig_7ree.modelName
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "AI大模型翻译API参数",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
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
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 0.sp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = modelName_7ree,
            onValueChange = { modelName_7ree = it },
            label = { Text("模型名称") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )
        
        Button(
            onClick = {
                wordQueryViewModel_7ree.saveApiConfig_7ree(apiKey_7ree, apiUrl_7ree, modelName_7ree)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("保存配置")
        }
    }
}