package com.x7ree.wordcard.test

import android.content.Context
import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat

/**
 * 字体诊断工具
 * 用于检查字体文件是否正确加载和支持粗体
 */
@Composable
fun FontDiagnosticTool_7ree() {
    val context = LocalContext.current
    var diagnosticResult by remember { mutableStateOf("正在检查...") }
    
    LaunchedEffect(Unit) {
        diagnosticResult = diagnoseFontIssues(context)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "字体诊断工具",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "诊断结果:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = diagnosticResult,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // 简单的粗体测试
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "快速粗体测试:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "这是普通文本 - Normal Weight",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal
                )
                
                Text(
                    text = "这是粗体文本 - Bold Weight",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "如果上面两行文本看起来一样，说明粗体不工作",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun diagnoseFontIssues(context: Context): String {
    val result = StringBuilder()
    
    try {
        // 检查Google Sans Code字体是否存在
        val googleSansCodeTypeface = ResourcesCompat.getFont(context, com.x7ree.wordcard.R.font.google_sans_code)
        if (googleSansCodeTypeface != null) {
            result.append("✓ Google Sans Code 字体文件加载成功\n")
            
            // 检查字体是否支持粗体
            val boldTypeface = Typeface.create(googleSansCodeTypeface, Typeface.BOLD)
            if (boldTypeface != null && boldTypeface.isBold) {
                result.append("✓ Google Sans Code 支持粗体\n")
            } else {
                result.append("✗ Google Sans Code 粗体支持有问题\n")
            }
        } else {
            result.append("✗ Google Sans Code 字体文件加载失败\n")
        }
        
        // 检查系统默认字体
        val defaultBoldTypeface = Typeface.DEFAULT_BOLD
        if (defaultBoldTypeface.isBold) {
            result.append("✓ 系统默认字体支持粗体\n")
        } else {
            result.append("✗ 系统默认字体粗体有问题\n")
        }
        
        // 检查Inter字体
        try {
            val interTypeface = ResourcesCompat.getFont(context, com.x7ree.wordcard.R.font.inter_font)
            if (interTypeface != null) {
                result.append("✓ Inter 字体文件加载成功\n")
                
                val interBoldTypeface = Typeface.create(interTypeface, Typeface.BOLD)
                if (interBoldTypeface != null && interBoldTypeface.isBold) {
                    result.append("✓ Inter 字体支持粗体\n")
                } else {
                    result.append("✗ Inter 字体粗体支持有问题\n")
                }
            } else {
                result.append("✗ Inter 字体文件加载失败\n")
            }
        } catch (e: Exception) {
            result.append("✗ Inter 字体检查出错: ${e.message}\n")
        }
        
        // 检查Sans Serif字体
        val sansSerifTypeface = Typeface.SANS_SERIF
        val sansSerifBoldTypeface = Typeface.create(sansSerifTypeface, Typeface.BOLD)
        if (sansSerifBoldTypeface.isBold) {
            result.append("✓ Sans Serif 字体支持粗体\n")
        } else {
            result.append("✗ Sans Serif 字体粗体有问题\n")
        }
        
        result.append("\n建议解决方案:\n")
        result.append("1. 如果Google Sans Code粗体不工作，尝试使用Inter字体\n")
        result.append("2. Inter是优秀的开源字体，粗体支持很好\n")
        result.append("3. 作为备用方案，可以使用系统默认字体\n")
        result.append("4. 检查字体文件是否为可变字体(Variable Font)\n")
        result.append("5. 确认字体配置文件中的字重定义是否正确\n")
        
    } catch (e: Exception) {
        result.append("诊断过程中出现错误: ${e.message}\n")
    }
    
    return result.toString()
}