package com.x7ree.wordcard.ui.DashBoard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
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
    wordQueryViewModel_7ree: WordQueryViewModel_7ree
) {
    val generalConfig_7ree by wordQueryViewModel_7ree.generalConfig_7ree.collectAsState()
    val operationResult_7ree by wordQueryViewModel_7ree.operationResult_7ree.collectAsState()
    
    var selectedKeyboardType_7ree by remember { mutableStateOf(generalConfig_7ree.keyboardType) }
    var autoReadAfterQuery_7ree by remember { mutableStateOf(generalConfig_7ree.autoReadAfterQuery) }
    var autoReadOnSpellingCard_7ree by remember { mutableStateOf(generalConfig_7ree.autoReadOnSpellingCard) }
    
    // 当配置更新时，同步到选择状态
    LaunchedEffect(generalConfig_7ree) {
        selectedKeyboardType_7ree = generalConfig_7ree.keyboardType
        autoReadAfterQuery_7ree = generalConfig_7ree.autoReadAfterQuery
        autoReadOnSpellingCard_7ree = generalConfig_7ree.autoReadOnSpellingCard
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
                                selectedKeyboardType_7ree = "custom"
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
                                selectedKeyboardType_7ree = "system"
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
                    onCheckedChange = { autoReadAfterQuery_7ree = it }
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
                    onCheckedChange = { autoReadOnSpellingCard_7ree = it }
                )
            }
        }
        
        Button(
            onClick = {
                wordQueryViewModel_7ree.saveGeneralConfig_7ree(
                    selectedKeyboardType_7ree,
                    autoReadAfterQuery_7ree,
                    autoReadOnSpellingCard_7ree
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("保存配置")
        }
    }
}