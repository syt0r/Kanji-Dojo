package ua.syt0r.kanji.presentation

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.theme_manager.ThemeManager
import ua.syt0r.kanji.presentation.common.ui.Orientation
import ua.syt0r.kanji.presentation.screen.main.features.DeepLinkHandler

open class KanjiDojoActivity : AppCompatActivity() {

    private val deepLinkHandler by inject<DeepLinkHandler>()
    private val themeManager by inject<ThemeManager>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        themeManager.invalidate()

        Logger.d("intentData[${intent.dataString}]")
        intent.dataString?.let { deepLinkHandler.notifyDeepLink(it) }

        val orientation = when (baseContext.resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> Orientation.Portrait
            else -> Orientation.Landscape
        }

        setContent {
            KanjiDojoApp(
                orientation = orientation,
                deepLinkHandler = deepLinkHandler
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Logger.d("intentData[${intent.dataString}]")
        intent.dataString?.let { deepLinkHandler.notifyDeepLink(it) }
    }

}