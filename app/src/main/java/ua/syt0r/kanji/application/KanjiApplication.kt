package ua.syt0r.kanji.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import ua.syt0r.kanji.appModules
import ua.syt0r.kanji.presentation.androidViewModelModule

@HiltAndroidApp
class KanjiApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            loadKoinModules(appModules + androidViewModelModule)
        }
    }

}