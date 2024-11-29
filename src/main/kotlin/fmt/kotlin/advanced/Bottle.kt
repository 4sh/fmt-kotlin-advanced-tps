package fmt.kotlin.advanced

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