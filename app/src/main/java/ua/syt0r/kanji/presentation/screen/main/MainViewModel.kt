package ua.syt0r.kanji.presentation.screen.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.syt0r.kanji.presentation.common.parcelableProperty
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeScreenConfiguration
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel(), MainContract.ViewModel {

    override var createPracticeConfiguration by savedStateHandle.parcelableProperty<CreatePracticeConfiguration>()
    override var practiceConfiguration by savedStateHandle.parcelableProperty<PracticeScreenConfiguration>()

}