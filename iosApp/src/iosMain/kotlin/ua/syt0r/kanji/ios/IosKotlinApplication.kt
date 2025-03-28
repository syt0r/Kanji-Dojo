package ua.syt0r.kanji.ios

import org.koin.core.context.startKoin
import ua.syt0r.kanji.core.japanese.JapaneseUtils
import ua.syt0r.kanji.di.appModules
import ua.syt0r.kanji.presentation.screen.main.features.DeepLinkHandler

class IosKotlinApplication(
    japaneseUtils: JapaneseUtils
) {

    private val deepLinkHandler: DeepLinkHandler

    init {
        JapaneseUtils.init(japaneseUtils)

        val koinApplication = startKoin {
            val iosAppModules = appModules
            modules(iosAppModules)
        }

        deepLinkHandler = koinApplication.koin.get<DeepLinkHandler>()
    }

    fun notifyDeepLink(url: String) {
        deepLinkHandler.notifyDeepLink(url)
    }

}