package ua.syt0r.kanji.core.kanji_data.db.converters

import androidx.room.TypeConverter

class CharConverter {

    @TypeConverter
    fun fromString(s: String): Char = s[0]

    @TypeConverter
    fun fromChar(c: Char): String = c.toString()

}