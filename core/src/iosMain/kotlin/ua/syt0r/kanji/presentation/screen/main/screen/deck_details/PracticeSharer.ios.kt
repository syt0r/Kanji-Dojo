package ua.syt0r.kanji.presentation.screen.main.screen.deck_details

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@Composable
actual fun rememberPracticeSharer(snackbarHostState: SnackbarHostState): PracticeSharer {
    return remember { IosPracticeSharer() }
}

class IosPracticeSharer : PracticeSharer {

    override fun share(data: String) {
        val activityViewController = UIActivityViewController(
            activityItems = listOf(data),
            applicationActivities = null
        )

        UIApplication.sharedApplication.keyWindow!!.rootViewController!!.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    }

}