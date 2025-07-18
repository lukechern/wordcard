package com.x7ree.wordcard.ui

/**
 * 拼写练习组件入口文件
 * 该文件作为拼写练习功能的统一入口，重新导出所有相关组件
 * 
 * 组件结构：
 * - SpellingUtils_7ree: 工具函数和常量
 * - SpellingCard_7ree: 拼写卡片组件
 * - SpellingPracticeDialog_7ree: 拼写练习对话框
 * - SpellingPracticeContent_7ree: 拼写练习内容组件
 * - LetterInputBoxes_7ree: 字母输入框组件
 * - SpellingResultDisplay_7ree: 结果显示组件
 */

// 重新导出所有拆分后的组件，保持对外接口兼容性

// 导入所有拆分后的组件
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import com.x7ree.wordcard.query.WordQueryViewModel_7ree

// 重新导出工具函数和常量
// 从 SpellingUtils_7ree.kt 导入并重新导出
fun getFirstTwoMeanings_7ree(chineseMeaning: String): String {
    return com.x7ree.wordcard.ui.SpellingPractice.getFirstTwoMeanings_7ree(chineseMeaning)
}

// 重新导出拼写卡片组件
@Composable
fun SpellingCard_7ree(
    spellingCount: Int,
    onSpellingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    com.x7ree.wordcard.ui.SpellingPractice.SpellingCard_7ree(
        spellingCount = spellingCount,
        onSpellingClick = onSpellingClick,
        modifier = modifier
    )
}

// 重新导出拼写练习对话框组件
@Composable
fun SpellingPracticeDialog_7ree(
    targetWord: String,
    chineseMeaning: String,
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSpellingSuccess: () -> Unit
) {
    com.x7ree.wordcard.ui.SpellingPractice.SpellingPracticeDialog_7ree(
        targetWord = targetWord,
        chineseMeaning = chineseMeaning,
        wordQueryViewModel_7ree = wordQueryViewModel_7ree,
        isVisible = isVisible,
        onDismiss = onDismiss,
        onSpellingSuccess = onSpellingSuccess
    )
}

// 重新导出拼写练习内容组件
@Composable
fun SpellingPracticeContent_7ree(
    targetWord: String,
    chineseMeaning: String,
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onDismiss: () -> Unit,
    onSpellingSuccess: () -> Unit
) {
    com.x7ree.wordcard.ui.SpellingPractice.SpellingPracticeContent_7ree(
        targetWord = targetWord,
        chineseMeaning = chineseMeaning,
        wordQueryViewModel_7ree = wordQueryViewModel_7ree,
        onDismiss = onDismiss,
        onSpellingSuccess = onSpellingSuccess
    )
}

// 重新导出字母输入框组件
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
    com.x7ree.wordcard.ui.SpellingPractice.LetterInputBoxes_7ree(
        targetWord = targetWord,
        userInput = userInput,
        onInputChange = onInputChange,
        focusRequester = focusRequester,
        wordQueryViewModel_7ree = wordQueryViewModel_7ree,
        textColor = textColor,
        onFocusChanged = onFocusChanged
    )
}

// 重新导出结果显示组件
@Composable
fun SpellingResultDisplay_7ree(
    showResult: Boolean,
    isCorrect: Boolean
) {
    com.x7ree.wordcard.ui.SpellingPractice.SpellingResultDisplay_7ree(
        showResult = showResult,
        isCorrect = isCorrect
    )
}

/*
 * 使用说明：
 * 现在可以直接从此文件导入所有组件，保持了原有的使用方式：
 * import com.x7ree.wordcard.ui.SpellingPracticeComponent_7ree.*
 * 
 * 或者单独导入需要的组件（从SpellingPractice子目录）：
 * import com.x7ree.wordcard.ui.SpellingPractice.SpellingCard_7ree
 * import com.x7ree.wordcard.ui.SpellingPractice.SpellingPracticeDialog_7ree
 * import com.x7ree.wordcard.ui.SpellingPractice.SpellingUtils_7ree
 * 
 * 拆分后的文件已组织到SpellingPractice子目录下，结构更清晰，便于维护和升级。
 */