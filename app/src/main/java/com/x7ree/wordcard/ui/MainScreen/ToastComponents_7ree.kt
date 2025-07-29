package com.x7ree.wordcard.ui.MainScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.x7ree.wordcard.query.WordQueryViewModel_7ree

/**
 * 处理操作结果提示条显示逻辑
 */
@Composable
fun HandleOperationResultToast_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree?,
    showCustomToast_7ree: (Boolean) -> Unit,
    setToastMessage_7ree: (String) -> Unit
) {
    val operationResult_7ree by wordQueryViewModel_7ree?.operationResult_7ree?.collectAsState() ?: androidx.compose.runtime.mutableStateOf(null)
    val articleOperationResult_7ree by wordQueryViewModel_7ree?.articleViewModel_7ree?.operationResult?.collectAsState() ?: androidx.compose.runtime.mutableStateOf(null)

    // 监听操作结果，显示自定义提示条
    LaunchedEffect(operationResult_7ree) {
        operationResult_7ree?.let { result ->
            setToastMessage_7ree(result)
            showCustomToast_7ree(true)
            // 清除操作结果
            wordQueryViewModel_7ree?.clearOperationResult_7ree()
        }
    }
    
    // 监听文章操作结果，显示自定义提示条
    LaunchedEffect(articleOperationResult_7ree) {
        articleOperationResult_7ree?.let { result ->
            setToastMessage_7ree(result)
            showCustomToast_7ree(true)
            // 清除操作结果
            wordQueryViewModel_7ree?.articleViewModel_7ree?.clearOperationResult()
        }
    }
}
