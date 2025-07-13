package com.x7ree.wordcard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
语言包定义

    'pl_search_word_7r' => '查单词',
    'pl_history_7r' => '历史',
    'pl_settings_7r' => '配置',
**/

// 定义统一的背景色，减少视觉干扰
private val NavigationBackgroundColor_7ree = Color(0xFFEAE2F3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar_7ree(
    currentScreen_7ree: Screen_7ree,
    onScreenSelected_7ree: (Screen_7ree) -> Unit,
    onSearchReset_7ree: () -> Unit
) {
    BottomAppBar(
        containerColor = NavigationBackgroundColor_7ree,
        tonalElevation = 0.dp // 移除阴影，进一步减少视觉干扰
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
        ) {
            // 自定义导航项，去掉hover效果
            CustomNavigationItem_7ree(
                icon = Icons.Filled.AutoAwesome,
                label = "查单词",
                isSelected = currentScreen_7ree == Screen_7ree.SEARCH,
                onClick = { 
                    onScreenSelected_7ree(Screen_7ree.SEARCH)
                    onSearchReset_7ree()
                }
            )
            
            CustomNavigationItem_7ree(
                icon = Icons.Filled.ListAlt,
                label = "历史",
                isSelected = currentScreen_7ree == Screen_7ree.HISTORY,
                onClick = { onScreenSelected_7ree(Screen_7ree.HISTORY) }
            )
            
            CustomNavigationItem_7ree(
                icon = Icons.Filled.Tune,
                label = "配置",
                isSelected = currentScreen_7ree == Screen_7ree.SETTINGS,
                onClick = { onScreenSelected_7ree(Screen_7ree.SETTINGS) }
            )
        }
    }
}

@Composable
private fun CustomNavigationItem_7ree(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource_7ree = remember { MutableInteractionSource() }
    
    Box(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource_7ree,
                indication = null // 去掉点击波纹效果
            ) { onClick() }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            // 使用固定高度的Box来确保图标垂直居中
            Box(
                modifier = Modifier.height(30.8.dp), // 使用最大图标高度
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(if (isSelected) 30.8.dp else 24.dp),
                    tint = if (isSelected) Color(0xFF6200EE) else Color(0xFF666666)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) Color(0xFF6200EE) else Color(0xFF666666)
            )
        }
    }
} 