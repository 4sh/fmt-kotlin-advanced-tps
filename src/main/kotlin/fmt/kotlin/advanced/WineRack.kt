package fmt.kotlin.advanced

import java.util.*

private const val EMPTY = "empty"

data class Capacity(val nbOfShelves: Int, val maxSlotByShelf: Int)

data class Position(val shelfIndex: Int, val slotIndex: Int)

class WineRack(val capacity: Capacity, val rackId: String = UUID.randomUUID().toString()) {

    private val bottles: MutableList<MutableList<Bottle?>> = mutableListOf<MutableList<Bottle?>>().apply {
        repeat(capacity.nbOfShelves) {
            add(arrayOfNulls<Bottle>(capacity.maxSlotByShelf).toMutableList())
        }
    }

    // tp1-step1-001
    // hint - check/require
    fun storeBottle(bottle: Bottle, position: Position) {
        if (position.shelfIndex >= capacity.nbOfShelves || position.slotIndex >= capacity.maxSlotByShelf) {
            throw IllegalArgumentException("wine rack position $position is out of capacity $capacity")
        }
        if (bottles[position.shelfIndex][position.slotIndex] != null) {
            throw IllegalArgumentException("the slot at $position is not free")
        }
        bottles[position.shelfIndex][position.slotIndex] = bottle
    }

    // tp1-step1-002
    // hint - scope function
    // hint - body expression
    fun takeBottle(position: Position): Bottle? {
        val bottle = viewBottle(position)
        bottles[position.shelfIndex][position.slotIndex] = null
        return bottle
    }


    // tp1-step1-003
    // hint - body expression
    fun viewBottle(position: Position): Bottle? {
        return bottles[position.shelfIndex][position.slotIndex]
    }

    // tp1-step1-004
    // hint - property getter
    // hint - collection - aggregation
    // hint - lambda - unique parameter
    // hint - lambda - trailing
    fun getNumberOfBottles(): Int {
        var counter = 0
        for (shelf in bottles) {
            for (slot in shelf) {
                if (slot != null) {
                    counter++
                }
            }
        }
        return counter
    }

    // tp1-step1-005
    // hint - sequence
    // hint - collection - filter
    // hint - collection - transformation
    fun streamBottles(): Sequence<Bottle> {
        val toReturn = mutableListOf<Bottle?>()
        for (shelf in bottles) {
            for (slot in shelf) {
                toReturn.add(slot)
            }
        }
        return sequence {
            var i = 0
            while (i < toReturn.size) {
                val bottle = toReturn[i]
                if (bottle != null) {
                    yield(bottle)
                }
                i++
            }
        }
    }

    override fun toString(): String {
        val maxLengthBySlotIndex: Map<Int, Int> = (0 until capacity.maxSlotByShelf)
            .associateWith { slotIndex ->
                (0 until capacity.nbOfShelves)
                    .maxOf { shelfIndex -> (bottles[shelfIndex][slotIndex]?.name ?: EMPTY).length + 5 }
            }
        return bottles.joinToString("\n") { shelf ->
            shelf
                .mapIndexed { index, bottle ->
                    (bottle?.let { "${it.name} ${it.year}" } ?: EMPTY)
                        .padEnd(maxLengthBySlotIndex.getValue(index))
                }
                .joinToString(" / ")
        } + "\n"
    }

    // tp1-step1-007
    // hint - class
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WineRack

        if (capacity != other.capacity) return false
        if (rackId != other.rackId) return false
        if (bottles != other.bottles) return false

        return true
    }

    // tp1-step1-007-bis
    // hint - class
    override fun hashCode(): Int {
        var result = capacity.hashCode()
        result = 31 * result + rackId.hashCode()
        result = 31 * result + bottles.hashCode()
        return result
    }

    fun at(shelfIndex: Int, slotIndex: Int): Bottle? {
        return bottles[shelfIndex][slotIndex]
    }

    fun at(shelfIndex: Int): MutableList<Bottle?> {
        return bottles[shelfIndex]
    }
}

