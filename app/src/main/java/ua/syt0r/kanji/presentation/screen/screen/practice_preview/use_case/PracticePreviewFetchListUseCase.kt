package ua.syt0r.kanji.presentation.screen.screen.practice_preview.use_case

import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.PreviewCharacterData
import javax.inject.Inject

class PracticePreviewFetchListUseCase @Inject constructor(
    private val userDataRepository: UserDataContract.PracticeRepository,
    private val kanjiDataRepository: KanjiDataRepository
) : PracticePreviewScreenContract.FetchListUseCase {

    override suspend fun fetch(practiceId: Long): List<PreviewCharacterData> {
        val characterReviewTimestampsMap = userDataRepository.getCharactersReviewTimestamps(
            practiceId = practiceId,
            maxMistakes = 2
        )
        return userDataRepository.getKanjiForPractice(practiceId)
            .map {
                PreviewCharacterData(
                    character = it,
                    frequency = kanjiDataRepository.getData(it)?.frequency,
                    lastReviewTime = characterReviewTimestampsMap[it]
                )
            }
    }

}