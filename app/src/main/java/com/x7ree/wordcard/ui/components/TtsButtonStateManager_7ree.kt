package com.x7ree.wordcard.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * TTS按钮状态枚举
 */
enum class TtsButtonState_7ree {
    IDLE,       // 默认状态 - 三角形播放图标
    LOADING,    // 加载状态 - 转圈载入图标
    PLAYING     // 播放状态 - 暂停图标
}

/**
 * TTS按钮状态管理器
 * 统一管理朗读按钮的三种状态切换
 */
@Composable
fun TtsButton_7ree(
    state: TtsButtonState_7ree,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "朗读按钮"
) {
    IconButton(
        onClick = {
            when (state) {
                TtsButtonState_7ree.IDLE -> onPlayClick()
                TtsButtonState_7ree.LOADING -> { /* 加载中不响应点击 */ }
                TtsButtonState_7ree.PLAYING -> onPauseClick()
            }
        },
        modifier = modifier,
        enabled = state != TtsButtonState_7ree.LOADING
    ) {
        when (state) {
            TtsButtonState_7ree.IDLE -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            TtsButtonState_7ree.LOADING -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            TtsButtonState_7ree.PLAYING -> {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "停止朗读",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * 简化版TTS按钮 - 只显示图标，不包含IconButton容器
 * 用于需要自定义点击区域的场景
 */
@Composable
fun TtsButtonIcon_7ree(
    state: TtsButtonState_7ree,
    modifier: Modifier = Modifier,
    contentDescription: String = "朗读按钮"
) {
    when (state) {
        TtsButtonState_7ree.IDLE -> {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.primary,
                modifier = modifier
            )
        }
        TtsButtonState_7ree.LOADING -> {
            CircularProgressIndicator(
                modifier = modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        TtsButtonState_7ree.PLAYING -> {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "停止朗读",
                tint = MaterialTheme.colorScheme.primary,
                modifier = modifier
            )
        }
    }
}