package fmt.kotlin.advanced

import kotlinx.coroutines.flow.Flow
import org.junit.jupiter.api.Test
import kotlin.time.Duration

class Tp2 {
    fun clockFlow(simuClock: () -> SimuClock): Flow<Tick> =
        TODO()

    /**
     * Exécute [simulations] instances [clockFlow] en parallèle, et renvoie la moyenne de leur [Tick.lagPerSecond].
     */
    suspend fun averageLag(clockFlow: Flow<Tick>, simulations: Int): Duration =
        TODO()

    @Test
    fun ex1() {

    }
}
