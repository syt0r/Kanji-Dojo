package ua.syt0r.kanji.di

import org.koin.core.module.Module
import ua.syt0r.kanji.core.kanji_data_store.di.kanjiDataStoreModule
import ua.syt0r.kanji.screen.main.sub_screen.home.homeScreenModule

val applicationModules = setOf<Module>(
    kanjiDataStoreModule,
    homeScreenModule
)