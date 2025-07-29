package com.x7ree.wordcard.article.utils

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 搜索模式UI
 */
@Composable
fun SearchModeUI_7ree(
    textFieldValue: TextFieldValue,
    onTextFieldValueChange: (TextFieldValue) -> Unit,
    focusRequester: FocusRequester,
    isFocused: Boolean,
    onFocusedChange: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchModeToggle: (Boolean) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { newTextFieldValue ->
                // 更新TextFieldValue，保持光标在末尾
                val updatedTextFieldValue = TextFieldValue(
                    text = newTextFieldValue.text,
                    selection = TextRange(newTextFieldValue.text.length)
                )
                onTextFieldValueChange(updatedTextFieldValue)

                println("DEBUG: ArticleSearchBarComponent onValueChange: '${newTextFieldValue.text}'")

                // 同步更新外部状态
                onSearchQueryChange(newTextFieldValue.text)
            },
            placeholder = { Text("搜索文章标题或关键词...") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // 恢复固定高度，确保与标题行高度一致
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    onFocusedChange(focusState.isFocused)
                }
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusRequester.requestFocus()
                    })
                },
            shape = RoundedCornerShape(16.dp), // 与单词本搜索框保持一致的圆角
            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                lineHeight = 20.sp // 适中的行高，确保文字完全显示且垂直居中
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            trailingIcon = {
                // 关闭搜索按钮放在搜索框内部右侧
                IconButton(
                    onClick = {
                        onSearchModeToggle(false)
                        onSearchQueryChange("")
                        keyboardController?.hide()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "关闭搜索",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                }
            )
        )
    }
}
