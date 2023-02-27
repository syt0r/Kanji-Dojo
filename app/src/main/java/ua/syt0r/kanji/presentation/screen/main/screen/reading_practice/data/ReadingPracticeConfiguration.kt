package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReadingPracticeConfiguration(
    val practiceId: Long,
    val characters: List<String>
) : Parcelable