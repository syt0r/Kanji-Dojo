package ua.syt0r.kanji.parser.model

import ua.syt0r.kanji.common.db.entity.FuriganaDBEntity

data class Word(
    val expressions: List<Expression>,
    val meanings: List<String>
)

sealed class Expression {

    abstract val rank: Int

    data class KanaExpression(
        val reading: String,
        override val rank: Int
    ) : Expression()

    data class KanjiExpression(
        val expression: String,
        val reading: String,
        val furigana: List<FuriganaDBEntity>,
        override val rank: Int
    ) : Expression()

}
