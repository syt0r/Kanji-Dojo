package ua.syt0r.kanji.core.kanji_data.db.converters

import androidx.room.TypeConverter
import ua.syt0r.kanji.common.db.entity.FuriganaDBEntityCreator
import ua.syt0r.kanji.common.db.entity.asJsonString
import ua.syt0r.kanji.core.kanji_data.db.entity.FuriganaDBField
import ua.syt0r.kanji.core.logger.Logger

object FuriganaConverter {

    @TypeConverter
    fun fromString(str: String?): FuriganaDBField? {
        return str?.let {
            FuriganaDBField(
                furigana = FuriganaDBEntityCreator.fromJsonString(it)
            )
        }
    }

    @TypeConverter
    fun toString(field: FuriganaDBField?): String? {
        return field?.furigana?.asJsonString()
    }

}