package ua.syt0r.kanji.presentation.screen

import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeConfiguration

interface MainContract {

    interface Navigation {

        fun navigateBack()

        fun popUpToHome()
        fun navigateToAbout()

        fun navigateToPracticeCreate(configuration: CreatePracticeConfiguration)
        fun navigateToPracticeImport()
        fun navigateToPracticePreview(practiceId: Long, title: String)

        fun navigateToWritingPractice(configuration: WritingPracticeConfiguration)

        fun navigateToKanjiInfo(kanji: String)

    }

    interface ViewModel {
        var createPracticeConfiguration: CreatePracticeConfiguration?
        var writingPracticeConfiguration: WritingPracticeConfiguration?
    }

}