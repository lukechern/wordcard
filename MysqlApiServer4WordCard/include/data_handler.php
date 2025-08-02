<?php
/**
 * 数据处理模块 - 处理单词和文章的上传下载
 */

/**
 * 安全的布尔值转换
 */
function safeBooleanConvert($value) {
    if ($value === true || $value === 'true' || $value === 1 || $value === '1') {
        return 1;
    }
    return 0;
}

/**
 * 安全的整数转换
 */
function safeIntConvert($value, $default = 0) {
    if (is_numeric($value)) {
        return intval($value);
    }
    return $default;
}

/**
 * 上传单词数据
 */
function uploadWords($pdo, $words) {
    if (empty($words)) {
        return 0;
    }
    
    $stmt = $pdo->prepare("
        INSERT INTO words (word, apiResult, queryTimestamp, viewCount, isFavorite, spellingCount, chineseDefinition, phonetic, partOfSpeech, referenceCount) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    ");
    
    $count = 0;
    foreach ($words as $word) {
        // 安全处理各种数据类型
        $isFavorite = safeBooleanConvert($word['isFavorite'] ?? false);
        $queryTimestamp = safeIntConvert($word['queryTimestamp'] ?? time() * 1000);
        $viewCount = safeIntConvert($word['viewCount'] ?? 0);
        $spellingCount = safeIntConvert($word['spellingCount'] ?? 0);
        $referenceCount = safeIntConvert($word['referenceCount'] ?? 0);
        
        $stmt->execute([
            $word['word'] ?? '',
            $word['apiResult'] ?? '',
            $queryTimestamp,
            $viewCount,
            $isFavorite,
            $spellingCount,
            $word['chineseDefinition'] ?? '',
            $word['phonetic'] ?? '',
            $word['partOfSpeech'] ?? '',
            $referenceCount
        ]);
        $count++;
    }
    
    return $count;
}

/**
 * 上传文章数据
 */
function uploadArticles($pdo, $articles) {
    if (empty($articles)) {
        return 0;
    }
    
    $articleStmt = $pdo->prepare("
        INSERT INTO articles (generationTimestamp, keyWords, viewCount, apiResult, englishTitle, titleTranslation, englishContent, chineseContent, bilingualComparison, isFavorite, author) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    ");
    
    $wordStmt = $pdo->prepare("
        INSERT INTO article_words (article_id, word, translation, position, created_at) 
        VALUES (?, ?, ?, ?, ?)
    ");
    
    $count = 0;
    foreach ($articles as $article) {
        // 安全处理各种数据类型
        $generationTimestamp = safeIntConvert($article['generationTimestamp'] ?? time() * 1000);
        $viewCount = safeIntConvert($article['viewCount'] ?? 0);
        $isFavorite = safeBooleanConvert($article['isFavorite'] ?? false);
        
        // 插入文章
        $articleStmt->execute([
            $generationTimestamp,
            $article['keyWords'] ?? '',
            $viewCount,
            $article['apiResult'] ?? '',
            $article['englishTitle'] ?? '',
            $article['titleTranslation'] ?? '',
            $article['englishContent'] ?? '',
            $article['chineseContent'] ?? '',
            $article['bilingualComparison'] ?? '',
            $isFavorite,
            $article['author'] ?? ''
        ]);
        
        // 获取文章ID
        $articleId = $pdo->lastInsertId();
        
        // 插入关键词作为文章单词
        $keyWords = $article['keyWords'] ?? '';
        if (!empty($keyWords)) {
            insertArticleKeywords($wordStmt, $articleId, $keyWords);
        }
        
        $count++;
    }
    
    return $count;
}

/**
 * 插入文章关键词
 */
function insertArticleKeywords($wordStmt, $articleId, $keyWords) {
    $words = array_filter(array_map('trim', explode(',', $keyWords)));
    foreach ($words as $index => $word) {
        if (!empty($word)) {
            $wordStmt->execute([
                $articleId,
                $word,
                '',
                $index,
                time()
            ]);
        }
    }
}

/**
 * 下载单词数据
 */
function downloadWords($pdo) {
    $stmt = $pdo->query("
        SELECT word, apiResult, queryTimestamp, viewCount, isFavorite, spellingCount, chineseDefinition, phonetic, partOfSpeech, referenceCount
        FROM words 
        ORDER BY queryTimestamp DESC
    ");
    $words = $stmt->fetchAll();
    
    // 确保数据类型正确
    $processedWords = [];
    foreach ($words as $word) {
        $processedWords[] = [
            'word' => (string)($word['word'] ?? ''),
            'apiResult' => (string)($word['apiResult'] ?? ''),
            'queryTimestamp' => (int)($word['queryTimestamp'] ?? 0),
            'viewCount' => (int)($word['viewCount'] ?? 0),
            'isFavorite' => (bool)($word['isFavorite'] ?? false),
            'spellingCount' => (int)($word['spellingCount'] ?? 0),
            'chineseDefinition' => (string)($word['chineseDefinition'] ?? ''),
            'phonetic' => (string)($word['phonetic'] ?? ''),
            'partOfSpeech' => (string)($word['partOfSpeech'] ?? ''),
            'referenceCount' => (int)($word['referenceCount'] ?? 0)
        ];
    }
    
    return $processedWords;
}

/**
 * 下载文章数据
 */
function downloadArticles($pdo) {
    $stmt = $pdo->query("
        SELECT id, generationTimestamp, keyWords, viewCount, apiResult, englishTitle, titleTranslation, englishContent, chineseContent, bilingualComparison, isFavorite, author
        FROM articles 
        ORDER BY generationTimestamp DESC
    ");
    $articles = $stmt->fetchAll();
    
    // 确保数据类型正确
    $processedArticles = [];
    foreach ($articles as $article) {
        $processedArticles[] = [
            'id' => (int)($article['id'] ?? 0),
            'generationTimestamp' => (int)($article['generationTimestamp'] ?? 0),
            'keyWords' => (string)($article['keyWords'] ?? ''),
            'viewCount' => (int)($article['viewCount'] ?? 0),
            'apiResult' => (string)($article['apiResult'] ?? ''),
            'englishTitle' => (string)($article['englishTitle'] ?? ''),
            'titleTranslation' => (string)($article['titleTranslation'] ?? ''),
            'englishContent' => (string)($article['englishContent'] ?? ''),
            'chineseContent' => (string)($article['chineseContent'] ?? ''),
            'bilingualComparison' => (string)($article['bilingualComparison'] ?? ''),
            'isFavorite' => (bool)($article['isFavorite'] ?? false),
            'author' => (string)($article['author'] ?? '')
        ];
    }
    
    return $processedArticles;
}

/**
 * 清空所有数据
 */
function clearAllData($pdo) {
    $pdo->exec("DELETE FROM article_words");
    $pdo->exec("DELETE FROM articles");
    $pdo->exec("DELETE FROM words");
}
?>