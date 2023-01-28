package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.runtime.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.transform

/***
 * Used to delay rapidly produced state changes for smooth animation
 */
@Composable
fun <T> delayedState(state: State<T>, produceDelay: (old: T, new: T) -> Long): State<T> {

    val currentStateChannel = remember { Channel<T>(Channel.BUFFERED) }
    LaunchedEffect(state.value) {
        val updatedState = state.value
        currentStateChannel.send(updatedState)
    }

    val currentState = remember { mutableStateOf(state.value) }

    LaunchedEffect(Unit) {
        currentStateChannel.consumeAsFlow()
            .distinctUntilChanged()
            .transform {
                delay(produceDelay(currentState.value, it))
                emit(it)
            }
            .collect {
                currentState.value = it
            }
    }

    return currentState
}