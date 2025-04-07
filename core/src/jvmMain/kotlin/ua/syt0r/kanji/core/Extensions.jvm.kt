package ua.syt0r.kanji.core

actual fun String.format(vararg args: String): String = java.lang.String.format(this, *args)
actual fun Float.formatted(precisionDigits: Int): String {
    return java.lang.String.format("%.${precisionDigits}f", this)
}

actual fun Char.getUnicodeHex(): String = String.format("U+%04X", code)