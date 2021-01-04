package ua.syt0r.kanji.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

object DiInitializer {

    fun initialize(application: Application) {
        startKoin {
            androidContext(application)
            modules(applicationModules.toList())
        }
    }

}