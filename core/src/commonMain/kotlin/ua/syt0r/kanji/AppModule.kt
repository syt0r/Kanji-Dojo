package ua.syt0r.kanji

import org.koin.core.module.Module
import ua.syt0r.kanji.core.coreModule
import ua.syt0r.kanji.presentation.screen.main.screen.about.aboutScreenModule
import ua.syt0r.kanji.presentation.screen.main.screen.home.homeScreenModule
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.practiceDashboardScreenModule
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.searchScreenModule
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.settingsScreenModule
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.practiceCreateScreenModule
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.practiceImportScreenModule
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.practicePreviewScreenModule

val appModules: List<Module>
    get() = listOf(
        coreModule,
        homeScreenModule,
        practiceDashboardScreenModule,
        searchScreenModule,
        settingsScreenModule,
        aboutScreenModule,
        practiceImportScreenModule,
        practiceCreateScreenModule,
        practicePreviewScreenModule,
        platformComponentsModule
    )