package fmt.kotlin.advanced

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days

class Tp0 {

    @Test
    fun `hello world!`() = runTest {
        delay(1.days) // Delay-skipping should kick in, this should be instant

        assertEquals("Hello, World!", HELLO_WORLD)

        println()
        println("""
            Bienvenue en Kotlin !
            Si ce message est affiché, c'est que le test s'est correctement exécuté et que ton environnement est correctement configuré.
            À dans quelques jours pour la formation…
        """.trimIndent())
        println()
    }
}
