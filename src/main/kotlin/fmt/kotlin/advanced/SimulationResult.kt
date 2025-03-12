package fmt.kotlin.advanced

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

//
// !!!! NE PAS MODIFIER !!!!
//
data class SimulationResult(
    val lagPerSecond: Duration?,
    val iterations: Int = -1,
    val cancellationCause: Exception? = null
)

fun interface SimulationResultsCollector {
    suspend fun collectResult(result: SimulationResult)
}

class SimulationsCountStats : SimulationResultsCollector {
    val capturedCount = AtomicInteger()
    val cancelledCount = AtomicInteger()
    val timeoutCount = AtomicInteger()
    val errorCount = AtomicInteger()
    val collectedCount = AtomicInteger()

    override suspend fun collectResult(result: SimulationResult) {
        capturedCount.incrementAndGet()
        when (result.cancellationCause) {
            is TimeoutCancellationException -> {
                timeoutCount.incrementAndGet()
            }

            is CancellationException -> {
                cancelledCount.incrementAndGet()
            }

            is Exception -> {
                errorCount.incrementAndGet()
            }
        }
        if (result.iterations > 0) {
            collectedCount.addAndGet(result.iterations)
        }
    }

    fun printStats() {
        val captured = capturedCount.get()
        val cancelled = cancelledCount.get()
        val timeout = timeoutCount.get()
        val collected = collectedCount.get()

        println("STATS • captured: $captured • cancelled: $cancelled • timeout: $timeout • collected: $collected")
    }
}

class AvgLagStatsCollector(val delay: Duration = 5.milliseconds) : SimulationResultsCollector {
    private val clock = Clock.System
    private var startedAt: Instant = clock.now()
    private var lastCollectedAt: Instant? = null
    private var count: Int = 0
    private var totalLagPerSecond: Duration = 0.milliseconds

    val duration: Duration get() = lastCollectedAt?.let { it - startedAt } ?: Duration.ZERO
    val simulationCount get() = count
    val avgLagPerSecond: Duration get() = totalLagPerSecond / count.toDouble()

    // not thread safe
    override suspend fun collectResult(result: SimulationResult) {
        delay(delay)
        count++
        result.lagPerSecond?.also { totalLagPerSecond += it }
        lastCollectedAt = clock.now()
    }

    fun printStats() {
        println("Retard par seconde moyen : $avgLagPerSecond pendant $simulationCount simulations computed in $duration")
    }
}
