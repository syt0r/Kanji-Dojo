package ua.syt0r.kanji.core.logger

import android.util.Log
import ua.syt0r.kanji.BuildConfig

object Logger {

    private const val TAG = "KanjiDrawer"

    val enabled = BuildConfig.DEBUG

    fun d(message: String) {
        if (enabled)
            Log.d(
                TAG,
                buildString {
                    append(createBaseMessage())
                    append(" ")
                    append(message)
                }
            )
    }

    fun logMethod() {
        if (enabled)
            Log.d(TAG, createBaseMessage())
    }

    private fun createBaseMessage() = buildString {

        val threadName = Thread.currentThread().name
        val invokingStackTrace = Thread.currentThread().stackTrace[4]

        append(threadName)
        append(" ")
        append(invokingStackTrace.className.run { substring(lastIndexOf(".") + 1) })
        append(":")
        append(invokingStackTrace.methodName)

    }

}