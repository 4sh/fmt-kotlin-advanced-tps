package fmt.kotlin.advanced

import fmt.kotlin.advanced.annotation.variance.VarianceInterface
import java.util.*


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

@VarianceInterface
data class Rack<T>(override val capacity: Capacity, override val rackId: String = UUID.randomUUID().toString()) :
    ReadableRack<T>, WriteableRack<T> {

    private val elements: Mutable2DList<T?> = mutableListOf<MutableList<T?>>().apply {
        repeat(capacity.nbOfShelves) {
            add(MutableList(capacity.maxSlotByShelf) { null })
        }
    }

    override operator fun get(position: Position) = elements[position]

    override operator fun get(shelfIndex: Int): List<T?> = elements[shelfIndex].toList()

    override operator fun set(position: Position, element: T) {
        store(element, position)
    }

    override fun store(element: T, position: Position) {
        check(position <= capacity) { "wine rack position $position is out of capacity $capacity" }
        check(elements[position] == null) { "the slot at $position is not free" }

        elements[position] = element
    }

    override fun take(position: Position): T? =
        view(position).also {
            elements[position] = null
        }

    override fun view(position: Position): T? = elements[position]

    override val numberOf: Int
        get() = elements.sumOf { it.filterNotNull().size }

    override fun stream(): Sequence<T> = elements.asSequence().flatten().filterNotNull()

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

