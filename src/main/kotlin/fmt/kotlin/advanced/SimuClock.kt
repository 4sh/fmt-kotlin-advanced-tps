package fmt.kotlin.advanced

import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

//
// !!!! NE PAS MODIFIER !!!!
//

interface SimuClock {
    suspend fun nextTick(): Tick

    companion object {
        fun newClock(): SimuClock {
            return when((Math.random() * 100).toInt()) {
                in 0..5 -> SlowSimuClock()
                in 0..10 -> SlowStartSimuClock()
                else -> StdSimuClock()
            }
        }
    }
}

abstract class AbstractSimuClock : SimuClock {
    private var started: Instant? = null
    private var index: Int = 0
    private val simulationPeriod = 100.milliseconds
    private val clock = Clock.System

    override suspend fun nextTick(): Tick {
        if (started == null) {
            prestart()
            started = clock.now()
        }
        simulate(simulationPeriod)
        index++
        return Tick(
            index,
            index * simulationPeriod.inWholeMilliseconds,
            index * simulationPeriod.inWholeMilliseconds,
            (clock.now() - started!!).inWholeMilliseconds
        )
    }

    open suspend fun prestart() {}

    abstract suspend fun simulate(simulationPeriod: Duration)
}

class StdSimuClock : AbstractSimuClock() {
    override suspend fun simulate(simulationPeriod: Duration) {
        delay(simulationPeriod)
    }
}

class BlockingSimuClock : AbstractSimuClock() {
    override suspend fun simulate(simulationPeriod: Duration) {
        Thread.sleep(simulationPeriod.inWholeMilliseconds)
    }
}

class SlowSimuClock : AbstractSimuClock() {
    override suspend fun simulate(simulationPeriod: Duration) {
        delay(simulationPeriod)
        delay((Math.random() * 10).toLong())
        if (Math.random() < 0.01) {
            delay(200.milliseconds)
        }
    }
}

class SlowStartSimuClock : AbstractSimuClock() {
    override suspend fun prestart() {
        delay(500.milliseconds)
    }

    override suspend fun simulate(simulationPeriod: Duration) {
        delay(simulationPeriod)
    }
}
