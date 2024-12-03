package fmt.kotlin.advanced

// tp7-step1

abstract class Consumer<T> {
    abstract fun consume(t: T)
}

class Printer<T> : Consumer<T>() {
    override fun consume(t: T) {
        println()
    }
}

class Stock<T> : Consumer<T>() {
    private val stock: MutableList<T> = mutableListOf()

    override fun consume(t: T) {
        stock.add(t)
    }
}

fun main(args: Array<String>) {
    val printer1 = Printer<Number>()
    val printer2: Printer<Int> = printer1
    val printer3: Printer<Long> = printer1

    val stockAny = Stock<Any>()
    val stockInt: Stock<Double> = stockAny
    val stockString: Stock<Bottle> = stockAny

    val consumer1: Consumer<Number> = printer1
    val consumer2: Consumer<Int> = printer2
    val consumer3: Consumer<Long> = printer3
}