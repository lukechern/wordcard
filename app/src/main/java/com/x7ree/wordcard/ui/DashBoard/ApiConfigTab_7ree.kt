package com.x7ree.wordcard.ui.DashBoard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.DashBoard.components.TranslationApiSection_7ree
import com.x7ree.wordcard.ui.DashBoard.components.DualTranslationApiSection_7ree
import com.x7ree.wordcard.ui.DashBoard.components.SpeechApiSection_7ree

@Composable
fun ApiConfigTab_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    localApi1Name_7ree: String,
    onApi1NameChange: (String) -> Unit,
    localApi1Key_7ree: String,
    onApi1KeyChange: (String) -> Unit,
    localApi1Url_7ree: String,
    onApi1UrlChange: (String) -> Unit,
    localApi1Model_7ree: String,
    onApi1ModelChange: (String) -> Unit,
    localApi1Enabled_7ree: Boolean,
    onApi1EnabledChange: (Boolean) -> Unit,
    localApi2Name_7ree: String,
    onApi2NameChange: (String) -> Unit,
    localApi2Key_7ree: String,
    onApi2KeyChange: (String) -> Unit,
    localApi2Url_7ree: String,
    onApi2UrlChange: (String) -> Unit,
    localApi2Model_7ree: String,
    onApi2ModelChange: (String) -> Unit,
    localApi2Enabled_7ree: Boolean,
    onApi2EnabledChange: (Boolean) -> Unit,
    azureSpeechRegion_7ree: String,
    onAzureSpeechRegionChange: (String) -> Unit,
    azureSpeechApiKey_7ree: String,
    onAzureSpeechApiKeyChange: (String) -> Unit,
    azureSpeechEndpoint_7ree: String,
    @Suppress("UNUSED_PARAMETER") onAzureSpeechEndpointChange: (String) -> Unit,
    azureSpeechVoice_7ree: String,
    onAzureSpeechVoiceChange: (String) -> Unit
) {
    val apiConfig_7ree by wordQueryViewModel_7ree.apiConfig_7ree.collectAsState()
    
    // 保存配置的函数
    fun saveConfig() {
        // println("DEBUG: 保存API配置 - API1名称: $localApi1Name_7ree, API1密钥: $localApi1Key_7ree, API1 URL: $localApi1Url_7ree, API1模型: $localApi1Model_7ree, API1启用: $localApi1Enabled_7ree")
        // println("DEBUG: 保存API配置 - API2名称: $localApi2Name_7ree, API2密钥: $localApi2Key_7ree, API2 URL: $localApi2Url_7ree, API2模型: $localApi2Model_7ree, API2启用: $localApi2Enabled_7ree")
        // println("DEBUG: 保存API配置 - Azure区域: $azureSpeechRegion_7ree, Azure密钥: $azureSpeechApiKey_7ree, Azure端点: $azureSpeechEndpoint_7ree, Azure语音: $azureSpeechVoice_7ree")
        wordQueryViewModel_7ree.saveTranslationApiConfig_7ree(
            api1Name = localApi1Name_7ree,
            api1Key = localApi1Key_7ree,
            api1Url = localApi1Url_7ree,
            api1Model = localApi1Model_7ree,
            api1Enabled = localApi1Enabled_7ree,
            api2Name = localApi2Name_7ree,
            api2Key = localApi2Key_7ree,
            api2Url = localApi2Url_7ree,
            api2Model = localApi2Model_7ree,
            api2Enabled = localApi2Enabled_7ree
        )
        // 保存Azure Speech配置
        wordQueryViewModel_7ree.saveApiConfig_7ree(
            apiKey = apiConfig_7ree.getActiveTranslationApi().apiKey,
            apiUrl = apiConfig_7ree.getActiveTranslationApi().apiUrl,
            modelName = apiConfig_7ree.getActiveTranslationApi().modelName,
            azureSpeechRegion = azureSpeechRegion_7ree,
            azureSpeechApiKey = azureSpeechApiKey_7ree,
            azureSpeechEndpoint = azureSpeechEndpoint_7ree,
            azureSpeechVoice = azureSpeechVoice_7ree
        )
    }
    
    
    val scrollState = rememberScrollState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(end = 16.dp) // 为滚动条留出空间
        ) {
            Text(
                text = "API配置",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // AI大模型翻译API参数配置区域
            
            DualTranslationApiSection_7ree(
                apiConfig = apiConfig_7ree.copy(
                    translationApi1 = apiConfig_7ree.translationApi1.copy(
                        apiName = localApi1Name_7ree,
                        apiKey = localApi1Key_7ree,
                        apiUrl = localApi1Url_7ree,
                        modelName = localApi1Model_7ree,
                        isEnabled = localApi1Enabled_7ree
                    ),
                    translationApi2 = apiConfig_7ree.translationApi2.copy(
                        apiName = localApi2Name_7ree,
                        apiKey = localApi2Key_7ree,
                        apiUrl = localApi2Url_7ree,
                        modelName = localApi2Model_7ree,
                        isEnabled = localApi2Enabled_7ree
                    )
                ),
                onApi1Change = { apiName, key, url, model, enabled ->
                    // 使用传入的回调函数更新状态
                    onApi1NameChange(apiName)
                    onApi1KeyChange(key)
                    onApi1UrlChange(url)
                    onApi1ModelChange(model)
                    onApi1EnabledChange(enabled)
                },
                onApi2Change = { apiName, key, url, model, enabled ->
                    // 使用传入的回调函数更新状态
                    onApi2NameChange(apiName)
                    onApi2KeyChange(key)
                    onApi2UrlChange(url)
                    onApi2ModelChange(model)
                    onApi2EnabledChange(enabled)
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
                onRegionChange = onAzureSpeechRegionChange,
                onApiKeyChange = onAzureSpeechApiKeyChange,
                onVoiceChange = onAzureSpeechVoiceChange,
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
                    val activeApi = apiConfig_7ree.getActiveTranslationApi()
                    wordQueryViewModel_7ree.saveApiConfig_7ree(
                        activeApi.apiKey, 
                        activeApi.apiUrl, 
                        activeApi.modelName,
                        azureSpeechRegion_7ree,
                        azureSpeechApiKey_7ree,
                        azureSpeechEndpoint_7ree,
                        azureSpeechVoice_7ree
                    )
                }
            )
        }
        
        // Android 兼容的滚动指示器
        if (scrollState.maxValue > 0) {
            BoxWithConstraints(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight()
                    .width(12.dp)
                    .padding(end = 2.dp, top = 8.dp, bottom = 8.dp)
                    .background(
                        color = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.11f),
                        shape = RoundedCornerShape(3.dp)
                    )
            ) {
                val trackHeight = maxHeight
                
                // 计算可见区域与总内容的比例
                val viewportHeight = scrollState.viewportSize.toFloat()
                val contentHeight = scrollState.maxValue.toFloat() + viewportHeight
                val thumbHeightRatio = (viewportHeight / contentHeight).coerceIn(0.1f, 1f)
                
                // 计算滚动进度
                val scrollProgress = if (scrollState.maxValue > 0) {
                    scrollState.value.toFloat() / scrollState.maxValue.toFloat()
                } else 0f
                
                // 计算拇指位置 - 基于实际轨道高度
                val thumbHeight = trackHeight * thumbHeightRatio
                val availableSpace = trackHeight - thumbHeight
                val thumbOffset = availableSpace * scrollProgress
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(thumbHeight)
                        .offset(y = thumbOffset)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
        }
    }
}
