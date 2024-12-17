package fmt.kotlin.advanced

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
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
                // create flow of batch
                val batchFlow = (1..batchCount)
                    // missing stuff
                    .map {
                        BatchSimulator(it, simulationPerBatch, simulator)
                            .withThreshold()
                    }

                // all batch simmulation must send tick to a unique flow
                val simulationResultFlowFlow = batchFlow
                    .map { batch ->
                       // missing stuff
                            batch.simulate(clockFlow) {
                               // missing stuff
                            }
                       // missing stuff
                    }

                // how compose from Flow of Flow to Flow ?
                val simulationResultFlow = simulationResultFlowFlow
                   // missing stuff

                simulationResultFlow
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

}