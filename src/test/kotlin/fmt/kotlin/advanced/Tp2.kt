package fmt.kotlin.advanced

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTimedValue

class Tp2 {
    fun clockFlow(simuClock: () -> SimuClock) = flow {
        val clock = simuClock()
        while (true) {
            emit(clock.nextTick())
        }
    }

    object NoOpResultsCollector : SimulationResultsCollector {
        override suspend fun collectResult(result: SimulationResult) {
        }
    }

    class SimuClockContextElement(val index: Int) : AbstractCoroutineContextElement(SimuClockContextElement) {
        companion object : CoroutineContext.Key<SimuClockContextElement>
    }

    class ConsoleResultsCollector : SimulationResultsCollector {

        val collectedCount = AtomicInteger()

        override suspend fun collectResult(result: SimulationResult) {
            delay(5.milliseconds)
            println("[${coroutineContext[SimuClockContextElement]?.index}] Retard par seconde : ${result.lagPerSecond}")
            collectedCount.incrementAndGet()
        }
    }

    /**
     * Exécute [simulations] instances [clockFlow] en parallèle, et renvoie la moyenne de leur [Tick.lagPerSecond].
     */
    suspend fun averageLag(
        clockFlow: Flow<Tick>,
        simulations: Int,
        collector: SimulationResultsCollector = NoOpResultsCollector
    ): Duration = coroutineScope {
        val cancelledCount = AtomicInteger()
        (1..simulations).map { index ->
            async(SimuClockContextElement(index)) {
                clockFlow.take(20).last().lagPerSecond
                    .also {
                        collector.collectResult(SimulationResult(it))
                    }
            }
        }
            .awaitAll()
            .average()
            .also {
                println("Annulés : ${cancelledCount.get()}")
            }
    }

    @Test
    fun ex1() = runBlocking {
        val (averageLag, totalTime) = measureTimedValue {
            averageLag(clockFlow { BlockingSimuClock() }, 5)
        }

        println("Retard par seconde : $averageLag pendant $totalTime")
    }

    @Test
    fun ex2_1() = runBlocking(Dispatchers.Default) {
        val (averageLag, totalTime) = measureTimedValue {
            averageLag(clockFlow { BlockingSimuClock() }, 5)
        }

        println("Retard par seconde : $averageLag pendant $totalTime")
    }

    @Test
    fun ex2_2() = runBlocking(Dispatchers.Default) {
        val (averageLag, totalTime) = measureTimedValue {
            averageLag(clockFlow { BlockingSimuClock() }, 300)
        }

        println("Retard par seconde : $averageLag pendant $totalTime")
    }

    @Test
    fun ex2_3() = runBlocking(Dispatchers.Default) {
        val (averageLag, totalTime) = measureTimedValue {
            averageLag(clockFlow { SimuClock.newClock() }, 300)
        }

        println("Retard par seconde : $averageLag pendant $totalTime")
    }

    @Test
    fun ex3() = runBlocking(Dispatchers.Default) {
        val (averageLag, totalTime) = measureTimedValue {
            averageLag(clockFlow { SimuClock.newClock() }, 5, ConsoleResultsCollector())
        }

        println("Retard par seconde : $averageLag pendant $totalTime")
    }

    @Test
    fun ex4() = runBlocking(Dispatchers.Default) {
        val (averageLag, totalTime) = measureTimedValue {
            val collector = ConsoleResultsCollector()
            averageLag(clockFlow { SimuClock.newClock() }, 100, collector)
                .also { println("Collectés : ${collector.collectedCount.get()}") }
        }

        println("Retard par seconde : $averageLag pendant $totalTime")
    }
}
