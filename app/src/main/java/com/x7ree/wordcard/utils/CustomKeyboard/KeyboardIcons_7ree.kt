package com.x7ree.wordcard.utils.CustomKeyboard

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 自定义键盘图标定义
 * 包含删除和回车图标
 */

// 自定义删除图标
val CustomDeleteIcon: ImageVector
    get() {
        if (_customDeleteIcon != null) {
            return _customDeleteIcon!!
        }
        _customDeleteIcon = ImageVector.Builder(
            name = "CustomDelete",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 1024f,
            viewportHeight = 1024f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFC5C5C5)),
                stroke = null
            ) {
                moveTo(886.30272f, 949.4528f)
                lineTo(511.90784f, 575.05792f)
                lineTo(137.5744f, 949.39136f)
                arcToRelative(44.63616f, 44.63616f, 0f, false, true, -63.15008f, 0.06144f)
                curveToRelative(-17.37728f, -17.37728f, -17.28512f, -45.69088f, 0.1024f, -63.10912f)
                lineToRelative(374.36416f, -374.33344f)
                lineToRelative(-374.3744f, -374.36416f)
                arcToRelative(44.71808f, 44.71808f, 0f, false, true, 0f, -63.13984f)
                arcToRelative(44.63616f, 44.63616f, 0f, false, true, 63.15008f, 0.03072f)
                lineTo(512f, 448.8704f)
                lineTo(886.33344f, 74.53696f)
                arcToRelative(44.58496f, 44.58496f, 0f, true, true, 63.04768f, 63.04768f)
                lineTo(575.04768f, 511.91808f)
                lineTo(949.4528f, 886.31296f)
                arcToRelative(44.56448f, 44.56448f, 0f, false, true, 0.03072f, 63.10912f)
                arcToRelative(44.58496f, 44.58496f, 0f, false, true, -63.1808f, 0.03072f)
                close()
            }
        }.build()
        return _customDeleteIcon!!
    }

// 自定义回车图标
val CustomEnterIcon: ImageVector
    get() {
        if (_customEnterIcon != null) {
            return _customEnterIcon!!
        }
        _customEnterIcon = ImageVector.Builder(
            name = "CustomEnter",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 1109f,
            viewportHeight = 1024f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF666666)),
                stroke = null
            ) {
                moveTo(1040.040933f, 0.001707f)
                horizontalLineToRelative(-114.858475f)
                arcToRelative(15.189308f, 15.189308f, 0f, false, false, -15.359974f, 14.933308f)
                verticalLineTo(674.133916f)
                horizontalLineTo(302.762162f)
                verticalLineTo(538.198143f)
                curveToRelative(-0.085333f, -5.802657f, -3.498661f, -11.093315f, -8.789319f, -13.653311f)
                arcToRelative(15.615974f, 15.615974f, 0f, false, false, -16.213306f, 1.877331f)
                lineTo(5.88799f, 735.232481f)
                arcToRelative(14.762642f, 14.762642f, 0f, false, false, 0f, 23.466628f)
                lineToRelative(271.95688f, 208.810318f)
                curveToRelative(4.522659f, 3.583994f, 10.837315f, 4.26666f, 16.127973f, 1.877331f)
                arcToRelative(15.189308f, 15.189308f, 0f, false, false, 8.789319f, -13.567978f)
                verticalLineTo(815.787014f)
                horizontalLineToRelative(630.100283f)
                curveToRelative(67.583887f, 0f, 122.538462f, -53.503911f, 122.538463f, -119.295801f)
                verticalLineTo(14.935015f)
                arcToRelative(15.018642f, 15.018642f, 0f, false, false, -4.522659f, -10.666649f)
                arcToRelative(15.359974f, 15.359974f, 0f, false, false, -10.837316f, -4.266659f)
                close()
            }
        }.build()
        return _customEnterIcon!!
    }

private var _customDeleteIcon: ImageVector? = null
private var _customEnterIcon: ImageVector? = null