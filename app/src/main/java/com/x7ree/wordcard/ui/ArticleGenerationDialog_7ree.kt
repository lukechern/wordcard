package com.x7ree.wordcard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun ArticleGenerationDialog_7ree(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onGenerate: (String) -> Unit,
    onSmartGenerate: (SmartGenerationType_7ree) -> Unit = {},
    onSmartGenerateWithKeywords: (SmartGenerationType_7ree, String) -> Unit = { _, _ -> },
    isGenerating: Boolean = false
) {
    if (isVisible) {
        var keyWords by remember { mutableStateOf("") }
        var showManualInput by remember { mutableStateOf(false) }
        
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
                        .padding(20.dp)
                ) {
                    Text(
                        text = "智能生成文章",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 20.dp),
                        textAlign = TextAlign.Center
                    )
                    
                    if (!showManualInput) {
                        // 智能选词按钮网格
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // 第一行：低查阅、低引用
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SmartGenerationButton_7ree(
                                    title = "低查阅",
                                    subtitle = "查阅量最少",
                                    icon = Icons.Default.Visibility,
                                    color = MaterialTheme.colorScheme.primary,
                                    enabled = !isGenerating,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    onSmartGenerate(SmartGenerationType_7ree.LOW_VIEW_COUNT)
                                }
                                
                                SmartGenerationButton_7ree(
                                    title = "低引用",
                                    subtitle = "引用次数最少",
                                    icon = Icons.AutoMirrored.Filled.Article,
                                    color = MaterialTheme.colorScheme.secondary,
                                    enabled = !isGenerating,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    onSmartGenerate(SmartGenerationType_7ree.LOW_REFERENCE_COUNT)
                                }
                            }
                            
                            // 第二行：低拼写、新收录
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SmartGenerationButton_7ree(
                                    title = "低拼写",
                                    subtitle = "拼写练习最少",
                                    icon = Icons.Default.Spellcheck,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    enabled = !isGenerating,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    onSmartGenerate(SmartGenerationType_7ree.LOW_SPELLING_COUNT)
                                }
                                
                                SmartGenerationButton_7ree(
                                    title = "新收录",
                                    subtitle = "最新加入单词",
                                    icon = Icons.Default.NewReleases,
                                    color = MaterialTheme.colorScheme.primary,
                                    enabled = !isGenerating,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    onSmartGenerate(SmartGenerationType_7ree.NEWEST_WORDS)
                                }
                            }
                            
                            // 第三行：随机词、人工输入
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SmartGenerationButton_7ree(
                                    title = "随机词",
                                    subtitle = "随机选择单词",
                                    icon = Icons.Default.Shuffle,
                                    color = MaterialTheme.colorScheme.secondary,
                                    enabled = !isGenerating,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    onSmartGenerate(SmartGenerationType_7ree.RANDOM_WORDS)
                                }
                                
                                SmartGenerationButton_7ree(
                                    title = "人工输入",
                                    subtitle = "手动输入单词",
                                    icon = Icons.Default.Edit,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    enabled = !isGenerating,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    showManualInput = true
                                }
                            }
                        }
                    } else {
                        // 手动输入模式
                        OutlinedTextField(
                            value = keyWords,
                            onValueChange = { keyWords = it },
                            label = { Text("请输入关键词") },
                            placeholder = { Text("请输入关键词，多个词用逗号分隔") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            enabled = !isGenerating,
                            minLines = 2,
                            maxLines = 4
                        )
                        
                        // 添加提示小字
                        Text(
                            text = "中文或者英文关键词均可",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                onClick = { showManualInput = false },
                                enabled = !isGenerating
                            ) {
                                Text("返回")
                            }
                            
                            Button(
                                onClick = {
                                    if (keyWords.isNotBlank()) {
                                        onSmartGenerateWithKeywords(SmartGenerationType_7ree.MANUAL_INPUT, keyWords.trim())
                                    } else {
                                        onGenerate("")
                                    }
                                },
                                enabled = !isGenerating
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
                    
                    // 底部取消按钮
                    if (!showManualInput) {
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(
                            onClick = onDismiss,
                            enabled = !isGenerating,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("取消")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SmartGenerationButton_7ree(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        onClick = onClick,
        enabled = enabled
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

// 智能生成类型枚举
enum class SmartGenerationType_7ree {
    LOW_VIEW_COUNT,      // 低查阅
    LOW_REFERENCE_COUNT, // 低引用
    LOW_SPELLING_COUNT,  // 低拼写
    NEWEST_WORDS,        // 新收录
    RANDOM_WORDS,        // 随机词
    MANUAL_INPUT         // 人工输入
}
