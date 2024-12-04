package fmt.kotlin.advanced

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

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
