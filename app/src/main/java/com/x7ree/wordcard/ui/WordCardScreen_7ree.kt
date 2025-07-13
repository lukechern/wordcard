package com.x7ree.wordcard.ui

import android.text.Html
import android.widget.TextView
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import io.noties.markwon.Markwon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.PlayArrow // Import PlayArrow for play icon
import androidx.compose.material.icons.filled.Pause // Import Pause for pause icon
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.graphics.graphicsLayer
import android.util.Log
import java.util.regex.Pattern // Import Pattern for regex
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.collectAsState // 导入 collectAsState
import com.x7ree.wordcard.ui.SwipeNavigationComponent_7ree // 导入滑动导航组件


// 辅助函数：应用文本样式调整
private fun applyTextStyleAdjustments_7ree(textView: TextView) {
    val spannable_7ree = textView.text as android.text.Spannable
    // 处理粗体样式，将其改为下划线样式
    val boldSpans_7ree = spannable_7ree.getSpans(0, spannable_7ree.length, android.text.style.StyleSpan::class.java)
    boldSpans_7ree.forEach { span_7ree ->
        if (span_7ree.style == android.graphics.Typeface.BOLD) {
            val start_7ree = spannable_7ree.getSpanStart(span_7ree)
            val end_7ree = spannable_7ree.getSpanEnd(span_7ree)
            // 移除原有的粗体样式
            spannable_7ree.removeSpan(span_7ree)
            // 应用下划线样式
            spannable_7ree.setSpan(
                android.text.style.UnderlineSpan(),
                start_7ree,
                end_7ree,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
    // 取消标题颜色和字体大小的设置，只保留默认渲染
    // 下面代码注释掉或删除即可
    /*
    val text_7ree = spannable_7ree.toString()
    val lines_7ree = text_7ree.split("\n")
    var currentPosition_7ree = 0
    for (line_7ree in lines_7ree) {
        if (line_7ree.trim().matches(Regex("^#+\\s+.+$"))) {
            val lineStart_7ree = currentPosition_7ree
            val lineEnd_7ree = currentPosition_7ree + line_7ree.length
            spannable_7ree.setSpan(
                android.text.style.ForegroundColorSpan(android.graphics.Color.parseColor("#1976D2")),
                lineStart_7ree,
                lineEnd_7ree,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable_7ree.setSpan(
                android.text.style.AbsoluteSizeSpan(16, true),
                lineStart_7ree,
                lineEnd_7ree,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        currentPosition_7ree += line_7ree.length + 1
    }
    */
}

// 信息卡片组件
@Composable
fun InfoCard_7ree(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .graphicsLayer {
                // 设置较低的z轴层级，避免遮挡手势检测
                shadowElevation = 0f
            },
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            // 添加额外的空间来匹配收藏卡片的高度
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// 收藏卡片组件
@Composable
fun FavoriteCard_7ree(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onToggle() }
            .graphicsLayer {
                // 设置较低的z轴层级，避免遮挡手势检测
                shadowElevation = 0f
            },
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isFavorite) "取消收藏" else "收藏",
                modifier = Modifier.size(24.dp),
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isFavorite) "已收藏" else "未收藏",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            // 添加额外的空间来匹配其他卡片的高度
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// 格式化日期函数
private fun formatDate_7ree(timestamp: Long): String {
    val dateFormat_7ree = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    return dateFormat_7ree.format(Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordCardScreen_7ree(wordQueryViewModel_7ree: WordQueryViewModel_7ree, speak_7ree: (String, String) -> Unit, stopSpeaking_7ree: () -> Unit) {
    val TAG_7ree = "WordCardScreen_7ree"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(animationSpec = tween(durationMillis = 300)),
        contentAlignment = Alignment.Center
    ) {
        // 如果正在加载且没有查询结果，显示加载动画和正在查询的单词
        if (wordQueryViewModel_7ree.isLoading_7ree && wordQueryViewModel_7ree.queryResult_7ree.isBlank()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 显示正在查询的单词
                Text(
                    text = wordQueryViewModel_7ree.wordInput_7ree,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "查询中...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else if (!wordQueryViewModel_7ree.isWordConfirmed_7ree || wordQueryViewModel_7ree.queryResult_7ree.isBlank()) {
            // 未开始查询时，标题、输入框、按钮向上移动15%的距离
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = (-0.15f * 100).dp)
            ) {
                // 标题
                Text(
                    text = "AI查单词",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                
                // 输入框 - 统一设计风格
                OutlinedTextField(
                    value = wordQueryViewModel_7ree.wordInput_7ree,
                    onValueChange = { wordQueryViewModel_7ree.onWordInputChanged_7ree(it) },
                    label = { Text("请输入英文单词") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(64.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
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
                            if (wordQueryViewModel_7ree.wordInput_7ree.length >= 3) {
                                wordQueryViewModel_7ree.queryWord_7ree()
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 查询按钮 - 统一设计风格，添加放大镜图标
                Button(
                    onClick = { wordQueryViewModel_7ree.queryWord_7ree() },
                    enabled = !wordQueryViewModel_7ree.isLoading_7ree && wordQueryViewModel_7ree.wordInput_7ree.length >= 3,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(
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

                // 统计数据
                val wordCount_7ree = wordQueryViewModel_7ree.wordCount_7ree.collectAsState().value
                val totalViews_7ree = wordQueryViewModel_7ree.totalViews_7ree.collectAsState().value
                Spacer(modifier = Modifier.height(112.dp)) // 在按钮和统计数据之间增加一些间距
                Text(
                    text = "已收集${wordCount_7ree}个单词",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp)) // 两个统计数据之间的间距
                Text(
                    text = "已累计查阅${totalViews_7ree}次",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp)) // 在统计数据和底部之间增加一些间距
            }
        } else {
            // 有查询结果时，显示完整内容
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), // 将内边距移到这里
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 查询结果详情页顶部单词标题下移两行
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = if (wordQueryViewModel_7ree.wordInput_7ree.isNotBlank()) wordQueryViewModel_7ree.wordInput_7ree else "AI查单词",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize * 1.4f // 放大40%（原来是1.3f，现在增加1号）
                    ),
                    fontWeight = FontWeight.ExtraBold, // 从Bold改为ExtraBold，更加粗
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                if (wordQueryViewModel_7ree.queryResult_7ree.isNotBlank()) {
                    SwipeNavigationComponent_7ree(
                        canNavigate = wordQueryViewModel_7ree.canNavigate_7ree(),
                        onNavigateToPrevious = { wordQueryViewModel_7ree.navigateToPreviousWord_7ree() },
                        onNavigateToNext = { wordQueryViewModel_7ree.navigateToNextWord_7ree() }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(
                                    state = rememberScrollState(),
                                    enabled = true // 确保滚动功能正常
                                )
                                .padding(bottom = 16.dp) // 添加底部间距
                        ) {
                        // 处理Markdown内容，将音标标题行、英文例句标题行和中文词义标题行单独处理
                        val lines_7ree = wordQueryViewModel_7ree.queryResult_7ree.split("\n")
                        val beforePhonetic_7ree = StringBuilder()
                        val afterPhonetic_7ree = StringBuilder()
                        val beforeExamples_7ree = StringBuilder()
                        val afterExamples_7ree = StringBuilder()
                        val chineseMeaning_7ree = StringBuilder()
                        var foundPhoneticSection_7ree = false
                        var foundExamplesSection_7ree = false
                        var foundChineseMeaningSection_7ree = false
                        var phoneticTitleLine_7ree = ""
                        var examplesTitleLine_7ree = ""
                        
                        // 过滤掉"查询单词"标题行、单词英文行和"中文词义"标题行
                        val filteredLines_7ree = mutableListOf<String>()
                        var skipNextLine_7ree = false
                        
                        for (line_7ree in lines_7ree) {
                            val trimmedLine_7ree = line_7ree.trim()

                            // 检查是否是"查询单词"标题行
                            if (trimmedLine_7ree.matches(Regex("^#+\\s*查询单词.*$"))) {
                                skipNextLine_7ree = true // 标记跳过下一行（单词英文）
                                continue // 跳过当前行
                            }
                            
                            // 如果标记了跳过下一行，则跳过当前行
                            if (skipNextLine_7ree) {
                                skipNextLine_7ree = false
                                continue
                            }
                            
                            // 检查是否是"中文词义"标题行，如果是则跳过
                            if (trimmedLine_7ree.matches(Regex("^#+\\s*中文词义.*$"))) {
                                continue // 跳过中文词义标题行
                            }
                            
                            // 保留其他行
                            filteredLines_7ree.add(line_7ree)
                        }
                        
                        // 第一遍：查找音标标题，同时排除中文词义内容
                        var inChineseMeaningSection_7ree = false
                        for (line_7ree in filteredLines_7ree) {
                            val trimmedLine_7ree = line_7ree.trim()

                            if (trimmedLine_7ree.matches(Regex("^#+\\s*音标.*$"))) {
                                foundPhoneticSection_7ree = true
                                phoneticTitleLine_7ree = line_7ree
                                break
                            } else if (trimmedLine_7ree.matches(Regex("^#+\\s*中文词义.*$"))) {
                                inChineseMeaningSection_7ree = true
                                continue
                            } else if (inChineseMeaningSection_7ree && trimmedLine_7ree.matches(Regex("^#+\\s+.+$"))) {
                                inChineseMeaningSection_7ree = false
                                continue // 🛠️ 重要：此处不应该添加这行
                            }

                            if (!inChineseMeaningSection_7ree) {
                                beforePhonetic_7ree.append(line_7ree).append("\n")
                            }
                        }

                        
                        // 第二遍：查找英文例句标题
                        for (line_7ree in filteredLines_7ree) {
                            if (line_7ree.trim().matches(Regex("^#+\\s*英文例句.*$"))) {
                                foundExamplesSection_7ree = true
                                examplesTitleLine_7ree = line_7ree
                                break
                            }
                        }
                        
                        // 第三遍：查找中文词义的"第二行"
                        var foundChineseMeaningStarted_7ree = false
                        var chineseMeaningLineCount_7ree = 0
                                    for (line_7ree in lines_7ree) {
                                        val trimmedLine_7ree = line_7ree.trim()

                            if (foundChineseMeaningStarted_7ree) {
                                // 如果遇到下一个标题，则停止处理
                                if (trimmedLine_7ree.matches(Regex("^#+\\s+.+$"))) {
                                            break
                                        }

                                chineseMeaningLineCount_7ree += 1

                                // 只保留第二行
                                if (chineseMeaningLineCount_7ree == 2) {
                                    chineseMeaning_7ree.append(line_7ree.trimStart()).append("\n")
                                    break // 提前终止
                                }
                            }

                            if (trimmedLine_7ree.matches(Regex("^#+\\s*中文词义.*$"))) {
                                foundChineseMeaningStarted_7ree = true
                                foundChineseMeaningSection_7ree = true
                            }
                        }
                        
                        // 显示音标标题之前的内容
                        if (beforePhonetic_7ree.isNotEmpty()) {
                            AndroidView(factory = { context ->
                                TextView(context).apply {
                                    textSize = 20f
                                    gravity = android.view.Gravity.CENTER // 水平居中
                                    setTypeface(android.graphics.Typeface.DEFAULT_BOLD) // 粗体字效果
                                    setTextColor(android.graphics.Color.parseColor("#690BED")) // 紫色
                                }
                            },
                            modifier = Modifier
                                    .fillMaxWidth() // 让 AndroidView 占据最大宽度
                                    .wrapContentHeight(), // 高度包裹内容
                            update = { textView ->
                                val markwon_7ree = Markwon.builder(textView.context).build()
                                markwon_7ree.setMarkdown(textView, beforePhonetic_7ree.toString())
                                
                                // 应用样式调整
                                applyTextStyleAdjustments_7ree(textView)
                            })
                        }
                        
                        // 显示中文词义内容（超大字体，水平居中，粗体）
                        if (foundChineseMeaningSection_7ree && chineseMeaning_7ree.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp)) // 原为16.dp，缩小空白
                            AndroidView(
                                factory = { context ->
                                    TextView(context)
                                },
                                update = { textView ->
                                    val markwon_7ree = Markwon.builder(textView.context).build()
                                    markwon_7ree.setMarkdown(textView, chineseMeaning_7ree.toString())
                                    
                                    // 应用样式调整
                                    applyTextStyleAdjustments_7ree(textView)
                                }
                            )
                        }
                        
                        // 显示音标标题行（带单词朗读喇叭按钮）
                        if (foundPhoneticSection_7ree) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // 音标标题
                                AndroidView(factory = { context ->
                                    TextView(context)
                                }, update = { textView ->
                                    val markwon_7ree = Markwon.builder(textView.context).build()
                                    markwon_7ree.setMarkdown(textView, phoneticTitleLine_7ree)
                                })
                                
                                // 单词朗读按钮
                                IconButton(
                                    onClick = {
                                        if (wordQueryViewModel_7ree.isSpeakingWord_7ree) {
                                            stopSpeaking_7ree() // Stop speaking if currently speaking
                                        } else {
                                            val wordText_7ree = wordQueryViewModel_7ree.getWordSpeechText_7ree()
                                            Log.d(TAG_7ree, "Word Speaker Icon Clicked: Speaking: \"$wordText_7ree\"")
                                            if (wordText_7ree.isNotBlank()) {
                                                speak_7ree(wordText_7ree, "word")
                                            }
                                        }
                                    },
                                    enabled = wordQueryViewModel_7ree.isTtsReady_7ree
                                ) {
                                    if (wordQueryViewModel_7ree.isSpeakingWord_7ree) {
                                        Icon(Icons.Filled.Pause, contentDescription = "暂停朗读单词")
                                    } else {
                                        Icon(Icons.Filled.VolumeUp, contentDescription = "开始朗读单词")
                                    }
                                }
                            }
                            
                            // 收集音标标题之后到英文例句标题之前的内容
                            var afterPhoneticStarted_7ree = false
                            for (line_7ree in filteredLines_7ree) {
                                if (afterPhoneticStarted_7ree && !line_7ree.trim().matches(Regex("^#+\\s*英文例句.*$"))) {
                                    afterPhonetic_7ree.append(line_7ree).append("\n")
                                } else if (line_7ree.trim().matches(Regex("^#+\\s*音标.*$"))) {
                                    afterPhoneticStarted_7ree = true
                                } else if (line_7ree.trim().matches(Regex("^#+\\s*英文例句.*$"))) {
                                    break
                                }
                            }
                            
                            // 显示音标标题之后的内容
                            if (afterPhonetic_7ree.isNotEmpty()) {
                                AndroidView(factory = { context ->
                                    TextView(context).apply {
                                        textSize = 14f
                                    }
                                }, update = { textView ->
                                    val markwon_7ree = Markwon.builder(textView.context).build()
                                    markwon_7ree.setMarkdown(textView, afterPhonetic_7ree.toString())
                                    
                                    // 应用样式调整
                                    applyTextStyleAdjustments_7ree(textView)
                                })
                            }
                        }
                        
                        // 显示英文例句标题行（带例句朗读喇叭按钮）
                        if (foundExamplesSection_7ree) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // 英文例句标题
                                AndroidView(factory = { context ->
                                    TextView(context)
                                }, update = { textView ->
                                    val markwon_7ree = Markwon.builder(textView.context).build()
                                    markwon_7ree.setMarkdown(textView, examplesTitleLine_7ree)
                                })
                                
                                // 例句朗读按钮
                                IconButton(
                                    onClick = {
                                        if (wordQueryViewModel_7ree.isSpeakingExamples_7ree) {
                                            stopSpeaking_7ree() // Stop speaking if currently speaking
                                        } else {
                                            val examplesText_7ree = wordQueryViewModel_7ree.getExamplesSpeechText_7ree()
                                            Log.d(TAG_7ree, "Examples Speaker Icon Clicked: Speaking: \"$examplesText_7ree\"")
                                            if (examplesText_7ree.isNotBlank()) {
                                                speak_7ree(examplesText_7ree, "examples")
                                    }
                                }
                            },
                            enabled = wordQueryViewModel_7ree.isTtsReady_7ree
                        ) {
                                    if (wordQueryViewModel_7ree.isSpeakingExamples_7ree) {
                                        Icon(Icons.Filled.Pause, contentDescription = "暂停朗读例句")
                            } else {
                                        Icon(Icons.Filled.VolumeUp, contentDescription = "开始朗读例句")
                            }
                        }
                    }
                            
                            // 收集英文例句标题之后的内容
                            var afterExamplesStarted_7ree = false
                            for (line_7ree in filteredLines_7ree) {
                                if (afterExamplesStarted_7ree) {
                                    // 去除行首的额外空格，确保对齐
                                    val trimmedLine_7ree = line_7ree.trimStart()
                                    afterExamples_7ree.append(trimmedLine_7ree).append("\n")
                                } else if (line_7ree.trim().matches(Regex("^#+\\s*英文例句.*$"))) {
                                    afterExamplesStarted_7ree = true
                                }
                            }
                            
                                                        // 显示英文例句标题之后的内容
                            if (afterExamples_7ree.isNotEmpty()) {
                        AndroidView(factory = { context ->
                            TextView(context).apply {
                                textSize = 14f
                            }
                        }, update = { textView ->
                            val markwon_7ree = Markwon.builder(textView.context).build()
                                    markwon_7ree.setMarkdown(textView, afterExamples_7ree.toString())
                                    
                                    // 应用样式调整
                                    applyTextStyleAdjustments_7ree(textView)
                                })
                            }
                        } else if (!foundPhoneticSection_7ree) {
                            // 如果既没有找到音标部分也没有找到英文例句部分，直接显示过滤后的内容
                            val filteredContent_7ree = filteredLines_7ree.joinToString("\n")
                            if (filteredContent_7ree.isNotBlank()) {
                                AndroidView(factory = { context ->
                                    TextView(context).apply {
                                        textSize = 14f
                                    }
                                }, update = { textView ->
                                    val markwon_7ree = Markwon.builder(textView.context).build()
                                    markwon_7ree.setMarkdown(textView, filteredContent_7ree)
                            
                                    // 应用样式调整
                                    applyTextStyleAdjustments_7ree(textView)
                                })
                            }
                        }
                        
                        // 添加3个并排的信息卡片
                        if (wordQueryViewModel_7ree.currentWordInfo_7ree != null) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // 卡片1：初次查询时间
                                InfoCard_7ree(
                                    title = "初次查询",
                                    value = formatDate_7ree(wordQueryViewModel_7ree.currentWordInfo_7ree!!.queryTimestamp),
                                    icon = Icons.Filled.History,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                // 卡片2：查阅次数
                                InfoCard_7ree(
                                    title = "查阅次数",
                                    value = "查阅${wordQueryViewModel_7ree.currentWordInfo_7ree!!.viewCount}次",
                                    icon = Icons.Filled.Visibility,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                // 卡片3：收藏桃心
                                FavoriteCard_7ree(
                                    isFavorite = wordQueryViewModel_7ree.currentWordInfo_7ree!!.isFavorite,
                                    onToggle = { wordQueryViewModel_7ree.toggleFavorite_7ree() },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        
                        // 在底部添加滑动提示信息
                        if (wordQueryViewModel_7ree.canNavigate_7ree()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "上下滑动切换单词",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                    }
                }
            }
        }
    }
} 

} 