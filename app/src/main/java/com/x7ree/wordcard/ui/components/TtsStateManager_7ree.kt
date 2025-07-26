package com.x7ree.wordcard.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import com.x7ree.wordcard.ui.components.TtsButtonState_7ree

@Composable
fun TtsStateManager_7ree(
    isSpeakingWord_7ree: Boolean,
    isSpeaking_7ree: Boolean,
    currentSpeakingWord_7ree: String,
    onTtsStateChange: (TtsButtonState_7ree) -> Unit,
    onCurrentSpeakingWordChange: (String) -> Unit
) {
    var currentSpeakingWordInternal by remember { mutableStateOf(currentSpeakingWord_7ree) }
    var ttsStateInternal by remember { mutableStateOf(TtsButtonState_7ree.IDLE) }
    
    // 监听TTS状态变化
    LaunchedEffect(isSpeakingWord_7ree, isSpeaking_7ree, currentSpeakingWord_7ree) {
        // 更新当前播放的单词
        if (currentSpeakingWord_7ree != currentSpeakingWordInternal) {
            currentSpeakingWordInternal = currentSpeakingWord_7ree
            // 如果新单词不为空，说明开始播放，设置为加载状态
            if (currentSpeakingWord_7ree.isNotEmpty()) {
                ttsStateInternal = TtsButtonState_7ree.LOADING
            }
        }
        
        // 处理状态转换
        when {
            // 当开始播放单词时，从加载状态切换到播放状态
            isSpeakingWord_7ree && currentSpeakingWordInternal.isNotEmpty() && ttsStateInternal == TtsButtonState_7ree.LOADING -> {
                // 延迟一下确保载入状态能被看到，然后切换到播放状态
                delay(600) // 确保载入图标显示足够长时间
                if (isSpeakingWord_7ree && currentSpeakingWordInternal.isNotEmpty()) {
                    ttsStateInternal = TtsButtonState_7ree.PLAYING
                }
            }
            // 当TTS完全停止时，立即恢复到默认状态
            !isSpeaking_7ree && !isSpeakingWord_7ree && currentSpeakingWordInternal.isNotEmpty() -> {
                // 立即重置状态，不要延迟
                currentSpeakingWordInternal = ""
                ttsStateInternal = TtsButtonState_7ree.IDLE
            }
        }
    }
    
    // 更新外部状态
    LaunchedEffect(ttsStateInternal) {
        onTtsStateChange(ttsStateInternal)
    }
    
    // 更新外部当前播放单词状态
    LaunchedEffect(currentSpeakingWordInternal) {
        onCurrentSpeakingWordChange(currentSpeakingWordInternal)
    }
}
