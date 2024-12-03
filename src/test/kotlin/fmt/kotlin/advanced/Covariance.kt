package fmt.kotlin.advanced



sealed class Result<out T, out E>()
class Success<T>( val value: T) : Result<T, Nothing>()
class Error<E>( val error: E) : Result<Nothing, E>()

fun main() {
    val intResult: Result<Int, Nothing> =  Success(42)
    val numberResult : Result<Number, Nothing> =  intResult
    val anyResult : Result<Any, Nothing> =  numberResult
}
