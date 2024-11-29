package fmt.kotlin.advanced

import java.util.*

private const val EMPTY = "empty"

data class Capacity(val nbOfShelves: Int, val maxSlotByShelf: Int)

data class Position(val shelfIndex: Int, val slotIndex: Int) : Comparable<Capacity> {
    override fun compareTo(other: Capacity): Int =
        when (shelfIndex.compareTo(other.nbOfShelves - 1)) {
            0, -1 -> slotIndex.compareTo(other.maxSlotByShelf - 1)
            else -> 1
        }
}

infix fun Int.by(maxSlotByShelf: Int) = Capacity(this, maxSlotByShelf)

infix fun Int.at(slotIndex: Int) = Position(this, slotIndex)

typealias Mutable2DList<T> = MutableList<MutableList<T>>


data class WineRack(val capacity: Capacity, val rackId: String = UUID.randomUUID().toString()) {

    private val bottles: Mutable2DList<Bottle?> = mutableListOf<MutableList<Bottle?>>().apply {
        repeat(capacity.nbOfShelves) {
            add(arrayOfNulls<Bottle>(capacity.maxSlotByShelf).toMutableList())
        }
    }

    operator fun get(position: Position) = bottles[position]

    operator fun get(shelfIndex: Int): List<Bottle?> = bottles[shelfIndex].toList()

    operator fun set(position: Position, bottle: Bottle) {
        storeBottle(bottle, position)
    }

    fun storeBottle(bottle: Bottle, position: Position) {
        check(position <= capacity) { "wine rack position $position is out of capacity $capacity" }
        check(bottles[position] == null) { "the slot at $position is not free" }

        bottles[position] = bottle
    }

    fun takeBottle(position: Position): Bottle? =
        viewBottle(position).also {
            bottles[position] = null
        }

    fun viewBottle(position: Position): Bottle? = bottles[position]

    val numberOfBottles: Int
        get() = bottles.sumOf { it.filterNotNull().size }

    fun streamBottles(): Sequence<Bottle> = bottles.asSequence().flatten().filterNotNull()

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

    private operator fun Mutable2DList<Bottle?>.get(position: Position) =
        this[position.shelfIndex][position.slotIndex]

    private operator fun Mutable2DList<Bottle?>.set(position: Position, bottle: Bottle?) {
        this[position.shelfIndex][position.slotIndex] = bottle

    }
}

