package fmt.kotlin.advanced.test.dsl

import fmt.kotlin.advanced.*
import fmt.kotlin.advanced.Region.*
import kotlin.random.Random

class StoreContext {

    private val bottlesToStore = mutableListOf<Bottle>()

    operator fun Bottle.unaryPlus() {
        bottlesToStore.add(this)
    }

    fun bottle(init: BottleContext.() -> Unit): Bottle {
        return BottleContext().apply { init() }.build()
    }

    fun build(): List<Bottle> = bottlesToStore.toList()
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

    fun build(): Bottle = Bottle(
        name = name ?: namesExample.random(),
        year = year ?: Random.nextInt(2010, 2020),
        region = region ?: Region.entries.random(),
        color = color ?: Color.entries.random(),
        rate = rate ?: Random.nextInt(0, 21),
        keepUntil = keepUntil
    )
}

class WineCellarContext {

    private val capacities: MutableList<Capacity> = mutableListOf()

    private lateinit var wineCellarOrganizer: WineCellarOrganizer

    fun wineCellar(capacity: Capacity) {
        capacities.add(capacity)
    }

    fun store(init: StoreContext.() -> Unit) {
        initWineCellarIfNecessary()
        StoreContext().apply(init).build().forEach {
            wineCellarOrganizer.storeBottle(it)
        }
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
        val (color, region) = colorToRegion
        println("Next best bottle : " + wineCellarOrganizer.viewBestBottleOf(color, region))
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
    operator fun invoke(init: WineCellarContext.() -> Unit): WineCellarOrganizer {
        return WineCellarContext().apply(init).build()
    }
}

infix fun Color.from(region: Region) = this to region
