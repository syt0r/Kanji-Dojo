package ua.syt0r.kanji.presentation.screen.main.screen.feedback

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import ua.syt0r.kanji.presentation.screen.main.screen.feedback.FeedbackScreenContract.ScreenState

class FeedbackViewModel(
    private val viewModelScope: CoroutineScope
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
            delay(1000)
            runCatching {
                throw IllegalStateException("Test")
                feedbackState.value = FeedbackState.Completed
            }.getOrElse {
                errorChannel.send(it.message)
                feedbackState.value = FeedbackState.Editing
            }
        }
    }

}
