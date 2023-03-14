package ua.syt0r.kanji.core

import org.koin.dsl.module
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.analytics.PrintAnalyticsManager
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.core.kanji_data.KanjiDatabaseProvider
import ua.syt0r.kanji.core.kanji_data.SqlDelightKanjiDataRepository
import ua.syt0r.kanji.core.stroke_evaluator.DefaultKanjiStrokeEvaluator
import ua.syt0r.kanji.core.stroke_evaluator.KanjiStrokeEvaluator
import ua.syt0r.kanji.core.time.DefaultTimeUtils
import ua.syt0r.kanji.core.time.TimeUtils
import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.core.user_data.SqlDelightPracticeRepository
import ua.syt0r.kanji.core.user_data.UserDataDatabaseProvider

val coreModule = module {

    single<AnalyticsManager> { PrintAnalyticsManager() }

    single<KanjiDataRepository> {
        val deferredDatabase = get<KanjiDatabaseProvider>().provideAsync()
        SqlDelightKanjiDataRepository(deferredDatabase)
    }

    single<PracticeRepository> {
        val provider = get<UserDataDatabaseProvider>()
        SqlDelightPracticeRepository(
            deferredDatabase = provider.provideAsync()
        )
    }

    factory<TimeUtils> { DefaultTimeUtils }

    factory<KanjiStrokeEvaluator> { DefaultKanjiStrokeEvaluator() }

}