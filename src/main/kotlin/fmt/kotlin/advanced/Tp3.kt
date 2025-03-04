package fmt.kotlin.advanced

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlin.test.Test

class Tp3 {
    fun clockFlow(simuClock: () -> SimuClock) = flow {
        val clock = simuClock()
        while (true) {
            emit(clock.nextTick())
        }
    }

    class BatchSimulator(val batchIndex: Int, val simulationsCount: Int, val simulator: Simulator) {
        suspend fun simulate(clockFlow: Flow<Tick>, collector: SimulationResultsCollector) = coroutineScope {
            simulateIn(clockFlow, CoroutineScope(coroutineContext), collector)
        }

        fun simulateIn(clockFlow: Flow<Tick>, scope: CoroutineScope, collector: SimulationResultsCollector) {
            (1..simulationsCount).map {
                scope.launch(CoroutineName("Batch${batchIndex}")) {
                    simulator.simulate(clockFlow, collector)
                }
            }
        }
    }

    class ThresholdBatchSimulatorDecorator(
        val thresholdInMs: Long, val batchSimulator: BatchSimulator
    ) {
        suspend fun simulate(clockFlow: Flow<Tick>, collector: SimulationResultsCollector) = coroutineScope {
            simulateIn(clockFlow, CoroutineScope(coroutineContext), collector)
        }

        fun simulateIn(clockFlow: Flow<Tick>, scope: CoroutineScope, collector: SimulationResultsCollector) {
            batchSimulator.simulateIn(
                clockFlow.onEach {
                    if (it.absoluteLagInMs > thresholdInMs) {
                        scope.cancel()
                    }
                },
                scope, collector
            )
        }
    }
    fun BatchSimulator.withThreshold(thresholdInMs: Long = 200) =
        ThresholdBatchSimulatorDecorator(thresholdInMs, this)

    @Test
    fun ex1() {
        runBlocking {
            val simulator = StdSimulator(iterations = 20).withTimeout()
            val collector = SimulationsCountStats()

            withContext(Dispatchers.Default) {
                (1..100).map { batchIndex ->
                    BatchSimulator(batchIndex, simulationsCount = 10, simulator)
                }.map { batch ->
                    launch {
                        batch.simulate(clockFlow { SimuClock.newClock() }, collector)
                    }
                }.joinAll()
            }
            collector.printStats()
        }
    }

    @Test
    fun ex2() {
        runBlocking {
            val simulator = StdSimulator(iterations = 20).withTimeout()
            val collector = SimulationsCountStats()

            withContext(Dispatchers.Default) {
                (1..100).map { batchIndex ->
                    BatchSimulator(batchIndex, simulationsCount = 10, simulator)
                        .withThreshold()
                }.map { batch ->
                    launch {
                        batch.simulate(clockFlow { SimuClock.newClock() }, collector)
                    }
                }.joinAll()
            }
            collector.printStats()
        }
    }

    @Test
    fun ex3() {
        runBlocking {
            val simulator = StdSimulator(iterations = 20).withTimeout()
            val collector = SimulationsCountStats()
            val avgCollector = AvgLagStatsCollector()
            val channel = Channel<SimulationResult>()

            val avgCollectorJob = TODO()

            withContext(Dispatchers.Default) {
                (1..100).map { batchIndex ->
                    BatchSimulator(batchIndex, simulationsCount = 10, simulator)
                        .withThreshold()
                }.map { batch ->
                    launch {
                        batch.simulate(clockFlow { SimuClock.newClock() }) {
                            TODO()
                        }
                    }
                }.joinAll()
            }
            while(!channel.isEmpty) {
                delay(5)
            }
            channel.close()
            avgCollectorJob.join()
            collector.printStats()
            avgCollector.printStats()
        }
    }
}