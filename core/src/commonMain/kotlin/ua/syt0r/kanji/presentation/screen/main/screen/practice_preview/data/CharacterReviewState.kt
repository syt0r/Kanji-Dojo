package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data

import ua.syt0r.kanji.core.app_state.CharacterProgressStatus

enum class CharacterReviewState {
    New,
    Due,
    Done
}

fun CharacterProgressStatus.toReviewState(): CharacterReviewState = when (this) {
    CharacterProgressStatus.New -> CharacterReviewState.New
    CharacterProgressStatus.Done -> CharacterReviewState.Done
    CharacterProgressStatus.Review -> CharacterReviewState.Due
}