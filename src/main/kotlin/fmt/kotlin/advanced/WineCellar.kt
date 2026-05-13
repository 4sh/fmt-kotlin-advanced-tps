package fmt.kotlin.advanced

import kotlin.collections.distinct

class WineCellar(val wineRacks: Map<String, Rack<Bottle>>) {
    val numberOfRacks = wineRacks.values.distinct().size
}
