# 双翻译API功能实现说明

## 功能概述
实现了仪表盘配置页面中的双翻译API功能，用户可以配置两个不同的AI大模型翻译API，并通过启用开关选择使用哪一个。

## 主要变更

### 1. 数据结构更新
- 新增 `TranslationApiConfig_7ree` 数据类，包含单个翻译API的配置信息
- 更新 `ApiConfig_7ree` 数据类，包含两个翻译API配置：
  - `translationApi1`: AI大模型翻译API(一)
  - `translationApi2`: AI大模型翻译API(二)
- 添加 `getActiveTranslationApi()` 方法获取当前启用的翻译API配置

### 2. 安全存储扩展
- 在 `ApiKeySecureStorage_7ree` 中添加了支持两个翻译API的存储方法
- 新增方法：
  - `storeTranslationApi1Config_7ree()` / `storeTranslationApi2Config_7ree()`
  - `getTranslationApi1Config_7ree()` / `getTranslationApi2Config_7ree()`
  - `storeNewApiConfig_7ree()` - 批量存储新的API配置结构

### 3. 配置管理更新
- 更新 `AppConfigManager_7ree` 的 `saveApiConfig_7ree()` 和 `loadApiConfig_7ree()` 方法
- 在 `ConfigManager_7ree` 中添加 `saveTranslationApiConfig_7ree()` 方法
- 支持从旧配置结构自动迁移到新结构

### 4. UI组件更新
- 创建新的 `DualTranslationApiSection_7ree` 组件，显示两个翻译API卡片
- 每个卡片包含：
  - 标题（"AI大模型翻译API(一)" / "AI大模型翻译API(二)"）
  - 启用开关
  - API URL、API Key、模型名称输入框
  - 测试按钮（仅在启用时显示）
- 更新 `ApiConfigTab_7ree` 使用新的双翻译API组件

### 5. API服务更新
- 更新 `OpenAiApiService_7ree` 中的所有方法使用 `getActiveTranslationApi()`
- 更新 `TranslationApiTester_7ree` 支持新的配置结构

### 6. ViewModel更新
- 在 `WordQueryViewModel_7ree` 中添加 `saveTranslationApiConfig_7ree()` 方法
- 通过 `ConfigManager_7ree` 处理双翻译API配置的保存

## 使用方法

### 用户操作流程
1. 进入仪表盘 -> 配置 -> API栏目
2. 看到两个翻译API卡片：
   - "AI大模型翻译API(一)"
   - "AI大模型翻译API(二)"
3. 每个卡片右侧有启用开关，用户可以选择启用哪一个
4. 配置相应的API URL、API Key、模型名称
5. 点击测试按钮验证配置
6. 系统会自动使用启用的API进行翻译

### 技术实现
- 当用户切换启用开关时，配置会自动保存
- 系统通过 `getActiveTranslationApi()` 获取当前启用的API配置
- 支持向后兼容，旧配置会自动迁移到新结构

## 向后兼容性
- 保留了旧的API配置字段（标记为 @Deprecated）
- 自动将旧配置迁移到 translationApi1
- 现有用户的配置不会丢失

## 安全性
- API Key 继续使用 Android Keystore 加密存储
- 非敏感信息（URL、模型名称、启用状态）使用普通 SharedPreferences 存储

## 测试
- 项目已通过编译测试
- 每个翻译API卡片都有独立的测试按钮
- 测试功能会验证当前启用的API配置