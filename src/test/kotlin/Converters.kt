@file:OptIn(ExperimentalAtomicApi::class, ExperimentalCoroutinesApi::class)

package fmt.kotlin.advanced

import fmt.kotlin.advanced.AsyncConverter.RequestPayload
import fmt.kotlin.advanced.AsyncConverter.ResponsePayload
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Duration.Companion.milliseconds

class OrderedConverter : AsyncConverter {
    val requests = Channel<RequestPayload>(Channel.BUFFERED)
    val responses = Channel<ResponsePayload>(Channel.BUFFERED)

    override suspend fun write(request: RequestPayload) {
        delay(50.milliseconds)
        requests.send(request)
    }

    override suspend fun read(): ResponsePayload {
        return responses.receive()
    }

    override suspend fun run() = coroutineScope {
        for ((id, data) in requests) {
            delay(((data.toInt() % 2) * 100).milliseconds + 50.milliseconds)
            val result = data.toString()
            responses.send(ResponsePayload(id, result))
        }
    }
}

private class LoggingConverter(
    private val upstream: AsyncConverter,
    private val scheduler: TestCoroutineScheduler,
) : AsyncConverter {
    override suspend fun write(request: RequestPayload) {
        println("[${request.id} ${scheduler.currentTime}ms] Write of ${request.data} started")
        upstream.write(request)
        println("[${request.id} ${scheduler.currentTime}ms] Write of ${request.data} finished")
    }

    override suspend fun read(): ResponsePayload {
        println("[........-....-....-....-............ ${scheduler.currentTime}ms] Read started")
        val result = upstream.read()
        println("[${result.id} ${scheduler.currentTime}ms] Read of ${result.data} finished")
        return result
    }

    override suspend fun run() =
        upstream.run()
}

context(scope: TestScope)
fun AsyncConverter.log(): AsyncConverter =
    LoggingConverter(this, scope.testScheduler)
