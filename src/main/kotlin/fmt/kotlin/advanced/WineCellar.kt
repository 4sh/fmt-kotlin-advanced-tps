package fmt.kotlin.advanced

class WineCellar(val wineRacks: Map<String, Rack<Bottle>>) {
    val numberOfRacks = wineRacks.values.distinct().size
}