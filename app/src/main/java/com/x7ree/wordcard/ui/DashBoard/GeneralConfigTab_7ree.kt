package com.x7ree.wordcard.ui.DashBoard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree

@Composable
fun GeneralConfigTab_7ree(
    @Suppress("UNUSED_PARAMETER") wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    selectedKeyboardType_7ree: String,
    onKeyboardTypeChange: (String) -> Unit,
    autoReadAfterQuery_7ree: Boolean,
    onAutoReadAfterQueryChange: (Boolean) -> Unit,
    autoReadOnSpellingCard_7ree: Boolean,
    onAutoReadOnSpellingCardChange: (Boolean) -> Unit,
    selectedTtsEngine_7ree: String,
    onTtsEngineChange: (String) -> Unit
) {
    // 添加调试日志
    LaunchedEffect(selectedKeyboardType_7ree, autoReadAfterQuery_7ree, autoReadOnSpellingCard_7ree, selectedTtsEngine_7ree) {
        // println("DEBUG: GeneralConfigTab UI状态更新 - 键盘类型: $selectedKeyboardType_7ree, 自动朗读查询后: $autoReadAfterQuery_7ree, 自动朗读拼写卡片: $autoReadOnSpellingCard_7ree, TTS引擎: $selectedTtsEngine_7ree")
    }
    
    
    
    val scrollState = rememberScrollState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(end = 8.dp) // 为滚动条留出空间
        ) {
        Text(
            text = "通用设置",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 键盘设置区域
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
                .padding(bottom = 0.dp)
        ) {
            Text(
                text = "单词输入键盘",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = (selectedKeyboardType_7ree == "custom"),
                            onClick = {
                                onKeyboardTypeChange("custom")
                            },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedKeyboardType_7ree == "custom"),
                        onClick = null
                    )
                    Text(
                        text = "App自带键盘",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = (selectedKeyboardType_7ree == "system"),
                            onClick = {
                                onKeyboardTypeChange("system")
                            },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedKeyboardType_7ree == "system"),
                        onClick = null
                    )
                    Text(
                        text = "手机系统键盘",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 朗读设置区域
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
                text = "朗读设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // 单词查询完成自动朗读开关
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "单词查询完成自动朗读",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = autoReadAfterQuery_7ree,
                    onCheckedChange = onAutoReadAfterQueryChange
                )
            }
            
            // 拼写单词卡片打开自动朗读开关
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "拼写卡片打开自动朗读",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = autoReadOnSpellingCard_7ree,
                    onCheckedChange = onAutoReadOnSpellingCardChange
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 朗读TTS引擎设置区域
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
                .padding(bottom = 0.dp)
        ) {
            Text(
                text = "朗读TTS引擎",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (selectedTtsEngine_7ree == "google"),
                            onClick = {
                                onTtsEngineChange("google")
                            },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedTtsEngine_7ree == "google"),
                        onClick = null
                    )
                    Text(
                        text = "本手机Google TTS引擎",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (selectedTtsEngine_7ree == "azure"),
                            onClick = {
                                onTtsEngineChange("azure")
                            },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedTtsEngine_7ree == "azure"),
                        onClick = null
                    )
                    Text(
                        text = "微软Azure Speech API",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
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
                        color = Color.Gray.copy(alpha = 0.11f),
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
