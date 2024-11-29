package fmt.kotlin.advanced

import fmt.kotlin.advanced.*
import fmt.kotlin.advanced.Color.RED
import fmt.kotlin.advanced.Region.BORDEAUX
import io.kotest.assertions.throwables.shouldThrowMessage
import org.junit.jupiter.api.Test

class WineRackTest {

    @Test
    fun `should throw error when out of capacity (shelf)`() {
        // Given
        val winRack = WineRack(1 by  2)

        // When // Then
        shouldThrowMessage("wine rack position Position(shelfIndex=1, slotIndex=0) is out of capacity Capacity(nbOfShelves=1, maxSlotByShelf=2)") {
            winRack.storeBottle(Bottle("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null), 1 at 0)
        }
    }

    @Test
    fun `should throw error when out of capacity (slot)`() {
        // Given
        val winRack = WineRack(1 by  2)

        // When // Then
        shouldThrowMessage("wine rack position Position(shelfIndex=0, slotIndex=2) is out of capacity Capacity(nbOfShelves=1, maxSlotByShelf=2)") {
            winRack.storeBottle(Bottle("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null), 0 at 2)
        }
    }

    @Test
    fun `should throw error when there is already a bottle`() {
        // Given
        val winRack = WineRack(1 by  2)

        // When // Then
        shouldThrowMessage("the slot at Position(shelfIndex=0, slotIndex=0) is not free") {
            winRack.storeBottle(Bottle("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null),0 at 0)
            winRack.storeBottle(Bottle("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null), 0 at 0)
        }
    }

}