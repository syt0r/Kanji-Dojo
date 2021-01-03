package ua.syt0r.kanji.core.logger

import android.util.Log

object Logger {

    private const val TAG = "KanjiDrawer"

    fun d(message: String) {
        Log.d(TAG, message)
    }

}