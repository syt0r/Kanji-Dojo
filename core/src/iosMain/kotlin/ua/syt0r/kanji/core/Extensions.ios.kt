package ua.syt0r.kanji.core

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cstr
import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

@OptIn(ExperimentalForeignApi::class)
actual fun String.format(vararg args: Any): String {
    val args = args.map { it.toString().cstr }
    return when (args.size) {
        0 -> NSString.stringWithFormat(this)
        1 -> NSString.stringWithFormat(this, args[0])
        2 -> NSString.stringWithFormat(this, args[0], args[1])
        3 -> NSString.stringWithFormat(this, args[0], args[1], args[2])
        4 -> NSString.stringWithFormat(this, args[0], args[1], args[2], args[3])
        5 -> NSString.stringWithFormat(this, args[0], args[1], args[2], args[3], args[4])
        else -> error("Too many arguments for formatting")
    }
}