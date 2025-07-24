package com.x7ree.wordcard.utils.CustomKeyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

/**
 * 自定义键盘组件
 * 包含26个英文字母、退格删除按钮、查找按钮和收起按钮
 * 具有美观的3D效果
 */
@Composable
fun CustomKeyboard_7ree(
    onKeyPress_7ree: (String) -> Unit,
    onBackspace_7ree: () -> Unit,
    onSearch_7ree: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardRows_7ree = listOf(
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    )
    
    // 获取反馈管理器
    val feedbackManager_7ree = rememberKeyboardFeedbackManager_7ree()
    
    // 固定在底部的键盘容器
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(0.dp)
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { /* 拦截点击事件，防止失去焦点 */ },
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp), // 只保留上下边距，移除左右边距
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // 字母键盘行
            keyboardRows_7ree.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp), // 添加左右边距
                    horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally)
                ) {
                    row.forEach { key ->
                        KeyboardKey_7ree(
                            key = key,
                            onClick = { 
                                feedbackManager_7ree.triggerHapticFeedback_7ree()
                                feedbackManager_7ree.playClickSound_7ree()
                                onKeyPress_7ree(key) 
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(67.dp)
                        )
                    }
                }
            }            
            // 第三行：退格 + 字母键 + 回车
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp), // 添加左右边距
                horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally)
            ) {
                // 退格按钮
                BackspaceKey_7ree(
                    onClick = {
                        feedbackManager_7ree.triggerHapticFeedback_7ree()
                        feedbackManager_7ree.playClickSound_7ree()
                        onBackspace_7ree()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(67.dp)
                )
                
                // 第三行字母键
                listOf("z", "x", "c", "v", "b", "n", "m").forEach { key ->
                    KeyboardKey_7ree(
                        key = key,
                        onClick = { 
                            feedbackManager_7ree.triggerHapticFeedback_7ree()
                            feedbackManager_7ree.playClickSound_7ree()
                            onKeyPress_7ree(key) 
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(67.dp)
                    )
                }
                
                // 回车按钮
                EnterKey_7ree(
                    onClick = {
                        feedbackManager_7ree.triggerHapticFeedback_7ree()
                        feedbackManager_7ree.playClickSound_7ree()
                        onSearch_7ree()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(67.dp)
                )
         }
        }
        }
    }
}
