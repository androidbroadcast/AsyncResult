@file:Suppress("unused")

package by.kirich1409.result

import by.kirich1409.result.Result.Failure
import by.kirich1409.result.Result.Success

/**
 * Representation of an operation Result that can be finished with [Success] or [Failure]
 */
sealed class Result<out T> {

    abstract fun copy(): Result<T>

    sealed class Success<T> : Result<T>() {

        abstract val value: T

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Success<*>
            return this.value == other.value
        }

        override fun hashCode() = value.hashCode()

        /**
         * Wrapper on value
         */
        open class Value<T>(override val value: T) : Success<T>() {

            override fun toString() = "Success.Value(value=$value)"

            override fun copy(): Value<T> = Value(value)

            data class HttpSuccess<T>(
                override val value: T,
                override val statusCode: Int,
                override val statusMessage: String? = null,
                override val url: String? = null
            ) : Value<T>(value), HttpResponse {

                override fun toString() = buildString {
                    append("Success.Http(value=").append(value)
                    append(", statusCode=").append(statusCode)
                    if (statusMessage != null) {
                        append(", statusMessage='").append(statusMessage).append('\'')
                    }
                    if (url != null) {
                        append(", url='").append(url).append('\'')
                    }
                    append(')')
                }

                override fun copy(): HttpSuccess<T> = HttpSuccess(value, statusCode, statusMessage, url)
            }
        }

        /**
         * Success result but without any value. Use for operation that completes or fails.
         */
        object Empty : Success<Nothing>() {

            override val value: Nothing get() = error("No value")

            override fun toString() = "Success.Empty"

            override fun copy(): Empty = Empty
        }
    }

    sealed class Failure<E : Throwable>(open val error: E? = null) : Result<Nothing>() {

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (javaClass != other?.javaClass) {
                return false
            }

            other as Failure<*>
            return error == other.error
        }

        override fun hashCode() = error.hashCode()

        override fun toString() = "${javaClass.simpleName}(error=$error)"

        /**
         * Error with network like no internet connection, timeout, etc.
         */
        class NetworkError(error: Throwable?) : Failure<Throwable>(error) {

            override fun copy(): Failure<Throwable> {
                return NetworkError(error)
            }
        }

        /**
         * Response of a server with an HTTP error.
         */
        class HttpError(override val error: HttpException) : Failure<HttpException>(), HttpResponse {

            override val statusCode: Int get() = error.statusCode

            override val statusMessage: String? get() = error.statusMessage

            override val url: String? get() = error.url

            override fun copy(): HttpError {
                return HttpError(error.copy())
            }
        }

        /**
         * Unknown error that is not categorized in any other subclass of [HttpError] or [NetworkError]
         */
        class Error(override val error: Throwable) : Failure<Throwable>(error) {

            override fun copy(): Error = Error(error)
        }
    }
}

/**
 * Calls the specified function [block] and returns its encapsulated result if invocation was successful,
 * catching and encapsulating any thrown exception as a failure.
 */
@Suppress("TooGenericExceptionCaught")
inline fun <T> tryCatch(block: () -> T): Result<T> {
    try {
        return Success.Value(block())
    } catch (e: Exception) {
        return Failure.Error(e)
    }
}

/**
 * Calls the specified function [block] and returns its encapsulated result if invocation was successful,
 * catching and encapsulating any thrown exception as a failure.
 */
@Suppress("TooGenericExceptionCaught")
@JvmName("tryCatchEmpty")
inline fun tryCatch(block: () -> Unit): EmptyResult {
    try {
        block()
        return Success.Empty
    } catch (e: Exception) {
        return Failure.Error(e)
    }
}

/**
 * [Result] that doesn't return any value
 */
typealias EmptyResult = Result<Nothing>
