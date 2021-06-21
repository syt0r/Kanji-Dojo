package ua.syt0r.kanji.application

import android.app.Application
import com.google.android.gms.ads.MobileAds

object FlavorInitializer {

    fun initialize(application: Application) {
        MobileAds.initialize(application)
    }

}