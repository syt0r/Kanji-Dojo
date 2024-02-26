package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.transform
import kotlinx.datetime.Clock
import ua.syt0r.kanji.core.logger.Logger
import kotlin.math.max

/***
 * Used to delay rapidly produced state changes for smooth animation
 */
@Composable
fun <T> delayedState(state: State<T>, produceDelay: (old: T, new: T) -> Long): State<T> {
    val stateFlow = remember { MutableStateFlow<T>(state.value) }

    LaunchedEffect(Unit) {

        var lastEmmitInstant = Clock.System.now()

        snapshotFlow { state.value }
            .drop(1)
            .transform {
                val currentInstant = Clock.System.now()
                val millisDifference = currentInstant.toEpochMilliseconds() -
                        lastEmmitInstant.toEpochMilliseconds()
                val delayMillis = max(
                    a = 0,
                    b = produceDelay(stateFlow.value, it) - millisDifference
                )

                Logger.d("delaying state for $delayMillis difference $millisDifference")
                delay(delayMillis)

                lastEmmitInstant = currentInstant
                emit(it)
            }
            .collect { stateFlow.value = it }
    }

    return stateFlow.collectAsState()
}