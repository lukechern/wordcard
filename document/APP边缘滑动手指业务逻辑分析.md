# 应用边缘滑动业务逻辑分析

## 1. 概述

本应用实现了两种不同的边缘滑动手势处理逻辑：
1. 应用级别的双击返回退出机制
2. 页面级别的边缘滑动导航机制（在单词详情页面和文章详情页面有不同的实现）

## 2. 应用级别退出逻辑

### 2.1 实现位置
- `utils/BackPressHandler_7ree.kt`

### 2.2 实现原理
应用通过`BackPressHandler_7ree`类处理返回键事件，实现双击返回退出应用的功能：

1. **第一次按返回键**：
   - 记录当前时间
   - 显示"再按一次退出应用"的Toast提示
   - 不退出应用

2. **第二次按返回键**：
   - 检查与上次按返回键的时间间隔
   - 如果在2秒内，则调用系统返回处理，退出应用
   - 如果超过2秒，则重新开始第一次按返回键的流程

### 2.3 核心代码逻辑
```kotlin
private fun handleBackPress() {
    // 检查是否从单词本进入单词详情页面
    val isFromWordBook_7ree = wordQueryViewModel_7ree?.isFromWordBook_7ree?.value ?: false
    val currentScreen_7ree = wordQueryViewModel_7ree?.currentScreen_7ree?.value ?: "SEARCH"
    
    if (isFromWordBook_7ree && currentScreen_7ree == "SEARCH") {
        // 如果是从单词本进入的单词详情页面，直接返回单词本
        wordQueryViewModel_7ree?.returnToWordBook_7ree()
        return
    }
    
    val currentTime_7ree = System.currentTimeMillis()
    
    if (currentTime_7ree - backPressedTime_7ree > 2000) {
        // 第一次按返回键
        backPressedTime_7ree = currentTime_7ree
        exitToast_7ree?.cancel() // 取消之前的Toast
        exitToast_7ree = Toast.makeText(dispatcherOwner as android.content.Context, exitMessage_7ree, Toast.LENGTH_SHORT)
        exitToast_7ree?.show()
    } else {
        // 第二次按返回键，退出应用
        exitToast_7ree?.cancel()
        // 直接调用系统的返回处理
        onBackPressedCallback.isEnabled = false
        dispatcherOwner.onBackPressedDispatcher.onBackPressed()
    }
}
```

## 3. 单词详情页面边缘滑动逻辑

### 3.1 实现位置
- `ui/EdgeSwipeNavigationComponent_7ree.kt`
- `ui/WordCardScreen_7ree.kt`

### 3.2 实现原理
单词详情页面通过`EdgeSwipeNavigationComponent_7ree`组件实现边缘滑动返回单词本功能：

1. **触发条件**：
   - 只有当`isFromWordBook`为true时（即从单词本进入单词详情页面）才启用边缘滑动
   - 滑动必须从屏幕左右边缘开始（50dp范围内）
   - 滑动距离需超过100dp阈值

2. **滑动检测**：
   - 检测从屏幕边缘开始的拖拽手势
   - 计算滑动方向和距离
   - 判断是否为有效的返回滑动

### 3.3 核心代码逻辑
```kotlin
// EdgeSwipeNavigationComponent_7ree.kt
detectDragGestures(
    onDragStart = { offset ->
        val startX = offset.x
        // 检查是否从屏幕边缘开始滑动
        if (startX <= edgeThreshold || startX >= screenWidthPx - edgeThreshold) {
            isDragging_7ree = true
            dragStartX_7ree = startX
            dragCurrentX_7ree = startX
        }
    },
    onDragEnd = {
        if (isDragging_7ree) {
            val dragDistance = abs(dragCurrentX_7ree - dragStartX_7ree)
            val isFromLeftEdge = dragStartX_7ree <= edgeThreshold
            val isFromRightEdge = dragStartX_7ree >= screenWidthPx - edgeThreshold
            
            // 检查滑动方向和距离
            val isValidSwipe = if (isFromLeftEdge) {
                // 从左边缘向右滑动
                dragCurrentX_7ree > dragStartX_7ree && dragDistance >= swipeThreshold
            } else if (isFromRightEdge) {
                // 从右边缘向左滑动
                dragCurrentX_7ree < dragStartX_7ree && dragDistance >= swipeThreshold
            } else {
                false
            }
            
            if (isValidSwipe) {
                onReturnToWordBook()
            }
            
            isDragging_7ree = false
            dragStartX_7ree = 0f
            dragCurrentX_7ree = 0f
        }
    }
) { _, dragAmount ->
    if (isDragging_7ree) {
        dragCurrentX_7ree += dragAmount.x
    }
}
```

### 3.4 使用方式
在`WordCardScreen_7ree.kt`中使用该组件包装内容：
```kotlin
EdgeSwipeNavigationComponent_7ree(
    isFromWordBook = isFromWordBook_7ree,
    onReturnToWordBook = {
        wordQueryViewModel_7ree.returnToWordBook_7ree()
    }
) {
    // 内容区域
}
```

## 4. 文章详情页面边缘滑动逻辑

### 4.1 实现位置
- `article/utils/ArticleEdgeSwipeComponent_7ree.kt`
- `ui/ArticleDetailScreen_7ree.kt`

### 4.2 实现原理
文章详情页面通过`ArticleEdgeSwipeComponent_7ree`组件实现边缘滑动返回功能，与单词详情页面的实现有所不同：

1. **触发条件**：
   - 在屏幕左右边缘各设置24dp的检测区域
   - 滑动距离需超过80dp阈值
   - 特别优化以避免与Android系统手势冲突

2. **滑动检测**：
   - 分别处理左边缘和右边缘的滑动手势
   - 左边缘只响应从左向右的滑动
   - 右边缘只响应从右向左的滑动

### 4.3 核心代码逻辑
```kotlin
// ArticleEdgeSwipeComponent_7ree.kt
// 左边缘滑动检测
detectDragGestures(
    onDragStart = { offset ->
        startX = offset.x
        startY = offset.y
        currentX = offset.x
        currentY = offset.y
        dragCount = 0
        // 只有在边缘区域开始的滑动才有效
        isValidSwipe = startX <= edgeWidth
    },
    onDragEnd = {
        val totalDistanceX = currentX - startX
        val totalDistanceY = currentY - startY
        val shouldTrigger = isValidSwipe && totalDistanceX >= swipeThreshold
        
        if (shouldTrigger) {
            onBackNavigation()
        }
        isValidSwipe = false
    }
) { change, dragAmount ->
    dragCount++
    currentX += dragAmount.x
    currentY += dragAmount.y
    
    if (isValidSwipe) {
        // 确保只处理向右滑动
        if (dragAmount.x < 0) {
            isValidSwipe = false
        }
    }
}
```

### 4.4 使用方式
在`ArticleDetailScreen_7ree.kt`中使用该组件：
```kotlin
ArticleEdgeSwipeComponent_7ree(
    onBackNavigation = onBackClick
)
```

## 5. 两种边缘滑动逻辑的区别

| 特性 | 应用级别退出 | 单词详情页面 | 文章详情页面 |
|------|-------------|-------------|-------------|
| 触发条件 | 双击返回键 | 从单词本进入且边缘滑动 | 边缘滑动 |
| 返回行为 | 退出应用 | 返回单词本 | 返回文章列表 |
| 滑动阈值 | 无 | 100dp | 80dp |
| 边缘检测区域 | 无限制 | 50dp | 24dp |
| 冲突处理 | 无 | 无 | 优化避免系统手势冲突 |

## 6. 如何规避应用级别退出逻辑

在单词详情页面中，通过以下方式规避了应用级别的双击返回退出逻辑：

1. **状态判断**：
   - 检查`isFromWordBook`状态是否为true
   - 检查当前屏幕是否为"SEARCH"

2. **优先处理**：
   - 如果是从单词本进入的单词详情页面，直接调用`returnToWordBook_7ree()`方法
   - 不会进入双击返回的逻辑判断

```kotlin
if (isFromWordBook_7ree && currentScreen_7ree == "SEARCH") {
    // 如果是从单词本进入的单词详情页面，直接返回单词本
    wordQueryViewModel_7ree?.returnToWordBook_7ree()
    return
}
```

这种方式确保了从单词本进入的单词详情页面不会触发应用退出逻辑，而是返回到单词本页面。


## 新版已经为文章详情页添加了类似的边缘滑动返回逻辑，具体实现方式与单词详情页类似，以免与APP系统默认的退出业务逻辑冲突。