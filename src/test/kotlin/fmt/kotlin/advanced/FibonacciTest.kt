package fmt.kotlin.advanced

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.math.BigInteger

class FibonacciTest {

    tailrec fun fibonacci(nthNumber: BigInteger, previous: BigInteger = BigInteger.ZERO, next: BigInteger = BigInteger.ONE): BigInteger {
        if (nthNumber == BigInteger.ONE) {
            return previous
        }

        return fibonacci(nthNumber - BigInteger.ONE, next, previous + next)
    }

    @Test
    fun `compute first`() {
        val n = BigInteger.valueOf(1)
        val fibonacciNumber = fibonacci(n)

        fibonacciNumber.toString().takeLast(10) shouldBe "0"
        println("The $n-th Fibonacci number is: $fibonacciNumber")
    }

    @Test
    fun `compute second`() {
        val n = BigInteger.valueOf(2)
        val fibonacciNumber = fibonacci(n)

        fibonacciNumber.toString().takeLast(10) shouldBe "1"
        println("The $n-th Fibonacci number is: $fibonacciNumber")
    }

    @Test
    fun `compute third`() {
        val n = BigInteger.valueOf(3)
        val fibonacciNumber = fibonacci(n)

        fibonacciNumber.toString().takeLast(10) shouldBe "1"
        println("The $n-th Fibonacci number is: $fibonacciNumber")
    }


    @Test
    fun `compute 4 nth`() {
        val n = BigInteger.valueOf(4)
        val fibonacciNumber = fibonacci(n)

        fibonacciNumber.toString().takeLast(10) shouldBe "2"
        println("The $n-th Fibonacci number is: $fibonacciNumber")
    }

    @Test
    fun `compute 5 nth`() {
        val n = BigInteger.valueOf(5)
        val fibonacciNumber = fibonacci(n)

        fibonacciNumber.toString().takeLast(10) shouldBe "3"
        println("The $n-th Fibonacci number is: $fibonacciNumber")
    }

    @Test
    fun `compute 6 nth`() {
        val n = BigInteger.valueOf(6)
        val fibonacciNumber = fibonacci(n)

        fibonacciNumber.toString().takeLast(10) shouldBe "5"
        println("The $n-th Fibonacci number is: $fibonacciNumber")
    }


    @Test
    fun `compute 7 nth`() {
        val n = BigInteger.valueOf(7)
        val fibonacciNumber = fibonacci(n)

        fibonacciNumber.toString().takeLast(10) shouldBe "8"
        println("The $n-th Fibonacci number is: $fibonacciNumber")
    }

    @Test
    fun `compute 100 000 nth`() {
        val n =  BigInteger.valueOf(100_000)
        val fibonacciNumber = fibonacci(n)

        fibonacciNumber.toString().takeLast(10) shouldBe "6278790626"
        println("The 10 last digits of the $n-th Fibonacci number is: ${fibonacciNumber.toString().takeLast(10)}")
    }
}