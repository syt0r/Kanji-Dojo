package ua.syt0r.kanji.presentation.common.resources.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ExtraIcons.AppIconBackground: ImageVector by lazy {
    Builder(
        name = "AppIconBackground", defaultWidth = 108.0.dp,
        defaultHeight = 108.0.dp, viewportWidth = 28.574999f, viewportHeight =
        28.575f
    ).apply {
        path(
            fill = SolidColor(Color(0xFFff5555)), stroke = SolidColor(Color(0x00000000)),
            strokeLineWidth = 0.132292f, strokeLineCap = Round, strokeLineJoin =
            StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
        ) {
            moveTo(0.0f, 0.0f)
            horizontalLineToRelative(28.575f)
            verticalLineToRelative(28.575f)
            horizontalLineToRelative(-28.575f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFff8080)), stroke = SolidColor(Color(0x00000000)),
            strokeLineWidth = 0.132553f, strokeLineCap = Round, strokeLineJoin =
            StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
        ) {
            moveTo(-0.3459f, 50.5332f)
            lineToRelative(-28.1642f, -39.3179f)
            lineToRelative(27.4097f, -16.1635f)
            lineToRelative(28.1642f, 39.3179f)
            close()
        }
    }
        .build()
}