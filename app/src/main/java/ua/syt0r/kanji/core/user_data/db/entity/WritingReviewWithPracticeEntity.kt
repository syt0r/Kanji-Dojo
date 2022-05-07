package ua.syt0r.kanji.core.user_data.db.entity

import androidx.room.Embedded

class WritingReviewWithPracticeEntity(
    @Embedded val practiceEntity: PracticeEntity,
    @Embedded val writingReviewEntity: WritingReviewEntity
)