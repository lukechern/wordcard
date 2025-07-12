# 贡献指南

感谢您对 WordCard 项目的关注！我们欢迎所有形式的贡献，包括但不限于：

- 🐛 Bug 报告
- 💡 功能建议
- 📝 文档改进
- 🔧 代码贡献
- 🌟 项目推广

## 如何贡献

### 1. 报告 Bug

如果您发现了 Bug，请：

1. 检查 [Issues](https://github.com/lukechern/wordcard/issues) 是否已经有人报告过
2. 创建新的 Issue，并包含以下信息：
   - Bug 的详细描述
   - 重现步骤
   - 预期行为和实际行为
   - 设备信息（Android 版本、设备型号等）
   - 应用版本
   - 截图或录屏（如果适用）

### 2. 功能建议

如果您有功能建议，请：

1. 检查是否已经有类似的功能请求
2. 创建新的 Issue，并包含：
   - 功能描述
   - 使用场景
   - 预期效果
   - 实现思路（可选）

### 3. 代码贡献

如果您想贡献代码，请遵循以下步骤：

#### 环境准备

1. Fork 本仓库
2. 克隆您的 Fork 到本地：
   ```bash
   git clone https://github.com/lukechern/wordcard.git
   cd wordcard
   ```
3. 添加上游仓库：
   ```bash
   git remote add upstream https://github.com/original-owner/wordcard.git
   ```

#### 开发流程

1. 创建功能分支：
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. 进行开发，遵循编码规范：
   - 使用 Kotlin 编码规范
   - 变量和函数名使用有意义的名称
   - 添加适当的注释
   - 确保代码通过所有测试

3. 提交代码：
   ```bash
   git add .
   git commit -m "feat: add your feature description"
   ```

4. 推送到您的 Fork：
   ```bash
   git push origin feature/your-feature-name
   ```

5. 创建 Pull Request：
   - 访问您的 GitHub Fork 页面
   - 点击 "New Pull Request"
   - 选择您的功能分支
   - 填写 PR 描述，包含：
     - 功能描述
     - 解决的问题
     - 测试情况
     - 截图（如果适用）

## 编码规范

### Kotlin 规范

- 遵循 [Kotlin 官方编码规范](https://kotlinlang.org/docs/coding-conventions.html)
- 使用 4 空格缩进
- 行长度不超过 120 字符
- 使用有意义的变量和函数名

### 命名规范

- 类名：PascalCase（如 `WordCardScreen`）
- 函数和变量：camelCase（如 `wordInput`）
- 常量：UPPER_SNAKE_CASE（如 `MAX_RETRY_COUNT`）
- 包名：全小写（如 `com.x7ree.wordcard.ui`）

### 注释规范

- 公共 API 必须添加 KDoc 注释
- 复杂逻辑添加行内注释
- 使用中文注释，便于理解

### 测试规范

- 新功能必须包含单元测试
- UI 组件需要 UI 测试
- 确保测试覆盖率不低于 80%

## 提交信息规范

使用 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### 类型说明

- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

### 示例

```
feat: add swipe navigation between words

- Add SwipeNavigationComponent for gesture-based navigation
- Support up/down swipe to switch between words
- Add visual feedback for swipe gestures

Closes #123
```

## 审查流程

1. **自动检查**：PR 创建后会自动运行 CI/CD 检查
2. **代码审查**：维护者会审查代码质量和功能实现
3. **测试验证**：确保新功能不会破坏现有功能
4. **合并**：审查通过后合并到主分支

## 行为准则

- 尊重所有贡献者
- 保持专业和友善的交流
- 接受建设性的批评和建议
- 帮助其他贡献者

## 联系方式

如果您有任何问题或需要帮助，请：

- 在 [Issues](https://github.com/lukechern/wordcard/issues) 中提问
- 发送邮件到：your.email@example.com
- 加入我们的讨论群（如果有的话）

## 致谢

感谢所有为 WordCard 项目做出贡献的开发者！您的贡献让这个项目变得更好。

---

**再次感谢您的贡献！** 🌟 