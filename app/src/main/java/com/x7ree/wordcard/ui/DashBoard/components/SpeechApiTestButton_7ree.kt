package com.x7ree.wordcard.ui.DashBoard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.config.ApiConfig_7ree
import com.x7ree.wordcard.test.SpeechApiTester_7ree
import kotlinx.coroutines.launch

@Composable
fun SpeechApiTestButton_7ree(
    apiConfig: ApiConfig_7ree,
    onResult: (Boolean, String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    
    Column {
        OutlinedButton(
            onClick = {
                if (!isLoading) {
                    isLoading = true
                    testResult = null
                    coroutineScope.launch {
                        try {
                            val tester = SpeechApiTester_7ree(context)
                            tester.testSpeechApi(apiConfig) { success, message ->
                                testResult = Pair(success, message)
                                onResult(success, message)
                                isLoading = false
                            }
                        } catch (e: Exception) {
                            val errorMessage = "测试异常: ${e.message}"
                            testResult = Pair(false, errorMessage)
                            onResult(false, errorMessage)
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("测试中...")
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("测试Speech API")
                }
            }
        }
        
        // 显示测试结果
        testResult?.let { (success, message) ->
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (success) 
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    else 
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = if (success) "✅ $message" else "❌ $message",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (success) 
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else 
                        MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}