package fmt.kotlin.advanced

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

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

    class ObservableAvgLagStatsCollector(val avgCollector: AvgLagStatsCollector) : SimulationResultsCollector {
        private val avgMutableFlow = TODO()
        val avgFlow: StateFlow<Duration> = TODO()
        val avgLagPerSecond: Duration /* ???? */ = TODO()

        override suspend fun collectResult(result: SimulationResult) {
            TODO()
        }

        fun printStats() {
            avgCollector.printStats()
        }
    }

    @Test
    fun ex2() {
        val simulator = StdSimulator(iterations = 20).withTimeout()
        val collector = SimulationsCountStats()
        val avgCollector = ObservableAvgLagStatsCollector(AvgLagStatsCollector(1.milliseconds))

        runBlocking(Dispatchers.Default) {
            val printAvgJob = launch(CoroutineName("print_avg_job")) {
                TODO()
            }
            FlowBasedSimulationManager(
                batchCount = 100,
                simulationPerBatch = 50,
                simulator = simulator,
                clockFlow = clockFlow { SimuClock.newClock() }
            )
                .simulate {
                    collector.collectResult(it)
                    avgCollector.collectResult(it)
                }
            printAvgJob.cancel()
            collector.printStats()
            avgCollector.printStats()
        }
    }

}
