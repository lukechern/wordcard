package com.x7ree.wordcard.ui.MainScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import kotlinx.coroutines.delay
import android.util.Log

/**
 * 处理启动画面显示逻辑
 */
@Composable
fun HandleSplashScreenLogic_7ree(
    isInitializationComplete_7ree: Boolean,
    showSplash_7ree: MutableState<Boolean>
) {
    // 智能启动画面控制 - 改进逻辑，确保不会卡住
    LaunchedEffect(isInitializationComplete_7ree) {
        if (isInitializationComplete_7ree) {
            // 如果初始化已完成，只显示500毫秒启动画面给用户视觉反馈
            delay(500)
            showSplash_7ree.value = false
        }
    }
    
    // 强制超时机制 - 确保启动画面不会无限显示
    LaunchedEffect(Unit) {
        delay(5000) // 最多显示5秒启动画面，给冷启动更多时间
        if (showSplash_7ree.value) {
            // Log.d("MainScreen_7ree", "启动画面超时，强制关闭")
            showSplash_7ree.value = false
        }
    }
}

/**
 * 处理ViewModel可用时的启动画面逻辑
 */
@Composable
fun HandleViewModelAvailableLogic_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree?,
    showSplash_7ree: MutableState<Boolean>
) {
    // 额外的安全机制 - 如果ViewModel可用但初始化标志未设置，也关闭启动画面
    LaunchedEffect(wordQueryViewModel_7ree) {
        if (wordQueryViewModel_7ree != null && showSplash_7ree.value) {
            delay(1000) // 给一点时间让初始化标志更新
            if (showSplash_7ree.value) {
                // Log.d("MainScreen_7ree", "ViewModel已可用，关闭启动画面")
                showSplash_7ree.value = false
            }
        }
    }
}

/**
 * 处理加载超时逻辑
 */
@Composable
fun HandleLoadingTimeoutLogic_7ree(): Boolean {
    var showLoadingTimeout by remember { mutableStateOf(false) }
    
    // 超时保护机制 - 如果10秒后还在加载状态，显示错误信息
    LaunchedEffect(Unit) {
        delay(10000) // 10秒超时
        Log.e("MainScreen_7ree", "应用初始化超时")
        showLoadingTimeout = true
    }
    
    return showLoadingTimeout
}
