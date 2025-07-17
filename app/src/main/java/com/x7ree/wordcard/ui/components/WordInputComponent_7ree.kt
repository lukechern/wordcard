package com.x7ree.wordcard.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.R
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.utils.CacheManager_7ree
import com.x7ree.wordcard.utils.DataStatistics_7ree
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

// 输入界面组件
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordInputComponent_7ree(
    wordQueryViewModel: WordQueryViewModel_7ree,
    showInputWarning: Boolean,
    onInputWarningChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.offset(y = (-0.15f * 100 + 50).dp) // 调整偏移量
    ) {
        // App图标
        Image(
            painter = painterResource(id = R.drawable.wordcardicon),
            contentDescription = "App图标",
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center,
            modifier = Modifier
                .size(90.dp)
                .padding(bottom = 16.dp)
        )
        
        // 标题
        Text(
            text = "AI查单词",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // 输入框 - 统一设计风格，限制只能输入英文字母
        OutlinedTextField(
            value = wordQueryViewModel.wordInput_7ree,
            onValueChange = { newValue ->
                // 检查是否包含非英文字符
                val hasInvalidChars = newValue.any { !it.isLetter() || (it !in 'a'..'z' && it !in 'A'..'Z') }
                if (hasInvalidChars) {
                    onInputWarningChange(true)
                }
                
                // 过滤输入，只允许英文字母
                val filteredValue = newValue.filter { it.isLetter() && (it in 'a'..'z' || it in 'A'..'Z') }
                wordQueryViewModel.onWordInputChanged_7ree(filteredValue)
            },
            label = { Text("请输入英文单词") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(64.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    // 点击回车时直接提交查询
                    if (wordQueryViewModel.wordInput_7ree.length >= 3) {
                        wordQueryViewModel.queryWord_7ree()
                     }
                 }
            )
        )
        
        // 输入提示条
        if (showInputWarning) {
            Text(
                text = "⚠️ 只能输入英文字母",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 查询按钮 - 统一设计风格，添加放大镜图标
        Button(
            onClick = { wordQueryViewModel.queryWord_7ree() },
            enabled = !wordQueryViewModel.isLoading_7ree && wordQueryViewModel.wordInput_7ree.length >= 3,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 2.dp,
                disabledElevation = 0.dp
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "查询",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "开始查",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // 统计数据组件
        StatisticsComponent_7ree(
            wordQueryViewModel = wordQueryViewModel,
            modifier = Modifier.padding(top = 112.dp)
        )
    }
}

// 统计数据组件
@Composable
fun StatisticsComponent_7ree(
    wordQueryViewModel: WordQueryViewModel_7ree,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cacheManager_7ree = remember { CacheManager_7ree(context) }
    var cachedStats_7ree by remember { mutableStateOf(DataStatistics_7ree.StatisticsData_7ree(0, 0, 0, 0, 0.0f, 0.0f, 0, 0.0f, 0.0f)) }
    
    // 加载缓存的统计数据
    LaunchedEffect(Unit) {
        // 延迟加载统计数据，不阻塞UI显示
        delay(100) // 短暂延迟，让UI先渲染
        
        // 检查是否需要更新缓存
        if (cacheManager_7ree.shouldUpdateCache_7ree()) {
            // 需要更新缓存时，获取最新数据
            val allWords_7ree = wordQueryViewModel.getHistoryWords_7ree().first()
            cachedStats_7ree = DataStatistics_7ree.calculateStatistics_7ree(allWords_7ree)
            cacheManager_7ree.updateCacheTimestamp_7ree()
        } else {
            // 使用缓存数据，快速获取一次性数据
            val allWords_7ree = wordQueryViewModel.getHistoryWords_7ree().first()
            cachedStats_7ree = DataStatistics_7ree.calculateStatistics_7ree(allWords_7ree)
        }
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "已收集${cachedStats_7ree.totalWords}个单词",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp)) // 两个统计数据之间的间距
        Text(
            text = "已累计查阅${cachedStats_7ree.totalViews}次",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp)) // 两个统计数据之间的间距
        Text(
            text = "已持续学习${cachedStats_7ree.studyDays}天",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp)) // 在统计数据和底部之间增加一些间距
    }
}