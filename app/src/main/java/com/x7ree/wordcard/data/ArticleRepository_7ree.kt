package com.x7ree.wordcard.data

import kotlinx.coroutines.flow.Flow

class ArticleRepository_7ree(private val articleDao_7ree: ArticleDao_7ree) {
    
    // 根据ID获取文章
    suspend fun getArticle_7ree(id: Long): ArticleEntity_7ree? {
        return articleDao_7ree.getArticle_7ree(id)
    }
    
    // 保存文章记录
    suspend fun saveArticle_7ree(
        keyWords: String,
        apiResult: String,
        englishTitle: String,
        titleTranslation: String,
        englishContent: String,
        chineseContent: String,
        bilingualComparison: String = "",
        author: String = ""
    ): Long {
        val articleEntity_7ree = ArticleEntity_7ree(
            generationTimestamp = System.currentTimeMillis(),
            keyWords = keyWords,
            viewCount = 0,
            apiResult = apiResult,
            englishTitle = englishTitle,
            titleTranslation = titleTranslation,
            englishContent = englishContent,
            chineseContent = chineseContent,
            bilingualComparison = bilingualComparison,
            isFavorite = false,
            author = author
        )
        return articleDao_7ree.insertArticle_7ree(articleEntity_7ree)
    }
    
    // 增加浏览次数
    suspend fun incrementViewCount_7ree(id: Long) {
        articleDao_7ree.incrementViewCount_7ree(id)
    }
    
    // 切换收藏状态
    suspend fun toggleFavorite_7ree(id: Long) {
        articleDao_7ree.toggleFavorite_7ree(id)
    }
    
    // 设置收藏状态
    suspend fun setFavorite_7ree(id: Long, isFavorite: Boolean) {
        articleDao_7ree.setFavorite_7ree(id, isFavorite)
    }
    
    // 获取所有文章记录
    fun getAllArticles_7ree(): Flow<List<ArticleEntity_7ree>> {
        return articleDao_7ree.getAllArticles_7ree()
    }
    
    // 获取收藏的文章
    fun getFavoriteArticles_7ree(): Flow<List<ArticleEntity_7ree>> {
        return articleDao_7ree.getFavoriteArticles_7ree()
    }
    
    // 获取最近生成的文章
    fun getRecentArticles_7ree(limit: Int = 20): Flow<List<ArticleEntity_7ree>> {
        return articleDao_7ree.getRecentArticles_7ree(limit)
    }
    
    // 获取热门文章
    fun getPopularArticles_7ree(limit: Int = 10): Flow<List<ArticleEntity_7ree>> {
        return articleDao_7ree.getPopularArticles_7ree(limit)
    }
    
    // 搜索文章
    fun searchArticles_7ree(keyword: String): Flow<List<ArticleEntity_7ree>> {
        return articleDao_7ree.searchArticles_7ree(keyword)
    }
    
    // 高级搜索：支持全文搜索
    fun searchArticlesAdvanced_7ree(keyword: String): Flow<List<ArticleEntity_7ree>> {
        return articleDao_7ree.searchArticlesAdvanced_7ree(keyword)
    }
    
    // 按关键词精确搜索
    fun searchByExactKeyword_7ree(keyword: String): Flow<List<ArticleEntity_7ree>> {
        return articleDao_7ree.searchByExactKeyword_7ree(keyword)
    }
    
    // 按关键词搜索热门文章
    fun searchByKeywordPopular_7ree(keyword: String): Flow<List<ArticleEntity_7ree>> {
        return articleDao_7ree.searchByKeywordPopular_7ree(keyword)
    }
    
    // 获取所有关键词（用于搜索建议）
    fun getAllKeywords_7ree(): Flow<List<String>> {
        return articleDao_7ree.getAllKeywords_7ree()
    }
    
    // 删除文章记录
    suspend fun deleteArticle_7ree(id: Long) {
        articleDao_7ree.deleteArticle_7ree(id)
    }
    
    // 删除所有记录
    suspend fun deleteAllArticles_7ree() {
        articleDao_7ree.deleteAllArticles_7ree()
    }
    
    // 删除非收藏的记录
    suspend fun deleteNonFavoriteArticles_7ree() {
        articleDao_7ree.deleteNonFavoriteArticles_7ree()
    }
    
    // 插入文章记录
    suspend fun insertArticle_7ree(articleEntity_7ree: ArticleEntity_7ree): Long {
        return articleDao_7ree.insertArticle_7ree(articleEntity_7ree)
    }
    
    // 更新文章记录
    suspend fun updateArticle_7ree(articleEntity_7ree: ArticleEntity_7ree) {
        articleDao_7ree.updateArticle_7ree(articleEntity_7ree)
    }
    
    // 获取文章总数
    val articleCount_7ree: Flow<Int> = articleDao_7ree.countAllArticles_7ree()
    
    // 获取总浏览次数
    val getTotalViews_7ree: Flow<Int> = articleDao_7ree.getTotalViews_7ree()
    
    // 分页获取文章记录
    suspend fun getArticlesPaged_7ree(limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.getArticlesPaged_7ree(limit, offset)
    }
    
    suspend fun getFavoriteArticlesPaged_7ree(limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.getFavoriteArticlesPaged_7ree(limit, offset)
    }
    
    // 支持排序的分页查询方法
    suspend fun getArticlesPagedWithSort_7ree(limit: Int, offset: Int, sortType: String, isFavoriteOnly: Boolean = false): List<ArticleEntity_7ree> {
        return when (sortType) {
            "VIEW_COUNT_ASC" -> if (isFavoriteOnly) articleDao_7ree.getFavoriteArticlesPagedByViewCountAsc_7ree(limit, offset) else articleDao_7ree.getArticlesPagedByViewCountAsc_7ree(limit, offset)
            "VIEW_COUNT_DESC" -> if (isFavoriteOnly) articleDao_7ree.getFavoriteArticlesPagedByViewCountDesc_7ree(limit, offset) else articleDao_7ree.getArticlesPagedByViewCountDesc_7ree(limit, offset)
            "GENERATION_TIME_ASC" -> if (isFavoriteOnly) articleDao_7ree.getFavoriteArticlesPagedByGenerationTimeAsc_7ree(limit, offset) else articleDao_7ree.getArticlesPagedByGenerationTimeAsc_7ree(limit, offset)
            "GENERATION_TIME_DESC" -> if (isFavoriteOnly) articleDao_7ree.getFavoriteArticlesPagedByGenerationTimeDesc_7ree(limit, offset) else articleDao_7ree.getArticlesPagedByGenerationTimeDesc_7ree(limit, offset)
            else -> if (isFavoriteOnly) articleDao_7ree.getFavoriteArticlesPaged_7ree(limit, offset) else articleDao_7ree.getArticlesPaged_7ree(limit, offset)
        }
    }
    
    // 新增的分页查询方法，与ArticlePaginationHandler_7ree配合使用
    suspend fun getAllArticlesSortedByTimeAsc(limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.getArticlesPagedByGenerationTimeAsc_7ree(limit, offset)
    }
    
    suspend fun getAllArticlesSortedByTimeDesc(limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.getArticlesPagedByGenerationTimeDesc_7ree(limit, offset)
    }
    
    suspend fun getAllArticlesSortedByViewCountAsc(limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.getArticlesPagedByViewCountAsc_7ree(limit, offset)
    }
    
    suspend fun getAllArticlesSortedByViewCountDesc(limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.getArticlesPagedByViewCountDesc_7ree(limit, offset)
    }
    
    suspend fun getFavoriteArticlesSortedByTimeAsc(limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.getFavoriteArticlesPagedByGenerationTimeAsc_7ree(limit, offset)
    }
    
    suspend fun getFavoriteArticlesSortedByTimeDesc(limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.getFavoriteArticlesPagedByGenerationTimeDesc_7ree(limit, offset)
    }
    
    suspend fun getFavoriteArticlesSortedByViewCountAsc(limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.getFavoriteArticlesPagedByViewCountAsc_7ree(limit, offset)
    }
    
    suspend fun getFavoriteArticlesSortedByViewCountDesc(limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.getFavoriteArticlesPagedByViewCountDesc_7ree(limit, offset)
    }
    
    // ========== 分页搜索方法 ==========
    
    // 在所有文章中搜索，支持分页和排序
    suspend fun searchAllArticlesSortedByTimeAsc(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.searchArticlesPagedByGenerationTimeAsc_7ree(query, limit, offset)
    }
    
    suspend fun searchAllArticlesSortedByTimeDesc(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.searchArticlesPagedByGenerationTimeDesc_7ree(query, limit, offset)
    }
    
    suspend fun searchAllArticlesSortedByViewCountAsc(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.searchArticlesPagedByViewCountAsc_7ree(query, limit, offset)
    }
    
    suspend fun searchAllArticlesSortedByViewCountDesc(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.searchArticlesPagedByViewCountDesc_7ree(query, limit, offset)
    }
    
    // 在收藏文章中搜索，支持分页和排序
    suspend fun searchFavoriteArticlesSortedByTimeAsc(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.searchFavoriteArticlesPagedByGenerationTimeAsc_7ree(query, limit, offset)
    }
    
    suspend fun searchFavoriteArticlesSortedByTimeDesc(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.searchFavoriteArticlesPagedByGenerationTimeDesc_7ree(query, limit, offset)
    }
    
    suspend fun searchFavoriteArticlesSortedByViewCountAsc(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.searchFavoriteArticlesPagedByViewCountAsc_7ree(query, limit, offset)
    }
    
    suspend fun searchFavoriteArticlesSortedByViewCountDesc(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.searchFavoriteArticlesPagedByViewCountDesc_7ree(query, limit, offset)
    }
    
    // 测试搜索方法
    suspend fun testSearchArticles(query: String, limit: Int, offset: Int): List<ArticleEntity_7ree> {
        return articleDao_7ree.testSearchArticles_7ree(query, limit, offset)
    }
}