package ua.syt0r.kanji.core.japanese

import java.lang.Character.UnicodeBlock

val hiraganaReadings = mapOf(
    'あ' to "a", 'い' to "i", 'う' to "u", 'え' to "e", 'お' to "o",
    'か' to "ka", 'き' to "ki", 'く' to "ku", 'け' to "ke", 'こ' to "ko",
    'さ' to "sa", 'し' to "shi", 'す' to "su", 'せ' to "se", 'そ' to "so",
    'た' to "ta", 'ち' to "chi", 'つ' to "tsu", 'て' to "te", 'と' to "to",
    'な' to "na", 'に' to "ni", 'ぬ' to "nu", 'ね' to "ne", 'の' to "no",
    'は' to "ha", 'ひ' to "hi", 'ふ' to "fu", 'へ' to "he", 'ほ' to "ho",
    'ま' to "ma", 'み' to "mi", 'む' to "mu", 'め' to "me", 'も' to "mo",
    'ら' to "ra", 'り' to "ri", 'る' to "ru", 'れ' to "re", 'ろ' to "ro",
    'や' to "ya", 'ゆ' to "yu", 'よ' to "yo",
    'わ' to "wa", 'を' to "wo", 'ん' to "n",
)

val dakutenHiraganaReadings = mapOf(
    'が' to "ga", 'ぎ' to "gi", 'ぐ' to "gu", 'げ' to "ge", 'ご' to "go",
    'ざ' to "za", 'じ' to "zi", 'ず' to "zu", 'ぜ' to "ze", 'ぞ' to "zo",
    'だ' to "da", 'ぢ' to "di", 'づ' to "du", 'で' to "de", 'ど' to "do",
    'ば' to "ba", 'び' to "bi", 'ぶ' to "bu", 'べ' to "be", 'ぼ' to "bo",
    'ぱ' to "pa", 'ぴ' to "pi", 'ぷ' to "pu", 'ぺ' to "pe", 'ぽ' to "po"
)

val smallHiraganaReadings = mapOf(
    'ぁ' to "a",
    'ぃ' to "i",
    'ぅ' to "u",
    'ぇ' to "e",
    'ぉ' to "o",
    'っ' to "tsu",
    'ゃ' to "ya",
    'ゅ' to "yu",
    'ょ' to "yo"
)

val allHiraganaReadings = hiraganaReadings + dakutenHiraganaReadings + smallHiraganaReadings

fun hiraganaToKatakana(hiragana: Char): Char {
    return (hiragana.code + 0x60).toChar()
}

fun katakanaToHiragana(katakana: Char): Char {
    return (katakana.code - 0x60).toChar()
}

fun hiraganaToRomaji(hiragana: Char): String {
    return allHiraganaReadings.getValue(hiragana)
}

fun katakanaToRomaji(katakana: Char): String {
    return hiraganaToRomaji(katakanaToHiragana(katakana))
}

fun getKanaReading(kana: Char): String {
    return if (kana.isHiragana()) hiraganaToRomaji(kana)
    else katakanaToRomaji(kana)
}

fun Char.isKanji(): Boolean = UnicodeBlock.of(this) == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS

fun Char.isHiragana(): Boolean = UnicodeBlock.of(this) == UnicodeBlock.HIRAGANA
fun Char.isKatakana(): Boolean = UnicodeBlock.of(this) == UnicodeBlock.KATAKANA

fun Char.isKana(): Boolean = isHiragana() || isKatakana()