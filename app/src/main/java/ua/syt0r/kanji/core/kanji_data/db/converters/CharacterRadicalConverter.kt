package ua.syt0r.kanji.core.kanji_data.db.converters

import ua.syt0r.kanji.common.db.entity.CharacterRadical
import ua.syt0r.kanji.core.kanji_data.db.entity.CharacterRadicalEntity

object CharacterRadicalConverter {

    fun fromEntity(entity: CharacterRadicalEntity): CharacterRadical = entity.run {
        CharacterRadical(character, radical, startStrokeIndex, strokesCount)
    }

}