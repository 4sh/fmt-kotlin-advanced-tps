package fmt.kotlin.advanced

//
// !!!! NE PAS MODIFIER !!!!
//
data class SimulationResult(val lagInMsPerSecond: Double?)

//
// !!!! NE PAS MODIFIER !!!!
//

interface SimulationResultsCollector {
    suspend fun collectResult(result: SimulationResult)
}
