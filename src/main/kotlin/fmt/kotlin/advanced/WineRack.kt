package fmt.kotlin.advanced

import java.util.*

private const val EMPTY = "empty"

data class Capacity(val nbOfShelves: Int, val maxSlotByShelf: Int)

// tp2-step1-001
data class Position(val shelfIndex: Int, val slotIndex: Int)

data class WineRack(val capacity: Capacity, val rackId: String = UUID.randomUUID().toString()) {

    // tp2-step1-003
    private val bottles: MutableList<MutableList<Bottle?>> = mutableListOf<MutableList<Bottle?>>().apply {
        repeat(capacity.nbOfShelves) {
           add(arrayOfNulls<Bottle>(capacity.maxSlotByShelf).toMutableList())
        }
    }

    // tp2-step1-001
    fun storeBottle(bottle: Bottle, position: Position) {
        check(position.shelfIndex < capacity.nbOfShelves && position.slotIndex < capacity.maxSlotByShelf)
        { "wine rack position $position is out of capacity $capacity" }
        check(bottles[position.shelfIndex][position.slotIndex] == null)
        { "the slot at $position is not free" }

        bottles[position.shelfIndex][position.slotIndex] = bottle
    }

    // tp2-step1-003
    fun takeBottle(position: Position): Bottle? =
        viewBottle(position).also {
            bottles[position.shelfIndex][position.slotIndex] = null
        }

    // tp2-step1-003
    fun viewBottle(position: Position): Bottle? = bottles[position.shelfIndex][position.slotIndex]

    val numberOfBottles: Int
        get() = bottles.sumOf { it.filterNotNull().size }

    fun streamBottles(): Sequence<Bottle> = bottles.asSequence().flatten().filterNotNull()

    // tp2-step1-004
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

    // tp2-step1-002
    fun at(shelfIndex: Int, slotIndex: Int): Bottle? {
        return bottles[shelfIndex][slotIndex]
    }

    // tp2-step1-002
    fun at(shelfIndex: Int): MutableList<Bottle?> {
        return bottles[shelfIndex]
    }
}

