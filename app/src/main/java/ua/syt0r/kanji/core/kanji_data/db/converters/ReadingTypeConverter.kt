package ua.syt0r.kanji.core.kanji_data.db.converters

import androidx.room.TypeConverter
import ua.syt0r.kanji.common.db.schema.KanjiReadingTableSchema

object ReadingTypeConverter {

    @TypeConverter
    fun fromString(str: String): KanjiReadingTableSchema.ReadingType {
        return KanjiReadingTableSchema.ReadingType.values().find { it.value == str }!!
    }

}