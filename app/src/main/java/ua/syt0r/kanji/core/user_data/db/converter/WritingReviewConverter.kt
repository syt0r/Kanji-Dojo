package ua.syt0r.kanji.core.user_data.db.converter

import androidx.room.TypeConverter
import ua.syt0r.kanji.core.user_data.db.entity.WritingReviewEntity
import ua.syt0r.kanji.core.user_data.model.KanjiWritingReview

object WritingReviewConverter {

    @TypeConverter
    fun convert(review: KanjiWritingReview) = review.run {
        WritingReviewEntity(kanji, practiceSetId, reviewTime, mistakes)
    }

    @TypeConverter
    fun convert(review: WritingReviewEntity) = review.run {
        KanjiWritingReview(kanji, practiceSetId, reviewTime, mistakes)
    }

}