package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data

sealed interface PracticeConfiguration {

    val shuffle: Boolean

    data class Writing(
        val isStudyMode: Boolean,
        override val shuffle: Boolean
    ) : PracticeConfiguration

    data class Reading(
        override val shuffle: Boolean
    ) : PracticeConfiguration

}
