package com.x7ree.wordcard.article.utils

import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.config.PromptConfig_7ree
import com.x7ree.wordcard.article.ArticleMarkdownParser_7ree
import com.x7ree.wordcard.data.ArticleRepository_7ree

class ArticleGenerationHelper_7ree(
    private val wordRepository_7ree: WordRepository_7ree?,
    private val articleRepository_7ree: ArticleRepository_7ree,
    private val apiService_7ree: OpenAiApiService_7ree
) {
    /**
     * 构建文章生成提示词
     */
    fun buildArticlePrompt(keyWords: String, promptConfig: PromptConfig_7ree): String {
        return """
            ${promptConfig.articleGenerationPrompt_7ree}
            
            请基于以下关键词生成文章：$keyWords
            
            请严格按照以下模板格式输出：
            ${promptConfig.articleOutputTemplate_7ree}
        """.trimIndent()
    }
}