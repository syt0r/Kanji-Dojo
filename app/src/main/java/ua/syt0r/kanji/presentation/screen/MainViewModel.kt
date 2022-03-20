package ua.syt0r.kanji.presentation.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.PracticeConfiguration
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel(), MainContract.ViewModel {

    override var currentPracticeConfiguration: PracticeConfiguration? = null

}