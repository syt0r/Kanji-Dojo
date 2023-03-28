package ua.syt0r.kanji.core.logger


actual fun platformLogMessage(message: String) {
    println(
        buildString {
            append(createBaseMessage())
            append(" ")
            append(message)
        }
    )
}


actual fun platformLogMethod() {
    println(createBaseMessage())
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