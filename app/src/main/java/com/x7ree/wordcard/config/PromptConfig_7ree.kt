package com.x7ree.wordcard.config

import kotlinx.serialization.Serializable

/**
语言包定义

    'pl_prompt_config_7r' => '提示词配置',
    'pl_query_prompt_7r' => '查询提示词',
    'pl_output_template_7r' => '输出模板',
**/

@Serializable
data class PromptConfig_7ree(
    val queryPrompt_7ree: String = """请用Markdown格式解释以下英文单词，要求输出包含以下内容：

1. 单词本身
2. 音标
3. 词性
4. 中文定义
5. 3-5个英文例句（不要包含中文翻译）
6. 例句的中文翻译
7. 同义词
8. 反义词

请严格按照提供的模板格式输出，确保内容准确完整。""",
    val outputTemplate_7ree: String = """# 单词
{word}

# 音标
{phonetic}

# 词性
{partOfSpeech}

# 定义
{definition}

# 英文例句
{englishExamples}

# 例句中文翻译
{chineseTranslations}

# 同义词
{synonyms}

# 反义词
{antonyms}"""
) 