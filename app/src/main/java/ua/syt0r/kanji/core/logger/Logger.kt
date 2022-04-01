package ua.syt0r.kanji.core.logger

import android.util.Log

object Logger {

    private const val TAG = "KanjiDrawer"

    fun d(message: String) {
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