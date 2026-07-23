package fmt.kotlin.advanced.test.dsl

import fmt.kotlin.advanced.*
import fmt.kotlin.advanced.Region.*
import kotlin.random.Random
import kotlin.random.nextInt

// TODO step3

class BottleContext(
    var name: String = namesExample.random(),
    var year: Int = Random.nextInt(2000..2010),
    var region: Region = Region.entries.random(),
    var color: Color = Color.entries.random(),
    var rate: Int = Random.nextInt(0..3),
    var keepUntil: Int? = null,
) {

    fun build(): Bottle =
        Bottle(name, year, region, color, rate, keepUntil)

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

    fun wineRack(capacity: Capacity) {
        capacities += capacity
    }

    fun build(): WineCellarOrganizer =
        WineCellarOrganizer(
            *capacities.groupingBy { it }
                .eachCount()
                .toList()
                .map { it.second to it.first }
                .toTypedArray()
        )
}

fun OrganizeWineCellar(init: WineCellarDimensionsContext.() -> Unit): WineCellarOrganizer =
    WineCellarDimensionsContext().apply(init).build()

class StoreContext(
    private val cellar: WineCellarOrganizer,
) {

    fun bottle(
        init: BottleContext.() -> Unit,
    ) {
        cellar.storeBottle(BottleContext().apply(init).build())
    }

    fun build() = cellar
}

fun WineCellarOrganizer.storing(init: StoreContext.() -> Unit): WineCellarOrganizer =
    StoreContext(this).apply(init).build()

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
