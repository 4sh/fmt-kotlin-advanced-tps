package fmt.kotlin.advanced

import fmt.kotlin.advanced.Color.*
import fmt.kotlin.advanced.Region.*

class StandardCellarOrganizerTest : BaseTestWineCellarOrganizer<Standard>() {

    override fun commonRedBottle() = Standard("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null)
    override fun goodRedBottle() = Standard("Château Saint-Pierre", 2016, BORDEAUX, RED, 15, null)
    override fun bestRedBottle() = Standard("Château Latour", 2012, BORDEAUX, RED, 18, null)
    override fun redBottleToKeep() = Standard("Château Meyney", 2018, BORDEAUX, RED, 19, keepUntil = 2042)

    override fun commonPinkBottle() = Standard("Mouton Cadet", 2023, BORDEAUX, PINK, 14, null)
    override fun goodPinkBottle() = Standard("Château Saint Catherine", 2023, BORDEAUX, PINK, 15, null)
    override fun bestPinkBottle() = Standard("Château Malagar", 2020, BORDEAUX, PINK, 18, null)
    override fun pinkBottleToKeep() = Standard("Château Simone", 2023, BORDEAUX, PINK, 19, keepUntil = 2033)


    override fun commonWhiteBottle() = Standard("Château Les Maubats", 2022, BORDEAUX, WHITE, 14, null)
    override fun goodWhiteBottle() = Standard("Château Suau", 2020, BORDEAUX, WHITE, 15, null)
    override fun bestWhiteBottle() = Standard("Château d'Yquem", 2005, BORDEAUX, WHITE, 18, null)
    override fun whiteBottleToKeep() = Standard("Château Meyney", 2018, BORDEAUX, WHITE, 19, keepUntil = 2042)

}


