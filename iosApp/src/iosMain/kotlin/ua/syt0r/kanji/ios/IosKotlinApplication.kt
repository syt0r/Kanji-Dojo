package ua.syt0r.kanji.ios

import org.koin.core.context.startKoin
import ua.syt0r.kanji.core.japanese.JapaneseUtils
import ua.syt0r.kanji.di.appModules

object IosKotlinApplication {

    class Dependencies(
        val japaneseUtils: JapaneseUtils
    )

    fun initialize(dependencies: Dependencies) {
        JapaneseUtils.init(dependencies.japaneseUtils)

        startKoin {
            val iosAppModules = appModules
            modules(iosAppModules)
        }
    }

}