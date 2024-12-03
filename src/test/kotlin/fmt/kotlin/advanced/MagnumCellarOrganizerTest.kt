package fmt.kotlin.advanced

import fmt.kotlin.advanced.Color.*
import fmt.kotlin.advanced.Region.BORDEAUX
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.junit.jupiter.api.Test

class MagnumCellarOrganizerTest : BaseTestWineCellarOrganizer<Magnum>() {

    override fun commonRedBottle() = Magnum("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null, 1)
    override fun goodRedBottle() = Magnum("Château Saint-Pierre", 2016, BORDEAUX, RED, 15, null, 2)
    override fun bestRedBottle() = Magnum("Château Latour", 2012, BORDEAUX, RED, 18, null, 3)
    override fun redBottleToKeep() = Magnum("Château Meyney", 2018, BORDEAUX, RED, 19, keepUntil = 2042, 4)

    override fun commonPinkBottle() = Magnum("Mouton Cadet", 2023, BORDEAUX, PINK, 14, null, 6)
    override fun goodPinkBottle() = Magnum("Château Saint Catherine", 2023, BORDEAUX, PINK, 15, null, 5)
    override fun bestPinkBottle() = Magnum("Château Malagar", 2020, BORDEAUX, PINK, 18, null, 7)
    override fun pinkBottleToKeep() = Magnum("Château Simone", 2023, BORDEAUX, PINK, 19, keepUntil = 2033, 8)


    override fun commonWhiteBottle() = Magnum("Château Les Maubats", 2022, BORDEAUX, WHITE, 14, null, 9)
    override fun goodWhiteBottle() = Magnum("Château Suau", 2020, BORDEAUX, WHITE, 15, null, 10)
    override fun bestWhiteBottle() = Magnum("Château d'Yquem", 2005, BORDEAUX, WHITE, 18, null, 12)
    override fun whiteBottleToKeep() = Magnum("Château Meyney", 2018, BORDEAUX, WHITE, 19, keepUntil = 2042, 13)

    val wineOrganizer = WineCellarOrganizer<Magnum>(3 to (4 by 6))

    @Test
    fun `a stored bottle should can be viewed with correct number`() {
        // Given
        val commonRedBottle = commonRedBottle()
        wineOrganizer.storeBottle(commonRedBottle)

        // When
        val viewedBottle = wineOrganizer.viewCommonBottleOf(commonRedBottle.color, BORDEAUX)

        // Then
        println(wineOrganizer.viewWineRackOf(BORDEAUX))
        viewedBottle shouldBeSameInstanceAs commonRedBottle
        wineOrganizer.viewCommonBottleOf(commonRedBottle.color, BORDEAUX)?.number shouldBe commonRedBottle.number
    }
}


