package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.compositionLocalOf
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeConfiguration

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