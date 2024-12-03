package fmt.kotlin.advanced

import fmt.kotlin.advanced.Color.RED
import fmt.kotlin.advanced.Region.BORDEAUX

// tp7-step6

fun <T : Bottle> producer(rack: WriteableRack<T>, numberToProduce: Int) {
    val alreadyPresent = rack.numberOf
    sequence {
        while (true) {
            yield(produceBottle("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null))
            yield(produceBottle("Château Saint-Pierre", 2016, BORDEAUX, RED, 15, null))
            yield(produceBottle("Château Latour", 2012, BORDEAUX, RED, 18, null))
            yield(produceBottle("Château Meyney", 2018, BORDEAUX, RED, 19, keepUntil = 2042))
        }
    }.take(numberToProduce).forEachIndexed { index, bottle ->
        rack.store(
            bottle,
            (alreadyPresent + index) / rack.capacity.maxSlotByShelf at (alreadyPresent + index) % rack.capacity.maxSlotByShelf
        )
    }
}

fun produceBottle(

)


fun wineContainerConsumer(rack: ReadableRack<WineContainer>): Sequence<String> {
    return sequence {
        var position = 0 at 0
        while (position in rack.capacity) {
            if (rack.view(position) != null) {
                rack.take(position)?.also { yield("drink : ${it::class.simpleName} ${it.name} ${it.year}") }
            }
            position = position.nextSlot().takeIf { it in rack.capacity } ?: position.nextBeginShelf()
        }
    }
}
