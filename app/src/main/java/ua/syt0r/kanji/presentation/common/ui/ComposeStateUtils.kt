package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.runtime.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.transform
import kotlin.math.min

/***
 * Used to delay rapidly produced state changes for smooth animation
 */
@Composable
fun <T> delayedState(state: T, produceDelay: (old: T, new: T) -> Long): State<T> {

    val stateChannel = remember { Channel<T>(Channel.CONFLATED) }

    LaunchedEffect(state) {
        stateChannel.send(state)
    }

    var lastChangeTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }

    return produceState(
        initialValue = state,
        producer = {
            stateChannel.consumeAsFlow()
                .distinctUntilChanged()
                .transform {
                    val minimumDelay = produceDelay(value, it)
                    val delayMillis = min(
                        minimumDelay - (System.currentTimeMillis() - lastChangeTimestamp),
                        minimumDelay
                    )
                    delay(delayMillis)
                    lastChangeTimestamp = System.currentTimeMillis()
                    emit(it)
                }
                .collect { value = it }
        }
    )
}