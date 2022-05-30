package ua.syt0r.kanji

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.math.min

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test() {
        runBlocking {


            var t = System.currentTimeMillis()
            val minAnimTime = 300L

            val c = Channel<Int>()

            launch(Dispatchers.IO) {
                delay(200)
                (1..3).forEach {
                    println("sending $it")
                    c.send(it)
                    println("sent $it")
                }
            }

            c.consumeAsFlow()
                .distinctUntilChanged()
                .transform {
                    val cT = System.currentTimeMillis()
                    val delayMillis = min(minAnimTime - cT + t, minAnimTime)
                    delay(delayMillis)
                    t = System.currentTimeMillis()
                    emit(it)
                }
                .collect {
                    println("Collecting $it time[${System.currentTimeMillis() % 1000}]")
                }

        }
    }
}