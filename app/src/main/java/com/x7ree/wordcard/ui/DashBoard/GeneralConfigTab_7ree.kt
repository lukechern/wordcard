package com.x7ree.wordcard.ui.DashBoard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree

@Composable
fun GeneralConfigTab_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree
) {
    val generalConfig_7ree by wordQueryViewModel_7ree.generalConfig_7ree.collectAsState()
    val operationResult_7ree by wordQueryViewModel_7ree.operationResult_7ree.collectAsState()
    
    var selectedKeyboardType_7ree by remember { mutableStateOf(generalConfig_7ree.keyboardType) }
    
    // 当配置更新时，同步到选择状态
    LaunchedEffect(generalConfig_7ree) {
        selectedKeyboardType_7ree = generalConfig_7ree.keyboardType
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "通用设置",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "单词输入键盘",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Column(
                modifier = Modifier.selectableGroup()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (selectedKeyboardType_7ree == "custom"),
                            onClick = {
                                selectedKeyboardType_7ree = "custom"
                            },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
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
                        .fillMaxWidth()
                        .selectable(
                            selected = (selectedKeyboardType_7ree == "system"),
                            onClick = {
                                selectedKeyboardType_7ree = "system"
                            },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
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
            
            Button(
                onClick = {
                    wordQueryViewModel_7ree.saveGeneralConfig_7ree(selectedKeyboardType_7ree)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("保存配置")
            }
        }
        
        // 移除重复的绿色提示条，使用统一的黑色提示条
    }
}