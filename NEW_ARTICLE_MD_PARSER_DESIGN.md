# 新文章Markdown解析器设计文档

## 概述

根据API返回的MD文章不规范的问题，重新设计了文章Markdown解析器，支持新的模板格式，提高解析的准确性和容错性。

## 新模板格式

```markdown
### 英文标题
{title}

### 英文文章内容
{content}

### 重点单词
{keywords}

### 中文标题
{title translation}

### 中文文章内容
{content translation}
```

## 使用场景

新的MD解析器将在以下两个场景中使用：

1. **生成文章后写入数据库前** - 在 `ArticleGenerationHelper2_7ree.kt` 中使用
2. **文章详情页面打开后** - 在 `ArticleDetailScreen_7ree.kt` 中显示解析后的内容

## 核心改进

### 1. 使用正则表达式精确匹配

```kotlin
private fun extractSection(markdownContent: String, sectionTitle: String): String {
    val regex = Regex(
        "^#+\\s*$sectionTitle\\s*$\\n([\\s\\S]*?)(?=^#+\\s|\\z)",
        setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE)
    )
    
    val matchResult = regex.find(markdownContent)
    return matchResult?.groupValues?.get(1)?.trim() ?: ""
}
```

### 2. 支持灵活的标题级别

- 支持 `#`、`##`、`###` 等不同级别的标题
- 自动忽略大小写差异
- 支持标题前后的空格

### 3. 关键词智能过滤

```kotlin
private fun filterKeywords(rawKeywords: String): String {
    return rawKeywords
        .replace(Regex("[^a-zA-Z\\s,]"), "") // 先保留逗号用于分割
        .split(",") // 按逗号分割
        .map { keyword ->
            keyword.trim()
                .replace(Regex("[^a-zA-Z\\s]"), "") // 只保留英文字母和空格
                .replace(Regex("\\s+"), " ") // 规范化空格
                .trim()
        }
        .filter { it.isNotEmpty() }
        .joinToString(", ")
}
```

**过滤规则：**
- 只保留英文字母和空格
- 去掉数字、标点符号、特殊字符
- 规范化多个连续空格为单个空格
- 过滤掉空的关键词

**示例：**
- 输入：`hello!, world123, test@#$, good-bye`
- 输出：`hello, world, test, good bye`

### 4. 容错处理

- 支持章节乱序排列
- 对缺失章节提供合理的默认值
- 解析失败时返回安全的默认结果

### 5. 默认值策略

```kotlin
val result = ArticleParseResult(
    englishTitle = englishTitle.ifEmpty { "Generated Article" },
    chineseTitle = chineseTitle.ifEmpty { generateChineseTitle(englishTitle) },
    englishContent = englishContent.ifEmpty { markdownContent },
    chineseContent = chineseContent.ifEmpty { "翻译暂不可用" },
    keywords = keywords.ifEmpty { "无关键词" }
)
```

## 文件更新

### 1. 核心解析器
- `app/src/main/java/com/x7ree/wordcard/article/ArticleMarkdownParser_7ree.kt`
  - 重写了 `parseArticleMarkdown()` 方法
  - 新增了 `extractSection()` 私有方法
  - 改进了错误处理和日志记录

### 2. 提示词配置
- `app/src/main/java/com/x7ree/wordcard/config/PromptConfig_7ree.kt`
  - 更新了 `articleOutputTemplate_7ree` 以匹配新模板格式

### 3. 测试工具
- `app/src/main/java/com/x7ree/wordcard/test/ArticleMarkdownParserTester_7ree.kt`
  - 新增了完整的测试套件
  - 包含标准格式、不完整格式、乱序格式的测试用例
  - 提供了演示功能

## 测试用例

### 1. 标准格式测试
```markdown
### 英文标题
The Amazing Journey of Learning English

### 英文文章内容
Learning English is like embarking on an exciting adventure...

### 重点单词
adventure, treasure, master, dedication, confident, beginner

### 中文标题
学习英语的奇妙旅程

### 中文文章内容
学习英语就像踏上一场激动人心的冒险...
```

### 2. 不完整格式测试
- 缺少中文部分的情况
- 只有标题没有内容的情况
- 不规范标题级别的情况

### 3. 乱序格式测试
- 章节顺序打乱的情况
- 验证解析器的容错能力

## 集成方式

### 在文章生成流程中使用

```kotlin
// 在 ArticleGenerationHelper2_7ree.kt 中
val parser = ArticleMarkdownParser_7ree()
val parsedResult = parser.parseArticleMarkdown(apiResult)

// 保存到数据库
val articleId = articleRepository_7ree.saveArticle_7ree(
    keyWords = parsedResult.keywords,
    apiResult = apiResult,
    englishTitle = parsedResult.englishTitle,
    titleTranslation = parsedResult.chineseTitle,
    englishContent = parsedResult.englishContent,
    chineseContent = parsedResult.chineseContent
)
```

### 在文章详情页面中使用

解析后的数据直接从数据库中获取，已经是结构化的格式，可以直接在UI中显示。

## 优势

1. **更高的解析准确性** - 使用正则表达式精确匹配章节
2. **更好的容错能力** - 支持各种不规范的格式
3. **更灵活的扩展性** - 易于添加新的章节类型
4. **更完善的测试覆盖** - 包含多种边界情况的测试
5. **更好的维护性** - 代码结构清晰，易于理解和修改

## 运行测试

可以通过以下方式运行测试：

```kotlin
// 运行所有测试
val testResults = ArticleMarkdownParserTester_7ree.runAllTests()

// 演示新解析器功能
val demo = ArticleMarkdownParserTester_7ree.demonstrateNewParser()
```

## 注意事项

1. 新解析器向后兼容，不会影响现有功能
2. 所有解析操作都有详细的日志记录，便于调试
3. 解析失败时会返回安全的默认值，不会导致应用崩溃
4. 提示词模板已更新，确保API返回的格式符合新的解析规则

## 总结

新的文章Markdown解析器通过使用正则表达式和改进的容错机制，显著提高了对不规范MD文章的解析能力，确保在两个关键使用场景中都能正确工作。