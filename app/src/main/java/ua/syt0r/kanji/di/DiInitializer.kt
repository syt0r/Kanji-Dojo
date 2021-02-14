package ua.syt0r.kanji.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

object DiInitializer {

    //TODO load screen scope modules separately
    fun initializeApplication(application: Application) {
        startKoin {
            androidContext(application)
            modules(
                (applicationScopeModules + screenScopeModules).toList()
            )
        }
    }

}