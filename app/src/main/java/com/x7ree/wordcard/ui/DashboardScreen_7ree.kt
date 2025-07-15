package com.x7ree.wordcard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree

/**
语言包定义

    'pl_dashboard_7r' => '仪表盘',
    'pl_under_construction_7r' => '建设中，请稍候',
    'pl_config_7r' => '配置',
    'pl_back_7r' => '返回',
**/

enum class SettingsTab_7ree {
    HELP,
    API_CONFIG,
    PROMPT_CONFIG,
    DATA_MANAGEMENT
}

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
                .padding(bottom = 16.dp),
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



@Composable
private fun ConfigPage_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onImportFile_7ree: () -> Unit
) {
    var selectedTab_7ree by remember { mutableStateOf(SettingsTab_7ree.HELP) }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TabRow(selectedTabIndex = selectedTab_7ree.ordinal) {
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.HELP,
                onClick = { selectedTab_7ree = SettingsTab_7ree.HELP },
                text = { Text("帮助") }
            )
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.API_CONFIG,
                onClick = { selectedTab_7ree = SettingsTab_7ree.API_CONFIG },
                text = { Text("API") }
            )
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.PROMPT_CONFIG,
                onClick = { selectedTab_7ree = SettingsTab_7ree.PROMPT_CONFIG },
                text = { Text("提示词") }
            )
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.DATA_MANAGEMENT,
                onClick = { selectedTab_7ree = SettingsTab_7ree.DATA_MANAGEMENT },
                text = { Text("数据") }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (selectedTab_7ree) {
            SettingsTab_7ree.HELP -> {
                HelpScreen_7ree()
            }
            SettingsTab_7ree.API_CONFIG -> {
                ApiConfigTab_7ree(wordQueryViewModel_7ree)
            }
            SettingsTab_7ree.PROMPT_CONFIG -> {
                PromptConfigTab_7ree(wordQueryViewModel_7ree)
            }
            SettingsTab_7ree.DATA_MANAGEMENT -> {
                DataManagementTab_7ree(
                    wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                    onImportFile_7ree = onImportFile_7ree
                )
            }
        }
    }
}

@Composable
fun ApiConfigTab_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree
) {
    val apiConfig_7ree by wordQueryViewModel_7ree.apiConfig_7ree.collectAsState()
    val operationResult_7ree by wordQueryViewModel_7ree.operationResult_7ree.collectAsState()
    
    var apiKey_7ree by remember { mutableStateOf(apiConfig_7ree.apiKey) }
    var apiUrl_7ree by remember { mutableStateOf(apiConfig_7ree.apiUrl) }
    var modelName_7ree by remember { mutableStateOf(apiConfig_7ree.modelName) }
    
    // 当配置更新时，同步到输入框
    LaunchedEffect(apiConfig_7ree) {
        apiKey_7ree = apiConfig_7ree.apiKey
        apiUrl_7ree = apiConfig_7ree.apiUrl
        modelName_7ree = apiConfig_7ree.modelName
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "API连接参数配置",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = apiUrl_7ree,
            onValueChange = { apiUrl_7ree = it },
            label = { Text("API URL") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true
        )
        
        OutlinedTextField(
            value = apiKey_7ree,
            onValueChange = { apiKey_7ree = it },
            label = { Text("API Key") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 0.sp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = modelName_7ree,
            onValueChange = { modelName_7ree = it },
            label = { Text("模型名称") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )
        
        Button(
            onClick = {
                wordQueryViewModel_7ree.saveApiConfig_7ree(apiKey_7ree, apiUrl_7ree, modelName_7ree)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("保存配置")
        }
    }
}

@Composable
fun DataManagementTab_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onImportFile_7ree: () -> Unit = {}
) {
    val operationResult_7ree by wordQueryViewModel_7ree.operationResult_7ree.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "查询历史单词数据导出与导入",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "数据导出",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "将查询历史导出为JSON文件，包含单词、查询结果、时间戳等信息",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Button(
                    onClick = {
                        wordQueryViewModel_7ree.exportHistoryData_7ree()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = "导出",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("导出历史数据")
                }
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "数据导入",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "从JSON文件导入查询历史数据，支持批量恢复历史记录",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Button(
                    onClick = {
                        onImportFile_7ree()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Upload,
                        contentDescription = "导入",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("导入历史数据")
                }
            }
        }

        // 显示数据导出默认路径
        val exportPath_7ree by wordQueryViewModel_7ree.exportPath_7ree.collectAsState()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "数据将默认导出到: \n$exportPath_7ree",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Start
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PromptConfigTab_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree
) {
    val promptConfig_7ree by wordQueryViewModel_7ree.promptConfig_7ree.collectAsState()
    val operationResult_7ree by wordQueryViewModel_7ree.operationResult_7ree.collectAsState()
    
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
        
        Button(
            onClick = {
                wordQueryViewModel_7ree.savePromptConfig_7ree(queryPrompt_7ree, outputTemplate_7ree)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("保存配置")
        }
    }
}