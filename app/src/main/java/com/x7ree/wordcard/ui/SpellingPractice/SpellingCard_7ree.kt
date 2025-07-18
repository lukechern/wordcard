package com.x7ree.wordcard.ui.SpellingPractice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 拼写卡片组件
 * 用于显示拼写练习的入口卡片
 */
@Composable
fun SpellingCard_7ree(
    spellingCount: Int,
    onSpellingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onSpellingClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // 从16.dp减少到12.dp
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Keyboard,
                contentDescription = "拼写",
                modifier = Modifier.size(20.dp), // 从24.dp减少到20.dp
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp)) // 从6.dp减少到4.dp（缩小30%）
            Text(
                text = if (spellingCount > 0) "拼写${spellingCount}次" else "拼写",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            // 删除额外的空间以减少高度
        }
    }
}