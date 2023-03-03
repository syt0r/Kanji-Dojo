package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface PracticeScreenConfiguration : Parcelable {

    @Parcelize
    data class Writing(
        val practiceId: Long,
        val characterList: List<String>,
        val isStudyMode: Boolean
    ) : PracticeScreenConfiguration

    @Parcelize
    data class Reading(
        val practiceId: Long,
        val characterList: List<String>
    ) : PracticeScreenConfiguration


}