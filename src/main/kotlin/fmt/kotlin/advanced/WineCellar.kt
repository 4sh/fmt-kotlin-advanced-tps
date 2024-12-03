package fmt.kotlin.advanced

// tp7-step5

class WineCellar(val wineRacks: Map<String, Rack<Bottle>>) {
    val numberOfRacks = wineRacks.values.distinct().size
}