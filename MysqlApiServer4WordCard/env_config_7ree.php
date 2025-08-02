<?php
/**
 * WordCard PHP API 环境配置文件
 * 用于配置数据库连接信息和API密钥
 */

// 数据库连接配置
define('DB_HOST', 'localhost');
define('DB_NAME', 'wordcard_db');
define('DB_USER', 'wordcard_user');
define('DB_PASS', 'your_password_here');
define('DB_CHARSET', 'utf8mb4');

// API密钥配置（与APP中的密钥保持一致）
define('API_KEY', 'your_secret_key_here');

// 其他配置
define('TIMEZONE', 'Asia/Shanghai');
define('DEBUG_MODE', false);

// 设置时区
date_default_timezone_set(TIMEZONE);

// 错误报告设置
if (DEBUG_MODE) {
    error_reporting(E_ALL);
    ini_set('display_errors', 1);
} else {
    error_reporting(0);
    ini_set('display_errors', 0);
}
?>