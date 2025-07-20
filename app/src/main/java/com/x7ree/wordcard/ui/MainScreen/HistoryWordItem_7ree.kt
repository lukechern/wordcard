package com.x7ree.wordcard.ui.MainScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.x7ree.wordcard.data.WordEntity_7ree
import com.x7ree.wordcard.ui.SwipeableRevealItem_7ree
import com.x7ree.wordcard.ui.components.TtsButtonIcon_7ree
import com.x7ree.wordcard.ui.components.TtsButtonState_7ree
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryWordItem_7ree(
    wordEntity_7ree: WordEntity_7ree,
    onWordClick_7ree: (String) -> Unit,
    onFavoriteToggle_7ree: (WordEntity_7ree) -> Unit,
    onDismiss_7ree: () -> Unit,
    onWordSpeak_7ree: (String) -> Unit = {},
    onWordStopSpeak_7ree: () -> Unit = {},
    ttsState_7ree: TtsButtonState_7ree = TtsButtonState_7ree.IDLE,
    currentSpeakingWord_7ree: String = ""
) {
    val dateFormat_7ree = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val dateStr_7ree = dateFormat_7ree.format(Date(wordEntity_7ree.queryTimestamp))

    SwipeableRevealItem_7ree(
        onDeleteClick = onDismiss_7ree,
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onWordClick_7ree(wordEntity_7ree.word) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = wordEntity_7ree.word,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            
                            // 收藏状态显示（仅显示，不可点击）
                            if (wordEntity_7ree.isFavorite) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "已收藏",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(13.6.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // 时间显示 - 固定宽度
                            Text(
                                text = dateStr_7ree,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.width(120.dp)
                            )
                            
                            // 查看次数 - 固定宽度
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.width(50.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Visibility,
                                    contentDescription = "浏览次数",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (wordEntity_7ree.viewCount > 99) "99+" else "${wordEntity_7ree.viewCount}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            
                            // 拼写练习次数 - 固定宽度（如果大于0才显示）
                            if (wordEntity_7ree.spellingCount > 0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.width(60.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Keyboard,
                                        contentDescription = "拼写练习次数",
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = if (wordEntity_7ree.spellingCount > 99) "99+" else "${wordEntity_7ree.spellingCount}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    // 朗读按钮（带状态切换）
                    val currentTtsState = if (currentSpeakingWord_7ree == wordEntity_7ree.word) {
                        ttsState_7ree
                    } else {
                        TtsButtonState_7ree.IDLE
                    }
                    
                    // 调试日志 - 帮助验证状态切换
                    android.util.Log.d("TTS_DEBUG", "单词: ${wordEntity_7ree.word}, 当前朗读: $currentSpeakingWord_7ree, 状态: $currentTtsState")
                    
                    IconButton(
                        onClick = {
                            when (currentTtsState) {
                                TtsButtonState_7ree.IDLE -> onWordSpeak_7ree(wordEntity_7ree.word)
                                TtsButtonState_7ree.LOADING -> { /* 加载中不响应 */ }
                                TtsButtonState_7ree.PLAYING -> onWordStopSpeak_7ree()
                            }
                        },
                        enabled = currentTtsState != TtsButtonState_7ree.LOADING
                    ) {
                        TtsButtonIcon_7ree(
                            state = currentTtsState,
                            contentDescription = "朗读单词 (使用配置的TTS引擎和音色)"
                        )
                    }
                }
            }
        }
    )
}