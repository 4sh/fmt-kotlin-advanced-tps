package fmt.kotlin.advanced

import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import kotlin.time.times

interface SimuClock {
    suspend fun nextTick(): Tick
}

class StdSimuClock : SimuClock {
    private var started: Instant? = null
    private var index: Int = 0
    private val simulationPeriod = 100.milliseconds
    private val clock = Clock.System

    override suspend fun nextTick(): Tick {
        if (started == null) {
            started = clock.now()
        }
        delay(simulationPeriod)
        index++
        return Tick(
            index,
            index * simulationPeriod,
            clock.now() - started!!
        )
    }
}
