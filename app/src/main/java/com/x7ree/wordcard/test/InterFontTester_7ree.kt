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
 * Inter字体专门测试组件
 * 验证Inter字体的粗体效果
 */
@Composable
fun InterFontTester_7ree() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Inter 字体粗体测试",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // Inter字体权重展示
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Inter 字体权重展示:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Light (300): The Impact of Technology on Modern Education",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Light
                )
                
                Text(
                    text = "Regular (400): The Impact of Technology on Modern Education",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal
                )
                
                Text(
                    text = "Medium (500): The Impact of Technology on Modern Education",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "SemiBold (600): The Impact of Technology on Modern Education",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "Bold (700): The Impact of Technology on Modern Education",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "ExtraBold (800): The Impact of Technology on Modern Education",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                
                Text(
                    text = "Black (900): The Impact of Technology on Modern Education",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Black
                )
            }
        }
        
        // Markdown粗体测试
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Markdown 粗体测试:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                MarkdownText_7ree(
                    text = "这是普通文本，**这是双星号粗体文本**，这又是普通文本。",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                MarkdownText_7ree(
                    text = "English text with **bold keywords** and ***extra bold text*** should be clearly visible.",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                MarkdownText_7ree(
                    text = "**The Impact of Technology** on modern education has been **significant and transformative**. Students now have access to ***unlimited resources*** and can learn at their own pace.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        // 文章标题模拟
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "文章标题效果模拟:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // 英文标题
                Text(
                    text = "The Impact of Technology on Modern Education",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // 中文标题
                Text(
                    text = "技术对现代教育的影响",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 内容示例
                MarkdownText_7ree(
                    text = "Technology has **revolutionized** the way we approach education. From ***interactive learning platforms*** to **virtual classrooms**, the educational landscape has been transformed. Students can now access **high-quality content** from anywhere in the world, making education more **accessible and inclusive** than ever before.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        // 使用说明
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "💡 使用Inter字体的方法:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Text(
                    text = "在 themes.xml 中将 CustomTextViewStyle 改为 InterTextViewStyle 即可切换到Inter字体。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}