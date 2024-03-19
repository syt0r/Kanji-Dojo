package ua.syt0r.kanji.presentation.screen.main.screen.feedback

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.theme.neutralButtonColors
import ua.syt0r.kanji.presentation.common.theme.neutralColors
import ua.syt0r.kanji.presentation.screen.main.screen.feedback.FeedbackScreenContract.ScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreenUI(
    feedbackTopic: FeedbackTopic,
    screenState: ScreenState,
    navigateBack: () -> Unit,
    submitFeedback: (FeedbackScreenSubmitData) -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage: (String?) -> String = { "Error: $it" }

    LaunchedEffect(Unit) {
        screenState.errorFlow.collect {
            snackbarHostState.showSnackbar(
                message = errorMessage(it),
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Feedback") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = {
                    Snackbar(
                        snackbarData = it,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    ) { paddingValues ->

        AnimatedContent(
            targetState = screenState.feedbackState.collectAsState().value,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            contentKey = { it == FeedbackState.Completed }
        ) { feedbackState ->

            if (feedbackState == FeedbackState.Completed) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Feedback sent")
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.extraColorScheme.success
                    )
                }
                LaunchedEffect(Unit) {
                    delay(2000)
                    navigateBack()
                }
                return@AnimatedContent
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .wrapContentWidth()
                    .widthIn(max = 400.dp)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                val topic = feedbackTopic.resolveString()

                TextField(
                    value = topic,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    label = { Text("Topic") },
                    colors = TextFieldDefaults.neutralColors(),
                    enabled = false
                )

                var message by rememberSaveable { mutableStateOf("") }

                TextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    label = { Text("Enter feedback here") },
                    colors = TextFieldDefaults.neutralColors()
                )

                val sendButtonEnabled = message.isNotEmpty() &&
                        feedbackState != FeedbackState.Sending

                Button(
                    onClick = {
                        submitFeedback(
                            FeedbackScreenSubmitData(topic, message)
                        )
                    },
                    enabled = sendButtonEnabled,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.neutralButtonColors()
                ) {
                    Text(
                        text = "Send",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Icon(Icons.Default.Send, null)
                }

            }

        }

    }

}

@Composable
private fun FeedbackTopic.resolveString() = when (this) {
    FeedbackTopic.General -> "General"
    is FeedbackTopic.Expression -> "$screen, expression $id"
}
