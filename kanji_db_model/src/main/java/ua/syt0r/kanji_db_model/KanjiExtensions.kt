package ua.syt0r.kanji_db_model

import java.lang.Character.UnicodeBlock

fun Char.isKanji(): Boolean = UnicodeBlock.of(this) == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS

fun Char.isHiragana(): Boolean = UnicodeBlock.of(this) == UnicodeBlock.HIRAGANA
fun Char.isKatakana(): Boolean = UnicodeBlock.of(this) == UnicodeBlock.KATAKANA

fun Char.isKana(): Boolean = isHiragana() || isKatakana()