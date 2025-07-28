package com.x7ree.wordcard.article.utils

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
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// 文章排序类型枚举
enum class ArticleSortType_7ree {
    PUBLISH_TIME_ASC,    // 发布时间：早->晚
    PUBLISH_TIME_DESC,   // 发布时间：晚->早
    VIEW_COUNT_ASC,      // 查看次数：少->多
    VIEW_COUNT_DESC      // 查看次数：多->少
}

// 文章筛选状态数据类
data class ArticleFilterState_7ree(
    val showFavoritesOnly: Boolean = false,
    val sortType: ArticleSortType_7ree? = ArticleSortType_7ree.PUBLISH_TIME_DESC // 默认为发布时间：晚→早
)

@Composable
fun ArticleFilterSideMenu_7ree(
    isVisible: Boolean,
    filterState: ArticleFilterState_7ree,
    onFilterStateChange: (ArticleFilterState_7ree) -> Unit,
    onDismiss: () -> Unit,
    onManageClick: () -> Unit = {},
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
                    .width(215.dp) // 与单词本菜单保持一致
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
                            imageVector = Icons.Default.Close,
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
                ArticleFilterMenuItem_7ree(
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

                // 发布时间排序
                ArticleSortMenuItem_7ree(
                    title = "发布时间",
                    sortDirection = "早→晚",
                    sortType = ArticleSortType_7ree.PUBLISH_TIME_ASC,
                    currentSortType = filterState.sortType,
                    onSortTypeChange = { sortType ->
                        onFilterStateChange(filterState.copy(sortType = sortType))
                    }
                )

                ArticleSortMenuItem_7ree(
                    title = "发布时间",
                    sortDirection = "晚→早",
                    sortType = ArticleSortType_7ree.PUBLISH_TIME_DESC,
                    currentSortType = filterState.sortType,
                    onSortTypeChange = { sortType ->
                        onFilterStateChange(filterState.copy(sortType = sortType))
                    }
                )

                // 查看次数排序
                ArticleSortMenuItem_7ree(
                    title = "查看次数",
                    sortDirection = "少→多",
                    sortType = ArticleSortType_7ree.VIEW_COUNT_ASC,
                    currentSortType = filterState.sortType,
                    onSortTypeChange = { sortType ->
                        onFilterStateChange(filterState.copy(sortType = sortType))
                    }
                )

                ArticleSortMenuItem_7ree(
                    title = "查看次数",
                    sortDirection = "多→少",
                    sortType = ArticleSortType_7ree.VIEW_COUNT_DESC,
                    currentSortType = filterState.sortType,
                    onSortTypeChange = { sortType ->
                        onFilterStateChange(filterState.copy(sortType = sortType))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 管理选项
                Text(
                    text = "管理",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 管理按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { 
                            onManageClick()
                            onDismiss()
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckBox,
                        contentDescription = "管理",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.4.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "管理",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Text(
                        text = "批量删除",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ArticleFilterMenuItem_7ree(
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
private fun ArticleSortMenuItem_7ree(
    title: String,
    sortDirection: String,
    sortType: ArticleSortType_7ree,
    currentSortType: ArticleSortType_7ree?,
    onSortTypeChange: (ArticleSortType_7ree?) -> Unit
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
                Icons.Default.KeyboardArrowUp
            }
            else -> {
                // 下降箭头图标
                Icons.Default.KeyboardArrowDown
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