package com.x7ree.wordcard.article.utils

import android.util.Log
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.config.AppConfigManager_7ree
import com.x7ree.wordcard.data.ArticleRepository_7ree
import com.x7ree.wordcard.article.ArticleMarkdownParser_7ree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ArticleGenerationHelper2_7ree(
    private val apiService_7ree: OpenAiApiService_7ree,
    private val appConfigManager_7ree: AppConfigManager_7ree,
    private val articleRepository_7ree: ArticleRepository_7ree,
    private val articleGenerationHelper_7ree: ArticleGenerationHelper_7ree
) {
    companion object {
        private const val TAG = "ArticleGenerationHelper2"
    }
    
    /**
     * 生成新文章
     */
    fun generateArticle(
        keyWords: String,
        coroutineScope: CoroutineScope,
        onGenerating: (Boolean) -> Unit,
        onResult: (String) -> Unit
    ) {
        coroutineScope.launch {
            try {
                Log.d(TAG, "==================== 开始生成文章 ====================")
                Log.d(TAG, "输入关键词: '$keyWords'")
                
                onGenerating(true)
                onResult("正在生成文章...")
                
                // 获取提示词配置
                val promptConfig = appConfigManager_7ree.loadPromptConfig_7ree()
                Log.d(TAG, "获取提示词配置完成")
                
                // 构建生成文章的提示词
                val prompt = articleGenerationHelper_7ree.buildArticlePrompt(keyWords, promptConfig)
                Log.d(TAG, "构建的提示词:")
                Log.d(TAG, "---BEGIN PROMPT---")
                Log.d(TAG, prompt)
                Log.d(TAG, "---END PROMPT---")
                
                // 调用API生成文章
                Log.d(TAG, "开始调用API...")
                val apiResultWrapper = apiService_7ree.queryWord_7ree(prompt)
                
                // 检查API调用结果
                val apiResult = apiResultWrapper.getOrElse { error ->
                    Log.e(TAG, "API调用失败: ${error.message}")
                    throw Exception("API调用失败: ${error.message}")
                }
                
                Log.d(TAG, "API调用成功，返回内容长度: ${apiResult.length}")
                Log.d(TAG, "API返回的原始内容:")
                Log.d(TAG, "---BEGIN API RESULT---")
                Log.d(TAG, apiResult)
                Log.d(TAG, "---END API RESULT---")
                
                // 解析API结果
                Log.d(TAG, "开始解析API结果...")
                val parser = ArticleMarkdownParser_7ree()
                val parsedResult = parser.parseArticleMarkdown(apiResult)
                
                Log.d(TAG, "解析完成，准备保存到数据库...")
                Log.d(TAG, "保存的数据:")
                Log.d(TAG, "关键词: '${parsedResult.keywords}'")
                Log.d(TAG, "英文标题: '${parsedResult.englishTitle}'")
                Log.d(TAG, "中文标题: '${parsedResult.chineseTitle}'")
                Log.d(TAG, "英文内容长度: ${parsedResult.englishContent.length}")
                Log.d(TAG, "英文内容: '${parsedResult.englishContent}'")
                Log.d(TAG, "中文内容长度: ${parsedResult.chineseContent.length}")
                Log.d(TAG, "中文内容: '${parsedResult.chineseContent}'")
                Log.d(TAG, "中英对照长度: ${parsedResult.bilingualComparison.length}")
                
                // 获取当前使用的API名称
                val currentApiConfig = appConfigManager_7ree.loadApiConfig_7ree()
                val activeApiConfig = currentApiConfig.getActiveTranslationApi()
                val apiName = activeApiConfig.apiName.ifEmpty { "未知API" }
                
                // 保存到数据库
                val articleId = articleRepository_7ree.saveArticle_7ree(
                    keyWords = parsedResult.keywords, // 使用解析出的关键词
                    apiResult = apiResult,
                    englishTitle = parsedResult.englishTitle,
                    titleTranslation = parsedResult.chineseTitle,
                    englishContent = parsedResult.englishContent,
                    chineseContent = parsedResult.chineseContent,
                    bilingualComparison = parsedResult.bilingualComparison,
                    author = apiName
                )
                
                Log.d(TAG, "文章保存成功，ID: $articleId")
                
                // 确保数据库操作完成后再发出成功消息
                // 添加短暂延时确保数据库事务完成
                kotlinx.coroutines.delay(100)
                
                Log.d(TAG, "==================== 文章生成完成 ====================")
                onResult("文章生成成功！")
                
            } catch (e: Exception) {
                Log.e(TAG, "文章生成失败: ${e.message}", e)
                onResult("文章生成失败: ${e.message}")
            } finally {
                onGenerating(false)
            }
        }
    }
}
