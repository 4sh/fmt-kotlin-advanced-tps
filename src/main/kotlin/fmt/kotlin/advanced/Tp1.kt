package fmt.kotlin.advanced

import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class Tp1 {
    @Test
    fun ex1() {
        runBlocking {
            val simuClock = StdSimuClock()
            repeat(20) {
                println(simuClock.nextTick())
            }
        }
    }
}