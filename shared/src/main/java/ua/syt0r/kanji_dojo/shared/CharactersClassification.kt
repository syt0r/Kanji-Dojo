package ua.syt0r.kanji_dojo.shared


interface CharactersClassification {

    enum class Kana : CharactersClassification {
        HIRAGANA,
        KATAKANA
    }

    enum class JLPT : CharactersClassification {
        N5, N4, N3, N2, N1
    }

    enum class Grade : CharactersClassification {
        G1, G2, G3, G4, G5, G6, G8, G9, G10
    }

}