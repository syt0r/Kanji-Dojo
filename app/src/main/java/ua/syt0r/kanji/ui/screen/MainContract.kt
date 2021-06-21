package ua.syt0r.kanji.ui.screen

interface MainContract {

    interface Navigation {

        fun navigateBack()

        fun navigateToHome()
        fun navigateToCreateCustomPracticeSet()
        fun navigateToPracticeSet(practiceSetId: Long)
        fun navigateToWritingPractice(kanjiList: List<String>)
        fun navigateToAbout()
        fun navigateToKanjiInfo(kanji: String)

    }

}