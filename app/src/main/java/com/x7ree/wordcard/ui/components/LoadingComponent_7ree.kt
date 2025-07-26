package com.x7ree.wordcard.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.config.AppConfigManager_7ree

// 加载状态组件
@Composable
fun LoadingComponent_7ree(
    wordInput: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configManager = AppConfigManager_7ree(context)
    val apiConfig = configManager.loadApiConfig_7ree()
    val activeApi = apiConfig.getActiveTranslationApi()
    val activeApiName = if (activeApi.apiName.isNotEmpty()) activeApi.apiName else ""
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // 显示正在查询的单词
        Text(
            text = wordInput,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (activeApiName.isNotEmpty()) "正在问$activeApiName,请稍候…" else "正在问AI,请稍候…",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
