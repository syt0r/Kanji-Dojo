package ua.syt0r.kanji.di

import org.koin.core.module.Module
import ua.syt0r.kanji.core.kanji_data_store.di.kanjiDataStoreModule
import ua.syt0r.kanji.ui.screen.screen.home.di.homeScreenModule
import ua.syt0r.kanji.ui.screen.screen.home.screen.settings.di.settingsScreenModule
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.di.writingDashboardScreenModule
import ua.syt0r.kanji.ui.screen.screen.writing_practice.di.writingPracticeScreenModule

val applicationScopeModules = setOf<Module>(
    kanjiDataStoreModule
)

val screenScopeModules = setOf<Module>(
    homeScreenModule,
    writingDashboardScreenModule,
    settingsScreenModule,
    writingPracticeScreenModule
)