package fmt.kotlin.advanced

class Bottle(
    val name: String,
    val year: Int,
    val region: Region,
    val color: Color,
    val rate: Int,
    val keepUntil: Int?,
) {
    val label = name + " " + year + " (" + color + ")\nRate : " + rate + " / " + region
}