# MainScreen 模块重构说明

## 概述
原本的 `MainScreen_7ree.kt` 文件过于复杂，包含了多个组件和功能。为了提高代码的可维护性和可读性，我们将其拆分为多个独立的文件。

## 文件结构

### 1. Screen_7ree.kt
- 定义了屏幕枚举类型 `Screen_7ree`
- 包含 SEARCH、HISTORY、SETTINGS 三个屏幕状态

### 2. CustomToast_7ree.kt
- 自定义提示条组件
- 包含动画效果和自动隐藏功能
- 独立的UI组件，可复用

### 3. HistoryWordItem_7ree.kt
- 历史单词列表项组件
- 包含单词信息显示、收藏状态、统计信息等
- 支持滑动删除和朗读功能

### 4. HistoryScreen_7ree.kt
- 历史记录屏幕的完整实现
- 包含分页加载、下拉刷新、收藏过滤等功能
- 管理滚动位置状态

### 5. MainScreen_7ree.kt (重构后)
- 主屏幕组件的核心实现
- 管理屏幕切换和启动画面
- 集成所有子组件

### 6. MainScreen_7ree.kt (原文件，现为兼容层)
- 保持向后兼容性
- 重新导出所有组件
- 作为其他代码的接口层

## 优势

1. **模块化**: 每个文件职责单一，便于维护
2. **可复用**: 组件可以在其他地方独立使用
3. **可测试**: 每个组件可以独立测试
4. **向后兼容**: 原有的导入和使用方式不变
5. **易于扩展**: 新功能可以添加到对应的文件中

## 使用方式

外部代码无需修改，仍然可以通过以下方式使用：

```kotlin
import com.x7ree.wordcard.ui.MainScreen_7ree
import com.x7ree.wordcard.ui.Screen_7ree
import com.x7ree.wordcard.ui.CustomToast_7ree
// 等等...
```

所有组件的API保持不变，确保现有代码的兼容性。