package com.x7ree.wordcard.ui.DashBoard.DataManagement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree

/**
 * 手机操作区域组件
 */
@Composable
fun PhoneOperationSection_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onImportWordFile_7ree: () -> Unit,
    onImportArticleFile_7ree: () -> Unit,
    isPhoneOperationEnabled: Boolean,
    onPhoneOperationToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.Gray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.PhoneAndroid,
                    contentDescription = "手机操作",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "从手机操作",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Switch(
                checked = isPhoneOperationEnabled,
                onCheckedChange = onPhoneOperationToggle
            )
        }
        
        
        // 只有当手机操作开关开启时才显示内容
        if (isPhoneOperationEnabled) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // 单词数据操作区域
            WordDataOperationSection_7ree(wordQueryViewModel_7ree, onImportWordFile_7ree)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 文章数据操作区域
            ArticleDataOperationSection_7ree(wordQueryViewModel_7ree, onImportArticleFile_7ree)
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * 单词数据操作区域（导入导出在同一行）
 */
@Composable
private fun WordDataOperationSection_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onImportWordFile_7ree: () -> Unit
) {
    val exportPath_7ree by wordQueryViewModel_7ree.exportPath_7ree.collectAsState()
    val hasExportedData_7ree by wordQueryViewModel_7ree.hasExportedData_7ree.collectAsState()
    
    Text(
        text = "单词数据操作",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    
    Text(
        text = "导出查询历史为JSON文件，或从JSON文件导入历史数据",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    
    // 导入导出按钮在同一行
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 导出按钮
        Button(
            onClick = {
                wordQueryViewModel_7ree.exportHistoryData_7ree()
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Filled.Download,
                contentDescription = "导出",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("导出")
        }
        
        // 导入按钮
        Button(
            onClick = onImportWordFile_7ree,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Filled.Upload,
                contentDescription = "导入",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("导入")
        }
    }
    
    // 显示单词数据导出成功路径
    if (hasExportedData_7ree && !exportPath_7ree.contains("ArticleData")) {
        val textColor = Color(0xFF2E7D32) // 单词数据使用绿色
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(8.dp)
                .padding(top = 8.dp)
        ) {
            Text(
                text = "单词数据已经导出到：",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = exportPath_7ree,
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}

/**
 * 文章数据操作区域（导入导出在同一行）
 */
@Composable
private fun ArticleDataOperationSection_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onImportArticleFile_7ree: () -> Unit
) {
    val exportPath_7ree by wordQueryViewModel_7ree.exportPath_7ree.collectAsState()
    val hasExportedData_7ree by wordQueryViewModel_7ree.hasExportedData_7ree.collectAsState()
    
    Text(
        text = "文章数据操作",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    
    Text(
        text = "导出文章数据为JSON文件，或从JSON文件导入文章数据",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    
    // 导入导出按钮在同一行
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 导出按钮
        Button(
            onClick = {
                wordQueryViewModel_7ree.exportArticleData_7ree()
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1565C0) // 深蓝色背景
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Download,
                contentDescription = "导出",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("导出")
        }
        
        // 导入按钮
        Button(
            onClick = onImportArticleFile_7ree,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1565C0) // 深蓝色背景
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Upload,
                contentDescription = "导入",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("导入")
        }
    }
    
    // 显示文章数据导出成功路径
    if (hasExportedData_7ree && exportPath_7ree.contains("ArticleData")) {
        val textColor = Color(0xFF1565C0) // 文章数据使用深蓝色
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(8.dp)
                .padding(top = 8.dp)
        ) {
            Text(
                text = "文章数据已经导出到：",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = exportPath_7ree,
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}

/**
 * 单词数据导出部分
 */
@Composable
private fun WordDataExportSection_7ree(wordQueryViewModel_7ree: WordQueryViewModel_7ree) {
    val exportPath_7ree by wordQueryViewModel_7ree.exportPath_7ree.collectAsState()
    val hasExportedData_7ree by wordQueryViewModel_7ree.hasExportedData_7ree.collectAsState()
    
    Text(
        text = "单词数据导出",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    
    Text(
        text = "将查询历史导出为JSON文件，包含单词、查询结果、时间戳等信息",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    
    Button(
        onClick = {
            wordQueryViewModel_7ree.exportHistoryData_7ree()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Filled.Download,
            contentDescription = "导出",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text("单词数据导出")
    }
    
    // 显示单词数据导出成功路径
    if (hasExportedData_7ree && !exportPath_7ree.contains("ArticleData")) {
        val textColor = Color(0xFF2E7D32) // 单词数据使用绿色
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(8.dp)
                .padding(top = 8.dp)
        ) {
            Text(
                text = "单词数据已经导出到：",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = exportPath_7ree,
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}

/**
 * 文章数据导出部分
 */
@Composable
private fun ArticleDataExportSection_7ree(wordQueryViewModel_7ree: WordQueryViewModel_7ree) {
    val exportPath_7ree by wordQueryViewModel_7ree.exportPath_7ree.collectAsState()
    val hasExportedData_7ree by wordQueryViewModel_7ree.hasExportedData_7ree.collectAsState()
    
    Text(
        text = "文章数据导出",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    
    Text(
        text = "将文章单词数据导出为JSON文件，包含文章内容、单词信息等",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    
    Button(
        onClick = {
            wordQueryViewModel_7ree.exportArticleData_7ree()
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1565C0) // 深蓝色背景
        )
    ) {
        Icon(
            imageVector = Icons.Filled.Download,
            contentDescription = "导出",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text("文章数据导出")
    }
    
    // 显示文章数据导出成功路径
    if (hasExportedData_7ree && exportPath_7ree.contains("ArticleData")) {
        val textColor = Color(0xFF1565C0) // 文章数据使用深蓝色，与文章导出按钮背景色一致
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(8.dp)
                .padding(top = 8.dp)
        ) {
            Text(
                text = "文章数据已经导出到：",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = exportPath_7ree,
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}

/**
 * 单词数据导入部分
 */
@Composable
private fun WordDataImportSection_7ree(onImportFile_7ree: () -> Unit) {
    Text(
        text = "单词数据导入",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    
    Text(
        text = "从JSON文件导入查询历史数据，支持批量恢复历史记录",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    
    Button(
        onClick = onImportFile_7ree,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Filled.Upload,
            contentDescription = "导入",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text("单词数据导入")
    }
}

/**
 * 文章数据导入部分
 */
@Composable
private fun ArticleDataImportSection_7ree(onImportFile_7ree: () -> Unit) {
    Text(
        text = "文章数据导入",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    
    Text(
        text = "从JSON文件导入文章单词数据，支持批量恢复文章记录",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    
    Button(
        onClick = onImportFile_7ree,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1565C0) // 深蓝色背景
        )
    ) {
        Icon(
            imageVector = Icons.Filled.Upload,
            contentDescription = "导入",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text("文章数据导入")
    }
}

// 已移除StoragePathSection_7ree函数，路径显示功能已移到对应的导出部分
