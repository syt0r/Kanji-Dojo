package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface PracticeConfiguration : Parcelable {

    val shuffle: Boolean

    @Parcelize
    data class Writing(
        val isStudyMode: Boolean,
        override val shuffle: Boolean
    ) : PracticeConfiguration

    @Parcelize
    data class Reading(
        override val shuffle: Boolean
    ) : PracticeConfiguration

}
