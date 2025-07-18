package com.x7ree.wordcard.widget

import android.content.Context
import android.util.Log
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Widget搜索管理器
 * 负责处理单词查询的核心逻辑
 */
class WidgetSearchManager_7ree(
    private val wordRepository_7ree: WordRepository_7ree,
    private val apiService_7ree: OpenAiApiService_7ree
) {
    
    private val TAG_7ree = "WidgetSearchManager_7ree"
    
    /**
     * 搜索结果数据类
     */
    data class SearchResult_7ree(
        val isFromCache: Boolean,
        val content: String,
        val isComplete: Boolean = false
    )
    
    /**
     * 执行单词搜索
     * @param queryText 查询的单词
     * @return Flow<SearchResult_7ree> 搜索结果流
     */
    suspend fun searchWord_7ree(queryText: String): Flow<SearchResult_7ree> = flow {
        try {
            // 先查询本地缓存
            val cachedWord = wordRepository_7ree.getWord_7ree(queryText)
            
            if (cachedWord != null) {
                // 从缓存获取结果
                emit(SearchResult_7ree(
                    isFromCache = true,
                    content = cachedWord.apiResult,
                    isComplete = true
                ))
                
                // 增加浏览次数
                wordRepository_7ree.incrementViewCount_7ree(queryText)
            } else {
                // 发起API请求
                var fullResult = ""
                apiService_7ree.queryWordStreamSimple_7ree(queryText).collect { chunk ->
                    fullResult += chunk
                    
                    // 实时更新显示内容
                    emit(SearchResult_7ree(
                        isFromCache = false,
                        content = fullResult,
                        isComplete = false
                    ))
                }
                
                // 流式返回结束后，发送完成信号
                emit(SearchResult_7ree(
                    isFromCache = false,
                    content = fullResult,
                    isComplete = true
                ))
                
                // 保存到数据库
                if (fullResult.isNotBlank() && !fullResult.startsWith("错误:")) {
                    wordRepository_7ree.saveWord_7ree(queryText, fullResult)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG_7ree, "搜索失败: ${e.message}", e)
            emit(SearchResult_7ree(
                isFromCache = false,
                content = "查询失败: ${e.localizedMessage}",
                isComplete = true
            ))
        }
    }
    
    /**
     * 提取基本信息
     */
    fun extractBasicInfo_7ree(fullResult: String): Pair<String, String> {
        if (fullResult.isBlank()) {
            return Pair("", "")
        }
        
        // 使用WidgetMarkdownParser_7ree解析内容
        val parsedContent = WidgetMarkdownParser_7ree.parseBasicInfo_7ree(fullResult)
        
        // 构建主要显示内容（单词和中文意思）
        val mainContent = StringBuilder()
        if (parsedContent.word.isNotEmpty()) {
            mainContent.append(parsedContent.word)
        }
        if (parsedContent.chineseMeaning.isNotEmpty()) {
            if (mainContent.isNotEmpty()) mainContent.append("\n")
            mainContent.append(parsedContent.chineseMeaning)
        }
        
        return Pair(mainContent.toString(), fullResult)
    }
}