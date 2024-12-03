package fmt.kotlin.advanced.test.dsl

import fmt.kotlin.advanced.*
import fmt.kotlin.advanced.Region.*
import kotlin.random.Random
import kotlin.random.nextInt

@DslMarker
annotation class WineCellarOrganizerDsl

@WineCellarOrganizerDsl
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

@WineCellarOrganizerDsl
class WineCellarDimensionsContext<T : Bottle> {
    private val capacities = ArrayList<Capacity>()

    fun wineRack(capacity: Capacity) {
        capacities += capacity
    }

    fun build(): WineCellarOrganizer<T> =
        WineCellarOrganizer(
            *capacities.groupingBy { it }
                .eachCount()
                .toList()
                .map { it.second to it.first }
                .toTypedArray()
        )
}

fun <T : Bottle> OrganizeWineCellar(init: WineCellarDimensionsContext<T>.() -> Unit): WineCellarOrganizer<T> =
    WineCellarDimensionsContext<T>().apply(init).build()

@WineCellarOrganizerDsl
class StoreContext<T : Bottle>(
    private val cellar: WineCellarOrganizer<T>,
) {

    fun store(bottle: T) {
        cellar.storeBottle(bottle)
    }

    fun build() = cellar
}

fun StoreContext<in Bottle>.bottle(
    init: BottleContext.() -> Unit,
) {
    store(BottleContext().apply(init).build())
}

fun <T : Bottle> WineCellarOrganizer<T>.storing(init: StoreContext<T>.() -> Unit): WineCellarOrganizer<T> =
    StoreContext(this).apply(init).build()

infix fun Color.from(region: Region) = this to region

fun <T : Bottle> WineCellarOrganizer<T>.displayWineRacks() {
    printWineRack(BORDEAUX)
    printWineRack(ALSACE)
    printWineRack(BOURGOGNE)
}

private fun <T : Bottle> WineCellarOrganizer<T>.printWineRack(region: Region) {
    val wineRack = viewWineRackOf(region)
    if (wineRack != null) {
        println("$region (${wineRack.rackId})")
        println(wineRack)
    }
}

fun <T : Bottle> WineCellarOrganizer<T>.displayNextBest(colorToRegion: Pair<Color, Region>)  {
    val (color, region) = colorToRegion
    println("Next best bottle : ${viewBestBottleOf(color, region)}")
}
