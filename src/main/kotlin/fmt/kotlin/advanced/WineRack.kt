package fmt.kotlin.advanced

import java.lang.StringBuilder
import java.util.*

data class Capacity(val nbOfShelves: Int, val maxSlotByShelf: Int)

data class Position(val shelfIndex: Int, val slotIndex: Int)

class WineRack(val capacity: Capacity, val rackId: String = UUID.randomUUID().toString()) {

    // tp1-step1-008
    // hint - scope function
    private val bottles: MutableList<MutableList<Bottle?>> = mutableListOf()
    init {
        repeat(capacity.nbOfShelves) {
            bottles.add(arrayOfNulls<Bottle>(capacity.maxSlotByShelf).toMutableList())
        }
    }

    // tp1-step1-001
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
    fun takeBottle(position: Position): Bottle? {
        val bottle = viewBottle(position)
        bottles[position.shelfIndex][position.slotIndex] = null
        return bottle
    }


    // tp1-step1-003
    fun viewBottle(position: Position): Bottle? {
        return bottles[position.shelfIndex][position.slotIndex]
    }

    // tp1-step1-004
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

    // tp1-step1-006
    override fun toString(): String {
        val maxLengthBySlotIndex = mutableMapOf<Int, Int>()
        var slotIndex = 0
        while (slotIndex < capacity.maxSlotByShelf) {
            var shelfIndex = 0
            while (shelfIndex < capacity.nbOfShelves) {
                var maxLength = 0
                if (bottles[shelfIndex][slotIndex] != null) {
                    maxLength = bottles[shelfIndex][slotIndex]!!.name.length + 5
                } else {
                    maxLength = "empty".length + 5
                }
                if (maxLengthBySlotIndex[slotIndex] != null) {
                    if (maxLengthBySlotIndex[slotIndex]!! < maxLength) {
                        maxLengthBySlotIndex[slotIndex] = maxLength
                    }
                } else {
                    maxLengthBySlotIndex[slotIndex] = maxLength
                }
                shelfIndex++
            }
            slotIndex++
        }

        val stringBuilder = StringBuilder()
        var shelfIndex = 0
        while (shelfIndex < capacity.nbOfShelves) {
            var slotIndex = 0
            while (slotIndex < capacity.maxSlotByShelf) {
                if (slotIndex > 0) {
                    stringBuilder.append(" / ")
                }
                var description = ""
                if (bottles[shelfIndex][slotIndex] != null) {
                    description = bottles[shelfIndex][slotIndex]!!.name + " " + bottles[shelfIndex][slotIndex]!!.year
                } else {
                    description = "empty"
                }
                stringBuilder.append(description.padEnd(maxLengthBySlotIndex[slotIndex]!!))
                slotIndex++
            }
            stringBuilder.append("\n")
            shelfIndex++
        }
        stringBuilder.append("\n")

        return stringBuilder.toString()
    }

    // tp1-step1-007
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WineRack

        if (capacity != other.capacity) return false
        if (rackId != other.rackId) return false
        if (bottles != other.bottles) return false

        return true
    }

    // tp1-step1-007
    override fun hashCode(): Int {
        var result = capacity.hashCode()
        result = 31 * result + rackId.hashCode()
        result = 31 * result + bottles.hashCode()
        return result
    }

    fun at(shelfIndex: Int, slotIndex: Int): Bottle? {
        return bottles[shelfIndex][slotIndex]
    }

    fun at( shelfIndex: Int): MutableList<Bottle?> {
        return bottles[shelfIndex]
    }
}

