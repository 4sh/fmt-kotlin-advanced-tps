package fmt.kotlin.advanced

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

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

        @OptIn(ExperimentalCoroutinesApi::class)
        override suspend fun simulate(collector: SimulationResultsCollector) {
            coroutineScope {
                (1..batchCount)
                    .asFlow()
                    .map {
                        BatchSimulator(it, simulationPerBatch, simulator)
                            .withThreshold()
                    }
                    .flatMapMerge(concurrency = simulationPerBatch) { batchSimulator ->
                        channelFlow {
                            batchSimulator.simulate(clockFlow) {
                                send(it)
                            }
                        }
                    }
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
            FlowBasedSimulationManager(
                batchCount = 100,
                simulationPerBatch = 10,
                simulator = simulator,
                clockFlow = clockFlow { SimuClock.newClock() },
            ).simulate {
                collector.collectResult(it)
                avgCollector.collectResult(it)
            }
            collector.printStats()
            avgCollector.printStats()
        }
    }

}
