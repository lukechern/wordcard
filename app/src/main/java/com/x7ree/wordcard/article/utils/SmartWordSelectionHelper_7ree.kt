package com.x7ree.wordcard.article.utils

import com.x7ree.wordcard.data.WordRepository_7ree
import kotlinx.coroutines.flow.collect

class SmartWordSelectionHelper_7ree(
    private val wordRepository_7ree: WordRepository_7ree?
) {
    /**
     * 获取查阅次数最少的5个单词
     */
    suspend fun getWordsWithLowViewCount(): List<String> {
        return try {
            val words = wordRepository_7ree?.getWordsPagedWithSort_7ree(
                limit = 5,
                offset = 0,
                sortType = "VIEW_COUNT_ASC"
            ) ?: emptyList()
            
            words.map { it.word }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * 获取引用次数最少的5个单词
     */
    suspend fun getWordsWithLowReferenceCount(): List<String> {
        return try {
            // 使用真实的referenceCount字段进行排序
            val words = wordRepository_7ree?.getWordsPagedWithSort_7ree(
                limit = 5,
                offset = 0,
                sortType = "REFERENCE_COUNT_ASC"
            ) ?: emptyList()
            
            words.map { it.word }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * 获取拼写练习次数最少的5个单词
     */
    suspend fun getWordsWithLowSpellingCount(): List<String> {
        return try {
            val words = wordRepository_7ree?.getWordsPagedWithSort_7ree(
                limit = 5,
                offset = 0,
                sortType = "SPELLING_COUNT_ASC"
            ) ?: emptyList()
            
            words.map { it.word }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * 获取最新加入的5个单词
     */
    suspend fun getNewestWords(): List<String> {
        return try {
            val words = wordRepository_7ree?.getWordsPagedWithSort_7ree(
                limit = 5,
                offset = 0,
                sortType = "RECORD_TIME_DESC"
            ) ?: emptyList()
            
            words.map { it.word }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * 获取随机的5个单词
     */
    suspend fun getRandomWords(): List<String> {
        return try {
            // 先获取总单词数
            val totalCount = wordRepository_7ree?.getTotalWordCount_7ree() ?: 0
            
            if (totalCount == 0) {
                return emptyList()
            }
            
            // 随机选择5个不同的偏移量
            val random = java.util.Random()
            val selectedWords = mutableSetOf<String>()
            
            // 尝试最多10次以确保能找到足够的单词
            var attempts = 0
            while (selectedWords.size < 5 && attempts < 10) {
                val offset = random.nextInt(totalCount)
                val words = wordRepository_7ree?.getWordsPaged_7ree(1, offset) ?: emptyList()
                if (words.isNotEmpty()) {
                    selectedWords.add(words[0].word)
                }
                attempts++
            }
            
            selectedWords.toList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
