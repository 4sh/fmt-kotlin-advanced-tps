package fmt.kotlin.advanced

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

class ClientTest {

    @Test
    fun ordered() = runTest {
        val client = Client(OrderedConverter().log())

        withService({ client.run() }) {
            assertEquals("1.0", client.convert(1.0))
            assertEquals("2.7", client.convert(2.7))
            assertEquals("-58.578", client.convert(-58.578))
        }
    }

    @Test
    fun unordered() = runTest {
        val converter = UnorderedConverter().log()
        val client = Client(converter)

        withService({ client.run() }) {
            launch { assertEquals("1.0", client.convert(1.0)) }
            launch { assertEquals("2.7", client.convert(2.7)) }
            launch { assertEquals("-58.578", client.convert(-58.578)) }
        }
    }

    @Test
    fun serviceCancellation() = runTest {
        val converter = UnorderedConverter().log()
        val client = Client(converter)

        val job = launch {
            withService({ client.run() }) {
                launch { assertEquals("1.0", client.convert(1.0)) }
                launch { assertEquals("2.7", client.convert(2.7)) }
                launch { assertEquals("-58.578", client.convert(-58.578)) }
            }
        }

        delay(200.milliseconds)
        job.cancel()
    }

    @Test
    fun userCancellation() = runTest {
        val converter = UnorderedConverter().log()
        val client = Client(converter)

        withService({ client.run() }) {
            val job = launch {
                val _ = client.convert(1.0)
                error("Should be cancelled before reaching this point")
            }

            launch { assertEquals("2.7", client.convert(2.7)) }
            launch { assertEquals("-58.578", client.convert(-58.578)) }

            delay(100.milliseconds)
            job.cancel()
        }
    }
}
