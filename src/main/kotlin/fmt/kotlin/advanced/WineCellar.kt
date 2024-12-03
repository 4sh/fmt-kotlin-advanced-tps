package fmt.kotlin.advanced

// tp7-step5

class WineCellar<T: Bottle>(val wineRacks: Map<String, Rack<T>>) {
    val numberOfRacks = wineRacks.values.distinct().size
}