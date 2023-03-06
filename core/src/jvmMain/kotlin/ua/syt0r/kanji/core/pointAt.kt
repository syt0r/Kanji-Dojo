package ua.syt0r.kanji.core

import androidx.compose.ui.graphics.PathMeasure
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

actual fun PathMeasure.pointAt(fraction: Float): PointF {
    val skiaPathMeasureField = this::class.memberProperties
        .find { it.name == "skia" }!!
        .apply { isAccessible = true }
        .run { this as KProperty1<PathMeasure, org.jetbrains.skia.PathMeasure> }

    // TODO I ENJOY THIS API VERY MUCH, THANKS EVERYONE
    val skia: org.jetbrains.skia.PathMeasure = skiaPathMeasureField.get(this)

    return skia.getPosition(fraction)!!.run { PointF(x, y) }
}
