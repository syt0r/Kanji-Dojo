package ua.syt0r.kanji.core.kanji_data.db.converters

import androidx.room.TypeConverter
import ua.syt0r.kanji.common.CharactersClassification

object CharacterClassificationConverter {

    @TypeConverter
    fun fromString(string: String): CharactersClassification {
        return CharactersClassification.fromString(string)
    }

    @TypeConverter
    fun toString(c: CharactersClassification): String = c.toString()

}