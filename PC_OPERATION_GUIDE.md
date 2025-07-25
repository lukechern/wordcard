# WordCard 电脑操作功能使用指南

## 功能概述

WordCard app 新增了"从电脑操作"功能，允许用户通过同一局域网内的电脑浏览器来导出和导入app的单词数据。

## 使用步骤

### 1. 启用电脑操作功能

1. 打开 WordCard app
2. 进入"仪表盘"页面
3. 点击右上角的"配置"图标
4. 选择"数据"标签页
5. 找到"从电脑操作"开关并打开

### 2. 获取访问地址

开关打开后，会显示一个局域网访问地址，格式类似：
```
http://192.168.1.100:8080
```

### 3. 电脑端操作

1. 确保电脑和手机连接在同一个WiFi网络
2. 在电脑浏览器中输入显示的地址
3. 打开网页后可以看到数据管理界面

### 4. 数据导出

在网页中点击"导出数据"按钮：
- 系统会自动下载包含所有单词数据的JSON文件
- 文件名格式：`wordcard_export_YYYYMMDD_HHMMSS.json`

### 5. 数据导入

有两种导入方式：

#### 方式一：文件导入
1. 点击"选择文件"按钮
2. 选择之前导出的JSON文件
3. 点击"导入数据"按钮

#### 方式二：文本导入
1. 将JSON数据直接粘贴到文本框中
2. 点击"从文本导入"按钮

## 注意事项

1. **网络要求**：手机和电脑必须连接在同一个局域网（WiFi）
2. **安全性**：服务器仅在局域网内可访问，外网无法访问
3. **数据格式**：仅支持WordCard导出的JSON格式文件
4. **服务器状态**：关闭开关后，服务器会自动停止
5. **电池消耗**：开启服务器会增加一定的电池消耗，建议使用完毕后关闭

## 数据格式说明

导出的JSON文件包含以下信息：
- 导出时间戳
- 数据版本
- 单词列表（包含单词、释义、查询历史、收藏状态等）

## 故障排除

### 无法访问网页
1. 检查手机和电脑是否在同一WiFi网络
2. 确认开关已打开且显示了正确的IP地址
3. 尝试关闭并重新打开开关
4. 检查电脑防火墙设置

### 导入失败
1. 确认JSON文件格式正确
2. 检查文件是否完整（未损坏）
3. 确认是WordCard导出的文件

### 服务器无法启动
1. 检查网络权限
2. 尝试重启app
3. 确认端口8080未被其他应用占用

## 技术细节

- 服务器端口：8080
- 支持的IP范围：192.168.x.x, 10.x.x.x, 172.16-31.x.x
- 数据传输：HTTP协议（局域网内安全）
- 文件格式：JSON（UTF-8编码）