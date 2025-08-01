package com.x7ree.wordcard.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao_7ree {
    
    // 根据ID获取文章
    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticle_7ree(id: Long): ArticleEntity_7ree?
    
    // 根据英文标题获取文章
    @Query("SELECT * FROM articles WHERE englishTitle = :title")
    suspend fun getArticleByTitle_7ree(title: String): ArticleEntity_7ree?
    
    // 获取所有文章记录，按生成时间倒序排列
    @Query("SELECT * FROM articles ORDER BY generationTimestamp DESC")
    fun getAllArticles_7ree(): Flow<List<ArticleEntity_7ree>>
    
    // 获取收藏的文章，按生成时间倒序排列
    @Query("SELECT * FROM articles WHERE isFavorite = 1 ORDER BY generationTimestamp DESC")
    fun getFavoriteArticles_7ree(): Flow<List<ArticleEntity_7ree>>
    
    // 获取最近生成的文章（限制数量）
    @Query("SELECT * FROM articles ORDER BY generationTimestamp DESC LIMIT :limit")
    fun getRecentArticles_7ree(limit: Int = 20): Flow<List<ArticleEntity_7ree>>
    
    // 获取浏览次数最多的文章
    @Query("SELECT * FROM articles ORDER BY viewCount DESC LIMIT :limit")
    fun getPopularArticles_7ree(limit: Int = 10): Flow<List<ArticleEntity_7ree>>
    
    // 搜索文章（根据标题或关键词模糊匹配）
    @Query("SELECT * FROM articles WHERE englishTitle LIKE '%' || :keyword || '%' OR keyWords LIKE '%' || :keyword || '%' ORDER BY generationTimestamp DESC")
    fun searchArticles_7ree(keyword: String): Flow<List<ArticleEntity_7ree>>
    
    // 高级搜索：支持标题、关键词、内容全文搜索
    @Query("""
        SELECT * FROM articles 
        WHERE englishTitle LIKE '%' || :keyword || '%' 
           OR keyWords LIKE '%' || :keyword || '%'
           OR englishContent LIKE '%' || :keyword || '%'
           OR chineseContent LIKE '%' || :keyword || '%'
        ORDER BY generationTimestamp DESC
    """)
    fun searchArticlesAdvanced_7ree(keyword: String): Flow<List<ArticleEntity_7ree>>
    
    // 按关键词精确搜索
    @Query("SELECT * FROM articles WHERE keyWords = :keyword ORDER BY generationTimestamp DESC")
    fun searchByExactKeyword_7ree(keyword: String): Flow<List<ArticleEntity_7ree>>
    
    // 按关键词包含搜索（支持多个关键词）
    @Query("SELECT * FROM articles WHERE keyWords LIKE '%' || :keyword || '%' ORDER BY viewCount DESC")
    fun searchByKeywordPopular_7ree(keyword: String): Flow<List<ArticleEntity_7ree>>
    
    // 获取所有不同的关键词（用于搜索建议）
    @Query("SELECT DISTINCT keyWords FROM articles WHERE keyWords != '' ORDER BY keyWords")
    fun getAllKeywords_7ree(): Flow<List<String>>
    
    // 多关键词搜索（AND逻辑）
    @Query("""
        SELECT * FROM articles 
        WHERE (:keyword1 = '' OR keyWords LIKE '%' || :keyword1 || '%')
          AND (:keyword2 = '' OR keyWords LIKE '%' || :keyword2 || '%')
          AND (:keyword3 = '' OR keyWords LIKE '%' || :keyword3 || '%')
        ORDER BY generationTimestamp DESC
    """)
    fun searchByMultipleKeywords_7ree(keyword1: String, keyword2: String = "", keyword3: String = ""): Flow<List<ArticleEntity_7ree>>
    
    // 按时间范围搜索
    @Query("""
        SELECT * FROM articles 
        WHERE keyWords LIKE '%' || :keyword || '%'
          AND generationTimestamp BETWEEN :startTime AND :endTime
        ORDER BY generationTimestamp DESC
    """)
    fun searchByKeywordAndTimeRange_7ree(keyword: String, startTime: Long, endTime: Long): Flow<List<ArticleEntity_7ree>>
    
    // 搜索收藏的文章中的关键词
    @Query("""
        SELECT * FROM articles 
        WHERE keyWords LIKE '%' || :keyword || '%'
          AND isFavorite = 1
        ORDER BY viewCount DESC
    """)
    fun searchFavoritesByKeyword_7ree(keyword: String): Flow<List<ArticleEntity_7ree>>
    
    // 插入文章记录
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle_7ree(article: ArticleEntity_7ree): Long
    
    // 更新文章记录
    @Update
    suspend fun updateArticle_7ree(article: ArticleEntity_7ree)
    
    // 增加浏览次数
    @Query("UPDATE articles SET viewCount = viewCount + 1 WHERE id = :id")
    suspend fun incrementViewCount_7ree(id: Long)
    
    // 切换收藏状态
    @Query("UPDATE articles SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleFavorite_7ree(id: Long)
    
    // 设置收藏状态
    @Query("UPDATE articles SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite_7ree(id: Long, isFavorite: Boolean)
    
    // 删除文章记录
    @Query("DELETE FROM articles WHERE id = :id")
    suspend fun deleteArticle_7ree(id: Long)
    
    // 删除所有文章记录
    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles_7ree()
    
    // 删除非收藏的文章记录
    @Query("DELETE FROM articles WHERE isFavorite = 0")
    suspend fun deleteNonFavoriteArticles_7ree()
    
    // 获取文章总数
    @Query("SELECT COUNT(*) FROM articles")
    fun countAllArticles_7ree(): Flow<Int>
    
    // 获取总浏览次数
    @Query("SELECT SUM(viewCount) FROM articles")
    fun getTotalViews_7ree(): Flow<Int>
    
    // 分页获取文章记录
    @Query("SELECT * FROM articles ORDER BY generationTimestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getArticlesPaged_7ree(limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    // 分页获取收藏文章记录
    @Query("SELECT * FROM articles WHERE isFavorite = 1 ORDER BY generationTimestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getFavoriteArticlesPaged_7ree(limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    // 按浏览次数排序的分页查询
    @Query("SELECT * FROM articles ORDER BY viewCount ASC LIMIT :limit OFFSET :offset")
    suspend fun getArticlesPagedByViewCountAsc_7ree(limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    @Query("SELECT * FROM articles ORDER BY viewCount DESC LIMIT :limit OFFSET :offset")
    suspend fun getArticlesPagedByViewCountDesc_7ree(limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    // 按生成时间排序的分页查询
    @Query("SELECT * FROM articles ORDER BY generationTimestamp ASC LIMIT :limit OFFSET :offset")
    suspend fun getArticlesPagedByGenerationTimeAsc_7ree(limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    @Query("SELECT * FROM articles ORDER BY generationTimestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getArticlesPagedByGenerationTimeDesc_7ree(limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    // 收藏文章的排序分页查询
    @Query("SELECT * FROM articles WHERE isFavorite = 1 ORDER BY viewCount ASC LIMIT :limit OFFSET :offset")
    suspend fun getFavoriteArticlesPagedByViewCountAsc_7ree(limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    @Query("SELECT * FROM articles WHERE isFavorite = 1 ORDER BY viewCount DESC LIMIT :limit OFFSET :offset")
    suspend fun getFavoriteArticlesPagedByViewCountDesc_7ree(limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    @Query("SELECT * FROM articles WHERE isFavorite = 1 ORDER BY generationTimestamp ASC LIMIT :limit OFFSET :offset")
    suspend fun getFavoriteArticlesPagedByGenerationTimeAsc_7ree(limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    @Query("SELECT * FROM articles WHERE isFavorite = 1 ORDER BY generationTimestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getFavoriteArticlesPagedByGenerationTimeDesc_7ree(limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    // ========== 搜索分页方法 ==========
    
    // 简化版搜索方法 - 只搜索标题和关键词
    // 在所有文章中搜索，支持分页和排序
    @Query("""
        SELECT * FROM articles 
        WHERE englishTitle LIKE '%' || :query || '%' 
           OR titleTranslation LIKE '%' || :query || '%'
           OR keyWords LIKE '%' || :query || '%'
           OR englishContent LIKE '%' || :query || '%'
           OR chineseContent LIKE '%' || :query || '%'
        ORDER BY generationTimestamp ASC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchArticlesPagedByGenerationTimeAsc_7ree(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    @Query("""
        SELECT * FROM articles 
        WHERE englishTitle LIKE '%' || :query || '%' 
           OR titleTranslation LIKE '%' || :query || '%'
           OR keyWords LIKE '%' || :query || '%'
           OR englishContent LIKE '%' || :query || '%'
           OR chineseContent LIKE '%' || :query || '%'
        ORDER BY generationTimestamp DESC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchArticlesPagedByGenerationTimeDesc_7ree(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    @Query("""
        SELECT * FROM articles 
        WHERE englishTitle LIKE '%' || :query || '%' 
           OR titleTranslation LIKE '%' || :query || '%'
           OR keyWords LIKE '%' || :query || '%'
           OR englishContent LIKE '%' || :query || '%'
           OR chineseContent LIKE '%' || :query || '%'
        ORDER BY viewCount ASC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchArticlesPagedByViewCountAsc_7ree(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    @Query("""
        SELECT * FROM articles 
        WHERE englishTitle LIKE '%' || :query || '%' 
           OR titleTranslation LIKE '%' || :query || '%'
           OR keyWords LIKE '%' || :query || '%'
           OR englishContent LIKE '%' || :query || '%'
           OR chineseContent LIKE '%' || :query || '%'
        ORDER BY viewCount DESC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchArticlesPagedByViewCountDesc_7ree(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    // 在收藏文章中搜索，支持分页和排序
    @Query("""
        SELECT * FROM articles 
        WHERE isFavorite = 1 
          AND (englishTitle LIKE '%' || :query || '%' 
               OR titleTranslation LIKE '%' || :query || '%'
               OR keyWords LIKE '%' || :query || '%'
               OR englishContent LIKE '%' || :query || '%'
               OR chineseContent LIKE '%' || :query || '%')
        ORDER BY generationTimestamp ASC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchFavoriteArticlesPagedByGenerationTimeAsc_7ree(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    @Query("""
        SELECT * FROM articles 
        WHERE isFavorite = 1 
          AND (englishTitle LIKE '%' || :query || '%' 
               OR titleTranslation LIKE '%' || :query || '%'
               OR keyWords LIKE '%' || :query || '%'
               OR englishContent LIKE '%' || :query || '%'
               OR chineseContent LIKE '%' || :query || '%')
        ORDER BY generationTimestamp DESC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchFavoriteArticlesPagedByGenerationTimeDesc_7ree(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    @Query("""
        SELECT * FROM articles 
        WHERE isFavorite = 1 
          AND (englishTitle LIKE '%' || :query || '%' 
               OR titleTranslation LIKE '%' || :query || '%'
               OR keyWords LIKE '%' || :query || '%'
               OR englishContent LIKE '%' || :query || '%'
               OR chineseContent LIKE '%' || :query || '%')
        ORDER BY viewCount ASC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchFavoriteArticlesPagedByViewCountAsc_7ree(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    @Query("""
        SELECT * FROM articles 
        WHERE isFavorite = 1 
          AND (englishTitle LIKE '%' || :query || '%' 
               OR titleTranslation LIKE '%' || :query || '%'
               OR keyWords LIKE '%' || :query || '%'
               OR englishContent LIKE '%' || :query || '%'
               OR chineseContent LIKE '%' || :query || '%')
        ORDER BY viewCount DESC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchFavoriteArticlesPagedByViewCountDesc_7ree(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree>
    
    // 简单的测试搜索方法
    @Query("SELECT * FROM articles WHERE englishTitle LIKE '%' || :query || '%' LIMIT :limit OFFSET :offset")
    suspend fun testSearchArticles_7ree(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree>
}