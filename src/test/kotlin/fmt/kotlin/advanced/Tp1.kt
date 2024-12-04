package fmt.kotlin.advanced

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class Tp1 {
    @Test
    fun ex1() = runBlocking {
        val simuClock = StdSimuClock()
        repeat(20) {
            println(simuClock.nextTick())
        }
    }
}
