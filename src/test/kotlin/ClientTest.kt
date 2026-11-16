package fmt.kotlin.advanced

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
