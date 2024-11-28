package fmt.kotlin.advanced

// tp1-step3-001
// hint - data class
// hint - default parameter
// hint - multiline & interpolation
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