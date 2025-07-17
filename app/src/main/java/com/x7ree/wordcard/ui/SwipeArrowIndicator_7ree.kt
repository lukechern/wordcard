package com.x7ree.wordcard.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.R

/**
 * 滑动箭头指示器组件
 * 专门用于显示滑动反馈箭头，箭头显示在屏幕中心
 */
@Composable
fun SwipeArrowIndicator_7ree(
    canNavigate: Boolean,
    modifier: Modifier = Modifier
) {
    // 从SwipeNavigationComponent_7ree获取滑动状态
    val swipeState = LocalSwipeState_7ree.current
    
    if (canNavigate && swipeState.showSwipeFeedback) {
        Box(
            modifier = modifier
        ) {
            // 根据滑动方向显示对应的箭头
            when (swipeState.swipeDirection) {
                SwipeDirection_7ree.UP -> {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_up_7ree),
                        contentDescription = "向上滑动",
                        modifier = Modifier
                            .size(220.dp) // 减少100dp：320dp - 100dp = 220dp
                            .alpha(0.5f) // 50%透明度
                            .align(Alignment.TopCenter)
                            .padding(top = 40.dp) // 增加距离上边缘的位置
                    )
                }
                SwipeDirection_7ree.DOWN -> {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_down_7ree),
                        contentDescription = "向下滑动",
                        modifier = Modifier
                            .size(220.dp) // 减少100dp：320dp - 100dp = 220dp
                            .alpha(0.5f) // 50%透明度
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 40.dp) // 增加距离下边缘的位置
                    )
                }
                SwipeDirection_7ree.NONE -> {
                    // 不显示箭头
                }
            }
        }
    }
}

// 滑动方向枚举
enum class SwipeDirection_7ree {
    UP, DOWN, NONE
}

// 滑动状态数据类
data class SwipeState_7ree(
    val showSwipeFeedback: Boolean = false,
    val swipeDirection: SwipeDirection_7ree = SwipeDirection_7ree.NONE,
    val totalDragDistance: Float = 0f
)

// 提供滑动状态的CompositionLocal
val LocalSwipeState_7ree = compositionLocalOf { SwipeState_7ree() }