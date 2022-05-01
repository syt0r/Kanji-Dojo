package ua.syt0r.kanji

import kotlinx.coroutines.*
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

            launch {
                repeat(5) {
                    println("ping")
                    delay(50)
                }
            }

            launch {
                println("start")
                val i = withContext(Dispatchers.IO) {
                    Thread.sleep(110)
                    1
                }
                println("end $i")
            }

        }
    }
}