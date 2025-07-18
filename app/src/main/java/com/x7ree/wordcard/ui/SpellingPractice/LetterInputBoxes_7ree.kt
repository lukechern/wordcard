package com.x7ree.wordcard.ui.SpellingPractice

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.utils.CustomKeyboard.CustomKeyboard_7ree
import com.x7ree.wordcard.utils.CustomKeyboard.CustomKeyboardState_7ree
import com.x7ree.wordcard.query.WordQueryViewModel_7ree

/**
 * 字母输入框组件
 * 用于拼写练习中的单词输入
 * 支持自定义键盘和系统键盘切换
 */
@Composable
fun LetterInputBoxes_7ree(
    targetWord: String,
    userInput: String,
    onInputChange: (String) -> Unit,
    focusRequester: FocusRequester,
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    textColor: Color = Color.Unspecified,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    // 获取通用配置
    val generalConfig_7ree by wordQueryViewModel_7ree.generalConfig_7ree.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    
    // 根据键盘类型决定是否显示自定义键盘
    val useCustomKeyboard = generalConfig_7ree.keyboardType == "custom"
    
    // 使用TextFieldValue来控制光标位置
    val textFieldValue = remember(userInput) {
        TextFieldValue(
            text = userInput,
            selection = TextRange(userInput.length) // 光标始终在最后
        )
    }
    
    // 焦点状态
    var isFocused by remember { mutableStateOf(false) }
    
    // 使用Box包装TextField和自定义光标
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        // 使用普通TextField，确保光标位置正确
        TextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            // 只有在非自定义键盘模式下才允许直接输入
            if (!useCustomKeyboard) {
                // 过滤输入，只允许英文字母
                val filteredText = newValue.text.filter { it.isLetter() && (it in 'a'..'z' || it in 'A'..'Z') }
                onInputChange(filteredText)
            }
        },
        readOnly = useCustomKeyboard, // 自定义键盘模式下设置为只读
        modifier = Modifier
             .fillMaxSize()
             .focusRequester(focusRequester)
             .onFocusChanged { focusState ->
                 isFocused = focusState.isFocused
                 onFocusChanged(focusState.isFocused)
                 if (focusState.isFocused && useCustomKeyboard) {
                     // 获得焦点时，如果使用自定义键盘，隐藏系统键盘
                     keyboardController?.hide()
                 }
             }
             .pointerInput(useCustomKeyboard) {
                 if (useCustomKeyboard) {
                     detectTapGestures(
                         onTap = {
                             // 禁用单击调出系统键盘，确保只显示自定义键盘
                         },
                         onDoubleTap = {
                             // 禁用双击调出系统键盘
                         }
                     )
                 }
             },
        placeholder = {
            Text(
                "点击这里开始拼写单词",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        textStyle = TextStyle(
              textAlign = TextAlign.Center,
              letterSpacing = 2.sp,
              fontSize = 27.sp,
              fontWeight = FontWeight.Black,
              color = if (textColor != Color.Unspecified) textColor else Color.Unspecified
          ),
        keyboardOptions = if (useCustomKeyboard) {
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.None
            )
        } else {
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        },
        keyboardActions = KeyboardActions(
            onDone = {
                // 完成输入时隐藏键盘
                focusManager.clearFocus()
            }
        ),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            cursorColor = Color.Transparent // 隐藏原生光标
        )
    )
    
    // 自定义光标组件
    if (useCustomKeyboard && isFocused) {
        CustomCursor_7ree(
            text = userInput,
            textStyle = TextStyle(
                fontSize = 27.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
 }
}

@Composable
fun CustomCursor_7ree(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    // 光标闪烁动画
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )
    
    // 计算文本宽度和光标位置
    val textWidth = with(density) {
        // 估算文本宽度，每个字符大约占用textStyle.fontSize * 0.6的宽度
        val charWidth = textStyle.fontSize.toPx() * 0.6f
        (text.length * charWidth).toDp()
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 光标线条
        Box(
            modifier = Modifier
                .offset(x = textWidth / 2) // 定位到文本末尾
                .width(2.dp)
                .height(textStyle.fontSize.value.dp * 1.2f)
                .alpha(alpha)
                .background(Color.Black)
        )
    }
}