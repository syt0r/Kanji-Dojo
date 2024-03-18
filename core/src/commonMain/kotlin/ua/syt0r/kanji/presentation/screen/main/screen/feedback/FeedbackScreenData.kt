package ua.syt0r.kanji.presentation.screen.main.screen.feedback

enum class FeedbackState { Editing, Sending, Completed }

data class FeedbackScreenSubmitData(
    val topic: String,
    val message: String
)