package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class WritingPracticeConfiguration(
    val practiceId: Long,
    val characterList: List<String>,
    val isStudyMode: Boolean
) : Parcelable