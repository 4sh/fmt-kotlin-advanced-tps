package fmt.kotlin.advanced

import fmt.kotlin.advanced.Color.RED
import fmt.kotlin.advanced.Region.BORDEAUX

// tp7-step4

fun magnumProducer(rack: WriteableRack<Magnum>, numberToProduce: Int) {
    val alreadyPresent = rack.numberOf
    sequence {
        while (true) {
            yield(Magnum("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null))
            yield(Magnum("Château Saint-Pierre", 2016, BORDEAUX, RED, 15, null))
            yield(Magnum("Château Latour", 2012, BORDEAUX, RED, 18, null))
            yield(Magnum("Château Meyney", 2018, BORDEAUX, RED, 19, keepUntil = 2042))
        }
    }.take(numberToProduce).forEachIndexed { index, bottle ->
        rack.store(
            bottle,
            (alreadyPresent + index) / rack.capacity.maxSlotByShelf at (alreadyPresent + index) % rack.capacity.maxSlotByShelf
        )
    }
}

fun standardProducer(rack: WriteableRack<Standard>, numberToProduce: Int) {
    val alreadyPresent = rack.numberOf
    sequence {
        while (true) {
            yield(Standard("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null))
            yield(Standard("Château Saint-Pierre", 2016, BORDEAUX, RED, 15, null))
            yield(Standard("Château Latour", 2012, BORDEAUX, RED, 18, null))
            yield(Standard("Château Meyney", 2018, BORDEAUX, RED, 19, keepUntil = 2042))
        }
    }.take(numberToProduce).forEachIndexed { index, bottle ->
        rack.store(
            bottle,
            (alreadyPresent + index) / rack.capacity.maxSlotByShelf at (alreadyPresent + index) % rack.capacity.maxSlotByShelf
        )
    }
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
