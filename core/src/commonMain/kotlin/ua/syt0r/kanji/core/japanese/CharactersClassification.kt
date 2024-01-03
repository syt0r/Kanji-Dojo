package ua.syt0r.kanji.core.japanese

import kotlinx.serialization.Serializable

@Serializable
sealed interface CharactersClassification {

    companion object {

        private const val HiraganaPrefix = "h"
        private const val KatakanaPrefix = "k"
        private const val JLPTPrefix = "n"
        private const val GradePrefix = "g"
        private const val WanikaniPrefix = "w"

        fun fromString(string: String): CharactersClassification {
            return when {
                string.startsWith(HiraganaPrefix) -> Kana.Hiragana
                string.startsWith(KatakanaPrefix) -> Kana.Katakana
                string.startsWith(JLPTPrefix) -> JLPT.fromString(string)
                string.startsWith(GradePrefix) -> Grade.fromString(string)
                string.startsWith(WanikaniPrefix) -> Wanikani.fromString(string)
                else -> throw IllegalStateException("Unsupported classification $string")
            }
        }

    }

    @Serializable
    sealed interface Kana : CharactersClassification {

        @Serializable
        object Hiragana : Kana {
            override fun toString(): String = HiraganaPrefix
        }

        @Serializable
        object Katakana : Kana {
            override fun toString(): String = KatakanaPrefix
        }
    }

    @Serializable
    data class JLPT(
        val level: Int
    ) : CharactersClassification {

        companion object {

            val all: List<JLPT>
                get() = (5 downTo 1).map { JLPT(it) }

            fun fromString(string: String): JLPT {
                return JLPT(level = string.drop(JLPTPrefix.length).toInt())
            }

        }

        init {
            require(level in 1..5)
        }

        override fun toString(): String {
            return "$JLPTPrefix$level"
        }

    }

    @Serializable
    data class Grade(
        val number: Int
    ) : CharactersClassification {

        companion object {

            val all: List<Grade>
                get() = (1..6).plus(8..10).map { Grade(it) }

            fun fromString(string: String): Grade {
                return Grade(number = string.drop(GradePrefix.length).toInt())
            }

        }

        init {
            require(number in 1..6 || number in 8..10)
        }

        override fun toString(): String {
            return "$GradePrefix$number"
        }

    }

    @Serializable
    data class Wanikani(
        val level: Int
    ) : CharactersClassification {

        companion object {

            val all: List<Wanikani>
                get() = (1..60).map { Wanikani(it) }

            fun fromString(string: String): Wanikani {
                return Wanikani(level = string.drop(WanikaniPrefix.length).toInt())
            }

        }

        init {
            require(level in 1..60)
        }

        override fun toString(): String {
            return "$WanikaniPrefix$level"
        }

    }

}