package ua.syt0r.kanji.presentation.common.resources.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val ExtraIcons.AppIconForeground: ImageVector by lazy {
    Builder(
        name = "AppIconForeground", defaultWidth = 108.0.dp,
        defaultHeight = 108.0.dp, viewportWidth = 28.574999f, viewportHeight =
        28.575f
    ).apply {
        path(
            fill = SolidColor(Color(0xFFffffff)), stroke = SolidColor(Color(0x00000000)),
            strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
            strokeLineMiter = 4.0f, pathFillType = NonZero
        ) {
            moveTo(0.0f, 0.0f)
            lineTo(0.0f, 28.575f)
            lineTo(28.575f, 28.575f)
            lineTo(28.575f, 0.0f)
            close()
            moveTo(14.0395f, 9.4213f)
            lineTo(14.0594f, 9.4255f)
            curveToRelative(0.0828f, 0.0166f, 0.1548f, 0.036f, 0.2239f, 0.0755f)
            curveToRelative(0.0691f, 0.0395f, 0.1502f, 0.1183f, 0.1641f, 0.2297f)
            curveToRelative(0.0139f, 0.1114f, -0.0389f, 0.1968f, -0.0891f, 0.2559f)
            curveToRelative(-0.024f, 0.0282f, -0.0584f, 0.0489f, -0.087f, 0.0729f)
            verticalLineToRelative(0.494f)
            horizontalLineToRelative(3.9003f)
            verticalLineToRelative(2.5185f)
            horizontalLineToRelative(-1.2108f)
            lineToRelative(0.0058f, 0.0063f)
            lineToRelative(-0.549f, 0.1841f)
            lineToRelative(-2.1048f, 1.3497f)
            verticalLineToRelative(0.2202f)
            horizontalLineToRelative(4.1105f)
            verticalLineToRelative(1.2165f)
            horizontalLineToRelative(-4.1105f)
            verticalLineToRelative(2.2239f)
            curveToRelative(0.0f, 0.2587f, -0.0683f, 0.4904f, -0.2297f, 0.6518f)
            curveToRelative(-0.1614f, 0.1614f, -0.3936f, 0.2302f, -0.6523f, 0.2302f)
            lineTo(12.1366f, 19.1547f)
            lineToRelative(-0.3209f, -1.2171f)
            horizontalLineToRelative(1.237f)
            lineTo(13.0527f, 16.0489f)
            lineTo(8.6904f, 16.0489f)
            lineTo(8.6904f, 14.8323f)
            horizontalLineToRelative(4.3622f)
            verticalLineToRelative(-1.1358f)
            lineToRelative(0.5847f, 0.1169f)
            lineToRelative(0.537f, -0.3644f)
            horizontalLineToRelative(-3.5537f)
            verticalLineToRelative(-1.2171f)
            horizontalLineToRelative(4.9883f)
            lineToRelative(0.2559f, -0.2559f)
            lineToRelative(1.0482f, 1.0487f)
            verticalLineToRelative(-1.2548f)
            horizontalLineToRelative(-6.6689f)
            verticalLineToRelative(1.3854f)
            lineTo(8.9835f, 13.1554f)
            lineTo(8.9835f, 10.5535f)
            lineTo(12.8859f, 10.5535f)
            lineTo(12.8859f, 9.4219f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFff5555)), stroke = SolidColor(Color(0xFFff8080)),
            strokeLineWidth = 0.449792f, strokeLineCap = Round, strokeLineJoin =
            StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
        ) {
            moveTo(18.5664f, 18.3282f)
            arcToRelative(0.7337f, 0.7514f, 0.0f, true, false, 1.4674f, 0.0f)
            arcToRelative(0.7337f, 0.7514f, 0.0f, true, false, -1.4674f, 0.0f)
            close()
        }
    }
        .build()
}
