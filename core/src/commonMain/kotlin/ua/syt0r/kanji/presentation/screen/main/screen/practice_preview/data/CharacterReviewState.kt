package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data

import ua.syt0r.kanji.core.app_state.CharacterProgressStatus

enum class CharacterReviewState {
    NeverReviewed,
    NeedReview,
    RecentlyReviewed
}

fun CharacterProgressStatus.toReviewState(): CharacterReviewState = when (this) {
    CharacterProgressStatus.New -> CharacterReviewState.NeverReviewed
    CharacterProgressStatus.Done -> CharacterReviewState.RecentlyReviewed
    CharacterProgressStatus.Review -> CharacterReviewState.NeedReview
}