package fmt.kotlin.advanced

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import org.junit.jupiter.api.Test
import kotlin.time.Duration
import kotlin.time.measureTimedValue

class Tp2 {
    fun clockFlow(simuClock: () -> SimuClock) = flow {
        val clock = simuClock()
        while (true) {
            emit(clock.nextTick())
        }
    }

    /**
     * Exécute [simulations] instances [clockFlow] en parallèle, et renvoie la moyenne de leur [Tick.lagPerSecond].
     */
    suspend fun averageLag(clockFlow: Flow<Tick>, simulations: Int): Duration = coroutineScope {
        (1..simulations).map { index ->
            async {
                clockFlow.take(20).last().lagPerSecond
            }
        }.awaitAll()
            .average()
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
}
