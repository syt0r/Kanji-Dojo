package ua.syt0r.kanji.ios

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.interop.LocalUIViewController
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.compose.koinInject
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIDeviceOrientation
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIViewController
import ua.syt0r.kanji.core.theme_manager.ThemeManager
import ua.syt0r.kanji.core.user_data.preferences.PreferencesTheme
import ua.syt0r.kanji.presentation.KanjiDojoApp
import ua.syt0r.kanji.presentation.common.ui.Orientation

@OptIn(ExperimentalForeignApi::class)
fun MainViewController(): UIViewController = ComposeUIViewController {

    val orientationState = remember { mutableStateOf(getOrientation()) }
    UpdateOrientationChanges(orientationState)

    UpdateTheme()

    KanjiDojoApp(
        orientation = orientationState.value
    )

}

@Composable
private fun UpdateOrientationChanges(
    orientationState: MutableState<Orientation>
) {
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

@Composable
private fun UpdateTheme() {
    val controller = LocalUIViewController.current
    val themeManager = koinInject<ThemeManager>()
    LaunchedEffect(Unit) {
        snapshotFlow { themeManager.currentTheme.value }.collect {
            val style = when (it) {
                PreferencesTheme.System -> UIUserInterfaceStyle.UIUserInterfaceStyleUnspecified
                PreferencesTheme.Light -> UIUserInterfaceStyle.UIUserInterfaceStyleLight
                PreferencesTheme.Dark -> UIUserInterfaceStyle.UIUserInterfaceStyleDark
            }
            controller.overrideUserInterfaceStyle = style
            controller.setNeedsStatusBarAppearanceUpdate()
        }
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
