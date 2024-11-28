package fmt.kotlin.advanced

class WineCellar(val wineRacks: Map<String, WineRack>) {
    val numberOfRacks = wineRacks.values.distinct().size
}