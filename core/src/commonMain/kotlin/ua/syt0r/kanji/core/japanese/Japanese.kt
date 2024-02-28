package ua.syt0r.kanji.core.japanese

import java.lang.Character.UnicodeBlock

data class KanaInfo(
    val kana: Char,
    val classification: CharacterClassification.Kana,
    val reading: KanaReading
)

data class KanaReading(
    val nihonShiki: String,
    val alternative: List<String>? = null
)

val hiraganaReadings = mapOf(
    'あ' to KanaReading("a"),
    'い' to KanaReading("i"),
    'う' to KanaReading("u"),
    'え' to KanaReading("e"),
    'お' to KanaReading("o"),
    'か' to KanaReading("ka"),
    'き' to KanaReading("ki"),
    'く' to KanaReading("ku"),
    'け' to KanaReading("ke"),
    'こ' to KanaReading("ko"),
    'さ' to KanaReading("sa"),
    'し' to KanaReading("shi"),
    'す' to KanaReading("su"),
    'せ' to KanaReading("se"),
    'そ' to KanaReading("so"),
    'た' to KanaReading("ta"),
    'ち' to KanaReading("chi"), // Not nihon shiki but ti and tu are weird. Update kana
    'つ' to KanaReading("tsu"), // tts timestamp keys if changed if
    'て' to KanaReading("te"),
    'と' to KanaReading("to"),
    'な' to KanaReading("na"),
    'に' to KanaReading("ni"),
    'ぬ' to KanaReading("nu"),
    'ね' to KanaReading("ne"),
    'の' to KanaReading("no"),
    'は' to KanaReading("ha", alternative = listOf("wa")),
    'ひ' to KanaReading("hi"),
    'ふ' to KanaReading("fu"),
    'へ' to KanaReading("he"),
    'ほ' to KanaReading("ho"),
    'ま' to KanaReading("ma"),
    'み' to KanaReading("mi"),
    'む' to KanaReading("mu"),
    'め' to KanaReading("me"),
    'も' to KanaReading("mo"),
    'ら' to KanaReading("ra"),
    'り' to KanaReading("ri"),
    'る' to KanaReading("ru"),
    'れ' to KanaReading("re"),
    'ろ' to KanaReading("ro"),
    'や' to KanaReading("ya"),
    'ゆ' to KanaReading("yu"),
    'よ' to KanaReading("yo"),
    'わ' to KanaReading("wa"),
    'を' to KanaReading("wo"),
    'ん' to KanaReading("n", alternative = listOf("m")),
)

val dakutenHiraganaReadings = mapOf(
    'が' to KanaReading("ga"),
    'ぎ' to KanaReading("gi"),
    'ぐ' to KanaReading("gu"),
    'げ' to KanaReading("ge"),
    'ご' to KanaReading("go"),
    'ざ' to KanaReading("za"),
    'じ' to KanaReading("zi", alternative = listOf("ji")),
    'ず' to KanaReading("zu"),
    'ぜ' to KanaReading("ze"),
    'ぞ' to KanaReading("zo"),
    'だ' to KanaReading("da"),
    'ぢ' to KanaReading("di", alternative = listOf("ji", "zi")),
    'づ' to KanaReading("du", alternative = listOf("zu")),
    'で' to KanaReading("de"),
    'ど' to KanaReading("do"),
    'ば' to KanaReading("ba"),
    'び' to KanaReading("bi"),
    'ぶ' to KanaReading("bu"),
    'べ' to KanaReading("be"),
    'ぼ' to KanaReading("bo"),
    'ぱ' to KanaReading("pa"),
    'ぴ' to KanaReading("pi"),
    'ぷ' to KanaReading("pu"),
    'ぺ' to KanaReading("pe"),
    'ぽ' to KanaReading("po"),
)

val smallHiraganaReadings = mapOf(
    'ぁ' to KanaReading("a"),
    'ぃ' to KanaReading("i"),
    'ぅ' to KanaReading("u"),
    'ぇ' to KanaReading("e"),
    'ぉ' to KanaReading("o"),
    'っ' to KanaReading("tsu"),
    'ゃ' to KanaReading("ya"),
    'ゅ' to KanaReading("yu"),
    'ょ' to KanaReading("yo"),
)

val allHiraganaReadings = hiraganaReadings + dakutenHiraganaReadings + smallHiraganaReadings

fun hiraganaToKatakana(hiragana: Char): Char {
    return (hiragana.code + 0x60).toChar()
}

fun katakanaToHiragana(katakana: Char): Char {
    return (katakana.code - 0x60).toChar()
}

fun getHiraganaReading(hiragana: Char): KanaReading {
    return allHiraganaReadings.getValue(hiragana)
}

fun getKatakanaReading(katakana: Char): KanaReading {
    return getHiraganaReading(katakanaToHiragana(katakana))
}

fun getKanaReading(kana: Char): KanaReading {
    return if (kana.isHiragana()) getHiraganaReading(kana)
    else getKatakanaReading(kana)
}

fun getKanaInfo(kana: Char): KanaInfo {
    return when {
        kana.isHiragana() -> {
            KanaInfo(
                kana = kana,
                classification = CharacterClassification.Kana.Hiragana,
                reading = getHiraganaReading(kana)
            )
        }

        else -> {
            KanaInfo(
                kana = kana,
                classification = CharacterClassification.Kana.Katakana,
                reading = getKatakanaReading(kana)
            )
        }
    }
}


fun Char.isKanji(): Boolean = UnicodeBlock.of(this) == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS

fun Char.isHiragana(): Boolean = UnicodeBlock.of(this) == UnicodeBlock.HIRAGANA
fun Char.isKatakana(): Boolean = UnicodeBlock.of(this) == UnicodeBlock.KATAKANA

fun Char.isKana(): Boolean = isHiragana() || isKatakana()