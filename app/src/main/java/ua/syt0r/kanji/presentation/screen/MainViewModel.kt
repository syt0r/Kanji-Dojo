package ua.syt0r.kanji.presentation.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeConfiguration
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel(), MainContract.ViewModel {

    override var createPracticeConfiguration: CreatePracticeConfiguration? = null
    override var writingPracticeConfiguration: WritingPracticeConfiguration? = null

}