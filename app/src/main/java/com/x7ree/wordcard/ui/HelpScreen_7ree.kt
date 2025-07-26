package com.x7ree.wordcard.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.ui.help.*
import com.x7ree.wordcard.utils.AppVersionUtils_7ree

@Composable
fun HelpScreen_7ree() {
    val context = LocalContext.current
    val currentVersion = AppVersionUtils_7ree.getFormattedVersion(context)
    val scrollState = rememberScrollState()
    
    // 加载帮助内容
    val helpContent = loadHelpContentFromAssets(context)
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 32.dp) // 右侧留更多空间给滚动条
        ) {
            // 循环显示帮助内容
            for (item in helpContent.content) {
                RenderHelpItem(item, currentVersion)
            }
        }
        
        Box(modifier = Modifier.align(Alignment.TopEnd)) {
            ScrollIndicator_7ree(scrollState)
        }
    }
}
