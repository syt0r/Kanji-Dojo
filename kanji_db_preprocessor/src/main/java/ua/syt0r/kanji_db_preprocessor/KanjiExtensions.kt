package ua.syt0r.kanji_db_preprocessor

fun Char.isKanji(): Boolean {
    return when (Character.UnicodeBlock.of(this)) {
        Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS -> true
        else -> false
    }
}