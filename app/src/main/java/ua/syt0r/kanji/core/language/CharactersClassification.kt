package ua.syt0r.kanji.core.language


interface CharactersClassification {

    enum class Kana : CharactersClassification {
        HIRAGANA,
        KATAKANA
    }

    enum class JLPT(
        val level: Int
    ) : CharactersClassification {
        N5(5), N4(4), N3(3), N2(2), N1(1)
    }

}