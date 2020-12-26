@file:Suppress("unused")

package by.kirich1409.result

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun <T> Result<T>.isSuccess(): Boolean {
    return this is Result.Success
}

fun <T> Result<T>.asSuccess(): Result.Success<T> {
    return this as Result.Success<T>
}

@OptIn(ExperimentalContracts::class)
fun <T> Result<T>.isFailure(): Boolean {
    contract {
        returns(true) implies(this@isFailure is Result.Failure<*>)
    }
    return this is Result.Failure<*>
}

fun <T> Result<T>.asFailure(): Result.Failure<*> {
    return this as Result.Failure<*>
}

/**
 * Transform value from [Result.Success][this] and return [Result.Success] with new value
 */
fun <IN, OUT> Result<IN>.map(transform: (IN) -> OUT): Result<OUT> = when (this) {
    is Result.Success -> Result.Success.Value(transform(value))
    is Result.Failure<*> -> copy()
}

fun <IN> Result<IN>.toEmpty(): Result<Nothing> = when (this) {
    is Result.Success -> Result.Success.Empty
    is Result.Failure<*> -> copy()
}

fun <IN, OUT> Result<IN>.flatMap(transform: (Result<IN>) -> Result<OUT>): Result<OUT> {
    return transform(this)
}
