package ua.syt0r.kanji.application

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import ua.syt0r.kanji.di.DiInitializer

@HiltAndroidApp
class KanjiApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FlavorInitializer.initialize(this)
    }

}