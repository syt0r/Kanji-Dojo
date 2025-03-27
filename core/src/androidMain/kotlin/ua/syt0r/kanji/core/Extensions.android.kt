package ua.syt0r.kanji.core

actual fun String.format(vararg args: Any): String = java.lang.String.format(this, args)