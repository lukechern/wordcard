package com.x7ree.wordcard.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import com.x7ree.wordcard.ui.components.MarkdownText_7ree

/**
 * 字体切换测试组件
 * 用于对比不同字体的粗体显示效果
 */
@Composable
fun FontSwitchTester_7ree() {
    var selectedFont by remember { mutableStateOf(0) } // 0: Google Sans Code, 1: Inter, 2: Default, 3: SansSerif
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "字体粗体对比测试",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // 字体选择器
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "选择测试字体:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // 第一行按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { selectedFont = 0 },
                        colors = if (selectedFont == 0) ButtonDefaults.buttonColors() 
                               else ButtonDefaults.outlinedButtonColors(),
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                    ) {
                        Text("Google Sans", fontSize = 12.sp)
                    }
                    
                    Button(
                        onClick = { selectedFont = 1 },
                        colors = if (selectedFont == 1) ButtonDefaults.buttonColors() 
                               else ButtonDefaults.outlinedButtonColors(),
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                    ) {
                        Text("Inter", fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 第二行按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { selectedFont = 2 },
                        colors = if (selectedFont == 2) ButtonDefaults.buttonColors() 
                               else ButtonDefaults.outlinedButtonColors(),
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                    ) {
                        Text("系统默认", fontSize = 12.sp)
                    }
                    
                    Button(
                        onClick = { selectedFont = 3 },
                        colors = if (selectedFont == 3) ButtonDefaults.buttonColors() 
                               else ButtonDefaults.outlinedButtonColors(),
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                    ) {
                        Text("Sans Serif", fontSize = 12.sp)
                    }
                }
            }
        }
        
        // 获取当前选择的字体
        val context = LocalContext.current
        val currentFontFamily = when (selectedFont) {
            0 -> FontFamily.Default // 这里会使用主题中设置的Google Sans Code
            1 -> {
                // 尝试加载Inter字体
                try {
                    val interTypeface = ResourcesCompat.getFont(context, com.x7ree.wordcard.R.font.inter_font)
                    if (interTypeface != null) FontFamily.Default else FontFamily.Default
                } catch (e: Exception) {
                    FontFamily.Default
                }
            }
            2 -> FontFamily.Default
            3 -> FontFamily.SansSerif
            else -> FontFamily.Default
        }
        
        val fontName = when (selectedFont) {
            0 -> "Google Sans Code"
            1 -> "Inter"
            2 -> "系统默认字体"
            3 -> "Sans Serif"
            else -> "未知字体"
        }
        
        // 字体权重测试
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "$fontName - 字体权重测试:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = currentFontFamily
                )
                
                Text(
                    text = "Normal (400): The Impact of Technology on Education",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal,
                    fontFamily = currentFontFamily
                )
                
                Text(
                    text = "Medium (500): The Impact of Technology on Education",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    fontFamily = currentFontFamily
                )
                
                Text(
                    text = "SemiBold (600): The Impact of Technology on Education",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = currentFontFamily
                )
                
                Text(
                    text = "Bold (700): The Impact of Technology on Education",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = currentFontFamily
                )
                
                Text(
                    text = "ExtraBold (800): The Impact of Technology on Education",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = currentFontFamily
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
                    text = "$fontName - Markdown粗体测试:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = currentFontFamily
                )
                
                // 注意：MarkdownText_7ree组件会使用主题中设置的字体
                MarkdownText_7ree(
                    text = "这是普通文本，**这是双星号粗体文本**，这又是普通文本。",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                MarkdownText_7ree(
                    text = "English text with **bold keywords** and ***extra bold text*** for testing.",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                MarkdownText_7ree(
                    text = "**The Impact of Technology** on modern education has been **significant and transformative**.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        // 文章标题模拟测试
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "$fontName - 文章标题模拟:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = currentFontFamily
                )
                
                // 模拟文章英文标题
                Text(
                    text = "The Impact of Technology on Modern Education",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = currentFontFamily
                )
                
                // 模拟文章中文标题
                Text(
                    text = "技术对现代教育的影响",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = currentFontFamily
                )
            }
        }
    }
}