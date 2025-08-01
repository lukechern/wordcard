package com.x7ree.wordcard.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "articles")
data class ArticleEntity_7ree(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,                    // 自增主键
    val generationTimestamp: Long,       // 生成时间戳
    val keyWords: String,                // 重点单词（多个单词用逗号分隔）
    val viewCount: Int = 0,              // 浏览次数
    val apiResult: String,               // API返回的完整结果
    val englishTitle: String,            // 英文文章标题
    val titleTranslation: String,        // 标题翻译
    val englishContent: String,          // 英文文章内容
    val chineseContent: String,          // 中文翻译文章内容
    val bilingualComparison: String = "",// 中英对照内容
    val isFavorite: Boolean = false      // 是否收藏状态
)