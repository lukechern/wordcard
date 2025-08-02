<?php
/**
 * 数据库连接和表结构管理模块
 */

/**
 * 获取数据库连接
 */
function getDbConnection() {
    try {
        $dsn = "mysql:host=" . DB_HOST . ";dbname=" . DB_NAME . ";charset=" . DB_CHARSET;
        $pdo = new PDO($dsn, DB_USER, DB_PASS, [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES " . DB_CHARSET
        ]);
        return $pdo;
    } catch (PDOException $e) {
        throw new Exception('Database connection failed: ' . $e->getMessage());
    }
}

/**
 * 创建数据库表结构
 */
function createTables($pdo) {
    // 创建单词表
    $createWordsTable = "
        CREATE TABLE IF NOT EXISTS words (
            word VARCHAR(255) PRIMARY KEY,
            apiResult TEXT,
            queryTimestamp BIGINT,
            viewCount INT DEFAULT 0,
            isFavorite TINYINT(1) DEFAULT 0,
            spellingCount INT DEFAULT 0,
            chineseDefinition TEXT,
            phonetic VARCHAR(500),
            partOfSpeech VARCHAR(100),
            referenceCount INT DEFAULT 0
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    ";
    
    // 创建文章表
    $createArticlesTable = "
        CREATE TABLE IF NOT EXISTS articles (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            generationTimestamp BIGINT,
            keyWords TEXT,
            viewCount INT DEFAULT 0,
            apiResult TEXT,
            englishTitle TEXT,
            titleTranslation TEXT,
            englishContent LONGTEXT,
            chineseContent LONGTEXT,
            bilingualComparison LONGTEXT,
            isFavorite TINYINT(1) DEFAULT 0,
            author VARCHAR(255)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    ";
    
    // 创建文章单词关联表
    $createArticleWordsTable = "
        CREATE TABLE IF NOT EXISTS article_words (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            article_id BIGINT,
            word VARCHAR(255),
            translation TEXT,
            position INT,
            created_at BIGINT,
            FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    ";
    
    $pdo->exec($createWordsTable);
    $pdo->exec($createArticlesTable);
    $pdo->exec($createArticleWordsTable);
}

/**
 * 测试数据库连接
 */
function testDatabaseConnection($pdo) {
    $stmt = $pdo->query("SELECT 1 as test");
    $result = $stmt->fetch();
    
    if ($result && $result['test'] == 1) {
        return true;
    } else {
        throw new Exception('Database test query failed');
    }
}
?>