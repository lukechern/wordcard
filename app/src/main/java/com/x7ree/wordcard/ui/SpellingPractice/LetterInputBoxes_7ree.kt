package com.x7ree.wordcard.ui.SpellingPractice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 字母输入框组件
 * 用于拼写练习中的单词输入
 */
@Composable
fun LetterInputBoxes_7ree(
    targetWord: String,
    userInput: String,
    onInputChange: (String) -> Unit,
    focusRequester: FocusRequester,
    textColor: Color = Color.Unspecified
) {
    // 使用普通TextField替代BasicTextField，避免BringIntoViewRequester问题
    TextField(
        value = userInput,
        onValueChange = onInputChange,
        modifier = Modifier
             .fillMaxWidth()
             .height(80.dp)
             .focusRequester(focusRequester),
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
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    )
}