<?php
/**
 * 工具函数模块
 */

/**
 * 获取API版本信息
 */
function getApiVersion() {
    return [
        'version' => '2.0',
        'build_date' => '2025-01-02',
        'php_version' => PHP_VERSION,
        'features' => [
            'token_auth',
            'data_upload',
            'data_download',
            'connection_test',
            'error_logging'
        ]
    ];
}

/**
 * 获取系统状态信息
 */
function getSystemStatus($pdo = null) {
    $status = [
        'api_status' => 'running',
        'timestamp' => time(),
        'date' => date('Y-m-d H:i:s'),
        'timezone' => date_default_timezone_get(),
        'memory_usage' => memory_get_usage(true),
        'memory_peak' => memory_get_peak_usage(true)
    ];
    
    // 如果提供了数据库连接，检查数据库状态
    if ($pdo) {
        try {
            $stmt = $pdo->query("SELECT COUNT(*) as word_count FROM words");
            $wordCount = $stmt->fetch()['word_count'] ?? 0;
            
            $stmt = $pdo->query("SELECT COUNT(*) as article_count FROM articles");
            $articleCount = $stmt->fetch()['article_count'] ?? 0;
            
            $status['database'] = [
                'status' => 'connected',
                'word_count' => $wordCount,
                'article_count' => $articleCount
            ];
        } catch (Exception $e) {
            $status['database'] = [
                'status' => 'error',
                'message' => $e->getMessage()
            ];
        }
    }
    
    return $status;
}

/**
 * 格式化文件大小
 */
function formatBytes($size, $precision = 2) {
    $units = ['B', 'KB', 'MB', 'GB', 'TB'];
    
    for ($i = 0; $size > 1024 && $i < count($units) - 1; $i++) {
        $size /= 1024;
    }
    
    return round($size, $precision) . ' ' . $units[$i];
}

/**
 * 验证数据完整性
 */
function validateDataIntegrity($data) {
    $errors = [];
    
    // 验证单词数据
    if (isset($data['words']) && is_array($data['words'])) {
        foreach ($data['words'] as $index => $word) {
            if (!isset($word['word']) || empty($word['word'])) {
                $errors[] = "Word at index $index is missing 'word' field";
            }
        }
    }
    
    // 验证文章数据
    if (isset($data['articles']) && is_array($data['articles'])) {
        foreach ($data['articles'] as $index => $article) {
            if (!isset($article['englishTitle']) || empty($article['englishTitle'])) {
                $errors[] = "Article at index $index is missing 'englishTitle' field";
            }
        }
    }
    
    return $errors;
}

/**
 * 清理和验证输入数据
 */
function sanitizeInput($input) {
    if (is_string($input)) {
        return trim(htmlspecialchars($input, ENT_QUOTES, 'UTF-8'));
    } elseif (is_array($input)) {
        return array_map('sanitizeInput', $input);
    }
    return $input;
}
?>