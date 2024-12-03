package fmt.kotlin.advanced

class WineCellar<T: Bottle>(val wineRacks: Map<String, Rack<T>>) {
    val numberOfRacks = wineRacks.values.distinct().size
}