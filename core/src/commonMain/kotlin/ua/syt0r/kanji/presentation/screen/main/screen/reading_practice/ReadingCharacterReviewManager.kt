package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice

import kotlinx.coroutines.CoroutineScope
import ua.syt0r.kanji.core.time.TimeUtils
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.BaseCharacterReviewManager
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.CharacterReviewData

enum class ReadingCharacterReviewHistory { Repeat }

typealias ReadingCharacterReviewData = CharacterReviewData<ReadingCharacterReviewHistory, ReadingReviewCharacterData>

class ReadingCharacterReviewManager(
    reviewItems: List<ReadingCharacterReviewData>,
    coroutineScope: CoroutineScope,
    timeUtils: TimeUtils,
    onCompletedCallback: () -> Unit
) : BaseCharacterReviewManager<ReadingCharacterReviewHistory, ReadingReviewCharacterData, ReadingPracticeCharacterSummaryDetails>(
    reviewItems,
    coroutineScope,
    timeUtils,
    onCompletedCallback
) {

    override fun getCharacterSummary(
        history: List<ReadingCharacterReviewHistory>,
        characterData: ReadingReviewCharacterData
    ): ReadingPracticeCharacterSummaryDetails {
        return ReadingPracticeCharacterSummaryDetails(history.size)
    }

    override fun isRepeat(
        data: ReadingCharacterReviewData
    ): Boolean {
        return data.history.isNotEmpty()
    }

}