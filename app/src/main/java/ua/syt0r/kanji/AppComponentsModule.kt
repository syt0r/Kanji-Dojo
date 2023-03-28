package ua.syt0r.kanji

import org.koin.dsl.module
import ua.syt0r.kanji.core.logger.LoggerConfiguration

val appComponentsModule = module {

    factory { LoggerConfiguration(isEnabled = BuildConfig.DEBUG) }

}