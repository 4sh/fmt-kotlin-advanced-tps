package fmt.kotlin.advanced.test.dsl

import fmt.kotlin.advanced.*
import fmt.kotlin.advanced.Color.RED
import fmt.kotlin.advanced.Region.*

class StoreContext {

    fun bottle(init: BottleContext.() -> Unit): Bottle {

    }

    fun build(): List<Bottle> =
}

class BottleContext(
    var name: String? = null,
    var year: Int? = null,
    var region: Region? = null,
    var color: Color? = null,
    var rate: Int? = null,
    var keepUntil: Int? = null,
) {
    private val namesExample = listOf(
        "Château Beau Rivage",
        "Château Saint-Pierre",
        "Château Latour",
        "Château Meyney",
        "Mouton Cadet",
        "Château Saint Catherine",
        "Château Malagar",
        "Château Simone",
        "Château Les Maubats",
        "Château Suau",
        "Château d'Yquem",
        "Château Meyney"
    )

    fun build(): Bottle = Bottle("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null)
}

class WineCellarContext {

    private val capacities: MutableList<Capacity> = mutableListOf()

    private lateinit var wineCellarOrganizer: WineCellarOrganizer

    fun wineCellar(capacity: Capacity) {

    }

    fun store() {
        initWineCellarIfNecessary()

    }

    private fun initWineCellarIfNecessary() {
        if (!::wineCellarOrganizer.isInitialized) {
            wineCellarOrganizer = WineCellarOrganizer(
                *capacities.groupingBy { it }.eachCount().toList()
                    .map { it.second to it.first }
                    .toTypedArray())
        }
    }

    fun displayNextBest(colorToRegion: Pair<Color, Region>)  {
        initWineCellarIfNecessary()

    }

    fun displayWinRacks() {
        initWineCellarIfNecessary()
        printWineRack(BORDEAUX)
        printWineRack(ALSACE)
        printWineRack(BOURGOGNE)
    }

    private fun printWineRack(region: Region) {
        val wineRack = wineCellarOrganizer.viewWineRackOf(region)
        if (wineRack != null) {
            println("$region (${wineRack.rackId})")
            println(wineRack)
        }
    }

    fun build() = wineCellarOrganizer
}

object OrganizeWineCellar {

}
