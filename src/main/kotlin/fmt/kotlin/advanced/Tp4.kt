package fmt.kotlin.advanced

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlin.test.Test

class Tp4 {
    fun clockFlow(simuClock: () -> SimuClock) = flow {
        val clock = simuClock()
        while (true) {
            emit(clock.nextTick())
        }
    }

    @Test
    fun simulation_manager_example() {
        val simulator = StdSimulator(iterations = 20).withTimeout()
        val collector = SimulationsCountStats()
        val avgCollector = AvgLagStatsCollector()

        runBlocking(Dispatchers.Default) {
            ChannelBasedSimulationManager(
                batchCount = 100,
                simulationPerBatch = 10,
                simulator = simulator,
                clockFlow = clockFlow { SimuClock.newClock() }
            )
                .simulate {
                    collector.collectResult(it)
                    avgCollector.collectResult(it)
                }
            collector.printStats()
            avgCollector.printStats()
        }
    }

    class FlowBasedSimulationManager(
        val batchCount: Int,
        val simulationPerBatch: Int,
        val simulator: Simulator,
        val clockFlow: Flow<Tick>
    ) : SimulationManager {

        override suspend fun simulate(collector: SimulationResultsCollector) {
            coroutineScope {
                (1..batchCount)
                    TODO()
                    .collect {
                        collector.collectResult(it)
                    }
            }
        }
    }

    @Test
    fun ex1() {
        val simulator = StdSimulator(iterations = 20).withTimeout()
        val collector = SimulationsCountStats()
        val avgCollector = AvgLagStatsCollector()

        runBlocking(Dispatchers.Default) {
            TODO()
            collector.printStats()
            avgCollector.printStats()
        }
    }

}