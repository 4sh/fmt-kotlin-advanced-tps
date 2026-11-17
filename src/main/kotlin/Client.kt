package fmt.kotlin.advanced

import fmt.kotlin.advanced.AsyncConverter.RequestPayload
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

        val sent = ArrayList<Pair<Uuid, CompletableDeferred<String>>>()
        val lock = Mutex()

        launch {
            for (request in requests) {
                val id = Uuid.random()
                lock.withLock {
                    sent += id to request.response
                }
                converter.write(RequestPayload(id, request.data))
            }
        }

        launch {
            while (isActive) {
                val response = converter.read()
                lock.withLock {
                    sent.find { it.first == response.id }
                        .also { sent.remove(it) }
                        ?.second
                        ?.complete(response.data)
                }
            }
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
