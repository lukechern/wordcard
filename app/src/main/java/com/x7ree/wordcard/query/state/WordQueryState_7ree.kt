package com.x7ree.wordcard.query.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.x7ree.wordcard.data.WordEntity_7ree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// 滚动位置数据类
data class ScrollPosition_7ree(
    val firstVisibleItemIndex: Int = 0,
    val firstVisibleItemScrollOffset: Int = 0
)

/**
 * 单词查询相关状态管理
 */
class WordQueryState_7ree {
    
    // 基础查询状态
    var wordInput_7ree by mutableStateOf("")
        private set

    var queryResult_7ree by mutableStateOf("")
        private set

    var isLoading_7ree by mutableStateOf(false)
        private set

    var isWordConfirmed_7ree by mutableStateOf(false)
        private set

    var isFromCache_7ree by mutableStateOf(false)
        private set
    
    // 当前单词的详细信息
    var currentWordInfo_7ree: WordEntity_7ree? by mutableStateOf(null)
        private set

    // TTS相关状态
    var isTtsReady_7ree by mutableStateOf(false)
    var isSpeaking_7ree by mutableStateOf(false)
        private set
    
    var isSpeakingWord_7ree by mutableStateOf(false)
        private set
    var isSpeakingExamples_7ree by mutableStateOf(false)
        private set
        
    fun updateTtsReadyState_7ree(ready: Boolean) {
        isTtsReady_7ree = ready
    }

    // 操作结果状态
    private val _operationResult_7ree = MutableStateFlow<String?>(null)
    val operationResult_7ree: StateFlow<String?> = _operationResult_7ree

    // 更新方法
    fun updateWordInput_7ree(newInput: String) {
        wordInput_7ree = newInput
    }

    fun updateQueryResult_7ree(result: String) {
        queryResult_7ree = result
    }

    fun updateLoadingState_7ree(loading: Boolean) {
        isLoading_7ree = loading
    }

    fun updateWordConfirmed_7ree(confirmed: Boolean) {
        isWordConfirmed_7ree = confirmed
    }

    fun updateFromCache_7ree(fromCache: Boolean) {
        isFromCache_7ree = fromCache
    }

    fun updateCurrentWordInfo_7ree(wordInfo: WordEntity_7ree?) {
        currentWordInfo_7ree = wordInfo
    }

    fun updateSpeakingState_7ree(speaking: Boolean) {
        isSpeaking_7ree = speaking
    }

    fun updateSpeakingWordState_7ree(speaking: Boolean) {
        isSpeakingWord_7ree = speaking
        isSpeaking_7ree = speaking
    }

    fun updateSpeakingExamplesState_7ree(speaking: Boolean) {
        isSpeakingExamples_7ree = speaking
        isSpeaking_7ree = speaking
    }

    fun updateOperationResult_7ree(message: String?) {
        _operationResult_7ree.value = message
    }

    fun clearOperationResult_7ree() {
        _operationResult_7ree.value = null
    }

    fun resetQueryState_7ree() {
        wordInput_7ree = ""
        queryResult_7ree = ""
        isWordConfirmed_7ree = false
        isFromCache_7ree = false
        currentWordInfo_7ree = null
        clearOperationResult_7ree()
    }
}