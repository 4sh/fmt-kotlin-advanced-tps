package fmt.kotlin.advanced.test.dsl

import fmt.kotlin.advanced.Capacity
import fmt.kotlin.advanced.Color
import fmt.kotlin.advanced.Region
import fmt.kotlin.advanced.Region.*
import fmt.kotlin.advanced.WineCellarOrganizer

class BottleContext(
    var name: String? = null,
    var year: Int? = null,
    var region: Region? = null,
    var color: Color? = null,
    var rate: Int? = null,
    var keepUntil: Int? = null,
) {

    // TODO step 2 003

    companion object {
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
    }
}

class WineCellarDimensionsContext {
    private val capacities = ArrayList<Capacity>()

    // TODO step 2 001

    fun build(): WineCellarOrganizer =
        WineCellarOrganizer(
            *capacities.groupingBy { it }
                .eachCount()
                .toList()
                .map { it.second to it.first }
                .toTypedArray()
        )
}

fun OrganizeWineCellar(): WineCellarOrganizer =
    TODO("step 2 001")

fun WineCellarOrganizer.storing(): WineCellarOrganizer =
    TODO("step 2 002")

infix fun Color.from(region: Region) = this to region

fun WineCellarOrganizer.displayWineRacks() {
    printWineRack(BORDEAUX)
    printWineRack(ALSACE)
    printWineRack(BOURGOGNE)
}

private fun WineCellarOrganizer.printWineRack(region: Region) {
    val wineRack = viewWineRackOf(region)
    if (wineRack != null) {
        println("$region (${wineRack.rackId})")
        println(wineRack)
    }
}

fun WineCellarOrganizer.displayNextBest(colorToRegion: Pair<Color, Region>)  {
    val (color, region) = colorToRegion
    println("Next best bottle : ${viewBestBottleOf(color, region)}")
}
