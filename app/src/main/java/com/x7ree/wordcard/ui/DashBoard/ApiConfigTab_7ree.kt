package com.x7ree.wordcard.ui.DashBoard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.DashBoard.components.TranslationApiSection_7ree
import com.x7ree.wordcard.ui.DashBoard.components.DualTranslationApiSection_7ree
import com.x7ree.wordcard.ui.DashBoard.components.SpeechApiSection_7ree

@Composable
fun ApiConfigTab_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree
) {
    val apiConfig_7ree by wordQueryViewModel_7ree.apiConfig_7ree.collectAsState()
    
    var azureSpeechRegion_7ree by remember { mutableStateOf(apiConfig_7ree.azureSpeechRegion) }
    var azureSpeechApiKey_7ree by remember { mutableStateOf(apiConfig_7ree.azureSpeechApiKey) }
    var azureSpeechEndpoint_7ree by remember { mutableStateOf(apiConfig_7ree.azureSpeechEndpoint) }
    var azureSpeechVoice_7ree by remember { mutableStateOf(apiConfig_7ree.azureSpeechVoice) }
    
    // 当配置更新时，同步到输入框
    LaunchedEffect(apiConfig_7ree) {
        azureSpeechRegion_7ree = apiConfig_7ree.azureSpeechRegion
        azureSpeechApiKey_7ree = apiConfig_7ree.azureSpeechApiKey
        azureSpeechEndpoint_7ree = apiConfig_7ree.azureSpeechEndpoint
        azureSpeechVoice_7ree = apiConfig_7ree.azureSpeechVoice
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
        DualTranslationApiSection_7ree(
            apiConfig = apiConfig_7ree,
            onApi1Change = { key, url, model, enabled ->
                // 保存API 1配置
                wordQueryViewModel_7ree.saveTranslationApiConfig_7ree(
                    key, url, model, enabled,
                    apiConfig_7ree.translationApi2.apiKey,
                    apiConfig_7ree.translationApi2.apiUrl,
                    apiConfig_7ree.translationApi2.modelName,
                    apiConfig_7ree.translationApi2.isEnabled
                )
            },
            onApi2Change = { key, url, model, enabled ->
                // 保存API 2配置
                wordQueryViewModel_7ree.saveTranslationApiConfig_7ree(
                    apiConfig_7ree.translationApi1.apiKey,
                    apiConfig_7ree.translationApi1.apiUrl,
                    apiConfig_7ree.translationApi1.modelName,
                    apiConfig_7ree.translationApi1.isEnabled,
                    key, url, model, enabled
                )
            },
            onTestResult = { success, message ->
                wordQueryViewModel_7ree.setOperationResult_7ree(
                    if (success) "✅ 翻译API测试: $message" else "❌ 翻译API测试: $message"
                )
            }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 微软Azure Speech API配置区域
        SpeechApiSection_7ree(
            azureSpeechRegion = azureSpeechRegion_7ree,
            azureSpeechApiKey = azureSpeechApiKey_7ree,
            azureSpeechVoice = azureSpeechVoice_7ree,
            onRegionChange = { azureSpeechRegion_7ree = it },
            onApiKeyChange = { azureSpeechApiKey_7ree = it },
            onVoiceChange = { azureSpeechVoice_7ree = it },
            apiConfig = apiConfig_7ree.copy(
                azureSpeechRegion = azureSpeechRegion_7ree,
                azureSpeechApiKey = azureSpeechApiKey_7ree,
                azureSpeechEndpoint = azureSpeechEndpoint_7ree,
                azureSpeechVoice = azureSpeechVoice_7ree
            ),
            onTestResult = { success, message ->
                wordQueryViewModel_7ree.setOperationResult_7ree(
                    if (success) "✅ Speech API测试: $message" else "❌ Speech API测试: $message"
                )
            },
            onAutoSave = {
                // 自动保存当前配置
                wordQueryViewModel_7ree.saveApiConfig_7ree(
                    apiConfig_7ree.getActiveTranslationApi().apiKey, 
                    apiConfig_7ree.getActiveTranslationApi().apiUrl, 
                    apiConfig_7ree.getActiveTranslationApi().modelName,
                    azureSpeechRegion_7ree,
                    azureSpeechApiKey_7ree,
                    azureSpeechEndpoint_7ree,
                    azureSpeechVoice_7ree
                )
            }
        )
        
        Button(
            onClick = {
                wordQueryViewModel_7ree.saveApiConfig_7ree(
                    apiConfig_7ree.getActiveTranslationApi().apiKey, 
                    apiConfig_7ree.getActiveTranslationApi().apiUrl, 
                    apiConfig_7ree.getActiveTranslationApi().modelName,
                    azureSpeechRegion_7ree,
                    azureSpeechApiKey_7ree,
                    azureSpeechEndpoint_7ree,
                    azureSpeechVoice_7ree
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