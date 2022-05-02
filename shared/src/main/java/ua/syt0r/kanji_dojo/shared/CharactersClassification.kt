package ua.syt0r.kanji_dojo.shared


interface CharactersClassification {

    enum class Kana : CharactersClassification {
        HIRAGANA,
        KATAKANA
    }

    enum class JLPT : CharactersClassification {
        N5, N4, N3, N2, N1
    }

}