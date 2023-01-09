package ua.syt0r.kanji.core.kanji_data.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ua.syt0r.kanji.common.db.entity.FuriganaDBEntity
import ua.syt0r.kanji.core.kanji_data.db.entity.FuriganaDBField

object FuriganaConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(str: String): FuriganaDBField {
        return FuriganaDBField(
            furigana = gson.fromJson(str, object : TypeToken<List<FuriganaDBEntity>>() {}.type)
        )
    }

    @TypeConverter
    fun toString(field: FuriganaDBField): String {
        return gson.toJson(field.furigana)
    }

}