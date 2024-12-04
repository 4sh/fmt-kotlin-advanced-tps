package fmt.kotlin.advanced

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

interface SimulationManager {
    suspend fun simulate(collector: SimulationResultsCollector)
}

class ChannelBasedSimulationManager(
    val batchCount: Int,
    val simulationPerBatch: Int,
    val simulator: Simulator,
    val clockFlow: Flow<Tick>
) : SimulationManager {

    override suspend fun simulate(collector: SimulationResultsCollector) {
        val channel = Channel<SimulationResult>()

        coroutineScope {
            launch {
                while(true) {
                    val result = channel.receiveCatching()
                    result.getOrNull()?.also {
                        collector.collectResult(it)
                    }
                    if (result.isClosed) {
                        return@launch
                    }
                }
            }

            (1..batchCount)
                .map {
                    BatchSimulator(it, simulationPerBatch, simulator)
                        .withThreshold()
                }
                .map { batch ->
                    launch {
                        batch.simulate(clockFlow) {
                            channel.send(it)
                        }
                    }
                }
                .joinAll()
            while(!channel.isEmpty) {
                delay(5)
            }
            channel.close()
        }
    }
}
