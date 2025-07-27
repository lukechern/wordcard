package com.x7ree.wordcard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun SmartGenerationProgressCard_7ree(
    isVisible: Boolean,
    status: String,
    keywords: List<String>,
    onDismiss: () -> Unit,
    currentSmartGenerationType: com.x7ree.wordcard.ui.SmartGenerationType_7ree? = null
) {
    if (isVisible) {
        Dialog(onDismissRequest = { /* 不允许通过点击外部关闭 */ }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 标题
                    Text(
                        text = "AI生成文章",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    // 类型（小一号字）
                    val typeText = when (currentSmartGenerationType) {
                        com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_VIEW_COUNT -> "低查询单词"
                        com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_REFERENCE_COUNT -> "低引用单词"
                        com.x7ree.wordcard.ui.SmartGenerationType_7ree.LOW_SPELLING_COUNT -> "低拼写单词"
                        com.x7ree.wordcard.ui.SmartGenerationType_7ree.NEWEST_WORDS -> "新收录单词"
                        com.x7ree.wordcard.ui.SmartGenerationType_7ree.RANDOM_WORDS -> "随机单词"
                        else -> ""
                    }
                    
                    if (typeText.isNotEmpty()) {
                        Text(
                            text = typeText,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.padding(bottom = 16.dp))
                    }
                    
                    // 状态信息（支持多行显示）
                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // 完成按钮（仅在生成完成后显示）
                    if (status.contains("文章已生成") || status.contains("失败")) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "完成",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("知道了")
                        }
                    } else {
                        // 进度条（生成过程中显示）
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
