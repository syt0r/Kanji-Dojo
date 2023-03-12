package ua.syt0r.kanji.presentation.screen.main.screen.practice_create.use_case

import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreenContract

class PracticeCreateDeletePracticeUseCase(
    private val practiceRepository: PracticeRepository
) : PracticeCreateScreenContract.DeletePracticeUseCase {

    override suspend fun delete(practiceId: Long) {
        practiceRepository.deletePractice(practiceId)
    }

}