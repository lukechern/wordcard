package com.x7ree.wordcard.ui.DashBoard

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree

@Composable
fun PromptConfigTab_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    queryPrompt_7ree: String,
    onQueryPromptChange: (String) -> Unit,
    outputTemplate_7ree: String,
    onOutputTemplateChange: (String) -> Unit
) {
    
    // 保存配置的函数
    fun saveConfig() {
        println("DEBUG: 保存提示词配置 - 查询提示词: $queryPrompt_7ree, 输出模板: $outputTemplate_7ree")
        wordQueryViewModel_7ree.savePromptConfig_7ree(
            queryPrompt = queryPrompt_7ree,
            outputTemplate = outputTemplate_7ree
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
                text = "查询提示词和输出模板配置",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "查询提示词",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = queryPrompt_7ree,
                onValueChange = onQueryPromptChange,
                label = { Text("查询提示词") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
            )
            
            Text(
                text = "输出模板",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = outputTemplate_7ree,
                onValueChange = onOutputTemplateChange,
                label = { Text("输出模板") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
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
