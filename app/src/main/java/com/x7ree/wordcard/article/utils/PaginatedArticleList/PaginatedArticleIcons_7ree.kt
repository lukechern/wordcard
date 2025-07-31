package com.x7ree.wordcard.article.utils.PaginatedArticleList

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// 复用单词本的刷新图标 - 重命名以避免冲突
val PaginatedArticleRefreshIcon: ImageVector
    get() {
        return ImageVector.Builder(
            name = "PaginatedArticleRefreshIcon",
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
