package com.x7ree.wordcard.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "words")
data class WordEntity_7ree(
    @PrimaryKey
    val word: String,                    // 查询的单词
    val apiResult: String,               // API查询的结果
    val queryTimestamp: Long,            // 查询的时间戳
    val viewCount: Int = 0,              // 单词卡片被浏览的次数
    val isFavorite: Boolean = false,    // 是否被收藏
    val spellingCount: Int = 0,          // 拼写练习次数
    val chineseDefinition: String = "",  // 中文释义
    val phonetic: String = "",           // 音标
    val partOfSpeech: String = "",       // 词性
    val referenceCount: Int = 0          // 在文章中被引用的次数
)