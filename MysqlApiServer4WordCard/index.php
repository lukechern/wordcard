<?php
/**
 * WordCard PHP API - 主入口文件
 * 用于接收APP推送的POST数据，验证密钥，上传/下载数据到MySQL数据库
 * 
 * @version 2.0
 * @author WordCard Team
 * @date 2025-01-02
 */

// 引入环境配置
require_once 'env_config_7ree.php';

// 引入功能模块
require_once 'include/response.php';
require_once 'include/auth.php';
require_once 'include/database.php';
require_once 'include/data_handler.php';
require_once 'include/handlers.php';
require_once 'include/utils.php';

// 设置响应头
setResponseHeaders();

// 验证请求方法
validateRequestMethod();

// 验证JSON数据
$data = validateJsonData();

// 验证必要参数
list($action, $token) = validateRequiredParams($data);

// 验证token
if (!validateToken($token)) {
    sendErrorResponse('Invalid token', 401);
    exit();
}

// 根据action执行相应操作
try {
    switch ($action) {
        case 'test':
            handleTest();
            break;
        case 'upload':
            handleUpload($data);
            break;
        case 'download':
            handleDownload();
            break;
        default:
            handleUnknownAction($action);
            break;
    }
} catch (Exception $e) {
    // 记录错误日志
    logError('API Error: ' . $e->getMessage(), [
        'action' => $action,
        'file' => $e->getFile(),
        'line' => $e->getLine()
    ]);
    
    // 发送错误响应
    sendServerErrorResponse($e);
}
