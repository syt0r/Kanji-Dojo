package ua.syt0r.kanji.presentation.screen.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeConfiguration
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel(), MainContract.ViewModel {

    override var createPracticeConfiguration: CreatePracticeConfiguration? = null
    override var writingPracticeConfiguration: WritingPracticeConfiguration? = null

}