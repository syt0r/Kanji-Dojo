package ua.syt0r.kanji.core.kanji_data.db.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import ua.syt0r.kanji_db_model.db.KanjiReadingTable

@ProvidedTypeConverter
object ReadingTypeConverter {

    @TypeConverter
    fun fromString(str: String): KanjiReadingTable.ReadingType {
        return KanjiReadingTable.ReadingType.values().find { it.value == str }!!
    }

}