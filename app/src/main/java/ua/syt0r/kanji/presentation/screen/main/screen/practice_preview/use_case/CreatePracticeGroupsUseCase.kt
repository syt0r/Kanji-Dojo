package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case

import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.CharacterReviewState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroup
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroupItem
import java.time.LocalDateTime
import javax.inject.Inject

class CreatePracticeGroupsUseCase @Inject constructor() :
    PracticePreviewScreenContract.CreatePracticeGroupsUseCase {

    companion object {
        private const val CharactersInGroup = 6
    }

    override fun create(characterList: List<PracticeGroupItem>): List<PracticeGroup> {
        return characterList.chunked(CharactersInGroup)
            .mapIndexed { index, groupItems ->
                PracticeGroup(
                    index = index + 1,
                    items = groupItems,
                    firstDate = groupItems
                        .minByOrNull { it.firstReviewDate ?: LocalDateTime.MAX }
                        ?.firstReviewDate,
                    lastDate = groupItems
                        .maxByOrNull { it.lastReviewDate ?: LocalDateTime.MIN }
                        ?.lastReviewDate,
                    reviewState = when {
                        groupItems.all { it.reviewState == CharacterReviewState.RecentlyReviewed } -> CharacterReviewState.RecentlyReviewed
                        groupItems.any { it.reviewState == CharacterReviewState.NeedReview } -> CharacterReviewState.NeedReview
                        else -> CharacterReviewState.NeverReviewed
                    }
                )
            }
    }

}
