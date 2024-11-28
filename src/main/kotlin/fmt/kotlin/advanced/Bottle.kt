package fmt.kotlin.advanced

// tp1-step3-001
// hint - data class
// hint - default parameter
// hint - multiline & interpolation
data class Bottle(
    val name: String,
    val year: Int,
    val region: Region,
    val color: Color,
    val rate: Int,
    val keepUntil: Int? = null,
) {
    val label = """
        $name $year ($color)
        Rate : $rate / $region
    """.trimIndent()
}