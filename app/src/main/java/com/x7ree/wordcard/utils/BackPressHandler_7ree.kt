package com.x7ree.wordcard.utils

import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import com.x7ree.wordcard.query.WordQueryViewModel_7ree

class BackPressHandler_7ree(
    private val dispatcherOwner: OnBackPressedDispatcherOwner,
    private val wordQueryViewModel_7ree: WordQueryViewModel_7ree?
) {
    private var backPressedTime_7ree: Long = 0
    private var exitToast_7ree: Toast? = null
    private val exitMessage_7ree = "再按一次退出应用"
    
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handleBackPress()
        }
    }
    
    init {
        dispatcherOwner.onBackPressedDispatcher.addCallback(dispatcherOwner, onBackPressedCallback)
    }
    
    private fun handleBackPress() {
        // 检查是否从单词本进入单词详情页面
        val isFromWordBook_7ree = wordQueryViewModel_7ree?.isFromWordBook_7ree?.value ?: false
        val currentScreen_7ree = wordQueryViewModel_7ree?.currentScreen_7ree?.value ?: "SEARCH"
        
        if (isFromWordBook_7ree && currentScreen_7ree == "SEARCH") {
            // 如果是从单词本进入的单词详情页面，直接返回单词本
            wordQueryViewModel_7ree?.returnToWordBook_7ree()
            return
        }
        
        // 检查是否从文章列表进入文章详情页面
        val articleViewModel = wordQueryViewModel_7ree?.articleViewModel_7ree
        val isFromArticleList = articleViewModel?.isFromArticleList?.value ?: false
        val showDetailScreen = articleViewModel?.showDetailScreen?.value ?: false
        
        if (isFromArticleList && showDetailScreen) {
            // 如果是从文章列表进入的文章详情页面，先停止TTS再返回文章列表
            // 检查是否正在朗读，如果是则先停止
            val isReading = articleViewModel?.isReading?.value ?: false
            if (isReading) {
                articleViewModel?.stopReading()
            }
            
            // 返回文章列表
            articleViewModel?.returnToArticleList()
            return
        }
        
        val currentTime_7ree = System.currentTimeMillis()
        
        if (currentTime_7ree - backPressedTime_7ree > 2000) {
            // 第一次按返回键
            backPressedTime_7ree = currentTime_7ree
            exitToast_7ree?.cancel() // 取消之前的Toast
            exitToast_7ree = Toast.makeText(dispatcherOwner as android.content.Context, exitMessage_7ree, Toast.LENGTH_SHORT)
            exitToast_7ree?.show()
        } else {
            // 第二次按返回键，退出应用
            exitToast_7ree?.cancel()
            // 直接调用系统的返回处理
            onBackPressedCallback.isEnabled = false
            dispatcherOwner.onBackPressedDispatcher.onBackPressed()
        }
    }
    
    fun cancelToast() {
        exitToast_7ree?.cancel()
    }
}
