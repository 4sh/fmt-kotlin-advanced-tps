package fmt.kotlin.advanced

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
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

    @Test
    fun ex2() {
        runBlocking {
            val clockFlow = flow {
                val simuClock = StdSimuClock()
                while(true) {
                    emit(simuClock.nextTick())
                }
            }

            var lastTick: Tick? = null
            clockFlow
                .take(20)
                .onEach { lastTick = it }
                .collect { println(it) }
            println("avg: ${lastTick?.lagInMsPerSecond}")
        }
    }
}