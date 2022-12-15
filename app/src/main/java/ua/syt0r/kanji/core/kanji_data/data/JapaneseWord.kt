package ua.syt0r.kanji.core.kanji_data.data

data class JapaneseWord(
    val text: List<FuriganaAnnotatedCharacter>,
    val meanings: List<String>
)