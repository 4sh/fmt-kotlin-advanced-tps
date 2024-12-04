package fmt.kotlin.advanced

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration

//
// !!!! NE PAS MODIFIER !!!!
//
data class SimulationResult(
    val lagInMsPerSecond: Double?,
    val iterations: Int = -1,
    val cancellationCause: Exception? = null)

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
        println(
            "STATS - captured: " + capturedCount.get() + "" +
                    " - cancelled: " + cancelledCount.get() + "" +
                    " - timeout: " + timeoutCount.get() + "" +
                    " - collected: " + collectedCount.get()
        )
    }
}

class AvgLagStatsCollector : SimulationResultsCollector {
    private val clock = Clock.System
    private var startedAt: Instant = clock.now()
    private var lastCollectedAt: Instant? = null
    private var count: Int = 0
    private var totalLagInMsPerSecond: Double = 0.0

    val duration: Duration get() = lastCollectedAt?.let { it - startedAt!! } ?: Duration.ZERO
    val simulationCount get() = count
    val avgLagInMsPerSecond: Double get() = totalLagInMsPerSecond / count.toDouble()

    // not thread safe
    override suspend fun collectResult(result: SimulationResult) {
        delay(5)
        count++
        result.lagInMsPerSecond?.also { totalLagInMsPerSecond += it }
        lastCollectedAt = clock.now()
    }

    fun printStats() {
        println("AVG LAG: $avgLagInMsPerSecond on $simulationCount simulations computed in $duration")
    }
}
