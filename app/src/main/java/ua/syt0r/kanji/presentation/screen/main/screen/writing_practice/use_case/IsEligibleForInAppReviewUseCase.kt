package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.use_case

import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract
import javax.inject.Inject

class IsEligibleForInAppReviewUseCase @Inject constructor(
    private val userDataRepository: UserDataContract.PracticeRepository
) : WritingPracticeScreenContract.IsEligibleForInAppReviewUseCase {

    companion object {
        private const val RequiredCharacterReviewsCount = 30L
    }

    override suspend fun check(): Boolean {
        return userDataRepository.getReviewedCharactersCount() >= RequiredCharacterReviewsCount
    }

}