package ua.syt0r.kanji.core.logger

actual object Logger {
    actual fun d(message: String) {
        println(
            buildString {
                append(createBaseMessage())
                append(" ")
                append(message)
            }
        )
    }

    actual fun logMethod() {
        println(createBaseMessage())
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