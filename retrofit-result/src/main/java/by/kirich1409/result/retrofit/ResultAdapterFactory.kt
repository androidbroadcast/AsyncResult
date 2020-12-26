package by.kirich1409.result.retrofit

import by.kirich1409.result.HttpException
import by.kirich1409.result.Result
import okhttp3.Request
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResultAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        val rawReturnType = getRawType(returnType)
        if (rawReturnType != Result::class.java) {
            return null
        }

        if (returnType is ParameterizedType) {
            val innerType = getParameterUpperBound(0, returnType)
            if (getRawType(innerType) == Response::class.java) {
                return ResponseAdapter<Any?>(getParameterUpperBound(0, innerType as ParameterizedType))
            } else {
                return BodyAdapter<Any?>(innerType)
            }
        } else {
            return NothingAdapter
        }
    }
}

private class BodyAdapter<R>(private val type: Type) : CallAdapter<R, Result<R?>> {

    override fun responseType() = type

    override fun adapt(call: Call<R>): Result<R?> {
        try {
            val request = call.request()
            val response = call.execute()
            return response.toResult(request)
        } catch (error: Throwable) {
            return callErrorToResult(error)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> Response<T>.toResult(request: Request): Result<T?> {
        if (isSuccessful) {
            return Result.Success.Value.HttpSuccess(
                value = body() as T,
                statusCode = code(),
                statusMessage = message(),
                url = request.url().toString(),
            )
        } else {
            val httpException = HttpException(
                statusCode = code(),
                statusMessage = message(),
                url = request.url().toString(),
            )
            return Result.Failure.HttpError(httpException)
        }
    }
}

private object NothingAdapter : CallAdapter<Nothing, Result<Nothing?>> {

    override fun responseType() = Nothing::class.java

    override fun adapt(call: Call<Nothing>): Result<Nothing?> {
        try {
            val request = call.request()
            val response = call.execute()
            return response.toResult(request)
        } catch (error: Throwable) {
            return callErrorToResult(error)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun Response<*>.toResult(request: Request): Result<Nothing?> {
        if (isSuccessful) {
            return Result.Success.Value.HttpSuccess(
                value = null,
                statusCode = code(),
                statusMessage = message(),
                url = request.url().toString(),
            )
        } else {
            val httpException = HttpException(
                statusCode = code(),
                statusMessage = message(),
                url = request.url().toString(),
            )
            return Result.Failure.HttpError(httpException)
        }
    }
}

private class ResponseAdapter<R>(private val type: Type) : CallAdapter<R, Result<Response<R?>?>> {

    override fun responseType() = type

    override fun adapt(call: Call<R>): Result<Response<R?>?> {
        try {
            val request = call.request()
            val response = call.execute()
            return response.toResult(request)
        } catch (error: Throwable) {
            return callErrorToResult(error)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> Response<T?>.toResult(request: Request): Result<Response<T?>?> {
        if (isSuccessful) {
            return Result.Success.Value.HttpSuccess(
                value = this,
                statusCode = code(),
                statusMessage = message(),
                url = request.url().toString(),
            )
        } else {
            val httpException = HttpException(
                statusCode = code(),
                statusMessage = message(),
                url = request.url().toString(),
            )
            return Result.Failure.HttpError(httpException)
        }
    }
}

private fun <T> callErrorToResult(error: Throwable): Result<T?> {
    return when (error) {
        is retrofit2.HttpException ->
            Result.Failure.HttpError(HttpException(error.code(), error.message(), cause = error))
        is IOException -> Result.Failure.NetworkError(error)
        else -> Result.Failure.Error(error)
    }
}
