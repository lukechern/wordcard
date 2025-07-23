package com.x7ree.wordcard.query.manager

import com.x7ree.wordcard.query.state.WordQueryState_7ree
import com.x7ree.wordcard.tts.TtsManager_7ree as CoreTtsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * TTS功能管理器
 */
class TtsManager_7ree(
    private val coreTtsManager_7ree: CoreTtsManager,
    private val queryState_7ree: WordQueryState_7ree,
    private val coroutineScope: CoroutineScope
) {

    fun speakWord_7ree(word: String) {
        coroutineScope.launch {
            try {
                // 开始朗读
                coreTtsManager_7ree.speak(
                    text = word,
                    utteranceId = "word",
                    onStart = {
                        // println("DEBUG: 开始朗读单词: $word")
                        queryState_7ree.updateSpeakingWordState_7ree(true)
                    },
                    onComplete = {
                        // println("DEBUG: 朗读完成: $word")
                        queryState_7ree.updateSpeakingWordState_7ree(false)
                    },
                    onError = { error ->
                        // println("DEBUG: 朗读失败: $error")
                        queryState_7ree.updateOperationResult_7ree("朗读失败: $error")
                        queryState_7ree.updateSpeakingWordState_7ree(false)
                    }
                )
            } catch (e: Exception) {
                // println("DEBUG: 朗读异常: ${e.message}")
                queryState_7ree.updateOperationResult_7ree("朗读异常: ${e.message}")
                queryState_7ree.updateSpeakingWordState_7ree(false)
            }
        }
    }

    fun speakExamples_7ree(examplesText: String) {
        coroutineScope.launch {
            try {
                // 开始朗读例句
                coreTtsManager_7ree.speak(
                    text = examplesText,
                    utteranceId = "examples",
                    onStart = {
                        // println("DEBUG: 开始朗读例句")
                        queryState_7ree.updateSpeakingExamplesState_7ree(true)
                    },
                    onComplete = {
                        // println("DEBUG: 例句朗读完成")
                        queryState_7ree.updateSpeakingExamplesState_7ree(false)
                    },
                    onError = { error ->
                        // println("DEBUG: 例句朗读失败: $error")
                        queryState_7ree.updateOperationResult_7ree("例句朗读失败: $error")
                        queryState_7ree.updateSpeakingExamplesState_7ree(false)
                    }
                )
            } catch (e: Exception) {
                // println("DEBUG: 例句朗读异常: ${e.message}")
                queryState_7ree.updateOperationResult_7ree("例句朗读异常: ${e.message}")
                queryState_7ree.updateSpeakingExamplesState_7ree(false)
            }
        }
    }
    
    fun stopSpeaking_7ree() {
        coreTtsManager_7ree.stopSpeaking()
        queryState_7ree.updateSpeakingState_7ree(false)
        queryState_7ree.updateSpeakingWordState_7ree(false)
        queryState_7ree.updateSpeakingExamplesState_7ree(false)
    }
    
    fun getTtsEngineStatus_7ree(): String {
        val status = coreTtsManager_7ree.getEngineStatus()
        return when {
            status.currentEngine == "google" && status.googleReady -> "Google TTS 已就绪"
            status.currentEngine == "azure" && status.azureReady -> "Azure Speech 已就绪"
            status.currentEngine == "google" && !status.googleReady -> "Google TTS 未就绪"
            status.currentEngine == "azure" && !status.azureReady -> "Azure Speech 配置无效"
            else -> "TTS 引擎未知状态"
        }
    }

    fun initializeTts_7ree() {
        // 设置TTS状态变化回调
        coreTtsManager_7ree.onTtsStateChanged = { _, _ ->
            // println("DEBUG: TTS引擎状态变化")
        }
        
        coreTtsManager_7ree.onSpeakingStateChanged = { isSpeaking, _ ->
            // println("DEBUG: TTS朗读状态变化")
            queryState_7ree.updateSpeakingState_7ree(isSpeaking)
        }
    }

    fun release_7ree() {
        coreTtsManager_7ree.release()
        // println("DEBUG: TTS资源已释放")
    }
}