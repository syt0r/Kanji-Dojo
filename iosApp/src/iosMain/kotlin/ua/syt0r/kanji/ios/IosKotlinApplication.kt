package ua.syt0r.kanji.ios

import io.ktor.http.decodeURLPart
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ua.syt0r.kanji.PlatformFeature
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.japanese.JapaneseUtils
import ua.syt0r.kanji.core.tts.KanaTtsManager
import ua.syt0r.kanji.core.tts.KanaVoiceData
import ua.syt0r.kanji.core.tts.Neural2BKanaVoiceData
import ua.syt0r.kanji.di.appModules
import ua.syt0r.kanji.presentation.screen.main.features.DeepLinkHandler
import ua.syt0r.kanji.presentation.screen.main.screen.credits.GetCreditLibrariesUseCase

val iosAppModule = module {
    factory<GetCreditLibrariesUseCase> { IosGetCreditLibrariesUseCase }
}

@OptIn(ExperimentalResourceApi::class)
class IosKotlinApplication(
    japaneseUtils: JapaneseUtils,
    kanaTtsManagerProvider: (KanaVoiceData) -> KanaTtsManager
) : KoinComponent {

    private val deepLinkHandler: DeepLinkHandler by inject()

    init {
        PlatformFeature.disableSupport()
        JapaneseUtils.init(japaneseUtils)

        val swiftComponentsModule = module {
            single<KanaTtsManager> {
                val voiceData = Neural2BKanaVoiceData(
                    Res.getUri("files/ja-JP-Neural2-B.wav")
                        .decodeURLPart()
                        .removePrefix("file://")
                )
                kanaTtsManagerProvider(voiceData)
            }
        }

        val koinModules = appModules
            .plus(iosAppModule)
            .plus(swiftComponentsModule)
        startKoin { modules(koinModules) }
    }

    fun notifyDeepLink(url: String) {
        deepLinkHandler.notifyDeepLink(url)
    }

}