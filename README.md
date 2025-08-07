<a name="chinese-version"></a>
# WordCard - AI 智能单词卡片应用

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)
![OpenAI](https://img.shields.io/badge/OpenAI-412991?style=for-the-badge&logo=openai&logoColor=white)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)
[![Version](https://img.shields.io/badge/version-6.6.3-blue.svg?style=for-the-badge)](https://github.com/lukechern/wordcard)

</div>

<div align="center">
  <a href="#chinese-version">中文版本</a> •
  <a href="#english-version">English Version</a>
</div>

## 📱 应用简介

WordCard单词卡片 是一款基于 AI 的智能单词学习安卓应用APP，集成了 OpenAI GPT 或兼容模型，为用户提供智能化的英语单词查询和学习体验。应用采用现代化的 Jetpack Compose UI 框架，提供流畅的用户界面和丰富的功能特性。

## ✨ 主要功能

### 🤖 AI 智能查询
- 集成 OpenAI GPT 及其兼容模型，提供智能单词解释
- 支持音标、词性、中文定义、英文例句等详细信息
- 智能例句生成，包含中文翻译
- 同义词和反义词推荐
- 优化中文释义解析，确保释义完全输出后才进行解析和显示
- 通过关键词检测机制判断流式输出完成状态，提升解析准确性

### 🎵 语音朗读
- 支持单词和例句的 TTS 语音朗读
- 支持英文和中文语音
- 智能语音状态管理
- 新增自动朗读功能，在单词查询完成或拼写练习时自动朗读单词发音

### 📚 学习管理
- 单词历史记录管理
- 收藏功能，支持标记重要单词
- 学习进度统计和可视化图表
- 本周学习趋势分析
- 数据导入导出功能
- 支持通过局域网在PC端进行数据导入导出操作
- PC端Web界面支持文件导入和手动粘贴JSON数据两种方式
- AI情景文章生成，基于单词本中的单词自动生成情景文章，帮助在语境中深入学习单词
- 文章收藏、搜索和管理功能，方便用户管理和复习生成的文章

### ✏️ 拼写练习
- 交互式单词拼写练习功能
- 智能中文词义提示（自动显示前两个词义）
- 实时拼写正确性检测
- 成功/失败动画反馈
- 自动聚焦输入框，提升练习体验
- 精美的UI设计，专注学习效果
- 拼写练习卡片打开时支持自动朗读目标单词

### 🎨 现代化界面
- 基于 Jetpack Compose 的现代化 UI
- Material Design 3 设计语言
- 流畅的动画效果
- 支持深色/浅色主题
- 优化界面布局，提升信息展示效率
- 统一设计语言，提供一致的视觉体验

### 📱 交互体验
- 手势导航，支持上下滑动切换单词
- 智能触摸区域划分，避免误触
- 响应式布局设计
- 新增边缘滑动返回功能，从屏幕左边缘向右滑动即可返回单词本
- 智能返回键优化，保持浏览位置和筛选状态
- **文章阅读位置记录**: 从文章列表页进入详情页前自动记录滚动位置，返回时精确还原到之前的阅读位置
- **边缘滑动返回**: 在文章详情页支持从屏幕两侧向中间滑动手指返回文章列表页，操作更加便捷自然
- **智能位置恢复**: 支持分页加载、搜索模式等多种场景下的滚动位置保存和恢复，确保阅读体验的连续性

### ⌨️ 全新自定义键盘
- 便捷高效的键位布局设计，操作更加流畅自然
- 现代化的键盘界面，视觉效果精美协调
- 精简的键位配置，有效减少误触和输入错误
- 三重感官反馈：视觉动画 + 触觉震动 + 音效提示
- 沉浸式的交互体验，每次按键都有丰富的反馈效果
- 灵活的键盘切换，支持应用内置键盘和系统键盘自由选择

### 🔍 智能搜索功能
- 实时搜索体验，在单词本页面新增智能搜索功能
- 无缝界面切换，标题栏与搜索框之间平滑切换
- 智能键盘控制，右下角动态按钮控制键盘展开与收起
- 实时搜索反馈，输入字符即时显示匹配结果，支持模糊搜索

### 🔄 下拉刷新功能
- 智能下拉刷新，在单词本页面轻松刷新单词列表数据
- 流畅刷新动画，提供平滑的下拉刷新动画效果
- 数据同步优化，刷新时自动重新加载单词计数和分页数据
- 状态保持，刷新过程中保持收藏过滤状态

### 🔐 安全存储
- API密钥加密存储，使用Android Keystore和AES加密算法保护敏感信息
- 安全迁移机制，自动检测并迁移旧版本的明文存储密钥到加密存储
- 完整性验证，内置存储完整性验证机制，确保密钥数据的可靠性

### 📊 数据统计功能
- 全新统计卡片设计，现代化的扁平设计风格
- 智能数字动画，数字卡片加载时实现从0增长到目标数值的1秒动画效果
- 精美视觉效果，统一卡片阴影效果，添加精致的边框线条

## 🛠️ 技术栈

### 前端技术
- **UI 框架**: Jetpack Compose
- **设计语言**: Material Design 3
- **编程语言**: Kotlin
- **最低 SDK**: Android 8.0 (API 26)
- **目标 SDK**: Android 14 (API 36)
- **编译 SDK**: Android 14 (API 36)
- **图表实现**: Compose Canvas 自定义绘制

### 后端技术
- **AI 服务**: OpenAI GPT API
- **网络请求**: Ktor Client 2.3.4
- **数据序列化**: Kotlinx Serialization 1.6.0
- **Markdown 渲染**: Markwon 4.6.2

### 数据存储
- **本地数据库**: Room Database 2.6.1
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
- Android SDK 26 或更高版本 (Android 8.0+)
- 支持设备: Android 8.0 及以上版本

### 构建步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/lukechern/wordcard.git
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
├── MainActivity.kt
├── api/                         # API 服务层
│   └── OpenAiApiService_7ree.kt
├── config/                      # 配置管理
│   ├── AppConfig_7ree.kt
│   └── PromptConfig_7ree.kt
├── data/                        # 数据层
│   ├── DataExportImportManager_7ree.kt
│   ├── WordDao_7ree.kt
│   ├── WordDatabase_7ree.kt
│   ├── WordEntity_7ree.kt
│   └── WordRepository_7ree.kt
├── query/                       # 查询逻辑
│   ├── WordQueryViewModel_7ree.kt
│   ├── manager/                 # 查询管理器
│   └── state/                   # 查询状态管理
├── test/                        # 测试工具
│   ├── DatabaseUpdateTester_7ree.kt
│   ├── MarkdownParserTester_7ree.kt
│   ├── SpeechApiTester_7ree.kt
│   └── TranslationApiTester_7ree.kt
├── tts/                         # 语音合成服务
│   ├── AzureTtsService_7ree.kt
│   └── TtsManager_7ree.kt
├── ui/                          # 用户界面
│   ├── BottomNavigationBar_7ree.kt
│   ├── DailyChartComponent_7ree.kt
│   ├── DashboardContent_7ree.kt
│   ├── DashboardScreen_7ree.kt
│   ├── EdgeSwipeNavigationComponent_7ree.kt
│   ├── HelpScreen_7ree.kt
│   ├── MainScreen_7ree.kt
│   ├── MonthlyChartComponent_7ree.kt
│   ├── NewStatCard_7ree.kt
│   ├── PaginatedWordList_7ree.kt
│   ├── ScrollIndicator_7ree.kt
│   ├── SpellingPracticeComponent_7ree.kt
│   ├── SplashScreen_7ree.kt
│   ├── SwipeArrowIndicator_7ree.kt
│   ├── SwipeNavigationComponent_7ree.kt
│   ├── SwipeableRevealItem_7ree.kt
│   ├── WordCardScreen_7ree.kt
│   ├── DashBoard/               # 仪表盘相关界面
│   │   ├── ApiConfigTab_7ree.kt
│   │   ├── ConfigPage_7ree.kt
│   │   ├── DataManagementTab_7ree.kt
│   │   ├── GeneralConfigTab_7ree.kt
│   │   ├── PromptConfigTab_7ree.kt
│   │   ├── SettingsTab_7ree.kt
│   │   ├── DataManagement/      # 数据管理界面
│   │   └── components/          # 仪表盘组件
│   ├── MainScreen/              # 主屏幕相关组件
│   │   ├── CustomToast_7ree.kt
│   │   ├── HistoryScreen_7ree.kt
│   │   ├── HistoryWordItem_7ree.kt
│   │   └── Screen_7ree.kt
│   ├── SpellingPractice/        # 拼写练习界面
│   │   ├── LetterInputBoxes_7ree.kt
│   │   ├── SpellingCard_7ree.kt
│   │   ├── SpellingPracticeContent_7ree.kt
│   │   ├── SpellingPracticeDialog_7ree.kt
│   │   ├── SpellingResultDisplay_7ree.kt
│   │   └── SpellingUtils_7ree.kt
│   ├── components/              # 通用UI组件
│   │   ├── InfoCards_7ree.kt
│   │   ├── LoadingComponent_7ree.kt
│   │   ├── SearchBarComponent_7ree.kt
│   │   ├── StatisticsComponent_7ree.kt
│   │   ├── TtsButtonStateManager_7ree.kt
│   │   ├── WordInputComponent_7ree.kt
│   │   └── WordResultComponent_7ree.kt
│   └── theme/                   # 主题相关
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── utils/                       # 工具类
│   ├── ApiKeySecureStorage_7ree.kt
│   ├── AppVersionUtils_7ree.kt
│   ├── CacheManager_7ree.kt
│   ├── DataStatistics_7ree.kt
│   ├── DatabaseMigrationHelper_7ree.kt
│   ├── HttpServerManager_7ree.kt
│   ├── KeyboardUtils_7ree.kt
│   ├── MarkdownParser_7ree.kt
│   ├── MarkdownRenderer_7ree.kt
│   ├── NetworkUtils_7ree.kt
│   ├── CustomKeyboard/          # 自定义键盘相关
│   └── httpServer/              # HTTP服务器相关
└── widget/                      # 桌面小组件
    ├── WidgetButtonManager_7ree.kt
    ├── WidgetCustomCursor_7ree.kt
    ├── WidgetInputValidator_7ree.kt
    ├── WidgetKeyboardManager_7ree.kt
    ├── WidgetLoadingActivity_7ree.kt
    ├── WidgetMarkdownParser_7ree.kt
    ├── WidgetOverlayManager_7ree.kt
    ├── WidgetPreloader_7ree.kt
    ├── WidgetResultButtonManager_7ree.kt
    ├── WidgetSearchManager_7ree.kt
    ├── WidgetTTSManager_7ree.kt
    ├── WidgetTouchFeedbackManager_7ree.kt
    ├── WidgetUIStateManager_7ree.kt
    ├── WidgetWindowManager_7ree.kt
    ├── WordQueryWidgetProvider_7ree.kt
    └── widgetconfigactivity_7ree.kt
```


## 🚀 使用指南

### 📖 快速入门（六步上手指南）

#### 第一步：安装配置，一步到位！⚙️
首先安装 APK 文件（这个你肯定已经搞定了，不然怎么看到这个页面呢？😄）。然后点击底部导航栏的【仪表盘】，再点击右上角的齿轮图标，进入配置页面。在这里配置你的大模型 API 参数——只要是兼容 OpenAI 格式的都可以哦！其他配置项保持默认就行，我们已经帮你调好了最佳参数。

#### 第二步：添加桌面小组件，随时随地查单词📱
在手机桌面长按空白处，选择【小组件】，找到 WordCard 并添加到桌面。以后想查单词时，直接点击小组件就能弹出查询卡片，超级方便！再也不用打开 APP 了～

#### 第三步：开始学习之旅！⭐
遇到生词？直接在小组件里输入就行！我们的 AI 大模型会瞬间为你解释单词含义，还会贴心地提供例句帮助理解。更棒的是，还有真人发音朗读功能，让你的发音也能更标准！

#### 第四步：单词本，你的专属词汇宝库📚
所有查过的单词都会自动保存到单词本里，再也不怕忘记了！在单词本里可以随时复习，点击单词右边的小喇叭🔊就能听发音，点击单词本身还能进入详情页查看更多信息。

#### 第五步：单词详情页，深度学习好帮手✅
在单词详情页里，你可以让 APP 朗读单词，收藏重要词汇，还能通过上下滑动手指来切换其他单词进行复习。更棒的是，现在还新增了拼写练习功能！点击拼写练习卡片，就能开始单词拼写训练，界面会以深绿色大标题显示中文词义（如果词义较多会智能显示前两个），让你在理解词义的基础上练习拼写。这样的学习体验，是不是很棒？

#### 第六步：AI情景文章，语境学习新体验📝
现在新增了AI情景文章生成功能！点击底部导航栏的【读文章】栏目，即可进入文章生成界面。系统会基于你单词本中的词汇，智能生成相关的英语情景文章，帮助你在语境中深入学习单词。你可以收藏喜欢的文章，方便日后复习。还支持文章搜索功能，快速找到你感兴趣的内容。通过阅读这些定制化的文章，你的词汇理解和应用能力将得到显著提升！

#### 第七步：仪表盘，见证你的成长📊
想看看自己的学习成果？进入仪表盘就能查看详细的学习数据和统计图表。看着那些不断增长的数字，成就感满满！📈

### 💡 小贴士
记住，学习语言最重要的是坚持！每天查几个生词，积少成多，你的词汇量会在不知不觉中突飞猛进。WordCard 会一直陪伴你的学习之路，加油！💪

### 🔧 基本操作
1. 启动应用后，在搜索框中输入要查询的英文单词
2. 点击"开始查"按钮，等待 AI 生成查询结果
3. 查看单词的详细信息，包括音标、定义、例句等
4. 点击喇叭图标收听单词或例句的发音
5. 点击收藏图标保存重要单词

### 🚀 高级功能
- **历史记录**: 在历史标签页查看所有查询过的单词
- **学习统计**: 在仪表盘查看学习进度和趋势图表
- **本周分析**: 可视化展示本周的单词收集和查阅统计
- **数据导出**: 在设置页面导出学习数据为 JSON 文件
- **数据导入**: 支持导入之前导出的学习数据
- **手势导航**: 在单词详情页面上下滑动切换单词

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
- [Markwon](https://noties.io/Markwon/) - Markdown 渲染库
- [Google Fonts](https://fonts.google.com/) - 提供现代化的 Google Sans Code 字体

## 📞 联系方式

- 项目主页: [https://github.com/lukechern/wordcard](https://github.com/lukechern/wordcard)
- 问题反馈: [Issues](https://github.com/lukechern/wordcard/issues)


---

<div align="center">

**如果这个项目对您有帮助，请给它一个 ⭐️**

---

<a name="english-version"></a>
# WordCard - AI Smart Word Card Application

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)
![OpenAI](https://img.shields.io/badge/OpenAI-412991?style=for-the-badge&logo=openai&logoColor=white)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)
[![Version](https://img.shields.io/badge/version-6.6.3-blue.svg?style=for-the-badge)](https://github.com/lukechern/wordcard)

</div>

<div align="center">
  <a href="#chinese-version">中文版本</a> •
  <a href="#english-version">English Version</a>
</div>

## 📱 Application Introduction

WordCard is an AI-powered smart word learning Android app that integrates OpenAI GPT or compatible models to provide users with intelligent English word query and learning experiences. The application uses the modern Jetpack Compose UI framework to provide a smooth user interface and rich functional features.

## ✨ Key Features

### 🤖 AI Smart Query
- Integration with OpenAI GPT and compatible models for intelligent word explanations
- Support for phonetics, parts of speech, Chinese definitions, English examples, and more
- Smart example sentence generation with Chinese translation
- Synonym and antonym recommendations
- Optimized Chinese meaning parsing to ensure complete output before parsing and display
- Keyword detection mechanism to determine streaming output completion status, improving parsing accuracy

### 🎵 Text-to-Speech
- Support for TTS voice reading of words and example sentences
- Support for English and Chinese voices
- Intelligent voice status management
- Added auto-read feature to automatically read word pronunciation upon word query completion or spelling practice

### 📚 Learning Management
- Word history management
- Favorites feature to mark important words
- Learning progress statistics and visualization charts
- Weekly learning trend analysis
- Data import/export functionality
- Support for data import/export operations via LAN on PC
- PC web interface supports both file import and manual JSON data pasting
- AI scenario article generation based on words in the word book to help deepen word learning in context
- Article favorites, search, and management functions for easy user management and review of generated articles

### ✏️ Spelling Practice
- Interactive word spelling practice feature
- Smart Chinese meaning hints (automatically displays the first two meanings)
- Real-time spelling correctness detection
- Success/failure animation feedback
- Auto-focus on input box to enhance practice experience
- Beautiful UI design focused on learning effectiveness
- Support for automatic reading of target words when opening spelling practice cards

### 🎨 Modern Interface
- Modern UI based on Jetpack Compose
- Material Design 3 design language
- Smooth animation effects
- Support for dark/light themes
- Optimized interface layout to improve information display efficiency
- Unified design language for consistent visual experience

### 📱 Interactive Experience
- Gesture navigation with support for swiping up and down to switch between words
- Smart touch area division to avoid accidental touches
- Responsive layout design
- Added edge swipe back feature to return to word book by swiping right from the left edge of the screen
- Smart back button optimization to maintain browsing position and filter status
- **Article reading position recording**: Automatically records scroll position before entering detail page from article list, precisely restoring to previous reading position when returning
- **Edge swipe back**: Supports swiping fingers from both sides of the screen to the center to return to article list page in article detail page, making operation more convenient and natural
- **Smart position recovery**: Supports scroll position saving and recovery in various scenarios such as pagination loading and search mode to ensure continuity of reading experience

### ⌨️ New Custom Keyboard
- Convenient and efficient key layout design for smoother and more natural operation
- Modern keyboard interface with beautiful and coordinated visual effects
- Streamlined key configuration to effectively reduce accidental touches and input errors
- Triple sensory feedback: visual animation + haptic vibration + sound effects
- Immersive interactive experience with rich feedback for each key press
- Flexible keyboard switching to freely choose between built-in app keyboard and system keyboard

### 🔍 Smart Search Function
- Real-time search experience with new smart search feature on word book page
- Seamless interface switching with smooth transition between title bar and search box
- Smart keyboard control with dynamic button at bottom right to control keyboard expansion and collapse
- Real-time search feedback with instant display of matching results as characters are entered, supporting fuzzy search

### 🔄 Pull-to-Refresh Function
- Smart pull-to-refresh to easily refresh word list data on word book page
- Smooth refresh animation with smooth pull-to-refresh animation effect
- Data synchronization optimization with automatic reloading of word count and pagination data during refresh
- Status retention with maintained favorite filter status during refresh process

### 🔐 Secure Storage
- API key encrypted storage using Android Keystore and AES encryption algorithm to protect sensitive information
- Secure migration mechanism to automatically detect and migrate plaintext stored keys from old versions to encrypted storage
- Integrity verification with built-in storage integrity verification mechanism to ensure reliability of key data

### 📊 Data Statistics Function
- All-new statistics card design with modern flat design style
- Smart number animation with 1-second animation effect from 0 to target value when loading number cards
- Beautiful visual effects with unified card shadow effects and delicate border lines

## 🛠️ Technology Stack

### Frontend Technology
- **UI Framework**: Jetpack Compose
- **Design Language**: Material Design 3
- **Programming Language**: Kotlin
- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 36)
- **Compile SDK**: Android 14 (API 36)
- **Chart Implementation**: Custom drawing with Compose Canvas

### Backend Technology
- **AI Service**: OpenAI GPT API
- **Network Request**: Ktor Client 2.3.4
- **Data Serialization**: Kotlinx Serialization 1.6.0
- **Markdown Rendering**: Markwon 4.6.2

### Data Storage
- **Local Database**: Room Database 2.6.1
- **Data Access**: Repository Pattern
- **Data Export**: JSON Format

### Architecture Pattern
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Manual Dependency Injection
- **State Management**: StateFlow + Compose State

## 📦 Installation Instructions

### Environment Requirements
- Android Studio Hedgehog | 2023.1.1 or higher
- JDK 11 or higher
- Android SDK 26 or higher (Android 8.0+)
- Supported devices: Android 8.0 and above

### Build Steps

1. **Clone Project**
   ```bash
   git clone https://github.com/lukechern/wordcard.git
   cd wordcard
   ```

2. **Configure API Key**
   - Configure your OpenAI API key in `app/src/main/java/com/x7ree/wordcard/config/AppConfig_7ree.kt`
   - Or configure through the app settings interface

3. **Build Project**
   ```bash
   ./gradlew build
   ```

4. **Install to Device**
   ```bash
   ./gradlew installDebug
   ```

## 🔧 Configuration Instructions

### OpenAI API Configuration
The application requires configuration of OpenAI API key to use AI query functionality:

1. Visit [OpenAI API](https://platform.openai.com/api-keys) to get an API key
2. Configure API key in app settings
3. Optionally configure custom API endpoint
4. If using another API provider, as long as it's compatible with OpenAI API interface, it's supported

### Prompt Configuration
Supports custom query prompts and output templates to get query results that better meet personal needs.

## 📁 Project Structure

```
app/src/main/java/com/x7ree/wordcard/
├── MainActivity.kt
├── api/                         # API service layer
│   └── OpenAiApiService_7ree.kt
├── config/                      # Configuration management
│   ├── AppConfig_7ree.kt
│   └── PromptConfig_7ree.kt
├── data/                        # Data layer
│   ├── DataExportImportManager_7ree.kt
│   ├── WordDao_7ree.kt
│   ├── WordDatabase_7ree.kt
│   ├── WordEntity_7ree.kt
│   └── WordRepository_7ree.kt
├── query/                       # Query logic
│   ├── WordQueryViewModel_7ree.kt
│   ├── manager/                 # Query manager
│   └── state/                   # Query state management
├── test/                        # Test tools
│   ├── DatabaseUpdateTester_7ree.kt
│   ├── MarkdownParserTester_7ree.kt
│   ├── SpeechApiTester_7ree.kt
│   └── TranslationApiTester_7ree.kt
├── tts/                         # Text-to-speech service
│   ├── AzureTtsService_7ree.kt
│   └── TtsManager_7ree.kt
├── ui/                          # User interface
│   ├── BottomNavigationBar_7ree.kt
│   ├── DailyChartComponent_7ree.kt
│   ├── DashboardContent_7ree.kt
│   ├── DashboardScreen_7ree.kt
│   ├── EdgeSwipeNavigationComponent_7ree.kt
│   ├── HelpScreen_7ree.kt
│   ├── MainScreen_7ree.kt
│   ├── MonthlyChartComponent_7ree.kt
│   ├── NewStatCard_7ree.kt
│   ├── PaginatedWordList_7ree.kt
│   ├── ScrollIndicator_7ree.kt
│   ├── SpellingPracticeComponent_7ree.kt
│   ├── SplashScreen_7ree.kt
│   ├── SwipeArrowIndicator_7ree.kt
│   ├── SwipeNavigationComponent_7ree.kt
│   ├── SwipeableRevealItem_7ree.kt
│   ├── WordCardScreen_7ree.kt
│   ├── DashBoard/               # Dashboard related interfaces
│   │   ├── ApiConfigTab_7ree.kt
│   │   ├── ConfigPage_7ree.kt
│   │   ├── DataManagementTab_7ree.kt
│   │   ├── GeneralConfigTab_7ree.kt
│   │   ├── PromptConfigTab_7ree.kt
│   │   ├── SettingsTab_7ree.kt
│   │   ├── DataManagement/      # Data management interface
│   │   └── components/          # Dashboard components
│   ├── MainScreen/              # Main screen related components
│   │   ├── CustomToast_7ree.kt
│   │   ├── HistoryScreen_7ree.kt
│   │   ├── HistoryWordItem_7ree.kt
│   │   └── Screen_7ree.kt
│   ├── SpellingPractice/        # Spelling practice interface
│   │   ├── LetterInputBoxes_7ree.kt
│   │   ├── SpellingCard_7ree.kt
│   │   ├── SpellingPracticeContent_7ree.kt
│   │   ├── SpellingPracticeDialog_7ree.kt
│   │   ├── SpellingResultDisplay_7ree.kt
│   │   └── SpellingUtils_7ree.kt
│   ├── components/              # General UI components
│   │   ├── InfoCards_7ree.kt
│   │   ├── LoadingComponent_7ree.kt
│   │   ├── SearchBarComponent_7ree.kt
│   │   ├── StatisticsComponent_7ree.kt
│   │   ├── TtsButtonStateManager_7ree.kt
│   │   ├── WordInputComponent_7ree.kt
│   │   └── WordResultComponent_7ree.kt
│   └── theme/                   # Theme related
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── utils/                       # Utility classes
│   ├── ApiKeySecureStorage_7ree.kt
│   ├── AppVersionUtils_7ree.kt
│   ├── CacheManager_7ree.kt
│   ├── DataStatistics_7ree.kt
│   ├── DatabaseMigrationHelper_7ree.kt
│   ├── HttpServerManager_7ree.kt
│   ├── KeyboardUtils_7ree.kt
│   ├── MarkdownParser_7ree.kt
│   ├── MarkdownRenderer_7ree.kt
│   ├── NetworkUtils_7ree.kt
│   ├── CustomKeyboard/          # Custom keyboard related
│   └── httpServer/              # HTTP server related
└── widget/                      # Desktop widgets
    ├── WidgetButtonManager_7ree.kt
    ├── WidgetCustomCursor_7ree.kt
    ├── WidgetInputValidator_7ree.kt
    ├── WidgetKeyboardManager_7ree.kt
    ├── WidgetLoadingActivity_7ree.kt
    ├── WidgetMarkdownParser_7ree.kt
    ├── WidgetOverlayManager_7ree.kt
    ├── WidgetPreloader_7ree.kt
    ├── WidgetResultButtonManager_7ree.kt
    ├── WidgetSearchManager_7ree.kt
    ├── WidgetTTSManager_7ree.kt
    ├── WidgetTouchFeedbackManager_7ree.kt
    ├── WidgetUIStateManager_7ree.kt
    ├── WidgetWindowManager_7ree.kt
    ├── WordQueryWidgetProvider_7ree.kt
    └── widgetconfigactivity_7ree.kt
```

## 🚀 User Guide

### 📖 Quick Start (Six-Step Guide)

#### Step 1: Installation and Configuration, All Set! ⚙️
First, install the APK file (you've definitely done this already, otherwise how would you be seeing this page? 😄). Then click on the [Dashboard] in the bottom navigation bar, and click the gear icon in the top right corner to enter the configuration page. Here you can configure your large model API parameters - any that are compatible with OpenAI format will work! Other configuration items can remain default, we've already tuned the best parameters for you.

#### Step 2: Add Desktop Widget, Check Words Anytime Anywhere 📱
Long press on a blank area of your phone's desktop, select [Widgets], find WordCard and add it to the desktop. In the future, when you want to check a word, just click the widget to pop up the query card, super convenient! No need to open the APP anymore~

#### Step 3: Start Your Learning Journey! ⭐
Encounter a new word? Just type it directly in the widget! Our AI large model will instantly explain the word meaning for you, and thoughtfully provide example sentences to help with understanding. Even better, there's also a real-person pronunciation reading function to make your pronunciation more standard!

#### Step 4: Word Book, Your Exclusive Vocabulary Treasure House 📚
All checked words will be automatically saved to the word book, no more worries about forgetting! In the word book, you can review anytime, click the small speaker 🔊 next to the word to hear the pronunciation, and click the word itself to enter the detail page to see more information.

#### Step 5: Word Detail Page, Deep Learning Helper ✅
On the word detail page, you can let the APP read the word, favorite important vocabulary, and switch to other words for review by swiping your fingers up and down. Even better, there's now a new spelling practice feature! Click the spelling practice card to start word spelling training, the interface will display the Chinese meaning in large dark green title (if there are multiple meanings, it will intelligently display the first two), letting you practice spelling based on understanding the meaning. Such a learning experience, isn't it great?

#### Step 6: AI Scenario Articles, Context Learning New Experience 📝
There's now a new AI scenario article generation feature! Click on the [Read Articles] column in the bottom navigation bar to enter the article generation interface. The system will intelligently generate relevant English scenario articles based on the vocabulary in your word book, helping you deeply learn words in context. You can favorite articles you like for future review. It also supports article search function to quickly find content you're interested in. Through reading these customized articles, your vocabulary understanding and application ability will be significantly improved!

#### Step 7: Dashboard, Witness Your Growth 📊
Want to see your learning results? Enter the dashboard to view detailed learning data and statistical charts. Watching those constantly growing numbers, a sense of achievement fills you up! 📈

### 💡 Tips
Remember, the most important thing in language learning is persistence! Check a few new words every day, accumulate little by little, and your vocabulary will soar unexpectedly. WordCard will always accompany your learning journey, keep going! 💪

### 🔧 Basic Operations
1. After launching the app, enter the English word to query in the search box
2. Click the "Start Query" button and wait for AI to generate query results
3. View detailed word information, including phonetics, definitions, example sentences, etc.
4. Click the speaker icon to listen to word or example sentence pronunciation
5. Click the favorite icon to save important words

### 🚀 Advanced Features
- **History**: View all queried words in the history tab
- **Learning Statistics**: View learning progress and trend charts in the dashboard
- **Weekly Analysis**: Visualize weekly word collection and viewing statistics
- **Data Export**: Export learning data as JSON file in settings page
- **Data Import**: Support importing previously exported learning data
- **Gesture Navigation**: Swipe up and down on word detail page to switch between words

### Development Guidelines
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add appropriate comments and documentation
- Ensure code passes all tests

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgements

- [OpenAI](https://openai.com/) - Providing powerful AI models
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI framework
- [Material Design](https://material.io/) - Design language guidance
- [Ktor](https://ktor.io/) - Network request library
- [Room](https://developer.android.com/training/data-storage/room) - Local database
- [Markwon](https://noties.io/Markwon/) - Markdown rendering library
- [Google Fonts](https://fonts.google.com/) - Providing modern Google Sans Code font

## 📞 Contact

- Project homepage: [https://github.com/lukechern/wordcard](https://github.com/lukechern/wordcard)
- Issue feedback: [Issues](https://github.com/lukechern/wordcard/issues)

---

<div align="center">

**If you find this project helpful, please give it a ⭐️**

</div>

</div>
