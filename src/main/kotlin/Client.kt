package fmt.kotlin.advanced

import fmt.kotlin.advanced.AsyncConverter.RequestPayload
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

/**
 * Client qui expose une méthode [convert] qui cache toute la mécanique de [AsyncConverter] derrière une
 * simple méthode `suspend`.
 */
class Client(
    private val converter: AsyncConverter,
) {

    private class Request(
        val data: Double,
        val response: CompletableDeferred<String>,
    )

    private val requests = Channel<Request>()

    /**
     * Cette méthode s'exécute pendant toute la durée de vie du client.
     *
     * Elle doit contenir tous les traitements.
     */
    suspend fun run(): Unit = coroutineScope {
        launch { converter.run() }

        for (request in requests) {
            val id = Uuid.random()
            converter.write(RequestPayload(id, request.data))

            request.response.complete(converter.read().data)
        }
    }

    /**
     * Cette méthode convertit [data] en chaîne de caractères, en appelant le [converter].
     */
    suspend fun convert(data: Double): String = coroutineScope {
        val job = CompletableDeferred<String>(coroutineContext.job)

        requests.send(Request(data, job))

        job.await()
    }
}
