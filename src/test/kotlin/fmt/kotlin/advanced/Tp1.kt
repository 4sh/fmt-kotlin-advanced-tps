package fmt.kotlin.advanced

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTimedValue

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

    @Test
    fun ex3() {
        val clockFlow = flow {
            val simuClock = StdSimuClock()
            while (true) {
                emit(simuClock.nextTick())
            }
        }

        suspend fun simulate(name: String) {
            val lastTick = clockFlow
                .take(20)
                .onEach { println("[$name] $it") }
                .last()
            println("[$name] Retard par seconde: ${lastTick.lagPerSecond}")
        }

        runBlocking {
            launch { simulate("A") }
            delay(1.seconds)
            launch { simulate("B") }
        }
    }

    @Test
    fun ex4() {
        val clockFlow = flow {
            val simuClock = StdSimuClock()
            while (true) {
                emit(simuClock.nextTick())
            }
        }

        suspend fun simulate(name: String) {
            val lastTick = clockFlow
                .take(20)
                .onEach { println("[$name] $it") }
                .last()
            println("[$name] Retard par seconde: ${lastTick.lagPerSecond}")
        }

        runBlocking {
            val first = launch {
                simulate("A")
            }

            val second = launch {
                delay(1.seconds)
                simulate("B")
            }

            first.join()
            second.cancel()
        }
    }

    @Test
    fun ex5() {
        val clockFlow = flow {
            val simuClock = StdSimuClock()
            while (true) {
                emit(simuClock.nextTick())
            }
        }

        suspend fun simulate() =
            clockFlow.take(20).last().lagPerSecond

        runBlocking {
            val (avgLagPerSecond, totalDuration) = measureTimedValue {
                (1..10)
                    .map { async { simulate() } }
                    .awaitAll()
                    .average()
            }

            println("Retard par seconde : $avgLagPerSecond pendant $totalDuration")
        }
    }
}
