package ua.syt0r.kanji.presentation.screen

interface MainContract {

    interface Navigation {

        fun navigateBack()

        fun navigateToHome()
        fun navigateToAbout()

        fun navigateToWritingDashboard()
        fun navigateToWritingPractice()
        fun navigateToWritingPracticeCreate()
        fun navigateToWritingPracticeImport()
        fun navigateToWritingPracticePreview()

        fun navigateToKanjiInfo(kanji: String)

    }

}