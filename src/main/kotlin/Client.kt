package fmt.kotlin.advanced

import fmt.kotlin.advanced.AsyncConverter.RequestPayload
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.select
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

    private sealed class Response {
        abstract val id: Uuid

        data class Success(
            override val id: Uuid,
            val data: String,
        ) : Response() {

            constructor(data: AsyncConverter.ResponsePayload) : this(data.id, data.data)
        }

        data class Failure(
            override val id: Uuid,
            val exception: Exception,
        ) : Response()
    }

    /**
     * Cette méthode s'exécute pendant toute la durée de vie du client.
     *
     * Elle doit contenir tous les traitements.
     */
    suspend fun run(): Unit = coroutineScope {
        launch { converter.run() }

        val sent = Channel<Pair<Uuid, CompletableDeferred<String>>>(Channel.BUFFERED)
        val received = Channel<Response>(Channel.BUFFERED)

        launch {
            for (request in requests) {
                val id = Uuid.random()
                sent.send(id to request.response)
                converter.write(RequestPayload(id, request.data))
            }
        }

        launch {
            while (isActive) {
                val response = try {
                    Response.Success(converter.read())
                } catch (e: AsyncConverter.ConversionFailed) {
                    currentCoroutineContext().ensureActive()
                    Response.Failure(e.id, e)
                }

                received.send(response)
            }
        }

        launch {
            val inFlight = ArrayList<Pair<Uuid, CompletableDeferred<String>>>()

            var hasInFlight = true
            while (isActive) {
                select {
                    if (hasInFlight) {
                        sent.onReceiveCatching { msg ->
                            when {
                                msg.isSuccess -> inFlight += msg.getOrThrow()
                                msg.isClosed -> hasInFlight = false
                                msg.isFailure -> msg.getOrThrow()
                            }
                        }
                    }

                    received.onReceiveCatching { msg ->
                        when {
                            msg.isSuccess -> {
                                val response = msg.getOrThrow()
                                val sent = inFlight.find { it.first == response.id }
                                if (sent == null) {
                                    System.err.println("Received response for unknown request: $response")
                                    return@onReceiveCatching
                                }

                                inFlight.remove(sent)
                                when (response) {
                                    is Response.Success -> sent.second.complete(response.data)
                                    is Response.Failure -> sent.second.completeExceptionally(response.exception)
                                }
                            }
                            msg.isClosed -> return@onReceiveCatching
                            msg.isFailure -> msg.getOrThrow()
                        }
                    }
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
