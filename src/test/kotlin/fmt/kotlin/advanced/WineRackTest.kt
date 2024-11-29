package fmt.kotlin.advanced

import fmt.kotlin.advanced.Bottle
import fmt.kotlin.advanced.Capacity
import fmt.kotlin.advanced.Color.RED
import fmt.kotlin.advanced.Position
import fmt.kotlin.advanced.Region.BORDEAUX
import fmt.kotlin.advanced.WineRack
import io.kotest.assertions.throwables.shouldThrowMessage
import org.junit.jupiter.api.Test

// tp2-step1-004
class WineRackTest {

    // tp2-step1-002
    // hint - extension function
    // hint - infix
    @Test
    fun `should throw error when out of capacity (shelf)`() {
        // Given
        val winRack = WineRack(Capacity(1, 2))

        // When // Then
        shouldThrowMessage("wine rack position Position(shelfIndex=1, slotIndex=0) is out of capacity Capacity(nbOfShelves=1, maxSlotByShelf=2)") {
            winRack.storeBottle(Bottle("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null), Position(1, 0))
        }
    }

    // tp2-step1-002
    // hint - extension function
    // hint - infix
    @Test
    fun `should throw error when out of capacity (slot)`() {
        // Given
        val winRack = WineRack(Capacity(1, 2))

        // When // Then
        shouldThrowMessage("wine rack position Position(shelfIndex=0, slotIndex=2) is out of capacity Capacity(nbOfShelves=1, maxSlotByShelf=2)") {
            winRack.storeBottle(Bottle("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null), Position(0, 2))
        }
    }

    // tp2-step1-002
    // hint - extension function
    // hint - infix
    @Test
    fun `should throw error when there is already a bottle`() {
        // Given
        val winRack = WineRack(Capacity(1, 2))

        // When // Then
        shouldThrowMessage("the slot at Position(shelfIndex=0, slotIndex=0) is not free") {
            winRack.storeBottle(Bottle("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null), Position(0, 0))
            winRack.storeBottle(Bottle("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null), Position(0, 0))
        }
    }

}