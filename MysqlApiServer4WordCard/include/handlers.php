<?php
/**
 * 请求处理器模块 - 处理不同的API请求
 */

/**
 * 处理测试请求
 */
function handleTest() {
    $pdo = getDbConnection();
    
    // 测试数据库连接
    if (testDatabaseConnection($pdo)) {
        $response = [
            'success' => true,
            'message' => 'Connection test successful',
            'timestamp' => time(),
            'date' => date('Y-m-d H:i:s'),
            'server_info' => [
                'php_version' => PHP_VERSION,
                'mysql_version' => $pdo->getAttribute(PDO::ATTR_SERVER_VERSION),
                'memory_usage' => formatBytes(memory_get_usage(true)),
                'memory_peak' => formatBytes(memory_get_peak_usage(true))
            ],
            'api_version' => getApiVersion(),
            'system_status' => getSystemStatus($pdo)
        ];
        
        echo json_encode($response);
    }
}

/**
 * 处理上传请求
 */
function handleUpload($data) {
    // 验证上传数据
    if (!isset($data['words']) || !isset($data['articles'])) {
        sendErrorResponse('Missing words or articles data', 400);
        return;
    }
    
    // 清理输入数据
    $data = sanitizeInput($data);
    
    // 验证数据完整性
    $validationErrors = validateDataIntegrity($data);
    if (!empty($validationErrors)) {
        sendErrorResponse('Data validation failed', 400, ['validation_errors' => $validationErrors]);
        return;
    }
    
    $pdo = getDbConnection();
    
    // 创建表结构
    createTables($pdo);
    
    // 开始事务
    $pdo->beginTransaction();
    
    try {
        // 记录上传前的数据统计
        $beforeStats = getSystemStatus($pdo);
        
        // 清空现有数据
        clearAllData($pdo);
        
        // 上传单词数据
        $wordCount = uploadWords($pdo, $data['words']);
        
        // 上传文章数据
        $articleCount = uploadArticles($pdo, $data['articles']);
        
        // 提交事务
        $pdo->commit();
        
        // 记录上传后的数据统计
        $afterStats = getSystemStatus($pdo);
        
        $response = [
            'success' => true,
            'message' => 'Data uploaded successfully',
            'word_count' => $wordCount,
            'article_count' => $articleCount,
            'timestamp' => time(),
            'upload_date' => date('Y-m-d H:i:s'),
            'statistics' => [
                'before' => $beforeStats['database'] ?? null,
                'after' => $afterStats['database'] ?? null
            ],
            'performance' => [
                'memory_usage' => formatBytes(memory_get_usage(true)),
                'memory_peak' => formatBytes(memory_get_peak_usage(true))
            ]
        ];
        
        echo json_encode($response);
        
    } catch (Exception $e) {
        // 回滚事务
        $pdo->rollBack();
        throw $e;
    }
}

/**
 * 处理下载请求
 */
function handleDownload() {
    $startTime = microtime(true);
    $pdo = getDbConnection();
    
    // 获取系统状态
    $systemStatus = getSystemStatus($pdo);
    
    // 下载单词数据
    $words = downloadWords($pdo);
    
    // 下载文章数据
    $articles = downloadArticles($pdo);
    
    $endTime = microtime(true);
    $executionTime = round(($endTime - $startTime) * 1000, 2); // 毫秒
    
    $response = [
        'success' => true,
        'message' => 'Data downloaded successfully',
        'words' => $words,
        'articles' => $articles,
        'word_count' => count($words),
        'article_count' => count($articles),
        'timestamp' => time(),
        'download_date' => date('Y-m-d H:i:s'),
        'performance' => [
            'execution_time_ms' => $executionTime,
            'memory_usage' => formatBytes(memory_get_usage(true)),
            'memory_peak' => formatBytes(memory_get_peak_usage(true)),
            'data_size' => formatBytes(strlen(json_encode(['words' => $words, 'articles' => $articles])))
        ],
        'system_status' => $systemStatus
    ];
    
    echo json_encode($response);
}

/**
 * 处理未知请求
 */
function handleUnknownAction($action) {
    http_response_code(400);
    echo json_encode([
        'success' => false, 
        'message' => 'Invalid action: ' . $action,
        'supported_actions' => ['test', 'upload', 'download']
    ]);
}
?>