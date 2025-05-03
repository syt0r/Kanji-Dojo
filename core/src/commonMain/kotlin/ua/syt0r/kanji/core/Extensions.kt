package ua.syt0r.kanji.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ua.syt0r.kanji.core.logger.Logger
import kotlin.math.PI
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


fun toRadians(deg: Double): Double = deg / 180.0 * PI

/***
 * iOS has issues with variable arguments, waiting for better kotlin native supports
 */
expect fun String.format(vararg args: String): String
expect fun Float.formatted(precisionDigits: Int): String

expect fun Char.getUnicodeHex(): String

fun <T> Flow<T>.debounceFirst(
    windowDuration: Duration = 500.milliseconds,
): Flow<T> {
    return flow {
        var lastEmitTime = Instant.DISTANT_PAST
        collect {
            val now = Clock.System.now()
            if (now.minus(lastEmitTime) > windowDuration) {
                emit(it)
                lastEmitTime = now
            } else {
                Logger.d("Ignoring quick emit")
            }
        }
    }
}

fun <T> T.runUnit(scope: T.() -> Unit) {
    scope()
}

fun CoroutineScope.launchUnit(block: suspend CoroutineScope.() -> Unit) {
    launch { block() }
}

fun CoroutineScope.launchOnInvoke(block: suspend CoroutineScope.() -> Unit): () -> Unit {
    return { launch { block() } }
}

fun <T> mergeSharedFlows(
    coroutineScope: CoroutineScope,
    vararg flows: Flow<T>,
): SharedFlow<T> {
    val sharedFlow = MutableSharedFlow<T>()
    coroutineScope.launch { merge(*flows).collect { sharedFlow.emit(it) } }
    return sharedFlow
}

suspend fun <T> MutableSharedFlow<T>.emitWhenWithSubscribers(value: T) {
    subscriptionCount.filter { it > 0 }.take(1).collect()
    emit(value)
}

fun <T> MutableSharedFlow<T>.launchWhenHasSubscribers(
    coroutineScope: CoroutineScope,
    block: suspend FlowCollector<T>.() -> Unit
) {
    subscriptionCount.filter { it > 0 }
        .take(1)
        .transform { block() }
        .launchIn(coroutineScope)
}

fun Instant.toLocalDateTime() = toLocalDateTime(TimeZone.currentSystemDefault())
