package fmt.kotlin.advanced

sealed class Result< T, E>()
class Success<T>( val value: T) : Result<T, >()
class Error<E>( val error: E) : Result<, E>()

fun main() {
    val intResult: Result<Int, Nothing> =  Success(42)
    val numberResult : Result<Number, Nothing> =  intResult
    val anyResult : Result<Any, Nothing> =  numberResult
}
