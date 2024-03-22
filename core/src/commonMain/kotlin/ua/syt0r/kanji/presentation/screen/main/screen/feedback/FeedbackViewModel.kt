package ua.syt0r.kanji.presentation.screen.main.screen.feedback

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.feedback.FeedbackManager
import ua.syt0r.kanji.core.feedback.FeedbackRequestData
import ua.syt0r.kanji.presentation.screen.main.screen.feedback.FeedbackScreenContract.ScreenState

class FeedbackViewModel(
    private val viewModelScope: CoroutineScope,
    private val feedbackManager: FeedbackManager
) : FeedbackScreenContract.ViewModel {

    private val feedbackState = MutableStateFlow<FeedbackState>(FeedbackState.Editing)
    private val errorChannel = Channel<String?>()

    override val state: ScreenState = ScreenState(
        feedbackState = feedbackState,
        errorFlow = errorChannel.consumeAsFlow()
    )

    override fun sendFeedback(data: FeedbackScreenSubmitData) {
        feedbackState.value = FeedbackState.Sending
        viewModelScope.launch {
            val requestData = FeedbackRequestData(data.topic, data.message)
            val result = feedbackManager.sendFeedback(requestData)

            if (result.isSuccess) {
                feedbackState.value = FeedbackState.Completed
            } else {
                errorChannel.send(result.exceptionOrNull()?.message)
                feedbackState.value = FeedbackState.Editing
            }
        }
    }

}
