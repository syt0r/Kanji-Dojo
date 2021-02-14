package ua.syt0r.kanji.application

import android.app.Application
import ua.syt0r.kanji.di.DiInitializer

class KanjiApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DiInitializer.initializeApplication(this)
    }

}