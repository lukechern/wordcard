package com.x7ree.wordcard.article.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 文章列表下拉刷新组件
 * 与单词本保持一致的UI样式和交互反馈
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticlePullToRefreshComponent_7ree(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // 下拉刷新状态
    val pullToRefreshState = rememberPullToRefreshState()
    
    // 下拉刷新容器
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier = modifier,
        indicator = {
            // 自定义刷新指示器 - 只在下拉或刷新时显示
            if (isRefreshing || pullToRefreshState.distanceFraction > 0f) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        if (isRefreshing) {
                            // 刷新中使用自定义图标，持续旋转
                            val infiniteTransition = rememberInfiniteTransition(label = "article_refresh_rotation")
                            val rotation by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "article_refresh_rotation"
                            )
                            
                            Icon(
                                imageVector = ArticleRefreshIcon,
                                contentDescription = "刷新中",
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(rotation),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // 刷新中的提示文字，带白色背景
                            Text(
                                text = "文章列表刷新中，请稍候",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        } else {
                            // 自定义下拉指示器 - 使用自定义刷新图标
                            // 根据下拉距离计算旋转角度
                            val rotation = 360f * pullToRefreshState.distanceFraction
                            
                            Icon(
                                imageVector = ArticleRefreshIcon,
                                contentDescription = "下拉刷新",
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(rotation),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // 下拉提示文字，带白色背景
                            Text(
                                text = "松手后，自动刷新文章列表",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    ) {
        content()
    }
}

/**
 * 自定义刷新图标 - 与单词本保持一致的样式
 */
private val ArticleRefreshIcon: ImageVector
    get() {
        return ImageVector.Builder(
            name = "ArticleRefreshIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 48f,
            viewportHeight = 48f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color(0xFFC5C5C5)),
                strokeLineWidth = 3.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 4f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(36.7279f, 36.7279f)
                curveTo(33.4706f, 39.9853f, 28.9706f, 42f, 24f, 42f)
                curveTo(14.0589f, 42f, 6f, 33.9411f, 6f, 24f)
                curveTo(6f, 14.0589f, 14.0589f, 6f, 24f, 6f)
                curveTo(28.9706f, 6f, 33.4706f, 8.01472f, 36.7279f, 11.2721f)
                curveTo(38.3859f, 12.9301f, 42f, 17f, 42f, 17f)
            }
            path(
                fill = null,
                stroke = SolidColor(Color(0xFFC5C5C5)),
                strokeLineWidth = 3.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 4f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(42f, 8f)
                verticalLineTo(17f)
                horizontalLineTo(33f)
            }
        }.build()
    }