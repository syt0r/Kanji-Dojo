package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.use_case

import ua.syt0r.kanji.core.time.TimeUtils
import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeContract

class ReadingPracticeSaveResultsUseCase(
    private val practiceRepository: PracticeRepository,
    private val timeUtils: TimeUtils
) : ReadingPracticeContract.SaveResultsUseCase {

    override suspend fun save(
        practiceId: Long,
        summary: ReadingPracticeContract.ScreenState.Summary
    ) {
        val practiceTime = timeUtils.now()
        val items = summary.items.map {
            CharacterReviewResult(
                character = it.character,
                practiceId = practiceId,
                mistakes = it.repeats,
                reviewDuration = it.reviewDuration,
                outcome = it.outcome,
                isStudy = false
            )
        }
        practiceRepository.saveReadingReviews(practiceTime, items)
    }

}
