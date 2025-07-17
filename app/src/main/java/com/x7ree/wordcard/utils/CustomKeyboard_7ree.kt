package com.x7ree.wordcard.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.remember
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp as Dp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.group
import androidx.compose.foundation.layout.size

// 自定义回车图标
val CustomDeleteIcon: ImageVector
    get() {
        if (_customDeleteIcon != null) {
            return _customDeleteIcon!!
        }
        _customDeleteIcon = ImageVector.Builder(
            name = "CustomDelete",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 1024f,
            viewportHeight = 1024f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFC5C5C5)),
                stroke = null
            ) {
                moveTo(886.30272f, 949.4528f)
                lineTo(511.90784f, 575.05792f)
                lineTo(137.5744f, 949.39136f)
                arcToRelative(44.63616f, 44.63616f, 0f, false, true, -63.15008f, 0.06144f)
                curveToRelative(-17.37728f, -17.37728f, -17.28512f, -45.69088f, 0.1024f, -63.10912f)
                lineToRelative(374.36416f, -374.33344f)
                lineToRelative(-374.3744f, -374.36416f)
                arcToRelative(44.71808f, 44.71808f, 0f, false, true, 0f, -63.13984f)
                arcToRelative(44.63616f, 44.63616f, 0f, false, true, 63.15008f, 0.03072f)
                lineTo(512f, 448.8704f)
                lineTo(886.33344f, 74.53696f)
                arcToRelative(44.58496f, 44.58496f, 0f, true, true, 63.04768f, 63.04768f)
                lineTo(575.04768f, 511.91808f)
                lineTo(949.4528f, 886.31296f)
                arcToRelative(44.56448f, 44.56448f, 0f, false, true, 0.03072f, 63.10912f)
                arcToRelative(44.58496f, 44.58496f, 0f, false, true, -63.1808f, 0.03072f)
                close()
            }
        }.build()
        return _customDeleteIcon!!
    }

val CustomEnterIcon: ImageVector
    get() {
        if (_customEnterIcon != null) {
            return _customEnterIcon!!
        }
        _customEnterIcon = ImageVector.Builder(
            name = "CustomEnter",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 1109f,
            viewportHeight = 1024f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF666666)),
                stroke = null
            ) {
                moveTo(1040.040933f, 0.001707f)
                horizontalLineToRelative(-114.858475f)
                arcToRelative(15.189308f, 15.189308f, 0f, false, false, -15.359974f, 14.933308f)
                verticalLineTo(674.133916f)
                horizontalLineTo(302.762162f)
                verticalLineTo(538.198143f)
                curveToRelative(-0.085333f, -5.802657f, -3.498661f, -11.093315f, -8.789319f, -13.653311f)
                arcToRelative(15.615974f, 15.615974f, 0f, false, false, -16.213306f, 1.877331f)
                lineTo(5.88799f, 735.232481f)
                arcToRelative(14.762642f, 14.762642f, 0f, false, false, 0f, 23.466628f)
                lineToRelative(271.95688f, 208.810318f)
                curveToRelative(4.522659f, 3.583994f, 10.837315f, 4.26666f, 16.127973f, 1.877331f)
                arcToRelative(15.189308f, 15.189308f, 0f, false, false, 8.789319f, -13.567978f)
                verticalLineTo(815.787014f)
                horizontalLineToRelative(630.100283f)
                curveToRelative(67.583887f, 0f, 122.538462f, -53.503911f, 122.538463f, -119.295801f)
                verticalLineTo(14.935015f)
                arcToRelative(15.018642f, 15.018642f, 0f, false, false, -4.522659f, -10.666649f)
                arcToRelative(15.359974f, 15.359974f, 0f, false, false, -10.837316f, -4.266659f)
                close()
            }
        }.build()
        return _customEnterIcon!!
    }

private var _customDeleteIcon: ImageVector? = null
private var _customEnterIcon: ImageVector? = null

/**
 * 自定义键盘组件
 * 包含26个英文字母、退格删除按钮和查找按钮
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
    
    // 固定在底部的键盘容器
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { /* 拦截点击事件，防止失去焦点 */ },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // 字母键盘行
            keyboardRows_7ree.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally)
                ) {
                    row.forEach { key ->
                        KeyboardKey_7ree(
                            key = key,
                            onClick = { onKeyPress_7ree(key) },
                            modifier = Modifier
                                .weight(1f)
                                .height(67.dp)
                        )
                    }
                }
            }            
            // 第三行：退格 + 字母键 + 回车
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally)
            ) {
                // 退格按钮
                BackspaceKey_7ree(
                    onClick = onBackspace_7ree,
                    modifier = Modifier
                        .weight(1f)
                        .height(67.dp)
                )
                
                // 第三行字母键
                listOf("z", "x", "c", "v", "b", "n", "m").forEach { key ->
                    KeyboardKey_7ree(
                        key = key,
                        onClick = { onKeyPress_7ree(key) },
                        modifier = Modifier
                            .weight(1f)
                            .height(67.dp)
                    )
                }
                
                // 回车按钮
                EnterKey_7ree(
                    onClick = onSearch_7ree,
                    modifier = Modifier
                        .weight(1f)
                        .height(67.dp)
                )
            }
        }
        }
    }
}

/**
 * 普通字母按键组件
 */
@Composable
private fun KeyboardKey_7ree(
    key: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.7f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "key_scale"
    )
    
    Box(
        modifier = modifier
            .padding(2.dp)
            .scale(scale)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(4.dp)
            )
            .clip(RoundedCornerShape(4.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = key,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
/**
 * 退格按键组件（暖色调）
 */
@Composable
private fun BackspaceKey_7ree(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.7f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "backspace_key_scale"
    )
    
    Box(
        modifier = modifier
            .padding(2.dp)
            .scale(scale)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(4.dp)
            )
            .clip(RoundedCornerShape(4.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4A2832), // 深红棕色
                        Color(0xFF4A2832).copy(alpha = 0.8f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = CustomDeleteIcon,
            contentDescription = "退格",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * 回车按键组件
 */
@Composable
private fun EnterKey_7ree(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.7f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "enter_key_scale"
    )
    
    Box(
        modifier = modifier
            .padding(2.dp)
            .scale(scale)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(4.dp)
            )
            .clip(RoundedCornerShape(4.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = CustomEnterIcon,
            contentDescription = "回车",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * 键盘显示状态管理
 */
@Composable
fun rememberCustomKeyboardState_7ree(): CustomKeyboardState_7ree {
    return remember { CustomKeyboardState_7ree() }
}

/**
 * 自定义键盘状态类
 */
class CustomKeyboardState_7ree {
    private val _isVisible_7ree = mutableStateOf(false)
    val isVisible_7ree: State<Boolean> = _isVisible_7ree
    
    fun show_7ree() {
        _isVisible_7ree.value = true
    }
    
    fun hide_7ree() {
        _isVisible_7ree.value = false
    }
    
    fun toggle_7ree() {
        _isVisible_7ree.value = !_isVisible_7ree.value
    }
}