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
{antonyms}""",
    val articleGenerationPrompt_7ree: String = """请根据提供的重点单词生成一篇英文文章，要求：

1. 文章长度适中（150-200词）
2. 自然地使用提供的单词
3. 文章内容通俗易懂、幽默、口语化
4. 语法正确，表达流畅
5. 中英文文章对照采用一句中文一句英文一个空行的格式输出文章的全文
6. 中英文的文中重点单词对照请用双星号**包裹
7. 适合单词量少的英语初学者阅读
8. 请确保文章质量高、符合逻辑，能够帮助用户在语境中理解和记忆单词

现在不输出完整英文文章和中文文章，只输出中英文章对照（按照中英文句子对照输出）。""",
    val articleOutputTemplate_7ree: String = """### 英文标题
{title}

### 中文标题
{title translation}

### 重点单词
{keywords}

### 中英文章对照
[英文]{英文句子1}

[中文]{中文翻译句子1}

[英文]{英文句子2}

[中文]{中文翻译句子2}

[英文]{英文句子n}

[中文]{中文翻译句子n}"""
) 