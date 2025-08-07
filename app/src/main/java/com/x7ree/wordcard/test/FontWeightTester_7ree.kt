package com.x7ree.wordcard.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.ui.components.MarkdownText_7ree

/**
 * 字体加粗效果测试组件
 * 用于验证Google Sans Code字体的各种字重是否正常显示
 */
@Composable
fun FontWeightTester_7ree() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "字体加粗效果测试",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // 测试不同的FontWeight
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "直接使用FontWeight测试:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Normal (400): This is normal text",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal
                )
                
                Text(
                    text = "Medium (500): This is medium text",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "SemiBold (600): This is semi-bold text",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "Bold (700): This is bold text",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "ExtraBold (800): This is extra-bold text",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                
                Text(
                    text = "Black (900): This is black text",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Black
                )
            }
        }
        
        // 测试Markdown加粗效果
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Markdown加粗效果测试:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                MarkdownText_7ree(
                    text = "这是普通文本，**这是双星号加粗文本**，这又是普通文本。",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                MarkdownText_7ree(
                    text = "这是普通文本，***这是三星号超粗文本***，这又是普通文本。",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                MarkdownText_7ree(
                    text = "English text with **bold formatting** and ***extra bold formatting*** should work properly.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        // 测试文章标题样式
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "文章标题样式测试:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // 模拟文章标题
                MarkdownText_7ree(
                    text = "**The Impact of Technology on Modern Education**",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                )
                
                Text(
                    text = "技术对现代教育的影响",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
            }
        }
    }
}