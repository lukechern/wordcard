package com.x7ree.wordcard.query.tts

import com.x7ree.wordcard.query.manager.WordQueryManager_7ree
import com.x7ree.wordcard.query.manager.TtsManager_7ree

/**
 * TTS功能处理模块
 */
class TtsHandler_7ree(
    private val wordQueryManager_7ree: WordQueryManager_7ree,
    private val ttsManager_7ree: TtsManager_7ree
) {
    
    /**
     * 设置朗读状态
     */
    fun setIsSpeaking_7ree(speaking: Boolean, speakingStateUpdater: (Boolean) -> Unit) {
        speakingStateUpdater(speaking)
    }
    
    /**
     * 设置单词朗读状态
     */
    fun setIsSpeakingWord_7ree(speaking: Boolean, speakingWordStateUpdater: (Boolean) -> Unit) {
        speakingWordStateUpdater(speaking)
    }
    
    /**
     * 设置例句朗读状态
     */
    fun setIsSpeakingExamples_7ree(speaking: Boolean, speakingExamplesStateUpdater: (Boolean) -> Unit) {
        speakingExamplesStateUpdater(speaking)
    }
    
    /**
     * 获取单词朗读文本
     */
    fun getWordSpeechText_7ree(wordInput: String): String {
        return wordInput
    }
    
    /**
     * 获取例句朗读文本
     */
    fun getExamplesSpeechText_7ree(): String {
        return wordQueryManager_7ree.getExamplesSpeechText_7ree()
    }
    
    /**
     * 朗读单词
     */
    fun speakWord_7ree(word: String) {
        ttsManager_7ree.speakWord_7ree(word)
    }
    
    /**
     * 朗读例句
     */
    fun speakExamples_7ree() {
        val examplesText = getExamplesSpeechText_7ree()
        ttsManager_7ree.speakExamples_7ree(examplesText)
    }
    
    /**
     * 停止朗读
     */
    fun stopSpeaking_7ree() {
        ttsManager_7ree.stopSpeaking_7ree()
    }
    
    /**
     * 获取TTS引擎状态
     */
    fun getTtsEngineStatus_7ree(): String {
        return ttsManager_7ree.getTtsEngineStatus_7ree()
    }
}
