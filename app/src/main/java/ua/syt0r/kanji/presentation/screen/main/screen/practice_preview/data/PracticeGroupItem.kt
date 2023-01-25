package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.jetbrains.annotations.TestOnly
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import java.time.LocalDateTime
import kotlin.random.Random

@Parcelize
data class PracticeGroupItem(
    val character: String,
    val positionInPractice: Int,
    val frequency: Int?,
    val firstReviewDate: LocalDateTime?,
    val lastReviewDate: LocalDateTime?,
    val reviewState: CharacterReviewState
) : Parcelable {

    companion object {

        @TestOnly
        fun random() = PracticeGroupItem(
            character = PreviewKanji.randomKanji(),
            positionInPractice = Random.nextInt(),
            frequency = Random.nextInt(),
            firstReviewDate = LocalDateTime.now(),
            lastReviewDate = LocalDateTime.now(),
            reviewState = CharacterReviewState.values().random()
        )

    }

}

