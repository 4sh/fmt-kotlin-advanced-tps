package fmt.kotlin.advanced

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

//
// !!!! NE PAS MODIFIER !!!!
//
interface Simulator {
    suspend fun simulate(clockFlow: Flow<Tick>, collector: SimulationResultsCollector)
}

class StdSimulator(val iterations: Int = 20) : Simulator {
    override suspend fun simulate(clockFlow: Flow<Tick>, collector: SimulationResultsCollector) {
        var lag: Double? = null
        var collected = 0
        var cancellationCause: Exception? = null
        try {
            clockFlow.take(iterations).collect {
                lag = it.lagInMsPerSecond
                collected++
            }
        } catch (e: Exception) {
            cancellationCause = e
        } finally {
            withContext(NonCancellable) {
                collector.collectResult(SimulationResult(lag, collected, cancellationCause))
            }
        }
    }
}

class TimeoutSimulatorDecorator(val simulator: Simulator, val timeout: Duration) : Simulator {
    override suspend fun simulate(clockFlow: Flow<Tick>, collector: SimulationResultsCollector) =
        kotlinx.coroutines.withTimeout(timeout) {
            simulator.simulate(clockFlow, collector)
        }
}

fun Simulator.withTimeout(timeout: Duration = 2500.milliseconds): Simulator =
    TimeoutSimulatorDecorator(this, timeout)
