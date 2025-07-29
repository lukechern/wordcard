package com.x7ree.wordcard.article.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 标题模式UI
 */
@Composable
fun TitleModeUI_7ree(
    title: String,
    onGenerateArticle: () -> Unit,
    onSearchModeToggle: (Boolean) -> Unit,
    onShowFilterMenu: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 标题模式：显示标题和操作按钮
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 生成文章按钮 - 背景减少30%，图标保持和单词本等大
            IconButton(
                onClick = onGenerateArticle,
                modifier = Modifier
                    .size(30.dp) // 背景减少
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "生成文章",
                    tint = Color(0xFF2B7033), // 设置为更深的绿色
                    modifier = Modifier.size(28.dp) // 调整图标大小为28.dp
                )
            }

            // 搜索按钮
            IconButton(
                onClick = { onSearchModeToggle(true) },
                modifier = Modifier.size(30.dp) // 与加号按钮保持一致的尺寸
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "搜索",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp) // 图标保持和单词本等大
                )
            }

            // 汉堡菜单按钮
            IconButton(
                onClick = onShowFilterMenu,
                modifier = Modifier.size(30.dp) // 与加号按钮保持一致的尺寸
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "筛选与排序",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp) // 图标保持和单词本等大
                )
            }
        }
    }
}