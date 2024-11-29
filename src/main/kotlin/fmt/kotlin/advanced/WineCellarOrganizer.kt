package fmt.kotlin.advanced

import fmt.kotlin.advanced.Color.*
import fmt.kotlin.advanced.WineCellarOrganizer.Category.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class InsufficientSpace : Exception()

class WineCellarOrganizer(vararg winRackAvailable: Pair<Int, Capacity>) {

    private enum class Category {
        COMMON, GOOD, BEST, TO_KEEP;

        companion object {
            fun from(bottle: Bottle) = when {
                bottle.keepUntil != null && hasToKeep(bottle) -> TO_KEEP
                bottle.rate >= 18 -> BEST
                (15 until 18).contains(bottle.rate) -> GOOD
                else -> COMMON
            }

            private fun hasToKeep(bottle: Bottle): Boolean =
                bottle.keepUntil?.let { it > now().year } ?: false

            private fun now() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    private val wineCellar = buildWineCellar(winRackAvailable)

    private fun buildWineCellar(winRackAvailable: Array<out Pair<Int, Capacity>>): WineCellar =
        winRackAvailable
            .flatMap { capacityByNb ->
                generateSequence { WineRack(capacityByNb.second) }.take(capacityByNb.first).toList()
            }
            .let { racks ->
                Region.entries.mapIndexed { index, region ->
                    region.name to (racks.takeIf { index < it.size }
                        ?.let { racks[index] }
                        ?: racks.last()
                            )
                }
            }
            .toMap()
            .let { WineCellar(it) }

    fun storeBottle(bottle: Bottle) {
        selectRack(bottle.region)?.let { wineRack ->
            selectPosition(wineRack, bottle.color, Category.from(bottle)) { it == null }
                ?.let {
                    wineRack.storeBottle(bottle, it)
                } ?: throw InsufficientSpace()
        }
    }

    fun takeCommonBottleOf(color: Color, region: Region): Bottle? = takeBottleOf(color, region, COMMON)

    fun takeGoodBottleOf(color: Color, region: Region): Bottle? = takeBottleOf(color, region, GOOD)

    fun takeBestBottleOf(color: Color, region: Region): Bottle? = takeBottleOf(color, region, BEST)

    private fun takeBottleOf(color: Color, region: Region, category: Category): Bottle? =
        selectRack(region)?.let { wineRack ->
            selectPosition(wineRack, color, category) { it != null && it.region == region }
                ?.let { wineRack.takeBottle(it) }
        }

    fun viewCommonBottleOf(color: Color, region: Region): Bottle? = viewBottleOf(color, region, COMMON)

    fun viewGoodBottleOf(color: Color, region: Region): Bottle? = viewBottleOf(color, region, GOOD)

    fun viewBestBottleOf(color: Color, region: Region): Bottle? = viewBottleOf(color, region, BEST)

    private fun viewBottleOf(color: Color, region: Region, category: Category): Bottle? =
        selectRack(region)?.let { wineRack ->
            selectPosition(wineRack, color, category) { it != null && it.region == region }
                ?.let { wineRack.viewBottle(it) }
        }

    fun viewWineRackOf(region: Region): WineRack? = selectRack(region)

    fun viewNumberOfWineRacks(): Int = wineCellar.numberOfRacks

    fun numberOfBottlesFrom(region: Region): Int {
        val bottles = mutableListOf<Bottle>()
        for (wineRack in wineCellar.wineRacks.values.toSet()) {
            bottles.addAll(wineRack.streamBottles().toList())
        }
        var i = 0
        for (bottle in bottles) {
            if (bottle.region == region) {
                i++
            }
        }
        return i
    }

    fun numberOfBottlesByRegion(): Map<Region, Int> {
        val bottles = mutableListOf<Bottle>()
        for (wineRack in wineCellar.wineRacks.values.toSet()) {
            bottles.addAll(wineRack.streamBottles().toList())
        }
        val map = mutableMapOf<Region, Int>()
        for (bottle in bottles) {
            if (map[bottle.region] == null) {
                map[bottle.region] = 0
            }
            map[bottle.region] = map[bottle.region]!! + 1
        }
        return map
    }

    fun numberOfBottlesByRegion(yearRange: IntRange): Map<Region, Int> {
        val bottles = mutableListOf<Bottle>()
        for (wineRack in wineCellar.wineRacks.values.toSet()) {
            bottles.addAll(wineRack.streamBottles().toList())
        }
        val map = mutableMapOf<Region, Int>()
        for (bottle in bottles) {
            if (map[bottle.region] == null) {
                map[bottle.region] = 0
            }
            if (yearRange.contains(bottle.year)) {
                map[bottle.region] = map[bottle.region]!! + 1
            }
        }
        return map
    }

    private fun selectRack(region: Region): WineRack? = wineCellar.wineRacks[region.name]

    private fun selectPosition(
        wineRack: WineRack,
        color: Color,
        category: Category,
        condition: (Bottle?) -> Boolean = { true }
    ): Position? {
        val categoryOnRackSize = category
            .let { if (wineRack.capacity.nbOfShelves == 1 && it == BEST) GOOD else it }

        return selectShelf(wineRack, color, categoryOnRackSize).let { shelfIndex ->
            selectSlot(wineRack.at(shelfIndex), categoryOnRackSize, condition)?.let {
                Position(shelfIndex, it)
            }
        }
    }

    private fun selectShelf(wineRack: WineRack, color: Color, category: Category): Int =
        when (category) {
            BEST -> 0
            else -> when (color) {
                RED -> {
                    if (1 < wineRack.capacity.nbOfShelves) {
                        1
                    } else {
                        wineRack.capacity.nbOfShelves - 1
                    }
                }

                PINK -> {
                    if (2 < wineRack.capacity.nbOfShelves) {
                        2
                    } else {
                        wineRack.capacity.nbOfShelves - 1
                    }
                }

                WHITE -> {
                    if (3 < wineRack.capacity.nbOfShelves) {
                        3
                    } else {
                        wineRack.capacity.nbOfShelves - 1
                    }
                }
            }
        }

    private fun selectSlot(shelf: List<Bottle?>, category: Category, condition: (Bottle?) -> Boolean): Int? =
        when (category) {
            BEST -> shelf.indexOfFirst { condition(it) }

            COMMON -> shelf
                .take(shelf.size / 2)
                .indexOfFirst { condition(it) }

            GOOD -> shelf
                .drop(shelf.size / 2)
                .indexOfFirst { condition(it) }
                .takeIf { it > -1 }
                ?.let { it + shelf.size / 2 }

            TO_KEEP -> shelf
                .reversed()
                .take(shelf.size / 2)
                .indexOfFirst { condition(it) }
                .takeIf { it > -1 }
                ?.let { shelf.size - 1 - it }

        }?.takeIf { it > -1 }
}