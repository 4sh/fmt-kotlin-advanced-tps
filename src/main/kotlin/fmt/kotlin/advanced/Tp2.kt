package fmt.kotlin.advanced

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlin.test.Test
import kotlin.time.measureTimedValue

class Tp2 {
    fun clockFlow(simuClock: ()->SimuClock) = flow {
        val clock = simuClock()
        while(true) {
            emit(clock.nextTick())
        }
    }

    suspend fun averageLag(clockFlow: Flow<Tick>, simulations: Int) = coroutineScope {
        (1..simulations).map {
            async { clockFlow.take(20).last().lagInMsPerSecond }
        }.awaitAll()
            .average()

    }

    @Test
    fun ex1() {
        runBlocking {
            measureTimedValue {
                averageLag(clockFlow { BlockingSimuClock() }, 5)
            }.also { println(it) }
        }
    }

    @Test
    fun ex2_1() {
        runBlocking(Dispatchers.Default) {
            measureTimedValue {
                averageLag(clockFlow { BlockingSimuClock() }, 5)
            }.also { println(it) }
        }
    }

    @Test
    fun ex2_2() {
        runBlocking(Dispatchers.Default) {
            measureTimedValue {
                averageLag(clockFlow { BlockingSimuClock() }, 300)
            }.also { println(it) }
        }
    }

    @Test
    fun ex2_3() {
        runBlocking(Dispatchers.Default) {
            measureTimedValue {
                averageLag(clockFlow { SimuClock.newClock() }, 300)
            }.also { println(it) }
        }
    }
}