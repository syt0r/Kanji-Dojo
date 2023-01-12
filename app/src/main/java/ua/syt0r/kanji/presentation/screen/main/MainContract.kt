package ua.syt0r.kanji.presentation.screen.main

import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeConfiguration

interface MainContract {

    interface ViewModel {

        var createPracticeConfiguration: CreatePracticeConfiguration?
        var writingPracticeConfiguration: WritingPracticeConfiguration?

    }

}