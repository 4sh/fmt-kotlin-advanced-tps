package fmt.kotlin.advanced

import fmt.kotlin.advanced.Color.*
import fmt.kotlin.advanced.Region.BORDEAUX

class MagnumCellarOrganizerTest : BaseTestWineCellarOrganizer<Magnum>() {

    override fun commonRedBottle() = Magnum("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null)
    override fun goodRedBottle() = Magnum("Château Saint-Pierre", 2016, BORDEAUX, RED, 15, null)
    override fun bestRedBottle() = Magnum("Château Latour", 2012, BORDEAUX, RED, 18, null)
    override fun redBottleToKeep() = Magnum("Château Meyney", 2018, BORDEAUX, RED, 19, keepUntil = 2042)

    override fun commonPinkBottle() = Magnum("Mouton Cadet", 2023, BORDEAUX, PINK, 14, null)
    override fun goodPinkBottle() = Magnum("Château Saint Catherine", 2023, BORDEAUX, PINK, 15, null)
    override fun bestPinkBottle() = Magnum("Château Malagar", 2020, BORDEAUX, PINK, 18, null)
    override fun pinkBottleToKeep() = Magnum("Château Simone", 2023, BORDEAUX, PINK, 19, keepUntil = 2033)


    override fun commonWhiteBottle() = Magnum("Château Les Maubats", 2022, BORDEAUX, WHITE, 14, null)
    override fun goodWhiteBottle() = Magnum("Château Suau", 2020, BORDEAUX, WHITE, 15, null)
    override fun bestWhiteBottle() = Magnum("Château d'Yquem", 2005, BORDEAUX, WHITE, 18, null)
    override fun whiteBottleToKeep() = Magnum("Château Meyney", 2018, BORDEAUX, WHITE, 19, keepUntil = 2042)
}


