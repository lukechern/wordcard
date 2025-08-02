# WordCard MySQL API Server

这是WordCard应用的PHP API服务端，用于通过安全的API接口实现APP和MySQL数据库中的单词和文章数据的双向管理。

## 📁 目录结构

```
MysqlApiServer4WordCard/
├── .gitignore               # Git忽略文件
├── debug_download.php       # 调试下载接口
├── env_config_7ree.php      # 环境配置文件
├── index.php                # 主入口文件
├── README.md                # 说明文档
├── status.php               # 状态检查接口
├── include/                 # 功能模块目录
│   ├── auth.php            # 认证和安全模块
│   ├── data_handler.php    # 数据处理模块
│   ├── database.php        # 数据库连接和表结构管理
│   ├── handlers.php        # 请求处理器模块
│   ├── response.php        # 响应处理模块
│   └── utils.php           # 工具函数模块
└── logs/                   # 日志目录
    └── .gitkeep           # 保持目录结构
```

## 🚀 部署步骤

### 1. 环境要求
- PHP >= 7.0
- MySQL >= 5.7
- PDO MySQL扩展
- Web服务器（Apache/Nginx）

### 2. 配置数据库
修改 `env_config_7ree.php` 文件中的数据库连接信息：

```php
// 数据库连接配置
define('DB_HOST', 'localhost');
define('DB_NAME', 'your_database_name');
define('DB_USER', 'your_username');
define('DB_PASS', 'your_password');

// API密钥配置（与APP中的密钥保持一致）
define('API_KEY', 'your_secret_key_here');
```

### 3. 上传文件
将整个 `MysqlApiServer4WordCard` 目录上传到Web服务器。

### 4. 设置权限
确保 `logs` 目录具有写入权限：
```bash
chmod 755 logs/
```

### 5. 测试API
访问 `http://your-domain.com/MysqlApiServer4WordCard/index.php` 进行测试。

### 6. API状态监控面板
访问 `http://your-domain.com/MysqlApiServer4WordCard/status.php` 查看。

## 🔧 API接口

### 支持的操作
- `test`: 测试连接和token验证
- `upload`: 上传单词和文章数据
- `download`: 下载单词和文章数据

### 请求格式
```json
{
    "action": "test|upload|download",
    "token": "generated_daily_token",
    "words": [...],      // 仅upload时需要
    "articles": [...]    // 仅upload时需要
}
```

### 响应格式
```json
{
    "success": true|false,
    "message": "操作结果描述",
    "timestamp": 1234567890,
    "date": "2025-01-02 12:00:00",
    // 其他数据字段...
}
```

## 🔒 安全特性

### Token验证
- 使用密钥 + 当前日期（YYYYMMDD）生成MD5哈希
- 每日自动更新，防止重放攻击
- 使用 `hash_equals()` 防止时序攻击

### 数据处理
- 使用PDO预处理语句防止SQL注入
- 严格的数据类型转换和验证
- 事务处理确保数据一致性

### 错误处理
- 统一的错误响应格式
- 详细的错误日志记录
- 调试模式控制敏感信息显示

## 📊 数据库表结构

### words 表
存储单词数据，包含单词、释义、查询次数等信息。

### articles 表
存储文章数据，包含标题、内容、翻译等信息。

### article_words 表
存储文章关键词关联数据。

## 🛠️ 维护和升级

### 模块化设计
- 各功能模块独立，便于维护
- 统一的响应处理机制
- 可扩展的认证系统

### 日志系统
- 自动记录错误日志到 `logs/error.log`
- 包含详细的上下文信息
- 支持调试模式

### 版本控制
- 所有文件可安全提交到Git
- 敏感配置通过环境变量管理
- 清晰的版本标识

## 📞 技术支持

如有问题，请检查：
1. PHP错误日志
2. `logs/error.log` 文件
3. 数据库连接配置
4. Web服务器配置

---


**版本**: 2.1
**更新日期**: 2025-08-02
**作者**: WordCard Team