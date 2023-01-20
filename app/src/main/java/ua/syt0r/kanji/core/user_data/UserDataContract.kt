package ua.syt0r.kanji.core.user_data

import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.core.user_data.model.Practice
import ua.syt0r.kanji.core.user_data.model.ReviewedPractice
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.SortConfiguration
import java.time.LocalDateTime

interface UserDataContract {

    interface PreferencesRepository {

        suspend fun getAnalyticsEnabled(): Boolean
        suspend fun setAnalyticsEnabled(value: Boolean)

        suspend fun getShouldShowAnalyticsSuggestion(): Boolean
        suspend fun setShouldShowAnalyticsSuggestion(value: Boolean)

        suspend fun getNoTranslationsLayoutEnabled(): Boolean
        suspend fun setNoTranslationsLayoutEnabled(value: Boolean)

        suspend fun getSortConfiguration(): SortConfiguration?
        suspend fun setSortConfiguration(configuration: SortConfiguration)

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

        suspend fun getAllPractices(): List<ReviewedPractice>
        suspend fun getPracticeInfo(id: Long): Practice
        suspend fun getKanjiForPractice(id: Long): List<String>

        suspend fun saveReview(
            time: LocalDateTime,
            reviewResultList: List<CharacterReviewResult>,
            isStudyMode: Boolean
        )

        suspend fun getLatestReviewedPractice(): ReviewedPractice?

        suspend fun getCharactersFirstReviewTimestamps(
            practiceId: Long, maxMistakes: Int
        ): Map<String, LocalDateTime>

        suspend fun getCharactersLastReviewTimestamps(
            practiceId: Long, maxMistakes: Int
        ): Map<String, LocalDateTime>

        suspend fun getReviewedCharactersCount(): Long

    }

}