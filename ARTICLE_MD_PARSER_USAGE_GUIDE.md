# 文章Markdown解析器使用指南

## 快速开始

新的文章Markdown解析器已经集成到应用中，支持更规范和灵活的文章格式解析。

## 新模板格式

API应该返回以下格式的Markdown内容：

```markdown
### 英文标题
The Amazing Journey of Learning English

### 英文文章内容
Learning English is like embarking on an exciting adventure. Every new word you discover is a treasure, and every sentence you master is a step forward on your journey.

### 重点单词
adventure, treasure, master, dedication, confident, beginner

### 中文标题
学习英语的奇妙旅程

### 中文文章内容
学习英语就像踏上一场激动人心的冒险。你发现的每一个新单词都是一个宝藏，你掌握的每一个句子都是你旅程中向前迈出的一步。
```

## 解析器特性

### ✅ 支持的格式
- 标准的三级标题格式 (`### 标题`)
- 二级标题格式 (`## 标题`)
- 一级标题格式 (`# 标题`)
- 章节乱序排列
- 部分章节缺失

### ✅ 容错处理
- 自动提供默认值
- 忽略大小写差异
- 处理标题前后的空格
- 解析失败时的安全降级

### ✅ 关键词智能过滤
- 只保留英文字母和空格
- 自动去除数字、标点符号、特殊字符
- 规范化多个连续空格
- 过滤空的关键词项

### ✅ 字段映射
| 解析字段 | 数据库字段 | UI显示位置 |
|---------|-----------|-----------|
| englishTitle | englishTitle | 文章标题卡片 |
| chineseTitle | titleTranslation | 文章标题卡片 |
| englishContent | englishContent | 英文文章卡片 |
| chineseContent | chineseContent | 中文翻译卡片 |
| keywords | keyWords | 关键词卡片 |

## 使用场景

### 1. 文章生成流程
在 `ArticleGenerationHelper2_7ree.kt` 中自动使用：

```kotlin
// API调用后自动解析
val parser = ArticleMarkdownParser_7ree()
val parsedResult = parser.parseArticleMarkdown(apiResult)

// 保存到数据库
articleRepository_7ree.saveArticle_7ree(
    keyWords = parsedResult.keywords,
    englishTitle = parsedResult.englishTitle,
    titleTranslation = parsedResult.chineseTitle,
    englishContent = parsedResult.englishContent,
    chineseContent = parsedResult.chineseContent
)
```

### 2. 文章详情页面显示
在 `ArticleDetailScreen_7ree.kt` 中直接使用数据库字段：

```kotlin
// 显示英文标题
Text(text = article.englishTitle)

// 显示中文标题
Text(text = article.titleTranslation)

// 显示英文内容
Text(text = article.englishContent)

// 显示中文内容
Text(text = article.chineseContent)

// 显示关键词
KeywordTags(keywords = article.keyWords.split(","))
```

## 测试和调试

### 运行测试
```kotlin
// 运行完整测试套件
val testResults = ArticleMarkdownParserTester_7ree.runAllTests()
println(testResults)

// 演示解析器功能
val demo = ArticleMarkdownParserTester_7ree.demonstrateNewParser()
println(demo)
```

### 查看解析日志
解析器会输出详细的日志信息，标签为 `ArticleMarkdownParser`：

```
D/ArticleMarkdownParser: 开始解析文章Markdown内容
D/ArticleMarkdownParser: 原始内容长度: 1234
D/ArticleMarkdownParser: 提取章节: 英文标题
D/ArticleMarkdownParser: 章节 '英文标题' 内容长度: 45
```

## 常见问题

### Q: 如果API返回的格式不完整怎么办？
A: 解析器会自动提供默认值：
- 缺少英文标题 → "Generated Article"
- 缺少中文标题 → 根据英文标题生成
- 缺少内容 → 使用原始Markdown内容
- 缺少关键词 → "无关键词"

### Q: 支持哪些标题级别？
A: 支持 `#`、`##`、`###` 三种级别的标题，解析器会自动识别。

### Q: 章节顺序重要吗？
A: 不重要。解析器使用正则表达式精确匹配，支持任意顺序的章节排列。

### Q: 如何处理解析失败？
A: 解析器永远不会抛出异常，失败时会返回包含原始内容的安全默认结果。

### Q: 关键词过滤是如何工作的？
A: 解析器会自动清理关键词，示例：

**输入：**
```
### 重点单词
hello!, world123, test@#$, good-bye, don't
```

**输出：**
```
hello, world, test, good bye, dont
```

**过滤规则：**
- 保留：英文字母 (a-z, A-Z) 和空格
- 移除：数字、标点符号、特殊字符
- 规范化：多个空格变为单个空格
- 清理：空的关键词项

## 配置更新

提示词配置已自动更新为新格式：

```kotlin
// 在 PromptConfig_7ree.kt 中
val articleOutputTemplate_7ree: String = """### 英文标题
{英文标题}

### 英文文章内容
{英文文章内容}

### 重点单词
{关键词，用逗号分隔}

### 中文标题
{中文标题}

### 中文文章内容
{中文翻译内容}"""
```

## 总结

新的文章Markdown解析器提供了：
- 🎯 更准确的解析结果
- 🛡️ 更强的容错能力
- 🔧 更好的维护性
- 📊 完整的测试覆盖

无需额外配置，解析器已自动集成到文章生成和显示流程中。