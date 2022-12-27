package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.compositionLocalOf
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract

object FlexibleScreens {

    val LocalWritingPracticeScreenContent =
        compositionLocalOf<WritingPracticeScreenContract.ScreenContent> {
            error("Writing practice screen is not set")
        }


}