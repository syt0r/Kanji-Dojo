package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.use_case

import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract

class IsEligibleForInAppReviewUseCase(
    private val practiceRepository: PracticeRepository
) : WritingPracticeScreenContract.IsEligibleForInAppReviewUseCase {

    companion object {
        private const val RequiredCharacterReviewsCount = 30L
    }

    override suspend fun check(): Boolean {
        return practiceRepository.getReviewedCharactersCount() >= RequiredCharacterReviewsCount
    }

}