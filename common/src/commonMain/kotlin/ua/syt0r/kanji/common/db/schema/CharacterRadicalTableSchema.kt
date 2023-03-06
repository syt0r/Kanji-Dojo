package ua.syt0r.kanji.common.db.schema

object CharacterRadicalTableSchema {

    const val name = "character_radicals"

    object Columns {
        const val character = "character"
        const val radical = "radical"
        const val startStrokeIndex = "start_stroke"
        const val strokesCount = "strokes_count"
    }

}
