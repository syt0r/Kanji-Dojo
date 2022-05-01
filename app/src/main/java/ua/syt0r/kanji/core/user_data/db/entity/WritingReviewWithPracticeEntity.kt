package ua.syt0r.kanji.core.user_data.db.entity

import androidx.room.Embedded

class WritingReviewWithPracticeEntity(
    @Embedded val practiceSetEntity: PracticeSetEntity,
    @Embedded val writingReviewEntity: WritingReviewEntity
)