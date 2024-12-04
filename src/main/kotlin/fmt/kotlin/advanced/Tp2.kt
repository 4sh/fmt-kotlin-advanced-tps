package fmt.kotlin.advanced

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.test.Test
import kotlin.time.measureTimedValue

class Tp2 {
    fun clockFlow(simuClock: ()->SimuClock) = flow {
        val clock = simuClock()
        while(true) {
            emit(clock.nextTick())
        }
    }

    object NoOpResultsCollector : SimulationResultsCollector {
        override suspend fun collectResult(result: SimulationResult) {
        }
    }

    class SimuClockContextElement(val index: Int) : AbstractCoroutineContextElement(Key) {
        companion object Key : CoroutineContext.Key<SimuClockContextElement>
    }

    class ConsoleResultsCollector : SimulationResultsCollector {
        override suspend fun collectResult(result: SimulationResult) {
            delay(5)
            println("[${coroutineContext[SimuClockContextElement]?.index}] ${result.lagInMsPerSecond}")
        }
    }

    suspend fun averageLag(clockFlow: Flow<Tick>, simulations: Int, collector: SimulationResultsCollector = NoOpResultsCollector) = coroutineScope {
        (1..simulations).map { index ->
            async(SimuClockContextElement(index)) {
                clockFlow.take(20).last().lagInMsPerSecond
                    .also {
                        collector.collectResult(SimulationResult(it))
                    }
            }
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

    @Test
    fun ex3() {
        runBlocking(Dispatchers.Default) {
            measureTimedValue {
                averageLag(clockFlow { SimuClock.newClock() }, 5, ConsoleResultsCollector())
            }.also { println(it) }
        }
    }
}