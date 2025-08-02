<?php
/**
 * 认证和安全相关功能模块
 */

/**
 * 验证token
 * 使用密钥 + 当前日期生成MD5哈希进行验证
 */
function validateToken($token) {
    $currentDate = date('Ymd'); // 格式：20250802
    $expectedToken = md5(API_KEY . $currentDate);
    return hash_equals($expectedToken, $token);
}

/**
 * 生成当日token（用于测试）
 */
function generateDailyToken() {
    $currentDate = date('Ymd');
    return md5(API_KEY . $currentDate);
}

/**
 * 验证请求方法
 */
function validateRequestMethod() {
    // 处理OPTIONS请求
    if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
        http_response_code(200);
        exit();
    }
    
    // 只允许POST请求
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        http_response_code(405);
        echo json_encode(['success' => false, 'message' => 'Only POST method allowed']);
        exit();
    }
}

/**
 * 验证JSON数据
 */
function validateJsonData() {
    $input = file_get_contents('php://input');
    $data = json_decode($input, true);
    
    if (!$data) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Invalid JSON data']);
        exit();
    }
    
    return $data;
}

/**
 * 验证必要参数
 */
function validateRequiredParams($data) {
    if (!isset($data['action']) || !isset($data['token'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Missing required parameters: action, token']);
        exit();
    }
    
    return [$data['action'], $data['token']];
}
?>