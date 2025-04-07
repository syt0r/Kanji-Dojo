package ua.syt0r.kanji.core

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cstr
import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

@OptIn(ExperimentalForeignApi::class)
actual fun String.format(vararg args: String): String {
    fun argVal(pos: Int) = args[pos].cstr
    return when (args.size) {
        0 -> NSString.stringWithFormat(this)
        1 -> NSString.stringWithFormat(this, argVal(0))
        2 -> NSString.stringWithFormat(this, argVal(0), argVal(1))
        3 -> NSString.stringWithFormat(this, argVal(0), argVal(1), argVal(2))
        4 -> NSString.stringWithFormat(this, argVal(0), argVal(1), argVal(2), argVal(3))
        5 -> NSString.stringWithFormat(this, argVal(0), argVal(1), argVal(2), argVal(3), argVal(4))
        else -> error("Too many arguments for formatting")
    }
}

actual fun Float.formatted(precisionDigits: Int): String {
    return NSString.stringWithFormat("%.${precisionDigits}f", this)
}

@OptIn(ExperimentalStdlibApi::class)
private val herFormat = HexFormat {
    number.prefix = "U+"
    number.removeLeadingZeros = true
    number.minLength = 4
    upperCase = true
}

@OptIn(ExperimentalStdlibApi::class)
actual fun Char.getUnicodeHex(): String {
    return code.toHexString(herFormat)
}