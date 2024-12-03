package fmt.kotlin.advanced

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

// tp7-step3

class TastingTest {

    @Test
    fun `should compatible with producer and consumer`() {
        // Given
        val rack = Rack<Bottle>(4 by 4)
        val rackForMagnumProducer: Rack<in Magnum> = rack
        val rackForStandardProducer: Rack<in Standard> = rack
        val rackForConsumer: Rack<out WineContainer> = rack

        magnumProducer(rackForMagnumProducer, 5)
        standardProducer(rackForStandardProducer, 5)

        wineContainerConsumer(rackForConsumer).toList() shouldBe listOf(
            "drink : Magnum Château Beau Rivage 2012",
            "drink : Magnum Château Saint-Pierre 2016",
            "drink : Magnum Château Latour 2012",
            "drink : Magnum Château Meyney 2018",
            "drink : Magnum Château Beau Rivage 2012",
            "drink : Standard Château Beau Rivage 2012",
            "drink : Standard Château Saint-Pierre 2016",
            "drink : Standard Château Latour 2012",
            "drink : Standard Château Meyney 2018",
            "drink : Standard Château Beau Rivage 2012",
        )
    }
}