package ua.syt0r.kanji.core.user_data

import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.core.user_data.model.Practice
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.FilterOption
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeType
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.SortOption
import java.time.LocalDateTime

interface UserDataContract {

    interface PreferencesRepository {

        suspend fun getAnalyticsEnabled(): Boolean
        suspend fun setAnalyticsEnabled(value: Boolean)

        suspend fun getShouldShowAnalyticsSuggestion(): Boolean
        suspend fun setShouldShowAnalyticsSuggestion(value: Boolean)

        suspend fun getNoTranslationsLayoutEnabled(): Boolean
        suspend fun setNoTranslationsLayoutEnabled(value: Boolean)

        suspend fun setPracticeType(type: PracticeType)
        suspend fun getPracticeType(): PracticeType?
        suspend fun setFilterOption(filterOption: FilterOption)
        suspend fun getFilterOption(): FilterOption?
        suspend fun getSortOption(): SortOption?
        suspend fun setSortOption(sortOption: SortOption)
        suspend fun setIsSortDescending(isDescending: Boolean)
        suspend fun getIsSortDescending(): Boolean?

        suspend fun getShouldHighlightRadicals(): Boolean
        suspend fun setShouldHighlightRadicals(value: Boolean)

    }

    interface PracticeRepository {

        suspend fun createPractice(title: String, characters: List<String>)
        suspend fun deletePractice(id: Long)
        suspend fun updatePractice(
            id: Long,
            title: String,
            charactersToAdd: List<String>,
            charactersToRemove: List<String>
        )

        fun getAllPractices(): List<Practice>
        fun getPracticeInfo(id: Long): Practice
        fun getLatestWritingReviewTime(practiceId: Long): LocalDateTime?
        fun getLatestReadingReviewTime(practiceId: Long): LocalDateTime?

        suspend fun getKanjiForPractice(id: Long): List<String>

        suspend fun saveWritingReview(
            time: LocalDateTime,
            reviewResultList: List<CharacterReviewResult>,
            isStudyMode: Boolean
        )

        suspend fun saveReadingReview(
            time: LocalDateTime,
            reviewResultList: List<CharacterReviewResult>
        )

        suspend fun getReviewedCharactersCount(): Long

        suspend fun getWritingReviewWithErrors(character: String): Map<LocalDateTime, Int>
        suspend fun getReadingReviewWithErrors(character: String): Map<LocalDateTime, Int>

    }

}