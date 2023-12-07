package ua.syt0r.kanji.core.review

import ua.syt0r.kanji.core.user_data.PracticeRepository

class ReviewEligibilityUseCase(
    private val practiceRepository: PracticeRepository
) : AppReviewContract.ReviewEligibilityUseCase {

    companion object {
        private const val RequiredCharacterReviewsCount = 30L
    }

    override suspend fun checkIsEligible(): Boolean {
        return practiceRepository.getTotalReviewsCount() >= RequiredCharacterReviewsCount
    }

}