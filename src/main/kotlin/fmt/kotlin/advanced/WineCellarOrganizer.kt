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

    // tp1-step2-004
    // hint - sequence
    // hint - nullability
    // hint - collection - transformation
    // hint - collection - fitler
    private fun buildWineCellar(winRackCapacitiesAvailable: Array<out Pair<Int, Capacity>>): WineCellar {
        // TODO body function
        val wineRacksAvailable = mutableListOf<WineRack>()
        for (pair in winRackCapacitiesAvailable) {
            for (i in IntRange(1, pair.first)) {
                wineRacksAvailable.add(WineRack(pair.second))
            }
        }

        val wineRacks = mutableMapOf<String, WineRack>()
        var i = 0
        val regions = Region.entries
        while (i < regions.size) {
            if (i < wineRacksAvailable.size) {
                wineRacks[regions[i].name] = wineRacksAvailable[i]
            } else {
                wineRacks[regions[i].name] = wineRacksAvailable[wineRacksAvailable.size - 1]
            }
            i++
        }

        return WineCellar(wineRacks)
    }

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
    // tp1-step2-007
    // hint - ternary
    // hint - scope function
    private fun selectPositionForRegion(
        wineRack: WineRack,
        color: Color,
        category: Category,
        region: Region
    ): Position? {
        var categoryOnRackSize: Category
        if (wineRack.capacity.nbOfShelves == 1 && category == BEST) {
            categoryOnRackSize = GOOD
        } else {
            categoryOnRackSize = category
        }

        val shelfIndex = selectShelf(wineRack, color, categoryOnRackSize)
        val slotIndex = selectSlotForRegion(wineRack.at(shelfIndex), categoryOnRackSize, region)
        if (slotIndex != null) {
            return Position(shelfIndex, slotIndex)
        }
        return null
    }

    private fun selectEmptyPosition(
        wineRack: WineRack,
        color: Color,
        category: Category
    ): Position? {
        var categoryOnRackSize: Category
        if (wineRack.capacity.nbOfShelves == 1 && category == BEST) {
            categoryOnRackSize = GOOD
        } else {
            categoryOnRackSize = category
        }

        val shelfIndex = selectShelf(wineRack, color, categoryOnRackSize)
        val slotIndex = selectEmptySlot(wineRack.at(shelfIndex), categoryOnRackSize)
        if (slotIndex != null) {
            return Position(shelfIndex, slotIndex)
        }
        return null
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

        if (category == TO_KEEP) {
            var i = shelf.size - 1
            while (i >= (shelf.size / 2)) {
                if (shelf[i] == null) {
                    index = i
                    break
                }
                i--
            }
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
        }

        if (category == BEST) {
            var i = 0
            while (i < shelf.size) {
                if (shelf[i] == null) {
                    index = i
                    break
                }
                i++
            }
        }

        if (index == -1) {
            return null
        }
        return index
    }

    private fun selectSlotForRegion(shelf: List<Bottle?>, category: Category, region: Region): Int? {
        var index: Int? = -1

        if (category == TO_KEEP) {
            var i = shelf.size - 1
            while (i >= (shelf.size / 2)) {
                if (shelf[i] != null && shelf[i]!!.region == region) {
                    index = i
                    break
                }
                i--
            }
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
        }

        if (category == BEST) {
            var i = 0
            while (i < shelf.size) {
                if (shelf[i] != null && shelf[i]!!.region == region) {
                    index = i
                    break
                }
                i++
            }
        }

        if (index == -1) {
            return null
        }
        return index
    }
}