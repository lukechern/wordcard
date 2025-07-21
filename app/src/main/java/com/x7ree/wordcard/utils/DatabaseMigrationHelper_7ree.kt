package com.x7ree.wordcard.utils

import com.x7ree.wordcard.data.WordRepository_7ree
import kotlinx.coroutines.flow.first

/**
 * 数据库迁移辅助工具
 * 用于为现有数据补充新添加的字段信息
 */
class DatabaseMigrationHelper_7ree(
    private val wordRepository_7ree: WordRepository_7ree
) {
    
    /**
     * 为现有数据补充中文释义、音标、词性信息
     * 从已存储的apiResult中解析这些信息
     */
    suspend fun updateExistingWordsWithParsedInfo(): Result<Int> {
        return try {
            val allWords = wordRepository_7ree.getAllWords_7ree().first()
            var updatedCount = 0
            
            for (word in allWords) {
                // 检查是否需要更新（新字段为空）
                if (word.chineseDefinition.isEmpty() || 
                    word.phonetic.isEmpty() || 
                    word.partOfSpeech.isEmpty()) {
                    
                    // 从API结果中解析信息
                    val wordInfo = MarkdownParser_7ree.parseWordInfo(word.apiResult)
                    
                    // 只有解析出有效信息才更新
                    if (wordInfo.chineseDefinition.isNotEmpty() || 
                        wordInfo.phonetic.isNotEmpty() || 
                        wordInfo.partOfSpeech.isNotEmpty()) {
                        
                        val updatedWord = word.copy(
                            chineseDefinition = if (word.chineseDefinition.isEmpty()) wordInfo.chineseDefinition else word.chineseDefinition,
                            phonetic = if (word.phonetic.isEmpty()) wordInfo.phonetic else word.phonetic,
                            partOfSpeech = if (word.partOfSpeech.isEmpty()) wordInfo.partOfSpeech else word.partOfSpeech
                        )
                        
                        wordRepository_7ree.updateWord_7ree(updatedWord)
                        updatedCount++
                    }
                }
            }
            
            Result.success(updatedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取需要更新的单词数量
     */
    suspend fun getWordsNeedingUpdate(): Int {
        return try {
            val allWords = wordRepository_7ree.getAllWords_7ree().first()
            allWords.count { word ->
                word.chineseDefinition.isEmpty() || 
                word.phonetic.isEmpty() || 
                word.partOfSpeech.isEmpty()
            }
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * 验证数据库中的数据完整性
     */
    suspend fun validateDataIntegrity(): DataIntegrityReport {
        return try {
            val allWords = wordRepository_7ree.getAllWords_7ree().first()
            val totalWords = allWords.size
            val wordsWithDefinition = allWords.count { it.chineseDefinition.isNotEmpty() }
            val wordsWithPhonetic = allWords.count { it.phonetic.isNotEmpty() }
            val wordsWithPartOfSpeech = allWords.count { it.partOfSpeech.isNotEmpty() }
            
            DataIntegrityReport(
                totalWords = totalWords,
                wordsWithDefinition = wordsWithDefinition,
                wordsWithPhonetic = wordsWithPhonetic,
                wordsWithPartOfSpeech = wordsWithPartOfSpeech,
                definitionCompleteness = if (totalWords > 0) (wordsWithDefinition.toFloat() / totalWords * 100) else 0f,
                phoneticCompleteness = if (totalWords > 0) (wordsWithPhonetic.toFloat() / totalWords * 100) else 0f,
                partOfSpeechCompleteness = if (totalWords > 0) (wordsWithPartOfSpeech.toFloat() / totalWords * 100) else 0f
            )
        } catch (e: Exception) {
            DataIntegrityReport()
        }
    }
}

/**
 * 数据完整性报告
 */
data class DataIntegrityReport(
    val totalWords: Int = 0,
    val wordsWithDefinition: Int = 0,
    val wordsWithPhonetic: Int = 0,
    val wordsWithPartOfSpeech: Int = 0,
    val definitionCompleteness: Float = 0f,
    val phoneticCompleteness: Float = 0f,
    val partOfSpeechCompleteness: Float = 0f
) {
    fun getFormattedReport(): String {
        return """
            数据完整性报告:
            - 总单词数: $totalWords
            - 有中文释义: $wordsWithDefinition (${String.format("%.1f", definitionCompleteness)}%)
            - 有音标: $wordsWithPhonetic (${String.format("%.1f", phoneticCompleteness)}%)
            - 有词性: $wordsWithPartOfSpeech (${String.format("%.1f", partOfSpeechCompleteness)}%)
        """.trimIndent()
    }
}