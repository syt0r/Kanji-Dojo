package ua.syt0r.kanji.core.japanese

private lateinit var instance: JapaneseUtils

interface JapaneseUtils {

    fun String.kanaToRomaji(): String
    fun Char.isKanji(): Boolean
    fun Char.isHiragana(): Boolean
    fun Char.isKatakana(): Boolean
    fun Char.isKana(): Boolean

    companion object {

        fun init(utils: JapaneseUtils) {
            instance = utils
        }

    }

}

actual fun String.kanaToRomaji(): String = with(instance) { kanaToRomaji() }
actual fun Char.isKanji(): Boolean = with(instance) { isKanji() }
actual fun Char.isHiragana(): Boolean = with(instance) { isHiragana() }
actual fun Char.isKatakana(): Boolean = with(instance) { isKatakana() }
actual fun Char.isKana(): Boolean = with(instance) { isKana() }
