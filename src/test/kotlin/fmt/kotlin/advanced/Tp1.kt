package fmt.kotlin.advanced

import org.junit.jupiter.api.Test

class Tp1 {
    @Test
    fun ex1() {
        val simuClock = StdSimuClock()
        repeat(20) {
            println(simuClock.nextTick())
        }
    }
}
