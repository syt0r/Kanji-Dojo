package ua.syt0r.kanji

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test() {

        runBlocking {

            val c = Channel<Int>(Channel.BUFFERED)

            coroutineScope {

                launch {
                    repeat(5) {
                        c.send(System.currentTimeMillis().toInt())
                        delay(5)
                    }
                    coroutineContext.cancel(CancellationException("done"))
                }


                c.consumeAsFlow()
                    .collectLatest {
                        println("start $it")
                        val a = runInterruptible(Dispatchers.IO){
                            Thread.sleep(600)
                            println("finish calc $it")
                        }
                        println("finish $it")
                    }

            }

        }

    }
}