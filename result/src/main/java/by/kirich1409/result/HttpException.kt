package by.kirich1409.result

class HttpException(
    val statusCode: Int,
    val statusMessage: String? = null,
    val url: String? = null,
    cause: Throwable? = null
) : Exception(null, cause) {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (javaClass != other?.javaClass) {
            return false
        }

        other as HttpException
        return statusCode == other.statusCode &&
                url == other.url &&
                statusMessage == other.statusMessage &&
                cause == other.cause
    }

    override fun hashCode(): Int {
        return 31 * (31 * statusCode + statusMessage.hashCode()) + url.hashCode()
    }

    override fun toString(): String = buildString {
        append("HttpException(")
        append("statusCode=").append(statusCode).append(',')
        if (statusMessage != null) {
            append("statusMessage=").append(statusMessage).append(',')
        }
        if (url != null) {
            append("url=").append(url).append(',')
        }
        if (cause != null) {
            append("cause=").append(cause).append(',')
        }
        deleteCharAt(lastIndex) // Remove last ','
        append(')')
    }

    fun copy(): HttpException {
        return HttpException(statusCode, statusMessage, url, cause)
    }
}
