package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ua.syt0r.kanji.core.app_state.AppStateManager
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.core.user_data.model.PracticeType
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.CharacterReviewState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroupItem
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeSummary
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.toReviewState

class PracticePreviewFetchGroupItemsUseCase(
    private val appStateManager: AppStateManager,
    private val kanjiDataRepository: KanjiDataRepository,
    private val practiceRepository: PracticeRepository
) : PracticePreviewScreenContract.FetchGroupItemsUseCase {

    override suspend fun fetch(
        practiceId: Long
    ): List<PracticeGroupItem> {
        val appState = appStateManager.appStateFlow.filter { !it.isLoading }
            .first()
            .lastData!!

        val deckInfo = appState.decks.find { it.id == practiceId }!!
        val timeZone = TimeZone.currentSystemDefault()

        return deckInfo.characters.mapIndexed { index, character ->
            val characterProgress = appState.characterProgresses[character]
            PracticeGroupItem(
                character = character,
                positionInPractice = index,
                frequency = kanjiDataRepository.getData(character)?.frequency,
                writingSummary = PracticeSummary(
                    firstReviewDate = practiceRepository
                        .getFirstReviewTime(character, PracticeType.Writing)
                        ?.toLocalDateTime(timeZone),
                    lastReviewDate = characterProgress?.writingProgress?.lastReviewTime
                        ?.toLocalDateTime(timeZone),
                    state = characterProgress?.writingStatus?.toReviewState()
                        ?: CharacterReviewState.NeverReviewed
                ),
                readingSummary = PracticeSummary(
                    firstReviewDate = practiceRepository
                        .getFirstReviewTime(character, PracticeType.Reading)
                        ?.toLocalDateTime(timeZone),
                    lastReviewDate = characterProgress?.readingProgress?.lastReviewTime
                        ?.toLocalDateTime(timeZone),
                    state = characterProgress?.writingStatus?.toReviewState()
                        ?: CharacterReviewState.NeverReviewed
                )
            )
        }

    }

}