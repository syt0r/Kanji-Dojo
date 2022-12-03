package ua.syt0r.kanji.presentation.screen.screen.practice_preview.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PracticeConfiguration(
    val isStudyMode: Boolean,
    val shuffle: Boolean
) : Parcelable