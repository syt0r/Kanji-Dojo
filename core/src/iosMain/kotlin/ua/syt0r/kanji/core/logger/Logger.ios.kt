package ua.syt0r.kanji.core.logger

actual fun platformLogMessage(message: String) {
    println(message)
}

actual fun platformLogMethod() {
    println("method TODO") // TODO? ios
}