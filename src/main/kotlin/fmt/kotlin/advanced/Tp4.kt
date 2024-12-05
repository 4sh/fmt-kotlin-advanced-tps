package fmt.kotlin.advanced

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.test.Test
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

        override suspend fun simulate(collector: SimulationResultsCollector) {
            coroutineScope {
                (1..batchCount)
                    .asFlow()
                    .map {
                        BatchSimulator(it, simulationPerBatch, simulator)
                            .withThreshold()
                    }
                    .map { batch ->
                        channelFlow {
                            batch.simulate(clockFlow) {
                                channel.send(it)
                            }
                        }
                    }
                    .flattenMerge(concurrency = batchCount)
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

    class ObservableAvgLagStatsCollector(val avgCollector: AvgLagStatsCollector) : SimulationResultsCollector {
        private val avgMutableFlow = MutableStateFlow<Double>(0.0)
        val avgFlow: StateFlow<Double> = avgMutableFlow.asStateFlow()
        val avgLagInMsPerSecond: Double get() = avgFlow.value

        override suspend fun collectResult(result: SimulationResult) {
            avgCollector.collectResult(result)
            avgMutableFlow.emit(avgCollector.avgLagInMsPerSecond)
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
                avgCollector.avgFlow.buffer().drop(1).conflate().collect {
                    println("AVG: $it")
                    delay(1000)
                }
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