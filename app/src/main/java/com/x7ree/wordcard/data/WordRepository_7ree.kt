package com.x7ree.wordcard.data

import kotlinx.coroutines.flow.Flow

class WordRepository_7ree(private val wordDao_7ree: WordDao_7ree) {
    
    // 根据单词获取记录
    suspend fun getWord_7ree(word: String): WordEntity_7ree? {
        return wordDao_7ree.getWord_7ree(word)
    }
    
    // 保存单词记录
    suspend fun saveWord_7ree(word: String, apiResult: String) {
        // 解析Markdown格式的API结果
        val wordInfo = com.x7ree.wordcard.utils.MarkdownParser_7ree.parseWordInfo(apiResult)
        
        val wordEntity_7ree = WordEntity_7ree(
            word = word,
            apiResult = apiResult,
            queryTimestamp = System.currentTimeMillis(),
            viewCount = 1, // 首次保存时浏览次数为1
            isFavorite = false,
            spellingCount = 0,
            chineseDefinition = wordInfo.chineseDefinition,
            phonetic = wordInfo.phonetic,
            partOfSpeech = wordInfo.partOfSpeech
        )
        wordDao_7ree.insertWord_7ree(wordEntity_7ree)
    }
    
    // 增加浏览次数
    suspend fun incrementViewCount_7ree(word: String) {
        wordDao_7ree.incrementViewCount_7ree(word)
    }
    
    // 增加拼写次数
    suspend fun incrementSpellingCount_7ree(word: String) {
        wordDao_7ree.incrementSpellingCount_7ree(word)
    }
    
    // 更新引用次数
    suspend fun updateReferenceCount_7ree(word: String, count: Int) {
        wordDao_7ree.updateReferenceCount_7ree(word, count)
    }
    
    // 切换收藏状态
    suspend fun toggleFavorite_7ree(word: String) {
        wordDao_7ree.toggleFavorite_7ree(word)
    }
    
    // 设置收藏状态
    suspend fun setFavorite_7ree(word: String, isFavorite: Boolean) {
        wordDao_7ree.setFavorite_7ree(word, isFavorite)
    }
    
    // 获取所有单词记录
    fun getAllWords_7ree(): Flow<List<WordEntity_7ree>> {
        return wordDao_7ree.getAllWords_7ree()
    }
    
    // 获取收藏的单词
    fun getFavoriteWords_7ree(): Flow<List<WordEntity_7ree>> {
        return wordDao_7ree.getFavoriteWords_7ree()
    }
    
    // 获取最近查询的单词
    fun getRecentWords_7ree(limit: Int = 20): Flow<List<WordEntity_7ree>> {
        return wordDao_7ree.getRecentWords_7ree(limit)
    }
    
    // 获取热门单词
    fun getPopularWords_7ree(limit: Int = 10): Flow<List<WordEntity_7ree>> {
        return wordDao_7ree.getPopularWords_7ree(limit)
    }
    
    // 搜索单词
    fun searchWords_7ree(keyword: String): Flow<List<WordEntity_7ree>> {
        return wordDao_7ree.searchWords_7ree(keyword)
    }
    
    // 删除单词记录
    suspend fun deleteWord_7ree(word: String) {
        wordDao_7ree.deleteWordByWord_7ree(word)
    }
    
    // 删除所有记录
    suspend fun deleteAllWords_7ree() {
        wordDao_7ree.deleteAllWords_7ree()
    }
    
    // 删除非收藏的记录
    suspend fun deleteNonFavoriteWords_7ree() {
        wordDao_7ree.deleteNonFavoriteWords_7ree()
    }
    
    // 插入单词记录
    suspend fun insertWord_7ree(wordEntity_7ree: WordEntity_7ree) {
        wordDao_7ree.insertWord_7ree(wordEntity_7ree)
    }
    
    // 更新单词记录
    suspend fun updateWord_7ree(wordEntity_7ree: WordEntity_7ree) {
        wordDao_7ree.updateWord_7ree(wordEntity_7ree)
    }
    
    // 获取单词总数
    val wordCount_7ree: Flow<Int> = wordDao_7ree.countAllWords_7ree()
    
    // 获取单词总数（挂起函数）
    suspend fun getTotalWordCount_7ree(): Int {
        return wordDao_7ree.getTotalWordCount_7ree()
    }
    
    // 获取总查阅次数
    val getTotalViews_7ree: Flow<Int> = wordDao_7ree.getTotalViews_7ree()
    
    // 分页获取单词记录
    suspend fun getWordsPaged_7ree(limit: Int, offset: Int): List<WordEntity_7ree> {
        return wordDao_7ree.getWordsPaged_7ree(limit, offset)
    }
    
    suspend fun getFavoriteWordsPaged_7ree(limit: Int, offset: Int): List<WordEntity_7ree> {
        return wordDao_7ree.getFavoriteWordsPaged_7ree(limit, offset)
    }
    
    // 支持排序的分页查询方法
    suspend fun getWordsPagedWithSort_7ree(limit: Int, offset: Int, sortType: String, isFavoriteOnly: Boolean = false): List<WordEntity_7ree> {
        return when (sortType) {
            "VIEW_COUNT_ASC" -> if (isFavoriteOnly) wordDao_7ree.getFavoriteWordsPagedByViewCountAsc_7ree(limit, offset) else wordDao_7ree.getWordsPagedByViewCountAsc_7ree(limit, offset)
            "VIEW_COUNT_DESC" -> if (isFavoriteOnly) wordDao_7ree.getFavoriteWordsPagedByViewCountDesc_7ree(limit, offset) else wordDao_7ree.getWordsPagedByViewCountDesc_7ree(limit, offset)
            "BROWSE_COUNT_ASC" -> if (isFavoriteOnly) wordDao_7ree.getFavoriteWordsPagedByViewCountAsc_7ree(limit, offset) else wordDao_7ree.getWordsPagedByViewCountAsc_7ree(limit, offset)
            "BROWSE_COUNT_DESC" -> if (isFavoriteOnly) wordDao_7ree.getFavoriteWordsPagedByViewCountDesc_7ree(limit, offset) else wordDao_7ree.getWordsPagedByViewCountDesc_7ree(limit, offset)
            "RECORD_TIME_ASC" -> if (isFavoriteOnly) wordDao_7ree.getFavoriteWordsPagedByRecordTimeAsc_7ree(limit, offset) else wordDao_7ree.getWordsPagedByRecordTimeAsc_7ree(limit, offset)
            "RECORD_TIME_DESC" -> if (isFavoriteOnly) wordDao_7ree.getFavoriteWordsPagedByRecordTimeDesc_7ree(limit, offset) else wordDao_7ree.getWordsPagedByRecordTimeDesc_7ree(limit, offset)
            "SPELLING_COUNT_ASC" -> if (isFavoriteOnly) wordDao_7ree.getFavoriteWordsPagedBySpellingCountAsc_7ree(limit, offset) else wordDao_7ree.getWordsPagedBySpellingCountAsc_7ree(limit, offset)
            "SPELLING_COUNT_DESC" -> if (isFavoriteOnly) wordDao_7ree.getFavoriteWordsPagedBySpellingCountDesc_7ree(limit, offset) else wordDao_7ree.getWordsPagedBySpellingCountDesc_7ree(limit, offset)
            "REFERENCE_COUNT_ASC" -> if (isFavoriteOnly) wordDao_7ree.getFavoriteWordsPagedByReferenceCountAsc_7ree(limit, offset) else wordDao_7ree.getWordsPagedByReferenceCountAsc_7ree(limit, offset)
            "REFERENCE_COUNT_DESC" -> if (isFavoriteOnly) wordDao_7ree.getFavoriteWordsPagedByReferenceCountDesc_7ree(limit, offset) else wordDao_7ree.getWordsPagedByReferenceCountDesc_7ree(limit, offset)
            else -> if (isFavoriteOnly) wordDao_7ree.getFavoriteWordsPaged_7ree(limit, offset) else wordDao_7ree.getWordsPaged_7ree(limit, offset)
        }
    }
    
    // 根据词性搜索单词
    fun getWordsByPartOfSpeech_7ree(partOfSpeech: String): Flow<List<WordEntity_7ree>> {
        return wordDao_7ree.getWordsByPartOfSpeech_7ree(partOfSpeech)
    }
    
    // 搜索中文释义
    fun searchByChineseDefinition_7ree(keyword: String): Flow<List<WordEntity_7ree>> {
        return wordDao_7ree.searchByChineseDefinition_7ree(keyword)
    }
    
    // 获取所有不同的词性
    fun getAllPartOfSpeech_7ree(): Flow<List<String>> {
        return wordDao_7ree.getAllPartOfSpeech_7ree()
    }
    
    // 获取有音标的单词数量
    val wordsWithPhoneticCount_7ree: Flow<Int> = wordDao_7ree.countWordsWithPhonetic_7ree()
    
    // 获取有中文释义的单词数量
    val wordsWithDefinitionCount_7ree: Flow<Int> = wordDao_7ree.countWordsWithDefinition_7ree()
}
