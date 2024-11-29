package fmt.kotlin.advanced

import java.util.*

private const val EMPTY = "empty"

data class Capacity(val nbOfShelves: Int, val maxSlotByShelf: Int)

// tp2-step1-001
// hint - extension function
// hint - operator overload
data class Position(val shelfIndex: Int, val slotIndex: Int) : Comparable<Capacity> {
    override fun compareTo(other: Capacity): Int =
        when (shelfIndex.compareTo(other.nbOfShelves - 1)) {
            0, -1 -> slotIndex.compareTo(other.maxSlotByShelf - 1)
            else -> 1
        }
}

// tp2-step1-004
// hint - extension function
// hint - operator overload
infix fun Int.by(maxSlotByShelf: Int) = Capacity(this, maxSlotByShelf)

// tp2-step1-004
// hint - extension function
// hint - operator overload
infix fun Int.at(slotIndex: Int) = Position(this, slotIndex)

// tp2-step1-003
// hint - typealias
typealias Mutable2DList<T> = MutableList<MutableList<T>>


data class WineRack(val capacity: Capacity, val rackId: String = UUID.randomUUID().toString()) {

    // tp2-step1-003
    // hint - typealias
    private val bottles: Mutable2DList<Bottle?> = mutableListOf<MutableList<Bottle?>>().apply {
        repeat(capacity.nbOfShelves) {
            add(arrayOfNulls<Bottle>(capacity.maxSlotByShelf).toMutableList())
        }
    }

    // tp2-step1-002
    // hint - extension function
    // hint - operator overload
    operator fun get(position: Position) = bottles[position]

    // tp2-step1-002
    // hint - extension function
    // hint - operator overload
    operator fun get(shelfIndex: Int): List<Bottle?> = bottles[shelfIndex].toList()

    // tp2-step1-002
    // hint - extension function
    // hint - operator overload
    operator fun set(position: Position, bottle: Bottle) {
        storeBottle(bottle, position)
    }

    // tp2-step1-001
    // hint - extension function
    // hint - operator overload
    fun storeBottle(bottle: Bottle, position: Position) {
        check(position <= capacity) { "wine rack position $position is out of capacity $capacity" }
        check(bottles[position] == null) { "the slot at $position is not free" }

        bottles[position] = bottle
    }

    // tp2-step1-003
    // hint - extension function
    // hint - operator overload
    fun takeBottle(position: Position): Bottle? =
        viewBottle(position).also {
            bottles[position] = null
        }

    // tp2-step1-003
    // hint - extension function
    // hint - operator overload
    fun viewBottle(position: Position): Bottle? = bottles[position]

    val numberOfBottles: Int
        get() = bottles.sumOf { it.filterNotNull().size }

    fun streamBottles(): Sequence<Bottle> = bottles.asSequence().flatten().filterNotNull()

    // tp2-step1-004
    // hint - extension function
    // hint - infix
    override fun toString(): String {
        val maxLengthBySlotIndex: Map<Int, Int> = (0 until capacity.maxSlotByShelf)
            .associateWith { slotIndex ->
                (0 until capacity.nbOfShelves)
                    .maxOf { shelfIndex -> (bottles[shelfIndex at slotIndex]?.name ?: EMPTY).length + 5 }
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


    // tp2-step1-003
    // hint - extension function
    // hint - operator overload
    private operator fun Mutable2DList<Bottle?>.get(position: Position) =
        this[position.shelfIndex][position.slotIndex]

    // tp2-step1-003
    // hint - extension function
    // hint - operator overload
    private operator fun Mutable2DList<Bottle?>.set(position: Position, bottle: Bottle?) {
        this[position.shelfIndex][position.slotIndex] = bottle

    }
}

