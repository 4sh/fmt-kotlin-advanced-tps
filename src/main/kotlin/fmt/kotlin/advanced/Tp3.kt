package fmt.kotlin.advanced

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.test.Test

class Tp3 {
    fun clockFlow(simuClock: () -> SimuClock) = flow {
        val clock = simuClock()
        while (true) {
            emit(clock.nextTick())
        }
    }

    class BatchSimulator(val batchIndex: Int, val simulationsCount: Int, val simulator: Simulator) {
        suspend fun simulate(clockFlow: Flow<Tick>, collector: SimulationResultsCollector): List<Job> = TODO()
    }

    @Test
    fun ex1() {
        runBlocking {
            val simulator = StdSimulator(iterations = 20).withTimeout()
            val collector = SimulationsCountStats()

            TODO()

            collector.printStats()
        }
    }
}