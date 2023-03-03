package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case

import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroupItem
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeType
import javax.inject.Inject

class PracticePreviewFetchGroupItemsUseCase @Inject constructor(
    private val userDataRepository: UserDataContract.PracticeRepository,
    private val kanjiDataRepository: KanjiDataRepository,
    private val getPracticeSummaryUseCase: PracticePreviewScreenContract.GetPracticeSummary
) : PracticePreviewScreenContract.FetchGroupItemsUseCase {

    override suspend fun fetch(
        practiceId: Long
    ): List<PracticeGroupItem> {
        return userDataRepository.getKanjiForPractice(practiceId).mapIndexed { index, character ->
            PracticeGroupItem(
                character = character,
                positionInPractice = index,
                frequency = kanjiDataRepository.getData(character)?.frequency,
                writingSummary = getPracticeSummaryUseCase.getSummary(
                    character,
                    PracticeType.Writing
                ),
                readingSummary = getPracticeSummaryUseCase.getSummary(
                    character,
                    PracticeType.Reading
                )
            )
        }
    }

}