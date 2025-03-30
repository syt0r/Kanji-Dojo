package ua.syt0r.kanji.core

actual fun String.format(vararg args: Any): String = java.lang.String.format(this, args)
actual fun Char.getUnicodeHex(): String = String.format("U+%04X", code)