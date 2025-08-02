<?php
/**
 * 响应处理模块 - 统一处理HTTP响应
 */

/**
 * 设置响应头
 */
function setResponseHeaders() {
    header('Content-Type: application/json; charset=utf-8');
    header('Access-Control-Allow-Origin: *');
    header('Access-Control-Allow-Methods: POST, OPTIONS');
    header('Access-Control-Allow-Headers: Content-Type');
}

/**
 * 发送成功响应
 */
function sendSuccessResponse($data = [], $message = 'Success') {
    $response = [
        'success' => true,
        'message' => $message,
        'timestamp' => time(),
        'date' => date('Y-m-d H:i:s')
    ];
    
    if (!empty($data)) {
        $response = array_merge($response, $data);
    }
    
    echo json_encode($response);
}

/**
 * 发送错误响应
 */
function sendErrorResponse($message, $code = 400, $details = []) {
    http_response_code($code);
    
    $response = [
        'success' => false,
        'message' => $message,
        'error_code' => $code,
        'timestamp' => time(),
        'date' => date('Y-m-d H:i:s')
    ];
    
    if (!empty($details)) {
        $response['details'] = $details;
    }
    
    echo json_encode($response);
}

/**
 * 发送服务器错误响应
 */
function sendServerErrorResponse($exception) {
    $message = 'Server error: ' . $exception->getMessage();
    
    $details = [];
    if (DEBUG_MODE) {
        $details = [
            'file' => $exception->getFile(),
            'line' => $exception->getLine(),
            'trace' => $exception->getTraceAsString()
        ];
    }
    
    sendErrorResponse($message, 500, $details);
}

/**
 * 记录错误日志
 */
function logError($message, $context = []) {
    $logMessage = date('Y-m-d H:i:s') . ' - ' . $message;
    if (!empty($context)) {
        $logMessage .= ' - Context: ' . json_encode($context);
    }
    
    // 写入错误日志文件
    error_log($logMessage . PHP_EOL, 3, __DIR__ . '/../logs/error.log');
}
?>