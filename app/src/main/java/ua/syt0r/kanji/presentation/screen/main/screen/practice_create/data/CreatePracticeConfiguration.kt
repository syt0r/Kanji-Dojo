package ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ua.syt0r.kanji.common.CharactersClassification

sealed class CreatePracticeConfiguration : Parcelable {

    @Parcelize
    object NewPractice : CreatePracticeConfiguration()

    @Parcelize
    data class EditExisting(
        val practiceId: Long
    ) : CreatePracticeConfiguration()

    @Parcelize
    data class Import(
        val title: String,
        val classification: CharactersClassification
    ) : CreatePracticeConfiguration()

}