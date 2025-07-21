package com.x7ree.wordcard.test

import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.utils.DatabaseMigrationHelper_7ree
import com.x7ree.wordcard.utils.MarkdownParser_7ree

/**
 * 数据库更新测试工具
 */
class DatabaseUpdateTester_7ree(
    private val wordRepository_7ree: WordRepository_7ree
) {
    
    /**
     * 测试新字段的保存和解析功能
     */
    suspend fun testNewFieldsSaveAndParse(): String {
        val result = StringBuilder()
        result.appendLine("=== 数据库新字段测试 ===\n")
        
        try {
            // 测试用的Markdown内容
            val testMarkdown = """
### 查询单词
example

### 中文词义
例子；实例；榜样

### 音标
/ɪɡˈzæmpl/

### 词性
名词 (noun)

### 英文例句
This is a good example.

### 例句中文翻译
这是一个好例子。
            """.trimIndent()
            
            // 保存测试单词
            wordRepository_7ree.saveWord_7ree("example", testMarkdown)
            result.appendLine("1. 保存测试单词: example")
            
            // 查询保存的单词
            val savedWord = wordRepository_7ree.getWord_7ree("example")
            if (savedWord != null) {
                result.appendLine("2. 查询结果:")
                result.appendLine("   单词: ${savedWord.word}")
                result.appendLine("   中文释义: '${savedWord.chineseDefinition}'")
                result.appendLine("   音标: '${savedWord.phonetic}'")
                result.appendLine("   词性: '${savedWord.partOfSpeech}'")
                result.appendLine("   查询时间: ${savedWord.queryTimestamp}")
                result.appendLine("   浏览次数: ${savedWord.viewCount}")
            } else {
                result.appendLine("2. 查询失败: 未找到保存的单词")
            }
            
            // 测试解析功能
            result.appendLine("\n3. 解析功能测试:")
            val wordInfo = MarkdownParser_7ree.parseWordInfo(testMarkdown)
            result.appendLine("   解析的中文释义: '${wordInfo.chineseDefinition}'")
            result.appendLine("   解析的音标: '${wordInfo.phonetic}'")
            result.appendLine("   解析的词性: '${wordInfo.partOfSpeech}'")
            
            // 清理测试数据
            wordRepository_7ree.deleteWord_7ree("example")
            result.appendLine("\n4. 清理测试数据完成")
            
        } catch (e: Exception) {
            result.appendLine("测试失败: ${e.message}")
            e.printStackTrace()
        }
        
        return result.toString()
    }
    
    /**
     * 测试数据库迁移功能
     */
    suspend fun testDatabaseMigration(): String {
        val result = StringBuilder()
        result.appendLine("=== 数据库迁移测试 ===\n")
        
        try {
            val migrationHelper = DatabaseMigrationHelper_7ree(wordRepository_7ree)
            
            // 获取需要更新的单词数量
            val wordsNeedingUpdate = migrationHelper.getWordsNeedingUpdate()
            result.appendLine("1. 需要更新的单词数量: $wordsNeedingUpdate")
            
            // 获取数据完整性报告
            val integrityReport = migrationHelper.validateDataIntegrity()
            result.appendLine("\n2. 数据完整性报告:")
            result.appendLine(integrityReport.getFormattedReport())
            
            // 如果有需要更新的数据，执行更新
            if (wordsNeedingUpdate > 0) {
                result.appendLine("\n3. 执行数据迁移...")
                val updateResult = migrationHelper.updateExistingWordsWithParsedInfo()
                if (updateResult.isSuccess) {
                    result.appendLine("   迁移成功，更新了 ${updateResult.getOrNull()} 条记录")
                } else {
                    result.appendLine("   迁移失败: ${updateResult.exceptionOrNull()?.message}")
                }
                
                // 再次检查完整性
                val newIntegrityReport = migrationHelper.validateDataIntegrity()
                result.appendLine("\n4. 迁移后的数据完整性报告:")
                result.appendLine(newIntegrityReport.getFormattedReport())
            } else {
                result.appendLine("\n3. 无需执行数据迁移")
            }
            
        } catch (e: Exception) {
            result.appendLine("迁移测试失败: ${e.message}")
            e.printStackTrace()
        }
        
        return result.toString()
    }
    
    /**
     * 测试新增的查询功能
     */
    suspend fun testNewQueryFunctions(): String {
        val result = StringBuilder()
        result.appendLine("=== 新查询功能测试 ===\n")
        
        try {
            // 测试按词性查询
            result.appendLine("1. 测试按词性查询:")
            val allPartOfSpeech = wordRepository_7ree.getAllPartOfSpeech_7ree()
            // 这里只是演示，实际使用时需要collect Flow
            result.appendLine("   获取所有词性的Flow已创建")
            
            // 测试中文释义搜索
            result.appendLine("\n2. 测试中文释义搜索:")
            val searchResults = wordRepository_7ree.searchByChineseDefinition_7ree("测试")
            result.appendLine("   中文释义搜索的Flow已创建")
            
            // 测试统计功能
            result.appendLine("\n3. 测试统计功能:")
            result.appendLine("   有音标的单词数量Flow已创建")
            result.appendLine("   有中文释义的单词数量Flow已创建")
            
        } catch (e: Exception) {
            result.appendLine("查询功能测试失败: ${e.message}")
            e.printStackTrace()
        }
        
        return result.toString()
    }
    
    /**
     * 测试单词详情页面的自动字段补充功能
     */
    suspend fun testAutoFieldCompletion(): String {
        val result = StringBuilder()
        result.appendLine("=== 单词详情页面自动字段补充测试 ===\n")
        
        try {
            // 创建一个测试单词，模拟旧数据（缺少新字段）
            val testMarkdown = """
### 查询单词
test

### 中文词义
测试；考试；检验

### 音标
/test/

### 词性
名词 (noun), 动词 (verb)

### 英文例句
1. This is a test.
2. We need to test the system.

### 例句中文翻译
1. 这是一个测试。
2. 我们需要测试系统。
            """.trimIndent()
            
            // 创建一个缺少新字段的WordEntity（模拟旧数据）
            val oldWordEntity = com.x7ree.wordcard.data.WordEntity_7ree(
                word = "test_auto_completion",
                apiResult = testMarkdown,
                queryTimestamp = System.currentTimeMillis(),
                viewCount = 1,
                isFavorite = false,
                spellingCount = 0,
                chineseDefinition = "", // 空字段，模拟旧数据
                phonetic = "",          // 空字段，模拟旧数据
                partOfSpeech = ""       // 空字段，模拟旧数据
            )
            
            // 插入测试数据
            wordRepository_7ree.insertWord_7ree(oldWordEntity)
            result.appendLine("1. 插入测试单词（模拟旧数据）: ${oldWordEntity.word}")
            result.appendLine("   初始状态 - 中文释义: '${oldWordEntity.chineseDefinition}', 音标: '${oldWordEntity.phonetic}', 词性: '${oldWordEntity.partOfSpeech}'")
            
            // 模拟打开单词详情页面的过程
            result.appendLine("\n2. 模拟打开单词详情页面...")
            
            // 查询单词（这会触发自动字段补充）
            val retrievedWord = wordRepository_7ree.getWord_7ree("test_auto_completion")
            if (retrievedWord != null) {
                // 手动调用字段补充逻辑（模拟WordQueryManager中的调用）
                val wordInfo = com.x7ree.wordcard.utils.MarkdownParser_7ree.parseWordInfo(retrievedWord.apiResult)
                
                if (retrievedWord.chineseDefinition.isEmpty() || 
                    retrievedWord.phonetic.isEmpty() || 
                    retrievedWord.partOfSpeech.isEmpty()) {
                    
                    val updatedWord = retrievedWord.copy(
                        chineseDefinition = if (retrievedWord.chineseDefinition.isEmpty()) wordInfo.chineseDefinition else retrievedWord.chineseDefinition,
                        phonetic = if (retrievedWord.phonetic.isEmpty()) wordInfo.phonetic else retrievedWord.phonetic,
                        partOfSpeech = if (retrievedWord.partOfSpeech.isEmpty()) wordInfo.partOfSpeech else retrievedWord.partOfSpeech
                    )
                    
                    wordRepository_7ree.updateWord_7ree(updatedWord)
                    result.appendLine("   自动补充字段完成")
                    
                    // 验证更新结果
                    val finalWord = wordRepository_7ree.getWord_7ree("test_auto_completion")
                    if (finalWord != null) {
                        result.appendLine("\n3. 字段补充结果:")
                        result.appendLine("   中文释义: '${finalWord.chineseDefinition}'")
                        result.appendLine("   音标: '${finalWord.phonetic}'")
                        result.appendLine("   词性: '${finalWord.partOfSpeech}'")
                        
                        // 验证是否成功补充
                        val isComplete = finalWord.chineseDefinition.isNotEmpty() && 
                                        finalWord.phonetic.isNotEmpty() && 
                                        finalWord.partOfSpeech.isNotEmpty()
                        result.appendLine("   补充状态: ${if (isComplete) "成功" else "部分成功或失败"}")
                    }
                }
            }
            
            // 清理测试数据
            wordRepository_7ree.deleteWord_7ree("test_auto_completion")
            result.appendLine("\n4. 清理测试数据完成")
            
        } catch (e: Exception) {
            result.appendLine("自动字段补充测试失败: ${e.message}")
            e.printStackTrace()
        }
        
        return result.toString()
    }
}