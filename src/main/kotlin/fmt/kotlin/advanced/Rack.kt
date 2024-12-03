package fmt.kotlin.advanced

import java.util.*

// tp7-step4

private const val EMPTY = "empty"

data class Capacity(val nbOfShelves: Int, val maxSlotByShelf: Int)

data class Position(val shelfIndex: Int, val slotIndex: Int) : Comparable<Capacity> {
    override fun compareTo(other: Capacity): Int =
        when (shelfIndex.compareTo(other.nbOfShelves - 1)) {
            0, -1 -> slotIndex.compareTo(other.maxSlotByShelf - 1)
            else -> 1
        }

    fun nextSlot() = Position(shelfIndex, slotIndex + 1)
    fun nextBeginShelf() = Position(shelfIndex + 1, 0)
}

infix fun Int.by(maxSlotByShelf: Int) = Capacity(this, maxSlotByShelf)

infix fun Int.at(slotIndex: Int) = Position(this, slotIndex)

typealias Mutable2DList<T> = MutableList<MutableList<T>>

data class Rack<T>(val capacity: Capacity, val rackId: String = UUID.randomUUID().toString()) {

    private val elements: Mutable2DList<T?> = mutableListOf<MutableList<T?>>().apply {
        repeat(capacity.nbOfShelves) {
            add(MutableList(capacity.maxSlotByShelf) { null })
        }
    }

    operator fun get(position: Position) = elements[position]

    operator fun get(shelfIndex: Int): List<T?> = elements[shelfIndex].toList()

    operator fun set(position: Position, element: T) {
        store(element, position)
    }

    fun store(element: T, position: Position) {
        check(position <= capacity) { "wine rack position $position is out of capacity $capacity" }
        check(elements[position] == null) { "the slot at $position is not free" }

        elements[position] = element
    }

    fun take(position: Position): T? =
        view(position).also {
            elements[position] = null
        }

    fun view(position: Position): T? = elements[position]

    val numberOf: Int
        get() = elements.sumOf { it.filterNotNull().size }

    fun stream(): Sequence<T> = elements.asSequence().flatten().filterNotNull()

    override fun toString(): String {
        val maxLengthBySlotIndex: Map<Int, Int> = (0 until capacity.maxSlotByShelf)
            .associateWith { slotIndex ->
                (0 until capacity.nbOfShelves)
                    .maxOf { shelfIndex -> (elements[shelfIndex at slotIndex].toString() ?: EMPTY).length + 5 }
            }
        return elements.joinToString("\n") { shelf ->
            shelf
                .mapIndexed { index, element ->
                    (element?.let { "$it" } ?: EMPTY)
                        .padEnd(maxLengthBySlotIndex.getValue(index))
                }
                .joinToString(" / ")
        } + "\n"
    }

    private operator fun Mutable2DList<T?>.get(position: Position) =
        this[position.shelfIndex][position.slotIndex]

    private operator fun Mutable2DList<T?>.set(position: Position, element: T?) {
        this[position.shelfIndex][position.slotIndex] = element

    }
}

