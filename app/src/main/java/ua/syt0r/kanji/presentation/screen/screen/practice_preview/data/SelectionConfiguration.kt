package ua.syt0r.kanji.presentation.screen.screen.practice_preview.data

data class SelectionConfiguration(
    val practiceMode: PracticeMode,
    val shuffle: Boolean,
    val option: SelectionOption,
    val firstItemsText: String
) {

    companion object {
        val default = SelectionConfiguration(
            practiceMode = PracticeMode.Writing,
            shuffle = true,
            option = SelectionOption.FirstItems,
            firstItemsText = "10"
        )
    }

}
