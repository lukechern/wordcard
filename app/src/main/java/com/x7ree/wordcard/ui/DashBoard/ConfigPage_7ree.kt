package com.x7ree.wordcard.ui.DashBoard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.HelpScreen_7ree

@Composable
fun ConfigPage_7ree(
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
                text = { Text("帮助", fontSize = 13.sp) },
                modifier = Modifier.padding(0.dp)
            )
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.GENERAL,
                onClick = { selectedTab_7ree = SettingsTab_7ree.GENERAL },
                text = { Text("通用", fontSize = 13.sp) },
                modifier = Modifier.padding(0.dp)
            )
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.API_CONFIG,
                onClick = { selectedTab_7ree = SettingsTab_7ree.API_CONFIG },
                text = { Text("API", fontSize = 13.sp) },
                modifier = Modifier.padding(0.dp)
            )
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.PROMPT_CONFIG,
                onClick = { selectedTab_7ree = SettingsTab_7ree.PROMPT_CONFIG },
                text = { Text("提示词", fontSize = 13.sp) },
                modifier = Modifier.padding(0.dp)
            )
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.DATA_MANAGEMENT,
                onClick = { selectedTab_7ree = SettingsTab_7ree.DATA_MANAGEMENT },
                text = { Text("数据", fontSize = 13.sp) },
                modifier = Modifier.padding(0.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab_7ree) {
                SettingsTab_7ree.HELP -> {
                    HelpScreen_7ree()
                }
                SettingsTab_7ree.GENERAL -> {
                    GeneralConfigTab_7ree(wordQueryViewModel_7ree)
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
        
        // 只有在需要保存按钮的标签页才显示保存按钮
        if (selectedTab_7ree == SettingsTab_7ree.GENERAL || 
            selectedTab_7ree == SettingsTab_7ree.API_CONFIG || 
            selectedTab_7ree == SettingsTab_7ree.PROMPT_CONFIG) {
            Button(
                onClick = {
                    // 根据当前标签页调用相应的保存方法
                    when (selectedTab_7ree) {
                        SettingsTab_7ree.GENERAL -> {
                            // 通用配置保存需要从ViewModel获取当前状态
                            wordQueryViewModel_7ree.saveCurrentGeneralConfig_7ree()
                        }
                        SettingsTab_7ree.API_CONFIG -> {
                            // API配置保存需要从ViewModel获取当前状态
                            wordQueryViewModel_7ree.saveCurrentApiConfig_7ree()
                        }
                        SettingsTab_7ree.PROMPT_CONFIG -> {
                            // 提示词配置保存需要从ViewModel获取当前状态
                            wordQueryViewModel_7ree.saveCurrentPromptConfig_7ree()
                        }
                        else -> {}
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("保存配置")
            }
        }
    }
}
