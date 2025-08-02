package com.x7ree.wordcard.ui.DashBoard.DataManagement.cloudflare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*

/**
 * CloudFlare操作进度状态
 */
data class CloudFlareProgressState_7ree(
    val isVisible: Boolean = false,
    val operationType: CloudFlareOperationType_7ree = CloudFlareOperationType_7ree.TEST,
    val progress: Float = 0f,
    val currentStep: String = "",
    val isCompleted: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)

/**
 * CloudFlare操作类型
 */
enum class CloudFlareOperationType_7ree(val displayName: String) {
    TEST("连接测试"),
    UPLOAD("数据上传"),
    DOWNLOAD("数据下载")
}

/**
 * CloudFlare进度管理器
 */
class CloudFlareProgressManager_7ree {
    private val _progressState = mutableStateOf(CloudFlareProgressState_7ree())
    val progressState: State<CloudFlareProgressState_7ree> = _progressState
    
    /**
     * 开始操作
     */
    fun startOperation(operationType: CloudFlareOperationType_7ree) {
        _progressState.value = CloudFlareProgressState_7ree(
            isVisible = true,
            operationType = operationType,
            progress = 0f,
            currentStep = "准备${operationType.displayName}...",
            isCompleted = false,
            isError = false
        )
    }
    
    /**
     * 更新进度
     */
    fun updateProgress(progress: Float, currentStep: String) {
        _progressState.value = _progressState.value.copy(
            progress = progress.coerceIn(0f, 1f),
            currentStep = currentStep,
            isCompleted = false,
            isError = false
        )
    }
    
    /**
     * 完成操作
     */
    fun completeOperation(successMessage: String) {
        _progressState.value = _progressState.value.copy(
            progress = 1f,
            currentStep = successMessage,
            isCompleted = true,
            isError = false
        )
        
        // 不再自动隐藏，保持显示直到用户进行下一项操作或离开页面
    }
    
    /**
     * 操作失败
     */
    fun failOperation(errorMessage: String) {
        _progressState.value = _progressState.value.copy(
            currentStep = errorMessage,
            isCompleted = true,
            isError = true
        )
        
        // 不再自动隐藏，保持显示直到用户进行下一项操作或离开页面
    }
    
    /**
     * 隐藏进度
     */
    fun hideProgress() {
        _progressState.value = CloudFlareProgressState_7ree()
    }
    
    /**
     * 解析进度文本并更新进度
     */
    fun parseProgressFromText(progressText: String) {
        when {
            // 测试连接相关进度
            progressText.contains("正在连接") -> updateProgress(0.2f, progressText)
            progressText.contains("正在验证") -> updateProgress(0.4f, progressText)
            progressText.contains("正在检查") -> updateProgress(0.6f, progressText)
            progressText.contains("连接成功") -> completeOperation(progressText)
            progressText.contains("连接失败") || progressText.contains("连接异常") -> failOperation(progressText)
            
            // 上传相关进度
            progressText.contains("正在导出本地") -> updateProgress(0.1f, progressText)
            progressText.contains("正在解析") -> updateProgress(0.2f, progressText)
            progressText.contains("正在上传") && progressText.contains("到CloudFlare") -> updateProgress(0.3f, progressText)
            progressText.contains("表结构验证") -> updateProgress(0.4f, progressText)
            progressText.contains("已上传") && progressText.contains("条单词记录") -> {
                // 解析单词上传进度
                val regex = """已上传 (\d+)/(\d+) 条单词记录""".toRegex()
                val matchResult = regex.find(progressText)
                if (matchResult != null) {
                    val current = matchResult.groupValues[1].toIntOrNull() ?: 0
                    val total = matchResult.groupValues[2].toIntOrNull() ?: 1
                    val progress = 0.4f + (current.toFloat() / total.toFloat()) * 0.3f // 40%-70%
                    updateProgress(progress, progressText)
                } else {
                    updateProgress(0.5f, progressText)
                }
            }
            progressText.contains("已上传") && progressText.contains("篇文章") -> {
                // 解析文章上传进度
                val regex = """已上传 (\d+)/(\d+) 篇文章""".toRegex()
                val matchResult = regex.find(progressText)
                if (matchResult != null) {
                    val current = matchResult.groupValues[1].toIntOrNull() ?: 0
                    val total = matchResult.groupValues[2].toIntOrNull() ?: 1
                    val progress = 0.7f + (current.toFloat() / total.toFloat()) * 0.2f // 70%-90%
                    updateProgress(progress, progressText)
                } else {
                    updateProgress(0.8f, progressText)
                }
            }
            progressText.contains("正在完成上传") -> updateProgress(0.95f, progressText)
            progressText.contains("数据上传成功") -> completeOperation(progressText)
            progressText.contains("上传失败") || progressText.contains("上传异常") -> failOperation(progressText)
            
            // 下载相关进度
            progressText.contains("正在获取云端") -> updateProgress(0.2f, progressText)
            progressText.contains("正在解析云端") -> updateProgress(0.4f, progressText)
            progressText.contains("正在导入") -> updateProgress(0.6f, progressText)
            progressText.contains("已导入") && progressText.contains("条单词") -> {
                // 解析单词导入进度
                val regex = """已导入 (\d+)/(\d+) 条单词""".toRegex()
                val matchResult = regex.find(progressText)
                if (matchResult != null) {
                    val current = matchResult.groupValues[1].toIntOrNull() ?: 0
                    val total = matchResult.groupValues[2].toIntOrNull() ?: 1
                    val progress = 0.6f + (current.toFloat() / total.toFloat()) * 0.2f // 60%-80%
                    updateProgress(progress, progressText)
                } else {
                    updateProgress(0.7f, progressText)
                }
            }
            progressText.contains("已导入") && progressText.contains("篇文章") -> {
                // 解析文章导入进度
                val regex = """已导入 (\d+)/(\d+) 篇文章""".toRegex()
                val matchResult = regex.find(progressText)
                if (matchResult != null) {
                    val current = matchResult.groupValues[1].toIntOrNull() ?: 0
                    val total = matchResult.groupValues[2].toIntOrNull() ?: 1
                    val progress = 0.8f + (current.toFloat() / total.toFloat()) * 0.15f // 80%-95%
                    updateProgress(progress, progressText)
                } else {
                    updateProgress(0.85f, progressText)
                }
            }
            progressText.contains("正在完成下载") -> updateProgress(0.95f, progressText)
            progressText.contains("数据下载成功") -> completeOperation(progressText)
            progressText.contains("下载失败") || progressText.contains("下载异常") -> failOperation(progressText)
            
            // 其他情况
            else -> updateProgress(_progressState.value.progress, progressText)
        }
    }
}

/**
 * CloudFlare进度显示组件
 */
@Composable
fun CloudFlareProgressDisplay_7ree(
    progressManager: CloudFlareProgressManager_7ree,
    modifier: Modifier = Modifier
) {
    val progressState by progressManager.progressState
    
    if (progressState.isVisible) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(
                    color = when {
                        progressState.isError -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.08f)
                        progressState.isCompleted -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(6.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 操作类型和状态标题
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${progressState.operationType.displayName}进度",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = when {
                            progressState.isError -> MaterialTheme.colorScheme.error
                            progressState.isCompleted -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                    
                    // 百分比显示
                    if (!progressState.isCompleted || !progressState.isError) {
                        Text(
                            text = "${(progressState.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 进度条
                if (!progressState.isCompleted) {
                    LinearProgressIndicator(
                        progress = progressState.progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = when {
                            progressState.isError -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.primary
                        },
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                } else {
                    // 完成状态的进度条
                    LinearProgressIndicator(
                        progress = 1f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = when {
                            progressState.isError -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.primary
                        },
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 当前步骤说明
                Text(
                    text = progressState.currentStep,
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        progressState.isError -> MaterialTheme.colorScheme.error
                        progressState.isCompleted -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 完成状态的额外信息
                if (progressState.isCompleted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (progressState.isError) "❌ 操作失败" else "✅ 操作完成",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = when {
                            progressState.isError -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
        }
    }
}