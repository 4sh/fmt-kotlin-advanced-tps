package fmt.kotlin.advanced

import fmt.kotlin.advanced.Color.PINK
import fmt.kotlin.advanced.Color.RED
import fmt.kotlin.advanced.WineCellarOrganizer.Category.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class InsufficientSpace : Exception()

class WineCellarOrganizer(vararg winRackAvailable: Pair<Int, Capacity>) {

    // tp1-step2-005
    // hint - object
    // hint - range
    enum class Category {
        COMMON, GOOD, BEST, TO_KEEP;
    }

    private fun categoryFrom(bottle: Bottle): Category {
        if (bottle.keepUntil == null && bottle.rate >= 15) {
            if (bottle.rate >= 18) {
                return BEST
            } else {
                return GOOD
            }
        } else {
            if (hasToKeep(bottle)) {
                return TO_KEEP
            }
        }
        return COMMON
    }

    private fun hasToKeep(bottle: Bottle): Boolean =
        bottle.keepUntil?.let { it > now().year } ?: false

    private fun now() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

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

    // tp1-step2-001
    // hint - lambda - high order
    // tp1-step2-006
    // hint - scope function
    // hint - nullability
    // hint - throws expression
    fun storeBottle(bottle: Bottle) {
        val wineRack = selectRack(bottle.region)
        if (wineRack != null) {
            val position = selectEmptyPosition(wineRack, bottle.color, categoryFrom(bottle))
            if (position != null) {
                wineRack.storeBottle(bottle, position)
            } else {
                throw InsufficientSpace()
            }
        }
    }

    // tp1-step2-003
    // hint - body expression
    fun takeCommonBottleOf(color: Color, region: Region): Bottle? {
        return takeBottleOf(color, region, COMMON)
    }

    // tp1-step2-003
    // hint - body expression
    fun takeGoodBottleOf(color: Color, region: Region): Bottle? {
        return takeBottleOf(color, region, GOOD)
    }

    // tp1-step2-003
    // hint - body expression
    fun takeBestBottleOf(color: Color, region: Region): Bottle? {
        return takeBottleOf(color, region, BEST)
    }

    // tp1-step2-001
    // hint - lambda - high order
    private fun takeBottleOf(color: Color, region: Region, category: Category): Bottle? =
        selectRack(region)?.let { wineRack ->
            selectPositionForRegion(wineRack, color, category, region)
                ?.let { wineRack.takeBottle(it) }
        }

    // tp1-step2-003
    // hint - body expression
    fun viewCommonBottleOf(color: Color, region: Region): Bottle? {
        return viewBottleOf(color, region, COMMON)
    }

    // tp1-step2-003
    // hint - body expression
    fun viewGoodBottleOf(color: Color, region: Region): Bottle? {
        return viewBottleOf(color, region, GOOD)
    }

    // tp1-step2-003
    // hint - body expression
    fun viewBestBottleOf(color: Color, region: Region): Bottle? {
        return viewBottleOf(color, region, BEST)
    }

    // tp1-step2-001
    // hint - lambda - high order
    private fun viewBottleOf(color: Color, region: Region, category: Category): Bottle? =
        selectRack(region)?.let { wineRack ->
            selectPositionForRegion(wineRack, color, category, region)
                ?.let { wineRack.viewBottle(it) }
        }

    // tp1-step2-003
    // hint - body expression
    fun viewWineRackOf(region: Region): WineRack? {
        return selectRack(region)
    }

    // tp1-step2-003
    // hint - body expression
    fun viewNumberOfWineRacks(): Int {
        return wineCellar.numberOfRacks
    }

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

    // tp1-step2-003
    // hint - body expression
    private fun selectRack(region: Region): WineRack? {
        return wineCellar.wineRacks[region.name]
    }

    // tp1-step2-001
    // hint - lambda - high order
    // hint - default argument
    private fun selectPositionForRegion(
        wineRack: WineRack,
        color: Color,
        category: Category,
        region: Region
    ): Position? {
        val categoryOnRackSize = category
            .let { if (wineRack.capacity.nbOfShelves == 1 && it == BEST) GOOD else it }

        return selectShelf(wineRack, color, categoryOnRackSize).let { shelfIndex ->
            selectSlotForRegion(wineRack.at(shelfIndex), categoryOnRackSize, region)?.let {
                Position(shelfIndex, it)
            }
        }
    }

    private fun selectEmptyPosition(
        wineRack: WineRack,
        color: Color,
        category: Category
    ): Position? {
        val categoryOnRackSize = category
            .let { if (wineRack.capacity.nbOfShelves == 1 && it == BEST) GOOD else it }

        return selectShelf(wineRack, color, categoryOnRackSize).let { shelfIndex ->
            selectEmptySlot(wineRack.at(shelfIndex), categoryOnRackSize)?.let {
                Position(shelfIndex, it)
            }
        }
    }

    // tp1-step2-003
    // hint - when expression
    private fun selectShelf(wineRack: WineRack, color: Color, category: Category): Int {
        if (category == BEST) {
            return 0
        } else {
            if (color == RED) {
                if (1 < wineRack.capacity.nbOfShelves) {
                    return 1
                } else {
                    return wineRack.capacity.nbOfShelves - 1
                }
            } else if (color == PINK) {
                if (2 < wineRack.capacity.nbOfShelves) {
                    return 2
                } else {
                    return wineRack.capacity.nbOfShelves - 1
                }
            } else {
                if (3 < wineRack.capacity.nbOfShelves) {
                    return 3
                } else {
                    return wineRack.capacity.nbOfShelves - 1
                }
            }
        }
    }

    // tp1-step2-001
    // hint - lambda - high order
    // tp1-step2-002
    // hint - collection
    private fun selectEmptySlot(shelf: List<Bottle?>, category: Category): Int? {
        var index: Int? = -1

        if (category == BEST) {
            var i = 0
            while (i < shelf.size) {
                if (shelf[i] == null) {
                    index = i
                    break
                }
                i++
            }

            // tp1-step2-002
            // In progress to refactor... insert from beginning of the shelf, but not after the middle
            //
            // shelf.???? { condition(it) } // find the first index matching condition
        }

        if (category == COMMON) {
            var i = 0
            while (i < shelf.size / 2) {
                if (shelf[i] == null) {
                    index = i
                    break
                }
                i++
            }

            // tp1-step2-002
            // In progress to refactor... insert from beginning of the shelf, but not after the middle
            //
            // shelf
            // .???(shelf.size / 2) // keep only the first half ?
            // .??? { condition(it) } // find the first index
        }

        if (category == GOOD) {
            var i = shelf.size / 2
            while (i < shelf.size) {
                if (shelf[i] == null) {
                    index = i
                    break
                }
                i++
            }

            // tp1-step2-002
            // In progress to refactor... insert bottle from the middle of the shelf
            //
            // shelf
            // .???(shelf.size / 2) // skip to the middle
            // .??? { condition(it) } // find the first index ? return what if not found ?
            // .??? { it > -1 } // not null if found, null if found
            // ?.let { it + shelf.size / 2 } // retrieve index from 0 instead of index from reversed
        }

        if (category == TO_KEEP) {
            var i = shelf.size - 1
            while (i >= (shelf.size / 2)) {
                if (shelf[i] == null) {
                    index = i
                    break
                }
                i--
            }

            // tp1-step2-002
            // In progress to refactor... insert bottle by the end, but not before the middle
            //
            // shelf
            // .???(). // by the end ? how inverse collection ?
            // .???(shelf.size / 2) // skip to the middle
            // .??? { condition(it) } // find the first index ? return what if not found ?
            // .??? { it > ??? } // not null if found, null if found
            // ?.let { shelf.size - 1 - it } // retrieve index from 0 instead of index from reversed
        }

        if (index == -1) {
            return null
        }
        return index
    }

    private fun selectSlotForRegion(shelf: List<Bottle?>, category: Category, region: Region): Int? {
        var index: Int? = -1

        if (category == BEST) {
            var i = 0
            while (i < shelf.size) {
                if (shelf[i] != null && shelf[i]!!.region == region) {
                    index = i
                    break
                }
                i++
            }

            // tp1-step2-002
            // In progress to refactor... insert from beginning of the shelf, but not after the middle
            //
            // shelf.???? { condition(it) } // find the first index matching condition

        }

        if (category == COMMON) {
            var i = 0
            while (i < shelf.size / 2) {
                if (shelf[i] != null && shelf[i]!!.region == region) {
                    index = i
                    break
                }
                i++
            }

            // tp1-step2-002
            // In progress to refactor... insert from beginning of the shelf, but not after the middle
            //
            // shelf
            // .???(shelf.size / 2) // keep only the first half ?
            // .??? { condition(it) } // find the first index
        }

        if (category == GOOD) {
            var i = shelf.size / 2
            while (i < shelf.size) {
                if (shelf[i] != null && shelf[i]!!.region == region) {
                    index = i
                    break
                }
                i++
            }

            // tp1-step2-002
            // In progress to refactor... insert bottle from the middle of the shelf
            //
            // shelf
            // .???(shelf.size / 2) // skip to the middle
            // .??? { condition(it) } // find the first index ? return what if not found ?
            // .??? { it > -1 } // not null if found, null if found
            // ?.let { it + shelf.size / 2 } // retrieve index from 0 instead of index from reversed

        }

        if (category == TO_KEEP) {
            var i = shelf.size - 1
            while (i >= (shelf.size / 2)) {
                if (shelf[i] != null && shelf[i]!!.region == region) {
                    index = i
                    break
                }
                i--
            }

            // tp1-step2-002
            // In progress to refactor... insert bottle by the end, but not before the middle
            //
            // shelf
            // .???(). // by the end ? how inverse collection ?
            // .???(shelf.size / 2) // skip to the middle
            // .??? { condition(it) } // find the first index ? return what if not found ?
            // .??? { it > ??? } // not null if found, null if found
            // ?.let { shelf.size - 1 - it } // retrieve index from 0 instead of index from reversed

        }

        if (index == -1) {
            return null
        }
        return index
    }
}