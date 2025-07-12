# WordCard - AI 智能单词卡片应用

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)
![OpenAI](https://img.shields.io/badge/OpenAI-412991?style=for-the-badge&logo=openai&logoColor=white)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)
[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg?style=for-the-badge)](https://github.com/yourusername/wordcard)

</div>

## 📱 应用简介

WordCard 是一款基于 AI 的智能单词学习安卓应用APP，集成了 OpenAI GPT 或兼容模型，为用户提供智能化的英语单词查询和学习体验。应用采用现代化的 Jetpack Compose UI 框架，提供流畅的用户界面和丰富的功能特性。

## ✨ 主要功能

### 🤖 AI 智能查询
- 集成 OpenAI GPT 及其兼容模型，提供智能单词解释
- 支持音标、词性、中文定义、英文例句等详细信息
- 智能例句生成，包含中文翻译
- 同义词和反义词推荐

### 🎵 语音朗读
- 支持单词和例句的 TTS 语音朗读
- 支持英文和中文语音
- 智能语音状态管理

### 📚 学习管理
- 单词历史记录管理
- 收藏功能，支持标记重要单词
- 学习进度统计
- 数据导入导出功能

### 🎨 现代化界面
- 基于 Jetpack Compose 的现代化 UI
- Material Design 3 设计语言
- 流畅的动画效果
- 支持深色/浅色主题

### 📱 交互体验
- 手势导航，支持上下滑动切换单词
- 智能触摸区域划分，避免误触
- 响应式布局设计

## 🛠️ 技术栈

### 前端技术
- **UI 框架**: Jetpack Compose
- **设计语言**: Material Design 3
- **编程语言**: Kotlin
- **最低 SDK**: Android 6.0 (API 24)
- **目标 SDK**: Android 14 (API 36)

### 后端技术
- **AI 服务**: OpenAI GPT API
- **网络请求**: Ktor Client
- **数据序列化**: Kotlinx Serialization
- **Markdown 渲染**: Markwon

### 数据存储
- **本地数据库**: Room Database
- **数据访问**: Repository Pattern
- **数据导出**: JSON 格式

### 架构模式
- **架构模式**: MVVM (Model-View-ViewModel)
- **依赖注入**: 手动依赖注入
- **状态管理**: StateFlow + Compose State

## 📦 安装说明

### 环境要求
- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 11 或更高版本
- Android SDK 24 或更高版本

### 构建步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/luckchern/wordcard.git
   cd wordcard
   ```

2. **配置 API 密钥**
   - 在 `app/src/main/java/com/x7ree/wordcard/config/AppConfig_7ree.kt` 中配置您的 OpenAI API 密钥
   - 或者通过应用内设置界面配置

3. **构建项目**
   ```bash
   ./gradlew build
   ```

4. **安装到设备**
   ```bash
   ./gradlew installDebug
   ```

## 🔧 配置说明

### OpenAI API 配置
应用需要配置 OpenAI API 密钥才能使用 AI 查询功能：

1. 访问 [OpenAI API](https://platform.openai.com/api-keys) 获取 API 密钥
2. 在应用设置中配置 API 密钥
3. 可选择配置自定义 API 端点
4. 如果是其他 api提供商，只要兼容openai api接口，也可支持

### 提示词配置
支持自定义查询提示词和输出模板，以获得更符合个人需求的查询结果。

## 📁 项目结构

```
app/src/main/java/com/x7ree/wordcard/
├── api/                    # API 服务层
│   └── OpenAiApiService_7ree.kt
├── config/                 # 配置管理
│   ├── AppConfig_7ree.kt
│   └── PromptConfig_7ree.kt
├── data/                   # 数据层
│   ├── WordEntity_7ree.kt
│   ├── WordDao_7ree.kt
│   ├── WordDatabase_7ree.kt
│   ├── WordRepository_7ree.kt
│   └── DataExportImportManager_7ree.kt
├── query/                  # 查询逻辑
│   └── WordQueryViewModel_7ree.kt
├── ui/                     # 用户界面
│   ├── MainActivity.kt
│   ├── MainScreen_7ree.kt
│   ├── WordCardScreen_7ree.kt
│   ├── SplashScreen_7ree.kt
│   ├── BottomNavigationBar_7ree.kt
│   ├── SwipeNavigationComponent_7ree.kt
│   ├── SwipeableRevealItem_7ree.kt
│   └── theme/              # 主题相关
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
```

## 🚀 使用指南

### 基本使用
1. 启动应用后，在搜索框中输入要查询的英文单词
2. 点击"开始查"按钮，等待 AI 生成查询结果
3. 查看单词的详细信息，包括音标、定义、例句等
4. 点击喇叭图标收听单词或例句的发音
5. 点击收藏图标保存重要单词

### 高级功能
- **历史记录**: 在历史标签页查看所有查询过的单词
- **数据导出**: 在设置页面导出学习数据为 JSON 文件
- **数据导入**: 支持导入之前导出的学习数据
- **手势导航**: 在单词详情页面上下滑动切换单词

## 🤝 贡献指南

我们欢迎所有形式的贡献！请遵循以下步骤：

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

### 开发规范
- 遵循 Kotlin 编码规范
- 使用有意义的变量和函数命名
- 添加适当的注释和文档
- 确保代码通过所有测试

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

- [OpenAI](https://openai.com/) - 提供强大的 AI 模型
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - 现代化的 UI 框架
- [Material Design](https://material.io/) - 设计语言指导
- [Ktor](https://ktor.io/) - 网络请求库
- [Room](https://developer.android.com/training/data-storage/room) - 本地数据库

## 📞 联系方式

- 项目主页: [https://github.com/luckchern/wordcard](https://github.com/luckchern/wordcard)
- 问题反馈: [Issues](https://github.com/luckchern/wordcard/issues)


---

<div align="center">

**如果这个项目对您有帮助，请给它一个 ⭐️**

</div> 