package com.x7ree.wordcard.ui.DashBoard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSelectionDropdown_7ree(
    selectedVoice: String,
    onVoiceSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    // 定义可用的音色选项
    val voiceOptions = listOf(
        VoiceOption_7ree("en-US-JennyNeural", "女性 - 美式英语", "Jenny"),
        VoiceOption_7ree("en-US-GuyNeural", "男性 - 美式英语", "Guy"),
        VoiceOption_7ree("en-GB-SoniaNeural", "女性 - 英式英语", "Sonia"),
        VoiceOption_7ree("en-GB-RyanNeural", "男性 - 英式英语", "Ryan"),
        VoiceOption_7ree("en-AU-NatashaNeural", "女性 - 澳式英语", "Natasha"),
        VoiceOption_7ree("en-AU-WilliamNeural", "男性 - 澳式英语", "William"),
        VoiceOption_7ree("zh-CN-XiaoxiaoNeural", "女性 - 中文普通话", "晓晓"),
        VoiceOption_7ree("zh-CN-YunxiNeural", "男性 - 中文普通话", "云希")
    )
    
    // 找到当前选择的音色选项
    val selectedOption = voiceOptions.find { it.value == selectedVoice } 
        ?: voiceOptions.first()
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption.displayName,
            onValueChange = { },
            readOnly = true,
            label = { Text("音色选择") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            voiceOptions.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = option.displayName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = option.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onVoiceSelected(option.value)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * 音色选项数据类
 */
data class VoiceOption_7ree(
    val value: String,
    val displayName: String,
    val description: String
)