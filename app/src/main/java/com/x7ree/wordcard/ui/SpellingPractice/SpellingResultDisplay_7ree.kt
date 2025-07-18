package com.x7ree.wordcard.ui.SpellingPractice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 拼写结果显示组件
 * 用于显示拼写练习的结果（正确或错误）
 */
@Composable
fun SpellingResultDisplay_7ree(
    showResult: Boolean,
    isCorrect: Boolean
) {
    AnimatedVisibility(
        visible = showResult,
        enter = scaleIn(animationSpec = tween(300)) + fadeIn(),
        exit = scaleOut(animationSpec = tween(300)) + fadeOut()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isCorrect) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "正确",
                    modifier = Modifier.size(64.dp),
                    tint = SpellingColors_7ree.SUCCESS_COLOR
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "恭喜，拼写正确！",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = SpellingColors_7ree.SUCCESS_COLOR
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "抱歉，拼写错误！",
                    modifier = Modifier.size(64.dp),
                    tint = SpellingColors_7ree.ERROR_COLOR
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "错误，请重新拼写！",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = SpellingColors_7ree.ERROR_COLOR
                )
            }
        }
    }
}