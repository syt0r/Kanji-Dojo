package ua.syt0r.kanji.core.user_data.db.converter

import androidx.room.TypeConverter
import ua.syt0r.kanji.core.user_data.model.ReviewResult

object ReviewResultConverter {

    @TypeConverter
    fun convert(reviewResult: String): ReviewResult = ReviewResult.valueOf(reviewResult)

    @TypeConverter
    fun convert(reviewResult: ReviewResult): String = reviewResult.toString()


}