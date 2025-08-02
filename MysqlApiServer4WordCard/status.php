<?php
/**
 * WordCard API 状态页面
 * 用于快速检查API服务状态
 */

// 引入必要的文件
require_once 'env_config_7ree.php';
require_once 'include/database.php';
require_once 'include/utils.php';

// 设置响应头
header('Content-Type: text/html; charset=utf-8');

try {
    $pdo = getDbConnection();
    $systemStatus = getSystemStatus($pdo);
    $apiVersion = getApiVersion();
    $dbConnected = true;
} catch (Exception $e) {
    $dbConnected = false;
    $errorMessage = $e->getMessage();
}
?>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WordCard API Status</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
            color: #333;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 30px;
        }
        .header {
            text-align: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #e0e0e0;
        }
        .status-badge {
            display: inline-block;
            padding: 8px 16px;
            border-radius: 20px;
            font-weight: bold;
            margin: 10px 0;
        }
        .status-online {
            background-color: #4CAF50;
            color: white;
        }
        .status-offline {
            background-color: #f44336;
            color: white;
        }
        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        .info-card {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 6px;
            border-left: 4px solid #007bff;
        }
        .info-card h3 {
            margin-top: 0;
            color: #007bff;
        }
        .info-item {
            margin: 8px 0;
            display: flex;
            justify-content: space-between;
        }
        .info-label {
            font-weight: 600;
        }
        .info-value {
            color: #666;
        }
        .error-message {
            background: #ffebee;
            color: #c62828;
            padding: 15px;
            border-radius: 4px;
            border-left: 4px solid #f44336;
            margin: 20px 0;
        }
        .footer {
            text-align: center;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #e0e0e0;
            color: #666;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>WordCard API Status</h1>
            <div class="status-badge <?php echo $dbConnected ? 'status-online' : 'status-offline'; ?>">
                <?php echo $dbConnected ? '🟢 API Online' : '🔴 API Offline'; ?>
            </div>
            <p>实时API服务状态监控</p>
        </div>

        <?php if (!$dbConnected): ?>
            <div class="error-message">
                <strong>数据库连接失败:</strong> <?php echo htmlspecialchars($errorMessage); ?>
            </div>
        <?php endif; ?>

        <div class="info-grid">
            <!-- API版本信息 -->
            <div class="info-card">
                <h3>📋 API信息</h3>
                <?php if (isset($apiVersion)): ?>
                    <div class="info-item">
                        <span class="info-label">版本:</span>
                        <span class="info-value"><?php echo $apiVersion['version']; ?></span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">构建日期:</span>
                        <span class="info-value"><?php echo $apiVersion['build_date']; ?></span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">支持功能:</span>
                        <span class="info-value"><?php echo implode(', ', $apiVersion['features']); ?></span>
                    </div>
                <?php endif; ?>
            </div>

            <!-- 服务器信息 -->
            <div class="info-card">
                <h3>🖥️ 服务器信息</h3>
                <div class="info-item">
                    <span class="info-label">PHP版本:</span>
                    <span class="info-value"><?php echo PHP_VERSION; ?></span>
                </div>
                <div class="info-item">
                    <span class="info-label">时区:</span>
                    <span class="info-value"><?php echo date_default_timezone_get(); ?></span>
                </div>
                <div class="info-item">
                    <span class="info-label">当前时间:</span>
                    <span class="info-value"><?php echo date('Y-m-d H:i:s'); ?></span>
                </div>
                <?php if (isset($systemStatus)): ?>
                    <div class="info-item">
                        <span class="info-label">内存使用:</span>
                        <span class="info-value"><?php echo formatBytes(memory_get_usage(true)); ?></span>
                    </div>
                <?php endif; ?>
            </div>

            <!-- 数据库信息 -->
            <?php if ($dbConnected && isset($systemStatus['database'])): ?>
            <div class="info-card">
                <h3>🗄️ 数据库信息</h3>
                <div class="info-item">
                    <span class="info-label">连接状态:</span>
                    <span class="info-value"><?php echo $systemStatus['database']['status']; ?></span>
                </div>
                <div class="info-item">
                    <span class="info-label">单词数量:</span>
                    <span class="info-value"><?php echo number_format($systemStatus['database']['word_count']); ?></span>
                </div>
                <div class="info-item">
                    <span class="info-label">文章数量:</span>
                    <span class="info-value"><?php echo number_format($systemStatus['database']['article_count']); ?></span>
                </div>
                <div class="info-item">
                    <span class="info-label">MySQL版本:</span>
                    <span class="info-value"><?php echo $pdo->getAttribute(PDO::ATTR_SERVER_VERSION); ?></span>
                </div>
            </div>
            <?php endif; ?>

            <!-- 配置信息 -->
            <div class="info-card">
                <h3>⚙️ 配置信息</h3>
                <div class="info-item">
                    <span class="info-label">数据库主机:</span>
                    <span class="info-value"><?php echo DB_HOST; ?></span>
                </div>
                <div class="info-item">
                    <span class="info-label">数据库名:</span>
                    <span class="info-value"><?php echo DB_NAME; ?></span>
                </div>
                <div class="info-item">
                    <span class="info-label">字符集:</span>
                    <span class="info-value"><?php echo DB_CHARSET; ?></span>
                </div>
                <div class="info-item">
                    <span class="info-label">调试模式:</span>
                    <span class="info-value"><?php echo DEBUG_MODE ? '开启' : '关闭'; ?></span>
                </div>
            </div>
        </div>

        <div class="footer">
            <p>WordCard API Server v<?php echo isset($apiVersion) ? $apiVersion['version'] : '2.0'; ?> | 
            最后更新: <?php echo date('Y-m-d H:i:s'); ?></p>
            <p>
                <a href="index.php" style="color: #007bff; text-decoration: none;">API入口</a> | 
                <a href="README.md" style="color: #007bff; text-decoration: none;">文档</a>
            </p>
        </div>
    </div>

    <script>
        // 自动刷新页面（每30秒）
        setTimeout(function() {
            location.reload();
        }, 30000);
    </script>
</body>
</html>