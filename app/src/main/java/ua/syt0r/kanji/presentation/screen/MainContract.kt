package ua.syt0r.kanji.presentation.screen

import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.PracticeConfiguration

interface MainContract {

    interface Navigation {

        fun navigateBack()

        fun navigateToHome()
        fun navigateToAbout()

        fun navigateToPracticeCreate()
        fun navigateToPracticeImport()
        fun navigateToPracticePreview(practiceId: Long)

        fun navigateToWritingPractice(config: PracticeConfiguration)

        fun navigateToKanjiInfo(kanji: String)

    }

    interface ViewModel {
        var currentPracticeConfiguration: PracticeConfiguration?
    }

}