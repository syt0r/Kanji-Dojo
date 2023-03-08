package ua.syt0r.kanji.core

import androidx.compose.ui.graphics.AndroidPathMeasure
import androidx.compose.ui.graphics.PathMeasure
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

private val androidPathMeasureField = AndroidPathMeasure::class.memberProperties
    .find { it.name == "internalPathMeasure" }!!
    .apply { isAccessible = true }

actual fun PathMeasure.pointAt(fraction: Float): PointF {
    val position = FloatArray(2)

    // TODO remove reflection if it becomes public
    androidPathMeasureField.get(this as AndroidPathMeasure)
        .let { it as android.graphics.PathMeasure }
        .getPosTan(fraction, position, null)

    return PointF(position[0], position[1])
}
