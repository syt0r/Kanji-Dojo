package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class PracticeGroup(
    val index: Int,
    val items: List<PracticeGroupItem>,
    val firstDate: LocalDateTime?,
    val lastDate: LocalDateTime?,
    val reviewState: CharacterReviewState
) : Parcelable