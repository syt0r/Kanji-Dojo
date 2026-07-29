package ua.syt0r.kanji.ios

import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ua.syt0r.kanji.PlatformFeature
import ua.syt0r.kanji.core.backup.BackupArchiveHandler
import ua.syt0r.kanji.core.japanese.JapaneseUtils
import ua.syt0r.kanji.core.logger.NativeLogger
import ua.syt0r.kanji.core.tts.KanaTtsManager
import ua.syt0r.kanji.core.tts.KanaVoiceData
import ua.syt0r.kanji.core.tts.WordTtsManager
import ua.syt0r.kanji.core.tts.WordVoiceData
import ua.syt0r.kanji.di.appModules
import ua.syt0r.kanji.presentation.screen.main.features.DeepLinkHandler
import ua.syt0r.kanji.presentation.screen.main.screen.credits.GetCreditLibrariesUseCase
import kotlin.experimental.ExperimentalNativeApi

val iosAppModule = module {
    factory<GetCreditLibrariesUseCase> { IosGetCreditLibrariesUseCase }
}

@OptIn(ExperimentalResourceApi::class, ExperimentalNativeApi::class)
class IosKotlinApplication(
    logger: NativeLogger,
    japaneseUtils: JapaneseUtils,
    kanaTtsManagerProvider: (KanaVoiceData) -> KanaTtsManager,
    wordTtsManagerProvider: (WordVoiceData) -> WordTtsManager,
    backupArchiveHandlerProvider: () -> BaseIosBackupArchiveHandler
) : KoinComponent {

    private val deepLinkHandler: DeepLinkHandler by inject()

    init {

        NativeLogger.instance = logger
        setUnhandledExceptionHook {
            logger.logError(it.message ?: it.stackTraceToString())
            terminateWithUnhandledException(it)
        }

        PlatformFeature.disableSupport()

        JapaneseUtils.init(japaneseUtils)

        val swiftComponentsModule = module {
            single<KanaTtsManager> { kanaTtsManagerProvider(get()) }
            single<WordTtsManager> { wordTtsManagerProvider(get()) }
            single<BackupArchiveHandler> { backupArchiveHandlerProvider() }
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