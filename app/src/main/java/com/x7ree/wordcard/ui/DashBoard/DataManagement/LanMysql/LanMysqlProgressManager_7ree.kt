package com.x7ree.wordcard.ui.DashBoard.DataManagement.LanMysql

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
 * 局域网MySQL操作进度状态
 */
data class LanMysqlProgressState_7ree(
    val isVisible: Boolean = false,
    val operationType: LanMysqlOperationType_7ree = LanMysqlOperationType_7ree.TEST,
    val progress: Float = 0f,
    val currentStep: String = "",
    val isCompleted: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)

/**
 * 局域网MySQL操作类型
 */
enum class LanMysqlOperationType_7ree(val displayName: String) {
    TEST("连接测试"),
    UPLOAD("数据上传"),
    DOWNLOAD("数据下载")
}

/**
 * 局域网MySQL进度管理器
 */
class LanMysqlProgressManager_7ree {
    private val _progressState = mutableStateOf(LanMysqlProgressState_7ree())
    val progressState: State<LanMysqlProgressState_7ree> = _progressState
    
    /**
     * 开始操作
     */
    fun startOperation(operationType: LanMysqlOperationType_7ree) {
        _progressState.value = LanMysqlProgressState_7ree(
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
        _progressState.value = LanMysqlProgressState_7ree()
    }
    
    /**
     * 解析进度文本并更新进度
     */
    fun parseProgressFromText(progressText: String) {
        when {
            // 测试连接相关进度
            progressText.contains("正在连接PHP API") -> updateProgress(0.2f, progressText)
            progressText.contains("正在验证API密钥") -> updateProgress(0.4f, progressText)
            progressText.contains("正在检查服务器") -> updateProgress(0.6f, progressText)
            progressText.contains("PHP API连接成功") -> completeOperation(progressText)
            progressText.contains("PHP API连接失败") || progressText.contains("PHP API连接异常") -> failOperation(progressText)
            
            // 上传相关进度
            progressText.contains("正在导出本地数据") -> updateProgress(0.1f, progressText)
            progressText.contains("正在解析数据") -> updateProgress(0.2f, progressText)
            progressText.contains("正在上传到PHP API") -> updateProgress(0.4f, progressText)
            progressText.contains("正在等待服务器处理") -> updateProgress(0.6f, progressText)
            progressText.contains("正在完成PHP API上传") -> updateProgress(0.95f, progressText)
            progressText.contains("PHP API数据上传成功") -> completeOperation(progressText)
            progressText.contains("PHP API上传失败") || progressText.contains("PHP API上传异常") -> failOperation(progressText)
            
            // 下载相关进度
            progressText.contains("正在请求数据") -> updateProgress(0.2f, progressText)
            progressText.contains("正在等待服务器响应") -> updateProgress(0.4f, progressText)
            progressText.contains("正在解析服务器数据") -> updateProgress(0.5f, progressText)
            progressText.contains("正在导入本地数据库") -> updateProgress(0.7f, progressText)
            progressText.contains("正在完成PHP API下载") -> updateProgress(0.95f, progressText)
            progressText.contains("PHP API数据下载成功") -> completeOperation(progressText)
            progressText.contains("PHP API下载失败") || progressText.contains("PHP API下载异常") -> failOperation(progressText)
            
            // 其他情况
            else -> updateProgress(_progressState.value.progress, progressText)
        }
    }
}

/**
 * 局域网MySQL进度显示组件
 */
@Composable
fun LanMysqlProgressDisplay_7ree(
    progressManager: LanMysqlProgressManager_7ree,
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