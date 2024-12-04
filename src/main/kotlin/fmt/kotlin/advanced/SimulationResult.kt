package fmt.kotlin.advanced

import kotlin.time.Duration

//
// !!!! NE PAS MODIFIER !!!!
//
data class SimulationResult(val lagPerSecond: Duration?)

//
// !!!! NE PAS MODIFIER !!!!
//

interface SimulationResultsCollector {
    suspend fun collectResult(result: SimulationResult)
}
