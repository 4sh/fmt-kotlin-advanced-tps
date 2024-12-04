package fmt.kotlin.advanced

import fmt.kotlin.advanced.Bottle
import fmt.kotlin.advanced.Color.RED
import fmt.kotlin.advanced.Region.BORDEAUX
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class BottleTest {

    @Test
    fun `should display label`() {
        // Given
        val bottle = Bottle("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null)

        // When
        val label = bottle.label

        // Then
        label shouldBe """
            Château Beau Rivage 2012 (RED)
            Rate : 1 / BORDEAUX
            """.trimIndent()
    }
}