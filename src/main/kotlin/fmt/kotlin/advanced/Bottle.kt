package fmt.kotlin.advanced

open class WineContainer(
    open val name: String,
    open val year: Int,
    open val region: Region,
    open val color: Color,
    open val rate: Int,
) {
    val label: String by lazy {
        """
        $name $year ($color)
        Rate : $rate / $region
        """.trimIndent()
    }
}

open class Glass(
    override val name: String,
    override val year: Int,
    override val region: Region,
    override val color: Color,
    override val rate: Int,
) : WineContainer(name, year, region, color, rate)

class Spiegelau(
    override val name: String,
    override val year: Int,
    override val region: Region,
    override val color: Color,
    override val rate: Int,
) : Glass(name, year, region, color, rate)


class Ikea(
    override val name: String,
    override val year: Int,
    override val region: Region,
    override val color: Color,
    override val rate: Int,
) : Glass(name, year, region, color, rate)

open class Bottle(
    override val name: String,
    override val year: Int,
    override val region: Region,
    override val color: Color,
    override val rate: Int,
    open val keepUntil: Int? = null,
) : WineContainer(name, year, region, color, rate) {
    fun <T : Bottle> copyBottle(
        name: String = this.name,
        year: Int = this.year,
        region: Region = this.region,
        color: Color = this.color,
        rate: Int = this.rate,
        keepUntil: Int? = this.keepUntil
    ): T =
        when (this) {
            is Magnum -> this.copy(name, year, region, color, rate, keepUntil) as T
            is Standard -> this.copy(name, year, region, color, rate, keepUntil) as T
            else -> Bottle(name, year, region, color, rate, keepUntil) as T
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Bottle

        if (year != other.year) return false
        if (rate != other.rate) return false
        if (keepUntil != other.keepUntil) return false
        if (name != other.name) return false
        if (region != other.region) return false
        if (color != other.color) return false

        return true
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + rate
        result = 31 * result + (keepUntil ?: 0)
        result = 31 * result + name.hashCode()
        result = 31 * result + region.hashCode()
        result = 31 * result + color.hashCode()
        return result
    }

    override fun toString(): String {
        return "Bottle(name=$name, year=$year, region=$region, color=$color, rate=$rate, keepUntil=$keepUntil)"
    }

}

data class Magnum(
    override val name: String,
    override val year: Int,
    override val region: Region,
    override val color: Color,
    override val rate: Int,
    override val keepUntil: Int? = null,
    val number: Int
) : Bottle(name, year, region, color, rate, keepUntil)

data class Standard(
    override val name: String,
    override val year: Int,
    override val region: Region,
    override val color: Color,
    override val rate: Int,
    override val keepUntil: Int? = null,
) : Bottle(name, year, region, color, rate, keepUntil)