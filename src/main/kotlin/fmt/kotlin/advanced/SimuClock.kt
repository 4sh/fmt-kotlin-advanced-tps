package fmt.kotlin.advanced

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.milliseconds

interface SimuClock {
    fun nextTick(): Tick
}

class StdSimuClock : SimuClock {
        private var started: Instant? = null
        private var index: Int = 0
        private val simulationPeriod = 100.milliseconds
        private val clock = Clock.System

        override fun nextTick(): Tick {
            if (started == null) {
                started = clock.now()
            }
            Thread.sleep(simulationPeriod.inWholeMilliseconds)
            index++
            return Tick(
                index,
                index * simulationPeriod.inWholeMilliseconds,
                index * simulationPeriod.inWholeMilliseconds,
                (clock.now() - started!!).inWholeMilliseconds
            )
        }
    }
