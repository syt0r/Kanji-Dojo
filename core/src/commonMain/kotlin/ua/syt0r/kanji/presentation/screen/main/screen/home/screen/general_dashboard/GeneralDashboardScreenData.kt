package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard

import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.srs.LetterPracticeType
import ua.syt0r.kanji.core.srs.PracticeType
import ua.syt0r.kanji.core.srs.VocabPracticeType
import ua.syt0r.kanji.discord
import ua.syt0r.kanji.practice_type_flashcard
import ua.syt0r.kanji.practice_type_reading_picker
import ua.syt0r.kanji.practice_type_writing
import ua.syt0r.kanji.social_discord
import ua.syt0r.kanji.social_youtube
import ua.syt0r.kanji.study_category_letter
import ua.syt0r.kanji.study_category_vocab
import ua.syt0r.kanji.youtube


sealed interface StudyTargetProgress {

    object NoDecks : StudyTargetProgress

    data class WithDecks(
        val options: StudyTargetPracticeOptions,
        val totalProgress: Float
    ) : StudyTargetProgress

}

sealed interface StudyTargetPracticeOptions {
    val newToDeckIdMap: Map<out Any, Long>
    val dueToDeckIdMap: Map<out Any, Long>
    val combined: Map<out Any, Long>
}

data class LetterStudyTargetPracticeOptions(
    override val newToDeckIdMap: Map<String, Long>,
    override val dueToDeckIdMap: Map<String, Long>
) : StudyTargetPracticeOptions {
    override val combined: Map<String, Long> = newToDeckIdMap + dueToDeckIdMap
}

data class VocabStudyTargetPracticeOptions(
    override val newToDeckIdMap: Map<Long, Long>,
    override val dueToDeckIdMap: Map<Long, Long>
) : StudyTargetPracticeOptions {
    override val combined: Map<Long, Long> = newToDeckIdMap + dueToDeckIdMap
}

data class StudyTargetState(
    val studyTarget: StudyTarget,
    val enabled: Boolean,
    val progress: StudyTargetProgress
)

enum class StudyTarget(
    val categoryTitle: StringResource,
    val typeTitleRes: StringResource,
    val practiceType: PracticeType
) {

    LetterWriting(
        Res.string.study_category_letter,
        Res.string.practice_type_writing,
        LetterPracticeType.Writing
    ),
    LetterFlashcards(
        Res.string.study_category_letter,
        Res.string.practice_type_flashcard,
        LetterPracticeType.Reading
    ),
    VocabFlashcard(
        Res.string.study_category_vocab,
        Res.string.practice_type_flashcard,
        VocabPracticeType.Flashcard
    ),
    VocabWriting(
        Res.string.study_category_vocab,
        Res.string.practice_type_writing,
        VocabPracticeType.Writing
    ),
    VocabReadingPicker(
        Res.string.study_category_vocab,
        Res.string.practice_type_reading_picker,
        VocabPracticeType.ReadingPicker
    )

}

data class StreakCalendarItem(
    val date: LocalDate,
    val anyReviews: Boolean
)

data class GeneralDashboardStats(
    val currentStreak: Int,
    val longestStreak: Int,
    val reviewsToday: Int
)

enum class SocialButton(
    val url: String,
    val title: StringResource,
    val icon: DrawableResource
) {
    Discord(
        url = "https://discord.gg/2Ny6h6pXTY",
        title = Res.string.social_discord,
        icon = Res.drawable.discord
    ),
    YouTube(
        url = "https://youtube.com/@kanji-dojo",
        title = Res.string.social_youtube,
        icon = Res.drawable.youtube
    )
}
