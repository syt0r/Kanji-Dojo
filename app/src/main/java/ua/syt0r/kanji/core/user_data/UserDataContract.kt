package ua.syt0r.kanji.core.user_data

import kotlinx.coroutines.flow.Flow
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.core.user_data.model.Practice
import ua.syt0r.kanji.core.user_data.model.ReviewedPractice
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SelectionConfiguration
import java.time.LocalDateTime

interface UserDataContract {

    interface PreferencesRepository {

        val showAnalyticsDialog: Flow<Boolean>
        suspend fun setShouldShownAnalyticsDialog(shouldShow: Boolean)

        val analyticsEnabled: Flow<Boolean>
        suspend fun setAnalyticsEnabled(enabled: Boolean)

        suspend fun getSelectionConfiguration(): SelectionConfiguration?
        suspend fun setSelectionConfiguration(configuration: SelectionConfiguration)

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

        suspend fun saveReview(reviewResultList: List<CharacterReviewResult>)
        suspend fun getLatestReviewedPractice(): ReviewedPractice?

        suspend fun getCharactersReviewTimestamps(
            practiceId: Long, maxMistakes: Int
        ): Map<String, LocalDateTime>

    }

}