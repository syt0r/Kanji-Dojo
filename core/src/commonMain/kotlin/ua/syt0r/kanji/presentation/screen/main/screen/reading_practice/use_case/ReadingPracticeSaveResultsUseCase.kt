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
            CharacterReviewResult(it.character, practiceId, it.repeats)
        }
        practiceRepository.saveReadingReview(practiceTime, items)
    }

}
