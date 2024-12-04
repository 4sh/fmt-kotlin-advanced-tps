package fmt.kotlin.advanced

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
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

    @Test
    fun ex3() {
        val clockFlow = flow {
            val simuClock = StdSimuClock()
            while(true) {
                emit(simuClock.nextTick())
            }
        }

        suspend fun simulate(name: String) {
            var lastTick: Tick? = null
            clockFlow
                .take(20)
                .onEach { lastTick = it }
                .collect { println("[$name] $it") }
            println("[$name] avg: ${lastTick?.lagInMsPerSecond}")
        }

        runBlocking {
            launch { simulate("A") }
            delay(1000)
            launch { simulate("B") }
        }
    }

    @Test
    fun ex4() {
        val clockFlow = flow {
            val simuClock = StdSimuClock()
            while(true) {
                emit(simuClock.nextTick())
            }
        }

        suspend fun simulate(name: String) {
            var lastTick: Tick? = null
            clockFlow
                .take(20)
                .onEach { lastTick = it }
                .collect { println("[$name] $it") }
            println("[$name] avg: ${lastTick?.lagInMsPerSecond}")
        }

        runBlocking {
            val first = launch { simulate("A") }
            delay(1000)
            val second = launch { simulate("B") }
            first.join()
            second.cancel()
        }
    }
}