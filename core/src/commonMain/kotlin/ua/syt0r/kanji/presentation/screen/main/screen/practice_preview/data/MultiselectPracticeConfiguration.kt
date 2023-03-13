package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data

class MultiselectPracticeConfiguration(
    val groups: List<PracticeGroup>,
    val selectedGroupIndexes: Set<Int>,
    val selectedItemsCount: Int
)