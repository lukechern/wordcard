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
    
    
    Column(
        modifier = Modifier.fillMaxWidth()
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
}
