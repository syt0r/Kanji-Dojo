package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import ua.syt0r.kanji.core.app_state.AppStateManager
import ua.syt0r.kanji.core.app_state.CharacterProgressStatus
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.CharacterReviewState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroupItem
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeSummary
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.toReviewState

class PracticePreviewFetchGroupItemsUseCase(
    private val appStateManager: AppStateManager,
    private val kanjiDataRepository: KanjiDataRepository
) : PracticePreviewScreenContract.FetchGroupItemsUseCase {

    override suspend fun fetch(
        practiceId: Long
    ): List<PracticeGroupItem> {
        val appState = appStateManager.appStateFlow.filter { !it.isLoading }
            .first()
            .lastData!!

        val deckInfo = appState.decks.find { it.id == practiceId }!!

        return deckInfo.characters.mapIndexed { index, character ->
            val characterProgress = appState.characterProgresses[character]
            PracticeGroupItem(
                character = character,
                positionInPractice = index,
                frequency = kanjiDataRepository.getData(character)?.frequency,
                writingSummary = getSummary(characterProgress?.writingStatus),
                readingSummary = getSummary(characterProgress?.readingStatus)
            )
        }

    }

    private fun getSummary(
        status: CharacterProgressStatus?
    ): PracticeSummary = PracticeSummary(
        firstReviewDate = null, // Todo
        lastReviewDate = null,
        state = status?.toReviewState() ?: CharacterReviewState.NeverReviewed
    )


}