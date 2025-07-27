package com.x7ree.wordcard.article.utils

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
                onGenerating(true)
                onResult("正在生成文章...")
                
                // 获取提示词配置
                val promptConfig = appConfigManager_7ree.loadPromptConfig_7ree()
                
                // 构建生成文章的提示词
                val prompt = articleGenerationHelper_7ree.buildArticlePrompt(keyWords, promptConfig)
                
                // 调用API生成文章
                val apiResultWrapper = apiService_7ree.queryWord_7ree(prompt)
                
                // 检查API调用结果
                val apiResult = apiResultWrapper.getOrElse { error ->
                    throw Exception("API调用失败: ${error.message}")
                }
                
                // 解析API结果
                val parser = ArticleMarkdownParser_7ree()
                val parsedResult = parser.parseArticleMarkdown(apiResult)
                
                // 保存到数据库
                val articleId = articleRepository_7ree.saveArticle_7ree(
                    keyWords = parsedResult.keywords, // 使用解析出的关键词
                    apiResult = apiResult,
                    englishTitle = parsedResult.englishTitle,
                    titleTranslation = parsedResult.chineseTitle,
                    englishContent = parsedResult.englishContent,
                    chineseContent = parsedResult.chineseContent
                )
                
                onResult("文章生成成功！")
                
            } catch (e: Exception) {
                onResult("文章生成失败: ${e.message}")
            } finally {
                onGenerating(false)
            }
        }
    }
}
