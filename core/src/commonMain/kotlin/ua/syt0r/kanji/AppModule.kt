package ua.syt0r.kanji

import org.koin.core.module.Module
import ua.syt0r.kanji.core.coreModule
import ua.syt0r.kanji.presentation.screen.main.screen.home.homeScreenModule
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.practiceDashboardScreenModule

val appModules: List<Module>
    get() = listOf(
        coreModule,
        homeScreenModule,
        practiceDashboardScreenModule,
        platformComponentsModule
    )