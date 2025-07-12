package com.x7ree.wordcard.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
语言包定义

    'pl_search_word_7r' => '查单词',
    'pl_history_7r' => '历史',
    'pl_settings_7r' => '配置',
**/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar_7ree(
    currentScreen_7ree: Screen_7ree,
    onScreenSelected_7ree: (Screen_7ree) -> Unit,
    onSearchReset_7ree: () -> Unit
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { 
                Icon(
                    Icons.Filled.AutoAwesome, 
                    contentDescription = "查单词",
                    modifier = Modifier.size(if (currentScreen_7ree == Screen_7ree.SEARCH) 28.dp else 24.dp),
                    tint = if (currentScreen_7ree == Screen_7ree.SEARCH) Color(0xFF6200EE) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = { 
                Text(
                    "查单词",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (currentScreen_7ree == Screen_7ree.SEARCH) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (currentScreen_7ree == Screen_7ree.SEARCH) Color(0xFF6200EE) else MaterialTheme.colorScheme.onSurfaceVariant
                ) 
            },
            selected = currentScreen_7ree == Screen_7ree.SEARCH,
            onClick = { 
                onScreenSelected_7ree(Screen_7ree.SEARCH)
                onSearchReset_7ree()
            },
            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.surface, // 与背景一致
                selectedIconColor = Color(0xFF6200EE),
                selectedTextColor = Color(0xFF6200EE),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            icon = { 
                Icon(
                    Icons.Filled.ListAlt, 
                    contentDescription = "历史",
                    modifier = Modifier.size(if (currentScreen_7ree == Screen_7ree.HISTORY) 28.dp else 24.dp),
                    tint = if (currentScreen_7ree == Screen_7ree.HISTORY) Color(0xFF6200EE) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = { 
                Text(
                    "历史",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (currentScreen_7ree == Screen_7ree.HISTORY) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (currentScreen_7ree == Screen_7ree.HISTORY) Color(0xFF6200EE) else MaterialTheme.colorScheme.onSurfaceVariant
                ) 
            },
            selected = currentScreen_7ree == Screen_7ree.HISTORY,
            onClick = { onScreenSelected_7ree(Screen_7ree.HISTORY) },
            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.surface, // 与背景一致
                selectedIconColor = Color(0xFF6200EE),
                selectedTextColor = Color(0xFF6200EE),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            icon = { 
                Icon(
                    Icons.Filled.Tune, 
                    contentDescription = "配置",
                    modifier = Modifier.size(if (currentScreen_7ree == Screen_7ree.SETTINGS) 28.dp else 24.dp),
                    tint = if (currentScreen_7ree == Screen_7ree.SETTINGS) Color(0xFF6200EE) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = { 
                Text(
                    "配置",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (currentScreen_7ree == Screen_7ree.SETTINGS) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (currentScreen_7ree == Screen_7ree.SETTINGS) Color(0xFF6200EE) else MaterialTheme.colorScheme.onSurfaceVariant
                ) 
            },
            selected = currentScreen_7ree == Screen_7ree.SETTINGS,
            onClick = { onScreenSelected_7ree(Screen_7ree.SETTINGS) },
            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.surface, // 与背景一致
                selectedIconColor = Color(0xFF6200EE),
                selectedTextColor = Color(0xFF6200EE),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
} 