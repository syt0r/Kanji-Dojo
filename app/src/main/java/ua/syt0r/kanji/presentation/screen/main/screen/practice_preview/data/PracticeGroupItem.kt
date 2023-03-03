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
    val writingSummary: PracticeSummary,
    val readingSummary: PracticeSummary
) : Parcelable {

    companion object {

        @TestOnly
        fun random(
            reviewState: CharacterReviewState = CharacterReviewState.values().random()
        ) = PracticeGroupItem(
            character = PreviewKanji.randomKanji(),
            positionInPractice = Random.nextInt(),
            frequency = Random.nextInt(),
            writingSummary = PracticeSummary(LocalDateTime.now(), LocalDateTime.now(), reviewState),
            readingSummary = PracticeSummary(LocalDateTime.now(), LocalDateTime.now(), reviewState)
        )

    }

}

@Parcelize
data class PracticeSummary(
    val firstReviewDate: LocalDateTime?,
    val lastReviewDate: LocalDateTime?,
    val state: CharacterReviewState
) : Parcelable
