package fmt.kotlin.advanced

import fmt.kotlin.advanced.Color.RED
import fmt.kotlin.advanced.Region.BORDEAUX

// tp7-step6

inline fun <reified T : Bottle> producer(rack: WriteableRack<T>, numberToProduce: Int) {
    val alreadyPresent = rack.numberOf
    sequence {
        while (true) {
            yield(produceBottle<T>("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null))
            yield(produceBottle<T>("Château Saint-Pierre", 2016, BORDEAUX, RED, 15, null))
            yield(produceBottle<T>("Château Latour", 2012, BORDEAUX, RED, 18, null))
            yield(produceBottle<T>("Château Meyney", 2018, BORDEAUX, RED, 19, keepUntil = 2042))
        }
    }.take(numberToProduce).forEachIndexed { index, bottle ->
        rack.store(
            bottle,
            (alreadyPresent + index) / rack.capacity.maxSlotByShelf at (alreadyPresent + index) % rack.capacity.maxSlotByShelf
        )
    }
}

inline fun <reified T : Bottle> produceBottle(
    name: String,
    year: Int,
    region: Region,
    color: Color,
    rate: Int,
    keepUntil: Int? = null,
): T = when (T::class) {
    Magnum::class -> Magnum(name, year, region, color, rate, keepUntil) as T
    Standard::class -> Standard(name, year, region, color, rate, keepUntil) as T
    else -> error("")
}


fun wineContainerConsumer(rack: ReadableRack<WineContainer>): Sequence<String> {
    return sequence {
        var position = 0 at 0
        while (position <= rack.capacity) {
            if (rack.view(position) != null) {
                rack.take(position)?.also { yield("drink : ${it::class.simpleName} ${it.name} ${it.year}") }
            }
            position = position.nextSlot().takeIf { it <= rack.capacity } ?: position.nextBeginShelf()
        }
    }
}
