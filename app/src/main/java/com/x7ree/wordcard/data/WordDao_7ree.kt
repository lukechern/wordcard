package com.x7ree.wordcard.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao_7ree {
    
    // 根据单词获取记录
    @Query("SELECT * FROM words WHERE word = :word")
    suspend fun getWord_7ree(word: String): WordEntity_7ree?
    
    // 获取所有单词记录，按查询时间倒序排列
    @Query("SELECT * FROM words ORDER BY queryTimestamp DESC")
    fun getAllWords_7ree(): Flow<List<WordEntity_7ree>>
    
    // 获取收藏的单词，按查询时间倒序排列
    @Query("SELECT * FROM words WHERE isFavorite = 1 ORDER BY queryTimestamp DESC")
    fun getFavoriteWords_7ree(): Flow<List<WordEntity_7ree>>
    
    // 获取最近查询的单词（限制数量）
    @Query("SELECT * FROM words ORDER BY queryTimestamp DESC LIMIT :limit")
    fun getRecentWords_7ree(limit: Int = 20): Flow<List<WordEntity_7ree>>
    
    // 获取浏览次数最多的单词
    @Query("SELECT * FROM words ORDER BY viewCount DESC LIMIT :limit")
    fun getPopularWords_7ree(limit: Int = 10): Flow<List<WordEntity_7ree>>
    
    // 搜索单词（模糊匹配）
    @Query("SELECT * FROM words WHERE word LIKE '%' || :keyword || '%' ORDER BY queryTimestamp DESC")
    fun searchWords_7ree(keyword: String): Flow<List<WordEntity_7ree>>
    
    // 插入或更新单词记录
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord_7ree(word: WordEntity_7ree)
    
    // 更新单词记录
    @Update
    suspend fun updateWord_7ree(word: WordEntity_7ree)
    
    // 增加浏览次数
    @Query("UPDATE words SET viewCount = viewCount + 1 WHERE word = :word")
    suspend fun incrementViewCount_7ree(word: String)
    
    // 切换收藏状态
    @Query("UPDATE words SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE word = :word")
    suspend fun toggleFavorite_7ree(word: String)
    
    // 设置收藏状态
    @Query("UPDATE words SET isFavorite = :isFavorite WHERE word = :word")
    suspend fun setFavorite_7ree(word: String, isFavorite: Boolean)
    
    // 删除单词记录
    @Delete
    suspend fun deleteWord_7ree(word: WordEntity_7ree)
    
    // 根据单词删除记录
    @Query("DELETE FROM words WHERE word = :word")
    suspend fun deleteWordByWord_7ree(word: String)
    
    // 删除所有记录
    @Query("DELETE FROM words")
    suspend fun deleteAllWords_7ree()
    
    // 删除非收藏的记录
    @Query("DELETE FROM words WHERE isFavorite = 0")
    suspend fun deleteNonFavoriteWords_7ree()
    
    // 获取所有单词的数量
    @Query("SELECT COUNT(*) FROM words")
    fun countAllWords_7ree(): Flow<Int>
    
    // 获取总查阅次数
    @Query("SELECT SUM(viewCount) FROM words")
    fun getTotalViews_7ree(): Flow<Int>
    
    // 分页获取单词记录，按查询时间倒序排列
    @Query("SELECT * FROM words ORDER BY queryTimestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getWordsPaged_7ree(limit: Int, offset: Int): List<WordEntity_7ree>
}