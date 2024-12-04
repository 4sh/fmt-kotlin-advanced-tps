package fmt.kotlin.advanced

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
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

    @Test
    fun ex2() = runBlocking {
        val clockFlow = flow {
            val simuClock = StdSimuClock()
            while (true) {
                emit(simuClock.nextTick())
            }
        }

        val lastTick = clockFlow
            .take(20)
            .onEach { println(it) }
            .last()
        println("Retard par seconde: ${lastTick.lagPerSecond}")
    }
}
