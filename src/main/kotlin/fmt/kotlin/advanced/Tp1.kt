package fmt.kotlin.advanced

import kotlin.test.Test

class Tp1 {
    @Test
    fun ex1() {
        val simuClock = StdSimuClock()
        repeat(20) {
            println(simuClock.nextTick())
        }
    }
}