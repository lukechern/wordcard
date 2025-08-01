package com.x7ree.wordcard.ui.DashBoard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.config.ApiConfig_7ree

@Composable
fun SpeechApiSection_7ree(
    azureSpeechRegion: String,
    azureSpeechApiKey: String,
    azureSpeechVoice: String,
    onRegionChange: (String) -> Unit,
    onApiKeyChange: (String) -> Unit,
    onVoiceChange: (String) -> Unit,
    apiConfig: ApiConfig_7ree,
    onTestResult: (Boolean, String) -> Unit,
    onAutoSave: () -> Unit = {}
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
            text = "微软Azure Speech API",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        OutlinedTextField(
            value = azureSpeechRegion,
            onValueChange = onRegionChange,
            label = { Text("位置/区域 *") },
            placeholder = { Text("例如: eastasia, eastus, westus2") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true
        )
        
        // 添加说明文字
        Text(
            text = "💡 系统会根据区域自动生成正确的终结点，无需手动配置",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // 密钥输入框（带显示/隐藏功能）
        var isSpeechKeyVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = azureSpeechApiKey,
            onValueChange = onApiKeyChange,
            label = { Text("密钥") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            visualTransformation = if (isSpeechKeyVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 0.sp),
            trailingIcon = {
                IconButton(onClick = { isSpeechKeyVisible = !isSpeechKeyVisible }) {
                    Icon(
                        imageVector = if (isSpeechKeyVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (isSpeechKeyVisible) "隐藏密钥" else "显示密钥"
                    )
                }
            }
        )
        
        // 音色选择下拉框
        VoiceSelectionDropdown_7ree(
            selectedVoice = azureSpeechVoice,
            onVoiceSelected = onVoiceChange,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        
        // Speech API测试按钮
        SpeechApiTestButton_7ree(
            apiConfig = apiConfig,
            onResult = onTestResult,
            onAutoSave = onAutoSave
        )
    }
}