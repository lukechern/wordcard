package com.x7ree.wordcard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.DashBoard.SettingsTab_7ree
import com.x7ree.wordcard.ui.DashBoard.ConfigPage_7ree

/**
语言包定义

    'pl_dashboard_7r' => '仪表盘',
    'pl_under_construction_7r' => '建设中，请稍候',
    'pl_config_7r' => '配置',
    'pl_back_7r' => '返回',
**/

@Composable
fun DashboardScreen_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onImportFile_7ree: () -> Unit = {}
) {
    var showConfigPage_7ree by remember { mutableStateOf(false) }
    var selectedTab_7ree by remember { mutableStateOf(SettingsTab_7ree.API_CONFIG) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 标题栏：标题 + 图标
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp) // 与SearchBarComponent保持一致的高度
                .padding(horizontal = 16.dp, vertical = 8.dp), // 与SearchBarComponent保持一致的内部边距
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (showConfigPage_7ree) "配置" else "仪表盘",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            // 右上角图标：配置或返回
            IconButton(
                onClick = { 
                    if (showConfigPage_7ree) {
                        showConfigPage_7ree = false
                    } else {
                        showConfigPage_7ree = true
                    }
                }
            ) {
                Icon(
                    imageVector = if (showConfigPage_7ree) Icons.Filled.ArrowBack else Icons.Filled.Settings,
                    contentDescription = if (showConfigPage_7ree) "返回" else "配置",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // 页面内容
        if (showConfigPage_7ree) {
            // 配置页面
            ConfigPage_7ree(
                wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                onImportFile_7ree = onImportFile_7ree
            )
        } else {
            // 仪表盘页面 - 实际内容
            DashboardContent_7ree(wordQueryViewModel_7ree)
        }
    }
}