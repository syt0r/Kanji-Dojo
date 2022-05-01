package ua.syt0r.kanji.core.language

private val hiraganaToRomajiMap = mapOf(
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
    'が' to "ga", 'ぎ' to "gi", 'ぐ' to "gu", 'げ' to "ge", 'ご' to "go",
    'ざ' to "za", 'じ' to "ji", 'ず' to "zu", 'ぜ' to "ze", 'ぞ' to "zo",
    'だ' to "da", 'ぢ' to "ji", 'づ' to "zu", 'で' to "de", 'ど' to "do",
    'ば' to "ba", 'び' to "bi", 'ぶ' to "bu", 'べ' to "be", 'ぼ' to "bo",
    'ぱ' to "pa", 'ぴ' to "pi", 'ぷ' to "pu", 'ぺ' to "pe", 'ぽ' to "po"
)

fun hiraganaToKatakana(hiragana: Char): Char {
    return (hiragana.code + 0x60).toChar()
}

fun katakanaToHiragana(katakana: Char): Char {
    return (katakana.code - 0x60).toChar()
}

fun hiraganaToRomaji(hiragana: Char): String {
    return hiraganaToRomajiMap.getValue(hiragana)
}

fun katakanaToRomaji(katakana: Char): String {
    return hiraganaToRomaji(katakanaToHiragana(katakana))
}

val hiragana: List<Char>
    get() = hiraganaToRomajiMap.keys.toList()

val katakana: List<Char>
    get() = hiragana.map { hiraganaToKatakana(it) }
