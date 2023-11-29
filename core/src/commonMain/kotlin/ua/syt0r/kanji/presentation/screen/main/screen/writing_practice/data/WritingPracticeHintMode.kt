package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data

import ua.syt0r.kanji.presentation.common.resources.string.StringResolveScope

enum class WritingPracticeHintMode(
    val titleResolver: StringResolveScope<String>,
) {
    OnlyNew(
        titleResolver = { writingPractice.hintStrokeNewOnlyMode }
    ),
    All(
        titleResolver = { writingPractice.hintStrokeAllMode }
    ),
    None(
        titleResolver = { writingPractice.hintStrokeNoneMode }
    ),
}