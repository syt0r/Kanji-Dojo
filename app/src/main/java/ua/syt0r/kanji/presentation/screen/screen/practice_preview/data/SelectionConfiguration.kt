package ua.syt0r.kanji.presentation.screen.screen.practice_preview.data

import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeMode

data class SelectionConfiguration(
    val practiceMode: PracticeMode,
    val shuffle: Boolean,
    val option: SelectionOption,
    val firstItemsText: String
) {

    companion object {
        val default = SelectionConfiguration(
            practiceMode = WritingPracticeMode.Review,
            shuffle = true,
            option = SelectionOption.FirstItems,
            firstItemsText = "10"
        )
    }

}
