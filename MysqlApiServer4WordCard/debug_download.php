<?php
/**
 * 调试下载功能 - 检查数据格式
 */

// 引入环境配置
require_once 'env_config_7ree.php';
require_once 'include/database.php';
require_once 'include/data_handler.php';

try {
    $pdo = getDbConnection();
    
    echo "=== 调试下载功能 ===\n\n";
    
    // 检查表是否存在
    $tables = $pdo->query("SHOW TABLES")->fetchAll(PDO::FETCH_COLUMN);
    echo "数据库表: " . implode(', ', $tables) . "\n\n";
    
    // 检查单词表数据
    $wordCount = $pdo->query("SELECT COUNT(*) FROM words")->fetchColumn();
    echo "单词表记录数: $wordCount\n";
    
    if ($wordCount > 0) {
        // 获取第一条单词记录
        $firstWord = $pdo->query("SELECT * FROM words LIMIT 1")->fetch();
        echo "第一条单词记录:\n";
        print_r($firstWord);
        echo "\n";
        
        // 检查字段类型
        echo "字段类型检查:\n";
        echo "- word: " . gettype($firstWord['word']) . " = '" . $firstWord['word'] . "'\n";
        echo "- queryTimestamp: " . gettype($firstWord['queryTimestamp']) . " = " . $firstWord['queryTimestamp'] . "\n";
        echo "- viewCount: " . gettype($firstWord['viewCount']) . " = " . $firstWord['viewCount'] . "\n";
        echo "- isFavorite: " . gettype($firstWord['isFavorite']) . " = " . $firstWord['isFavorite'] . "\n";
        echo "- spellingCount: " . gettype($firstWord['spellingCount']) . " = " . $firstWord['spellingCount'] . "\n";
        echo "- referenceCount: " . gettype($firstWord['referenceCount']) . " = " . $firstWord['referenceCount'] . "\n";
        echo "\n";
    }
    
    // 检查文章表数据
    $articleCount = $pdo->query("SELECT COUNT(*) FROM articles")->fetchColumn();
    echo "文章表记录数: $articleCount\n";
    
    if ($articleCount > 0) {
        // 获取第一条文章记录
        $firstArticle = $pdo->query("SELECT * FROM articles LIMIT 1")->fetch();
        echo "第一条文章记录:\n";
        print_r($firstArticle);
        echo "\n";
    }
    
    // 模拟下载请求
    echo "=== 模拟下载请求 ===\n";
    $words = downloadWords($pdo);
    $articles = downloadArticles($pdo);
    
    echo "下载的单词数量: " . count($words) . "\n";
    echo "下载的文章数量: " . count($articles) . "\n";
    
    if (!empty($words)) {
        echo "\n第一个单词的JSON格式:\n";
        echo json_encode($words[0], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "\n";
    }
    
    if (!empty($articles)) {
        echo "\n第一个文章的JSON格式:\n";
        echo json_encode($articles[0], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "\n";
    }
    
    // 检查完整响应格式
    echo "\n=== 完整响应格式 ===\n";
    $response = [
        'success' => true,
        'message' => 'Data downloaded successfully',
        'words' => $words,
        'articles' => $articles,
        'word_count' => count($words),
        'article_count' => count($articles),
        'timestamp' => time(),
        'download_date' => date('Y-m-d H:i:s')
    ];
    
    echo "响应大小: " . strlen(json_encode($response)) . " bytes\n";
    echo "JSON格式是否有效: " . (json_last_error() === JSON_ERROR_NONE ? "是" : "否") . "\n";
    
    if (json_last_error() !== JSON_ERROR_NONE) {
        echo "JSON错误: " . json_last_error_msg() . "\n";
    }
    
} catch (Exception $e) {
    echo "错误: " . $e->getMessage() . "\n";
    echo "堆栈跟踪:\n" . $e->getTraceAsString() . "\n";
}
?>