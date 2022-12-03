package ua.syt0r.kanji.presentation.screen.screen.practice_preview.use_case

import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.PracticeGroupItem
import javax.inject.Inject

class PracticePreviewFetchListUseCase @Inject constructor(
    private val userDataRepository: UserDataContract.PracticeRepository,
    private val kanjiDataRepository: KanjiDataRepository
) : PracticePreviewScreenContract.FetchListUseCase {

    override suspend fun fetch(practiceId: Long): List<PracticeGroupItem> {
        val firstTimestamps = userDataRepository.getCharactersFirstReviewTimestamps(
            practiceId = practiceId,
            maxMistakes = Int.MAX_VALUE
        )

        val lastTimestamps = userDataRepository.getCharactersLastReviewTimestamps(
            practiceId = practiceId,
            maxMistakes = Int.MAX_VALUE
        )

        return userDataRepository.getKanjiForPractice(practiceId)
            .mapIndexed { index, character ->
                PracticeGroupItem(
                    character = character,
                    positionInPractice = index,
                    frequency = kanjiDataRepository.getData(character)?.frequency,
                    firstReviewDate = firstTimestamps[character],
                    lastReviewDate = lastTimestamps[character]
                )
            }
    }

}