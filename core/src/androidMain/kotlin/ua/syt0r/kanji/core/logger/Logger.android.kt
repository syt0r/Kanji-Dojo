package ua.syt0r.kanji.core.logger

import android.util.Log

private const val LogTag = "KanjiDrawer"

actual fun platformLogMessage(message: String) {
    Log.d(
        LogTag,
        buildString {
            append(createBaseMessage())
            append(" ")
            append(message)
        }
    )
}

actual fun platformLogMethod() {
    Log.d(LogTag, createBaseMessage())
}

private fun createBaseMessage() = buildString {

    val threadName = Thread.currentThread().name
    val invokingStackTrace = Thread.currentThread().stackTrace[5]

    append(threadName)
    append(" ")
    append(invokingStackTrace.className.run { substring(lastIndexOf(".") + 1) })
    append(":")
    append(invokingStackTrace.methodName)

}