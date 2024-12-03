package fmt.kotlin.advanced

import fmt.kotlin.advanced.Color.*
import fmt.kotlin.advanced.WineCellarOrganizer.Category.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class InsufficientSpace : Exception()

// tp7-step5

class WineCellarOrganizer<T: Bottle>(vararg winRackAvailable: Pair<Int, Capacity>) {

    enum class Category {
        COMMON, GOOD, BEST, TO_KEEP;

        companion object {
            fun <T : Bottle> from(bottle: T) = when {
                bottle.keepUntil != null && hasToKeep(bottle) -> TO_KEEP
                bottle.rate >= 18 -> BEST
                (15 until 18).contains(bottle.rate) -> GOOD
                else -> COMMON
            }

            private fun <T : Bottle>  hasToKeep(bottle: T): Boolean =
                bottle.keepUntil?.let { it > now().year } ?: false

            private fun now() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    private val wineCellar = buildWineCellar(winRackAvailable)

    private fun buildWineCellar(winRackAvailable: Array<out Pair<Int, Capacity>>): WineCellar<T> =
        winRackAvailable
            .flatMap { (nb, capacity) ->
                generateSequence { Rack<T>(capacity) }.take(nb).toList()
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

    fun storeBottle(bottle: T) {
        selectRack(bottle.region)?.let { wineRack ->
            selectPosition(wineRack, bottle.color, Category.from(bottle)) { it == null }
                ?.let {
                    wineRack.store(bottle, it)
                } ?: throw InsufficientSpace()
        }
    }

    fun takeCommonBottleOf(color: Color, region: Region): T? = takeBottleOf(color, region, COMMON)

    fun takeGoodBottleOf(color: Color, region: Region): T? = takeBottleOf(color, region, GOOD)

    fun takeBestBottleOf(color: Color, region: Region): T? = takeBottleOf(color, region, BEST)

    private fun takeBottleOf(color: Color, region: Region, category: Category): T? =
        selectRack(region)?.let { wineRack ->
            selectPosition(wineRack, color, category) { it != null && it.region == region }
                ?.let { wineRack.take(it) }
        }

    fun viewCommonBottleOf(color: Color, region: Region): T? = viewBottleOf(color, region, COMMON)

    fun viewGoodBottleOf(color: Color, region: Region): T? = viewBottleOf(color, region, GOOD)

    fun viewBestBottleOf(color: Color, region: Region): T? = viewBottleOf(color, region, BEST)

    private fun viewBottleOf(color: Color, region: Region, category: Category): T? =
        selectRack(region)?.let { wineRack ->
            selectPosition(wineRack, color, category) { it != null && it.region == region }
                ?.let { wineRack.view(it) }
        }

    fun viewWineRackOf(region: Region): Rack<T>? = selectRack(region)

    fun viewNumberOfWineRacks(): Int = wineCellar.numberOfRacks

    fun numberOfBottlesFrom(region: Region): Int =
        wineCellar.wineRacks.values.distinct().flatMap { it.stream() }.count { it.region == region }

    fun numberOfBottlesByRegion(): Map<Region, Int> = wineCellar.wineRacks.values
        .distinct()
        .flatMap { it.stream() }
        .groupingBy { it.region }
        .eachCount()

    fun numberOfBottlesByRegion(yearRange: IntRange): Map<Region, Int> = wineCellar.wineRacks.values
        .distinct()
        .flatMap { it.stream() }
        .groupingBy { it.region }
        .fold(0) { acc, bottle -> bottle.takeIf { yearRange.contains(it.year) }?.let { acc + 1 } ?: acc }


    private fun selectRack(region: Region): Rack<T>? = wineCellar.wineRacks[region.name]

    private fun selectPosition(
        wineRack: Rack<T>,
        color: Color,
        category: Category,
        condition: (T?) -> Boolean = { true }
    ): Position? {
        val categoryOnRackSize = category
            .let { if (wineRack.capacity.nbOfShelves == 1 && it == BEST) GOOD else it }

        return selectShelf(wineRack, color, categoryOnRackSize).let { shelfIndex ->
            selectSlot(wineRack[shelfIndex], categoryOnRackSize, condition)?.let {
                shelfIndex at it
            }
        }
    }

    private fun selectShelf(wineRack: Rack<T>, color: Color, category: Category): Int =
        when (category) {
            BEST -> 0
            else -> when (color) {
                RED -> 1 orLastShelf wineRack
                PINK -> 2 orLastShelf wineRack
                WHITE -> 3 orLastShelf wineRack
            }
        }

    private fun selectSlot(shelf: List<T?>, category: Category, condition: (T?) -> Boolean): Int? =
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

    private infix fun Int.orLastShelf(wineRack: Rack<T>) =
        this.takeIf { it < wineRack.capacity.nbOfShelves } ?: (wineRack.capacity.nbOfShelves - 1)
}