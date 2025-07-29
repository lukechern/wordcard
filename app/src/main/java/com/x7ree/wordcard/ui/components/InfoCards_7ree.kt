package com.x7ree.wordcard.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 信息卡片组件 - ImageVector版本
@Composable
fun InfoCard_7ree(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    InfoCard_7ree(
        title = title,
        value = value,
        icon = icon,
        drawableRes = null,
        modifier = modifier,
        onClick = onClick
    )
}

// 信息卡片组件 - Drawable资源版本
@Composable
fun InfoCard_7ree(
    title: String,
    value: String,
    drawableRes: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    InfoCard_7ree(
        title = title,
        value = value,
        icon = null,
        drawableRes = drawableRes,
        modifier = modifier,
        onClick = onClick
    )
}

// 信息卡片组件 - 内部实现
@Composable
private fun InfoCard_7ree(
    title: String,
    value: String,
    icon: ImageVector? = null,
    drawableRes: Int? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .let { if (onClick != null) it.clickable { onClick() } else it }
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
                .padding(12.dp), // 从16.dp减少到12.dp
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (drawableRes != null) {
                Icon(
                    painter = painterResource(id = drawableRes),
                    contentDescription = title,
                    modifier = Modifier.size(20.dp), // 从24.dp减少到20.dp
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = icon!!,
                    contentDescription = title,
                    modifier = Modifier.size(20.dp), // 从24.dp减少到20.dp
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp)) // 从6.dp减少到4.dp（缩小30%）
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            // 删除额外的空间以减少高度
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
                .padding(12.dp), // 从16.dp减少到12.dp
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isFavorite) "取消收藏" else "收藏",
                modifier = Modifier.size(20.dp), // 从24.dp减少到20.dp
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp)) // 从6.dp减少到4.dp（缩小30%）
            Text(
                text = if (isFavorite) "已收藏" else "收藏",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            // 删除额外的空间以减少高度
        }
    }
}

// 格式化日期函数
fun formatDate_7ree(timestamp: Long): String {
    val dateFormat_7ree = SimpleDateFormat("yy.MM.dd", Locale.getDefault())
    return dateFormat_7ree.format(Date(timestamp))
}
