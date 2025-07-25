package com.x7ree.wordcard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import com.x7ree.wordcard.R // 暂时注释掉，使用完整路径

// 排序类型枚举
enum class SortType_7ree {
    BROWSE_COUNT_ASC,    // 浏览数量：少->多
    BROWSE_COUNT_DESC,   // 浏览数量：多->少
    RECORD_TIME_ASC,     // 记录时间：早->晚
    RECORD_TIME_DESC,    // 记录时间：晚->早
    SPELLING_COUNT_ASC,  // 拼写次数：少->多
    SPELLING_COUNT_DESC, // 拼写次数：多->少
    VIEW_COUNT_ASC,      // 浏览次数：少->多
    VIEW_COUNT_DESC      // 浏览次数：多->少
}

// 筛选状态数据类
data class FilterState_7ree(
    val showFavoritesOnly: Boolean = false,
    val sortType: SortType_7ree? = SortType_7ree.RECORD_TIME_DESC // 默认为记录时间：晚→早
)

@Composable
fun FilterSideMenu_7ree(
    isVisible: Boolean,
    filterState: FilterState_7ree,
    onFilterStateChange: (FilterState_7ree) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 动画偏移量
    val offsetX by animateFloatAsState(
        targetValue = if (isVisible) 0f else 300f,
        animationSpec = tween(durationMillis = 300),
        label = "menu_slide"
    )

    if (isVisible || offsetX < 300f) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable { onDismiss() }
        ) {
            // 侧边菜单内容
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(215.dp) // 再减少5dp：220 - 5 = 215
                    .offset(x = offsetX.dp)
                    .align(Alignment.CenterEnd)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer, // 使用和底部导航栏相同的颜色
                        shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp) // 左上角和左下角保持直角
                    )
                    .clickable(enabled = false) { } // 阻止点击事件传播
                    .padding(16.dp)
            ) {
                // 标题栏 - 包含标题和关闭按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 24.dp), // 向下调整位置
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "筛选与排序",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    IconButton(
                        onClick = { onDismiss() }
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Close,
                            contentDescription = "关闭",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 收藏筛选标题
                Text(
                    text = "筛选",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 收藏筛选
                FilterMenuItem_7ree(
                    icon = if (filterState.showFavoritesOnly) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    title = "只看收藏",
                    subtitle = if (filterState.showFavoritesOnly) "是" else "否",
                    isSelected = filterState.showFavoritesOnly,
                    onClick = {
                        onFilterStateChange(
                            filterState.copy(showFavoritesOnly = !filterState.showFavoritesOnly)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 排序选项标题
                Text(
                    text = "排序规则",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 记录时间排序
                SortMenuItem_7ree(
                    title = "记录时间",
                    sortDirection = "早→晚",
                    sortType = SortType_7ree.RECORD_TIME_ASC,
                    currentSortType = filterState.sortType,
                    onSortTypeChange = { sortType ->
                        onFilterStateChange(filterState.copy(sortType = sortType))
                    }
                )

                SortMenuItem_7ree(
                    title = "记录时间",
                    sortDirection = "晚→早",
                    sortType = SortType_7ree.RECORD_TIME_DESC,
                    currentSortType = filterState.sortType,
                    onSortTypeChange = { sortType ->
                        onFilterStateChange(filterState.copy(sortType = sortType))
                    }
                )

                // 拼写次数排序
                SortMenuItem_7ree(
                    title = "拼写次数",
                    sortDirection = "少→多",
                    sortType = SortType_7ree.SPELLING_COUNT_ASC,
                    currentSortType = filterState.sortType,
                    onSortTypeChange = { sortType ->
                        onFilterStateChange(filterState.copy(sortType = sortType))
                    }
                )

                SortMenuItem_7ree(
                    title = "拼写次数",
                    sortDirection = "多→少",
                    sortType = SortType_7ree.SPELLING_COUNT_DESC,
                    currentSortType = filterState.sortType,
                    onSortTypeChange = { sortType ->
                        onFilterStateChange(filterState.copy(sortType = sortType))
                    }
                )

                // 浏览次数排序（与浏览数量相同，都对应viewCount字段）
                SortMenuItem_7ree(
                    title = "查阅次数",
                    sortDirection = "少→多",
                    sortType = SortType_7ree.VIEW_COUNT_ASC,
                    currentSortType = filterState.sortType,
                    onSortTypeChange = { sortType ->
                        onFilterStateChange(filterState.copy(sortType = sortType))
                    }
                )

                SortMenuItem_7ree(
                    title = "查阅次数",
                    sortDirection = "多→少",
                    sortType = SortType_7ree.VIEW_COUNT_DESC,
                    currentSortType = filterState.sortType,
                    onSortTypeChange = { sortType ->
                        onFilterStateChange(filterState.copy(sortType = sortType))
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterMenuItem_7ree(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) Color(0xFFE8F5E8) // 很浅的浅绿色背景
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.4.dp) // 减小15%：24 * 0.85 = 20.4
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface // 改为onSurface以适配浅绿色背景
            else MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) // 改为onSurface以适配浅绿色背景
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SortMenuItem_7ree(
    title: String,
    sortDirection: String,
    sortType: SortType_7ree,
    currentSortType: SortType_7ree?,
    onSortTypeChange: (SortType_7ree?) -> Unit
) {
    val isSelected = currentSortType == sortType
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) Color(0xFFE8F5E8) // 很浅的浅绿色背景
                else Color.Transparent
            )
            .clickable { 
                // 如果已选中，则取消选择；否则选择该项
                onSortTypeChange(if (isSelected) null else sortType)
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 排序图标 - 使用Material Icons，选中时加大20%
        val iconVector = when {
            sortDirection.contains("少→多") || sortDirection.contains("早→晚") -> {
                // 上升箭头图标
                androidx.compose.material.icons.Icons.Default.KeyboardArrowUp
            }
            else -> {
                // 下降箭头图标
                androidx.compose.material.icons.Icons.Default.KeyboardArrowDown
            }
        }
        
        // 使用固定大小的Box来确保对齐和行高一致
        Box(
            modifier = Modifier.size(20.4.dp), // 与收藏筛选图标大小一致
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = "$title $sortDirection",
                tint = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(
                    if (isSelected) 29.81.dp // 选中时再加大20%：24.84 * 1.2 = 29.81
                    else 24.84.dp // 未选中时也加大20%：20.7 * 1.2 = 24.84
                )
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface // 改为onSurface以适配浅绿色背景
            else MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 排序方向文字右对齐，去掉绿色圆点
        Text(
            text = sortDirection,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) // 改为onSurface以适配浅绿色背景
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    
    Spacer(modifier = Modifier.height(4.dp))
}