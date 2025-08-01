package com.x7ree.wordcard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 中英对照组件
 * 直接显示清理后的中英对照内容
 */
@Composable
fun BilingualComparisonContent_7ree(
    bilingualComparison: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (bilingualComparison.isNotEmpty() && bilingualComparison != "暂无中英对照内容") {
            // 直接显示清理后的中英对照内容
            MarkdownText_7ree(
                text = bilingualComparison,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        } else {
            // 内容不完整的提示
            Text(
                text = "暂无中英对照内容",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}