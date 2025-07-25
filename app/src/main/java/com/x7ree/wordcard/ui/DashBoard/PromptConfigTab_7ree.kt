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
    wordQueryViewModel_7ree: WordQueryViewModel_7ree
) {
    val promptConfig_7ree by wordQueryViewModel_7ree.promptConfig_7ree.collectAsState()
    
    var queryPrompt_7ree by remember { mutableStateOf(promptConfig_7ree.queryPrompt_7ree) }
    var outputTemplate_7ree by remember { mutableStateOf(promptConfig_7ree.outputTemplate_7ree) }
    
    // 当配置更新时，同步到输入框
    LaunchedEffect(promptConfig_7ree) {
        queryPrompt_7ree = promptConfig_7ree.queryPrompt_7ree
        outputTemplate_7ree = promptConfig_7ree.outputTemplate_7ree
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
            onValueChange = { queryPrompt_7ree = it },
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
            onValueChange = { outputTemplate_7ree = it },
            label = { Text("输出模板") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 16.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
        )
    }
}
