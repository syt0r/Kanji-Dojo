package ua.syt0r.kanji.presentation.screen.main

import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeScreenConfiguration

interface MainContract {

    interface ViewModel {

        var createPracticeConfiguration: CreatePracticeConfiguration?
        var practiceConfiguration: PracticeScreenConfiguration?

    }

}