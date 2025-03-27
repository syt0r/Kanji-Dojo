package ua.syt0r.kanji.ios

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIDeviceOrientation
import platform.UIKit.UIViewController
import ua.syt0r.kanji.presentation.KanjiDojoApp
import ua.syt0r.kanji.presentation.common.ui.Orientation

@OptIn(ExperimentalForeignApi::class)
fun MainViewController(): UIViewController = ComposeUIViewController {

    val orientationState = remember { mutableStateOf(getOrientation()) }

    KanjiDojoApp(
        orientation = orientationState.value
    )

    DisposableEffect(Unit) {
        val notificationCenter = NSNotificationCenter.defaultCenter

        val observer = notificationCenter.addObserverForName(
            name = platform.UIKit.UIDeviceOrientationDidChangeNotification,
            `object` = null,
            queue = null,
            usingBlock = { _: NSNotification? -> orientationState.value = getOrientation() }
        )

        onDispose { notificationCenter.removeObserver(observer) }
    }

}

private fun getOrientation(): Orientation {
    val orientation = platform.UIKit.UIDevice.currentDevice.orientation
    return when (orientation) {
        UIDeviceOrientation.UIDeviceOrientationLandscapeLeft,
        UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> Orientation.Landscape

        else -> Orientation.Portrait
    }
}
