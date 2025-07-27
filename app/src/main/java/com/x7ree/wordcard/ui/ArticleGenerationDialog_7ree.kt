package com.x7ree.wordcard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun ArticleGenerationDialog_7ree(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onGenerate: (String) -> Unit,
    isGenerating: Boolean = false
) {
    if (isVisible) {
        var keyWords by remember { mutableStateOf("") }
        
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "生成文章",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    OutlinedTextField(
                        value = keyWords,
                        onValueChange = { keyWords = it },
                        label = { Text("关键词") },
                        placeholder = { Text("请输入关键词，多个词用逗号分隔") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        enabled = !isGenerating,
                        minLines = 2,
                        maxLines = 4
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            enabled = !isGenerating
                        ) {
                            Text("取消")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                if (keyWords.isNotBlank()) {
                                    onGenerate(keyWords.trim())
                                }
                            },
                            enabled = !isGenerating && keyWords.isNotBlank()
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(if (isGenerating) "生成中..." else "生成")
                        }
                    }
                }
            }
        }
    }
}