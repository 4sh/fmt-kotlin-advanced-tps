package fmt.kotlin.advanced

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.time.measureTimedValue

class Tp2 {
    fun clockFlow(simuClock: () -> SimuClock): Flow<Tick> =

    suspend fun averageLag(clockFlow: Flow<Tick>, simulations: Int): Double =

    @Test
    fun ex1() {

    }

}