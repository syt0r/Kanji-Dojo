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
) : Parcelable {

    companion object {

        fun random(
            index: Int,
            needReviewOnly: Boolean = false
        ): PracticeGroup {
            val items = (1..6).map {
                if (needReviewOnly) PracticeGroupItem.random(CharacterReviewState.NeedReview)
                else PracticeGroupItem.random()
            }
            return PracticeGroup(
                index = index,
                items = items,
                firstDate = LocalDateTime.now(),
                lastDate = LocalDateTime.now(),
                reviewState = items.random().reviewState
            )
        }

    }

}