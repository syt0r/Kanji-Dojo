package ua.syt0r.kanji.presentation.screen.main.screen.feedback

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FeedbackScreenContract {

    interface ViewModel {
        val state: ScreenState
        fun sendFeedback(data: FeedbackScreenSubmitData)
    }

    data class ScreenState(
        val feedbackState: StateFlow<FeedbackState>,
        val errorFlow: Flow<String?>
    )

}